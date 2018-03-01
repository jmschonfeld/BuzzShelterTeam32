package edu.gatech.spacebarz.buzzshelter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import edu.gatech.spacebarz.buzzshelter.model.FirebaseAuthManager;
import edu.gatech.spacebarz.buzzshelter.model.Shelter;
import edu.gatech.spacebarz.buzzshelter.model.ShelterListAdapter;

public class ShelterListActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressBar progressBar;
    private ShelterListAdapter listAdapter;

    @Override
    public void finish() {
        super.finish();
        FirebaseAuthManager.signout();
        Toast.makeText(getApplicationContext(), R.string.toast_logged_out, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuthManager.signout();
                finish();
            }
        });
        fab.setVisibility(View.GONE);

        listView = findViewById(R.id.shelters_list_view);
        progressBar = findViewById(R.id.shelters_loading_pbar);

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

        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Shelter shelter = listAdapter.getItem(i);
                Intent intent = new Intent(context, ShelterDetailActivity.class);
                intent.putExtra("shelter", shelter);
                startActivity(intent);
            }
        });

    }


}
