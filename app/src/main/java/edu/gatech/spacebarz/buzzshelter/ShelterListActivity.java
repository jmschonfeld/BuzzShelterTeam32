package edu.gatech.spacebarz.buzzshelter;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import edu.gatech.spacebarz.buzzshelter.model.FirebaseAuthManager;
import edu.gatech.spacebarz.buzzshelter.model.ShelterListAdapter;

public class ShelterListActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private ShelterListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuthManager.signout();
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.shelters_list_view);
        progressBar = (ProgressBar) findViewById(R.id.shelters_loading_pbar);

        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        listAdapter = new ShelterListAdapter(this);
        listView.setAdapter(listAdapter);

        listAdapter.fetchAllRemoteData(new Runnable() {
            @Override
            public void run() {
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }


}
