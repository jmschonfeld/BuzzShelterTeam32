package edu.gatech.spacebarz.buzzshelter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import edu.gatech.spacebarz.buzzshelter.model.FirebaseAuthManager;
import edu.gatech.spacebarz.buzzshelter.model.Shelter;
import edu.gatech.spacebarz.buzzshelter.model.ShelterListAdapter;
import edu.gatech.spacebarz.buzzshelter.model.ShelterListAdapter.ShelterFilter;

public class ShelterListActivity extends AppCompatActivity {

    private static final int FILTER_LIST_RETURN_REQUEST_CODE = 1001;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("ActivityResult", "code=" + requestCode + " res=" + resultCode + " ok=" + RESULT_OK + " cancel=" + RESULT_CANCELED);
        if (requestCode == FILTER_LIST_RETURN_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data.hasExtra("filter")) {
                ShelterFilter filter = (ShelterFilter) data.getSerializableExtra("filter");
                this.listAdapter.setFilter(filter);
                Log.i("New Size", ""+ this.listAdapter.getCount());
                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Filtered shelter list", Snackbar.LENGTH_INDEFINITE);
                snack.setAction("View All Shelters", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listAdapter.setFilter(null);
                        snack.dismiss();
                    }
                });
                snack.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        final Context context = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilterSheltersActivity.class);
                startActivityForResult(intent, FILTER_LIST_RETURN_REQUEST_CODE);
            }
        });

        listView = findViewById(R.id.shelters_list_view);
        progressBar = findViewById(R.id.shelters_loading_pbar);

        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        listAdapter = new ShelterListAdapter(this);
        listView.setAdapter(listAdapter);

        listView.setNestedScrollingEnabled(true);

        listAdapter.fetchAllRemoteData(new Runnable() {
            @Override
            public void run() {
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

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
