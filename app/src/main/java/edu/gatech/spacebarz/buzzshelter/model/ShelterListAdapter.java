package edu.gatech.spacebarz.buzzshelter.model;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class ShelterListAdapter extends ArrayAdapter<Shelter> {

    public abstract static class ShelterFilter implements Serializable {
        /** Returns whether or not the given shelter should be included */
        public abstract boolean filter(Shelter shelter);
    }

    public interface FetchDataCallback {
        /** Fetch remote shelters and return them (will be called from a non-main thread) */
        Shelter[] fetch();
    }

    private ShelterFilter filter;
    private Shelter[] fullData;

    /** Creates an empty shelter list adapter (used to fetch remote data) */
    public ShelterListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2, new ArrayList<Shelter>());
    }

    /** Creates a list adapter for the given list of shelters */
    public ShelterListAdapter(Context context, ArrayList<Shelter> shelters) {
        super(context, android.R.layout.simple_list_item_2, shelters);
    }

    /** Fetches remote shelters through the fetching callback and adds them to the adapter */
    public void fetchRemoteData(@NonNull final FetchDataCallback fetchCaller, @Nullable final Runnable callback) {
        Log.i("ShelterListAdapter", "Fetching shelter data...");
        final Handler uiHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                final Shelter[] shelters = fetchCaller.fetch();
                fullData = shelters;
                filter = null;
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
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        if (shelter != null) {
            TextView mainText = convertView.findViewById(android.R.id.text1);
            TextView subText = convertView.findViewById(android.R.id.text2);
            mainText.setTypeface(Typeface.DEFAULT_BOLD);
            mainText.setText(shelter.getName());
            subText.setText(shelter.getAddress());
        } else {
            Log.e("ShelterListAdapter", "Tried to load data for shelter at index " + position + " which does not exist");
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public void setFilter(@Nullable ShelterFilter filter) {
        this.filter = filter;
        clear();
        if (this.filter == null) {
            addAll(fullData);
        } else {
            for (Shelter shelter : fullData) {
                if (this.filter.filter(shelter)) {
                    add(shelter);
                }
            }
        }
        notifyDataSetChanged();
    }
}
