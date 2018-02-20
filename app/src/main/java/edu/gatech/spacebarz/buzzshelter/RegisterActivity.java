package edu.gatech.spacebarz.buzzshelter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

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
    }

    private AutoCompleteTextView nameView;
    private AutoCompleteTextView emailView;
    private EditText passwordView;
    private EditText phoneView;
    private Spinner userTypeSpinner;
}
