package edu.gatech.spacebarz.buzzshelter.util;

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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.spacebarz.buzzshelter.R;
import edu.gatech.spacebarz.buzzshelter.model.Shelter;

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
        this(context, new ArrayList<Shelter>());
    }

    /** Creates a list adapter for the given list of shelters */
    public ShelterListAdapter(Context context, List<Shelter> shelters) {
        super(context, R.layout.item_shelter_list, shelters);
        fullData = shelters.toArray(new Shelter[shelters.size()]);
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
        View theView = convertView;
        if (theView == null) {
            theView = LayoutInflater.from(getContext()).inflate(R.layout.item_shelter_list, parent, false);
        }

        if (shelter != null) {
            TextView mainText = theView.findViewById(R.id.text1);
            TextView subText = theView.findViewById(R.id.text2);
            ImageView newbornImage = theView.findViewById(R.id.shelter_image_childfriendly);
            ImageView childImage = theView.findViewById(R.id.shelter_image_youngadult);
            ImageView maleImage = theView.findViewById(R.id.shelter_image_male);
            ImageView femaleImage = theView.findViewById(R.id.shelter_image_female);
            ImageView allGenderImage = theView.findViewById(R.id.shelter_image_allgender);

            mainText.setTypeface(Typeface.DEFAULT_BOLD);
            mainText.setText(shelter.getName());
            subText.setText(shelter.getAddress());

            newbornImage.setVisibility(View.GONE);
            childImage.setVisibility(View.GONE);
            maleImage.setVisibility(View.GONE);
            femaleImage.setVisibility(View.GONE);
            allGenderImage.setVisibility(View.GONE);

            switch (shelter.getGender()) {
                case MALE:
                    maleImage.setVisibility(View.VISIBLE); break;
                case FEMALE:
                    femaleImage.setVisibility(View.VISIBLE); break;
                case ALL:
                    break;
            }

            switch (shelter.getAgeRest()) {
                case FAMILIESWITHNEWBORNS:
                    newbornImage.setVisibility(View.VISIBLE); break;
                case CHILDREN:
                    childImage.setVisibility(View.VISIBLE); break;
                case YOUNGADULTS:
                    break;
                case ALL:
                    break;
            }

        } else {
            Log.e("ShelterListAdapter", "Tried to load data for shelter at index " + position + " which does not exist");
        }

        // Return the completed view to render on screen
        return theView;
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
