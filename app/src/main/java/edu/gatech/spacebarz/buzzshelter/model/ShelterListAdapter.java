package edu.gatech.spacebarz.buzzshelter.model;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.gatech.spacebarz.buzzshelter.R;

/**
 * Created by jmschonfeld on 2/27/18.
 */

public class ShelterListAdapter extends ArrayAdapter<Shelter> {

    public interface FetchDataCallback {
        /** Fetch remote shelters and return them (will be called from a non-main thread) */
        Shelter[] fetch();
    }

    /** Creates an empty shelter list adapter (used to fetch remote data) */
    public ShelterListAdapter(Context context) {
        super(context, 0, new ArrayList<Shelter>());
    }

    /** Creates a list adapter for the given list of shelters */
    public ShelterListAdapter(Context context, ArrayList<Shelter> shelters) {
        super(context, 0, shelters);
    }

    /** Fetches remote shelters through the fetching callback and adds them to the adapter */
    public void fetchRemoteData(@NonNull final FetchDataCallback fetchCaller, @Nullable final Runnable callback) {
        Log.i("ShelterListAdapter", "Fetching all shelter data...");
        final Handler uiHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                final Shelter[] shelters = fetchCaller.fetch();
                Log.i("ShelterListAdapter", "Retrieved shelter data (" + shelters.length + " shelters)");
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        clear();
                        addAll(shelters);
                        notifyDataSetChanged();
                        if (callback != null) {
                            callback.run();
                        }
                    }
                });
            }
        }.start();
    }

    /** Fetches all shelters from Firebase and stores them within the list adapter */
    public void fetchAllRemoteData(@Nullable final Runnable callback) {
        this.fetchRemoteData(new FetchDataCallback() {
            @Override
            public Shelter[] fetch() {
                return FirebaseDBManager.retrieveAllShelters();
            }
        }, callback);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Shelter shelter = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_shelter, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.item_shelter_name)).setText(shelter.getShelterName());
        ((TextView) convertView.findViewById(R.id.item_shelter_detail)).setText(shelter.getPhoneNum());

        // Return the completed view to render on screen
        return convertView;
    }

}
