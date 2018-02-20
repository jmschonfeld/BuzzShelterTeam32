package edu.gatech.spacebarz.buzzshelter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
        // Close soft keyboard
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void closeOnSuccess() {
        Toast.makeText(getApplicationContext(), R.string.toast_account_created, Toast.LENGTH_SHORT).show();
        finish();
    }


    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {
        UserRegistrationTask(String eml, String pass) {
            email = eml;
            password = pass;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final CountDownLatch l = new CountDownLatch(1);
            FirebaseAuthManager.signupUser(email, password, new Runnable() {
                @Override
                public void run() {
                    l.countDown();
                }
            });
            try {
                l.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return FirebaseAuthManager.getAccountCreateException() == null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            regTask = null;
            if (success) {
                closeOnSuccess();
            } else {
                Exception exe = FirebaseAuthManager.getAccountCreateException();
                View focusView = null;

                try {
                    throw exe;
                } catch (FirebaseAuthUserCollisionException e) {
                    emailView.setError(getString(R.string.error_email_already_registered));
                    focusView = emailView;
                    closeSoftKeyboard();
                } catch (FirebaseAuthWeakPasswordException e) {
                    passwordView.setError(getString(R.string.error_password_requirements));
                    focusView = passwordView;
                } catch (Exception e) {
                    Log.e("Registration(Firebase)", e.getMessage());
                } finally {
                    if (focusView != null)
                        focusView.requestFocus();
                }
            }
        }

        private final String email;
        private final String password;
    }

    private EditText nameView;
    private EditText emailView;
    private EditText passwordView;
    private EditText phoneView;
    private Spinner userTypeSpinner;

    private UserRegistrationTask regTask = null;

    private enum UserRole {
        USER("User"), ADMINISTRATOR("Administrator"), SHELTER_EMPLOYEE("Shelter Employee");

        UserRole(String s) {
            label = s;
        }
        private String label;
    }
}