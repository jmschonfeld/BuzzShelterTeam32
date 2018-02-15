package edu.gatech.spacebarz.buzzshelter;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.gatech.spacebarz.buzzshelter.model.LocalUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalUser.getInstance(getApplicationContext()).logout();
                Toast.makeText(getApplicationContext(), R.string.toast_logged_out, Toast.LENGTH_SHORT).show();
                returnToLogin();
            }
        });
    }

    private void returnToLogin() {
        finish();
    }
}