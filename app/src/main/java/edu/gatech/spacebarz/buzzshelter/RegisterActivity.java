package edu.gatech.spacebarz.buzzshelter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.concurrent.CountDownLatch;

import edu.gatech.spacebarz.buzzshelter.model.FirebaseAuthManager;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //localize fields
        nameView = findViewById(R.id.input_name);
        emailView = findViewById(R.id.input_email);
        passwordView = findViewById(R.id.input_password);
        phoneView = findViewById(R.id.input_phoneNumber);
        userTypeSpinner = findViewById(R.id.spinner_userType);
        progressBar = findViewById(R.id.reg_progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        //Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, UserRole.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        Button registerButton = findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        if (regTask != null)
            return;

        boolean preSubErr = false;
        View focusView = null;

        emailView.setError(null);
        passwordView.setError(null);

        String eml = emailView.getText().toString().trim();
        String pass = passwordView.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(eml).matches()) {
            preSubErr = true;
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
        }

        // Check other fields once implemented

        if (preSubErr) {
            focusView.requestFocus();
            return;
        }

        regTask = new UserRegistrationTask(eml, pass);
        regTask.execute((Void) null);
    }

    private void closeSoftKeyboard() {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void closeOnSuccess() {
        Toast.makeText(getApplicationContext(), R.string.toast_account_created, Toast.LENGTH_SHORT).show();
//      Undo auto sign in
        FirebaseAuthManager.signout();
        finish();
    }


    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
        private Exception exe;
        private boolean succ;
        private final String email;
        private final String password;

        UserRegistrationTask(String eml, String pass) {
            email = eml;
            password = pass;
            progressBar.setVisibility(View.VISIBLE);
            closeSoftKeyboard();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final CountDownLatch l = new CountDownLatch(1);
            FirebaseAuthManager.signupUser(email, password, new FirebaseAuthManager.FirebaseAuthCallback() {
                @Override
                public void callback(boolean success, @Nullable Exception exception) {
                    exe = exception;
                    succ = success;
                    l.countDown();
                }
            });
            try {
                l.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return succ;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progressBar.setVisibility(View.INVISIBLE);
            regTask = null;
            if (success) {
                closeOnSuccess();
            } else {
                View focusView = null;
                if (exe instanceof FirebaseAuthUserCollisionException){
                    emailView.setError(getString(R.string.error_email_already_registered));
                    focusView = emailView;
                    closeSoftKeyboard();
                } else if (exe instanceof FirebaseAuthWeakPasswordException) {
                    passwordView.setError(getString(R.string.error_password_requirements));
                    focusView = passwordView;
                } else {
                    Log.e("Registration(Firebase)", exe.getMessage());
                }

                if (focusView != null)
                    focusView.requestFocus();
            }
        }
    }

    private EditText nameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText phoneView;
    private Spinner userTypeSpinner;

    private ProgressBar progressBar;

    private UserRegistrationTask regTask = null;

    private enum UserRole {
        USER("User"), ADMINISTRATOR("Administrator"), SHELTER_EMPLOYEE("Shelter Employee");

        UserRole(String s) {
            label = s;
        }
        private String label;
    }
}