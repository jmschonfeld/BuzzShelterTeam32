package edu.gatech.spacebarz.buzzshelter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.concurrent.CountDownLatch;

import edu.gatech.spacebarz.buzzshelter.model.FirebaseAuthManager;

public class LoginActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText loginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuthManager.initialize();

        loggingIn = false;

        // Set up the login form.
        loginView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button registerButton = findViewById(R.id.signin_register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
                moveToRegisterActivity();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (FirebaseAuthManager.isLoggedIn()) {
            moveToMainActivity();
        }
    }

    private void moveToRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        clearFields();
        loginView.requestFocus();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid [username], missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        loginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String password = mPasswordView.getText().toString().trim();
        String email = loginView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a empty email
        if (TextUtils.isEmpty(email)) {
            loginView.setError(getString(R.string.error_field_required));
            focusView = loginView;
            cancel = true;
        }

        // Check for valid email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginView.setError(getString(R.string.error_invalid_email));
            focusView = loginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Close soft keyboard
            View v = this.getCurrentFocus();
            if (v != null) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });


        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUser;
        private final String mPassword;
        private Exception exe;
        private boolean succ;

        UserLoginTask(String user, String password) {
            mUser = user;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            loggingIn = true;

            final CountDownLatch latch = new CountDownLatch(1);
            FirebaseAuthManager.signin(mUser, mPassword, new FirebaseAuthManager.FirebaseAuthCallback() {
                @Override
                public void callback(boolean success, @Nullable Exception exception) {
                    succ = success;
                    exe = exception;
                    latch.countDown();
                }
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return succ;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loggingIn = false;
            mAuthTask = null;
            showProgress(false);

            if (success) {
                moveToMainActivity();
            } else {
                if (exe instanceof FirebaseAuthInvalidCredentialsException) {
                    mPasswordView.requestFocus();
                    mPasswordView.setError("Invalid email or password");
                } else {
                    exe.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            loggingIn = false;
            mAuthTask = null;
            showProgress(false);
            mPasswordView.setText("");
            mPasswordView.requestFocus();

            Toast.makeText(getApplicationContext(), R.string.toast_login_canceled, Toast.LENGTH_SHORT).show();
            Log.i("Login", "Login canceled");
            cancel(true);
        }
    }

    private void clearFields() {
        loginView.setText("");
        mPasswordView.setText("");
    }

    @Override
    public void onBackPressed() {
        if (loggingIn) {
            mAuthTask.cancel(true);
            return;
        }

        super.onBackPressed();
    }

    private boolean loggingIn;
}