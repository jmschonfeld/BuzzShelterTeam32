package edu.gatech.spacebarz.buzzshelter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.concurrent.CountDownLatch;

import edu.gatech.spacebarz.buzzshelter.util.FirebaseAuthManager;
import edu.gatech.spacebarz.buzzshelter.util.UIUtil;

public class LoginActivity extends AppCompatActivity {

    private final String SS_PREFERENCES = "ShelterSeekerLoginPreferences", LOGIN_ATTEMPTS_PREF = "loginAttempts", LAST_LOGIN_PREF = "lastLoginAttempt";

    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText loginView;
    private EditText mPasswordView;
    private ProgressBar progressBar;

    private SharedPreferences prefs;
    private int attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuthManager.initialize();

        prefs = this.getSharedPreferences(SS_PREFERENCES, 0);

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

        Button recoveryButton = findViewById(R.id.recovery_button);
        final Context context = this;
        recoveryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginView.getText().toString().equals("")) {
                    loginView.setError("Email address is required");
                    return;
                }
                Log.i("FirebaseAuth", "Sending recovery email...");
                FirebaseAuthManager.sendRecoveryEmail(loginView.getText().toString());
                Toast.makeText(context, "Recovery email sent!", Toast.LENGTH_SHORT).show();
            }
        });

        progressBar = findViewById(R.id.login_progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        if (FirebaseAuthManager.isLoggedIn()) {
            moveToMainActivity();
        }
    }

    private void moveToRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(this, MapActivity.class);
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

        attempts = prefs.getInt(LOGIN_ATTEMPTS_PREF, 0);
        long lastAttempt = prefs.getLong(LAST_LOGIN_PREF, 0);
        if (lastAttempt < System.currentTimeMillis() - 1000 * 60 * 3) {
            attempts = 0;
            prefs.edit().putInt(LOGIN_ATTEMPTS_PREF, 0).putLong(LAST_LOGIN_PREF, 0).apply();
        }

        if (attempts >= 3) {
            mPasswordView.setError("Too many attempts. You have been locked out for 5 minutes");
            return;
        }

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
            UIUtil.closeSoftKeyboard(getCurrentFocus(), getBaseContext());

//          BG login task
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUser;
        private final String mPassword;
        private Exception exe;
        private boolean succ;

        UserLoginTask(String user, String password) {
            mUser = user;
            mPassword = password;
            progressBar.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);
            loggingIn = false;
            mAuthTask = null;

            if (success) {
                prefs.edit().putInt(LOGIN_ATTEMPTS_PREF, 0).putLong(LAST_LOGIN_PREF, 0).apply();
                moveToMainActivity();
            } else {
                prefs.edit().putInt(LOGIN_ATTEMPTS_PREF, attempts + 1).putLong(LAST_LOGIN_PREF, System.currentTimeMillis()).apply();
                if (exe instanceof FirebaseAuthInvalidCredentialsException || exe instanceof FirebaseAuthInvalidUserException) {
                    mPasswordView.requestFocus();
                    mPasswordView.setError("Invalid email or password");
                } else {
                    exe.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.INVISIBLE);
            loggingIn = false;
            mAuthTask = null;
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