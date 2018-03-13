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
import android.widget.TwoLineListItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import edu.gatech.spacebarz.buzzshelter.R;
import edu.gatech.spacebarz.buzzshelter.model.Shelter;

public class ShelterListAdapter extends ArrayAdapter<Shelter> {

    public abstract static class ShelterFilter implements Serializable {
        /** Returns whether or not the given shelter should be included */
        public abstract boolean filter(Shelter shelter);
    }

    public enum ShelterSort {
        DEFAULT() {
            @Override
            public int compare(Shelter a, Shelter b) {
                return a.getUID().compareToIgnoreCase(b.getUID());
            }
        }, ALPHABETICAL() {
            @Override
            public int compare(Shelter a, Shelter b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        };

        /**
         * Compares the two given shelters
         * @return a negative value if a comes before b, zero if a equals b, and a positive value if a comes after b
         */
        public abstract int compare(Shelter a, Shelter b);
    }

    public interface FetchDataCallback {
        /** Fetch remote shelters and return them (will be called from a non-main thread) */
        Shelter[] fetch();
    }

    private ShelterFilter filter;
    private ShelterSort sort = ShelterSort.DEFAULT;
    private ArrayList<Shelter> fullData;
    private Shelter priorityItem;

    /** Creates an empty shelter list adapter (used to fetch remote data) */
    public ShelterListAdapter(Context context) {
        this(context, new ArrayList<Shelter>());
    }

    /** Creates a list adapter for the given list of shelters */
    public ShelterListAdapter(Context context, ArrayList<Shelter> shelters) {
        super(context, R.layout.item_shelter_list, shelters);
    }

    /** Fetches remote shelters through the fetching callback and adds them to the adapter */
    public void fetchRemoteData(@NonNull final FetchDataCallback fetchCaller, @Nullable final Runnable callback) {
        Log.i("ShelterListAdapter", "Fetching shelter data...");
        final Handler uiHandler = new Handler();
        new Thread() {
            @Override
            public void run() {
                final Shelter[] shelters = fetchCaller.fetch();
                fullData = new ArrayList<>(Arrays.asList(shelters));
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_shelter_list, parent, false);
        }

        if (shelter != null) {
            TextView mainText = convertView.findViewById(R.id.text1);
            TextView subText = convertView.findViewById(R.id.text2);
            ImageView newbornImage = convertView.findViewById(R.id.shelter_image_childfriendly);
            ImageView childImage = convertView.findViewById(R.id.shelter_image_youngadult);
            ImageView maleImage = convertView.findViewById(R.id.shelter_image_male);
            ImageView femaleImage = convertView.findViewById(R.id.shelter_image_female);
            ImageView allGenderImage = convertView.findViewById(R.id.shelter_image_allgender);

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
            }

            switch (shelter.getAgeRest()) {
                case FAMILIESWITHNEWBORNS:
                    newbornImage.setVisibility(View.VISIBLE);
                case CHILDREN:
                    childImage.setVisibility(View.VISIBLE);
            }

            if (position == 0 && priorityItem != null && priorityItem.equals(shelter)) {
                Log.i("Color", "SETTING COLOR on index " + shelter.getName());
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.listPriorityItemBackground));
            }

        } else {
            Log.e("ShelterListAdapter", "Tried to load data for shelter at index " + position + " which does not exist");
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private ArrayList<Shelter> getFilteredData() {
        ArrayList<Shelter> allowed = new ArrayList<>();
        for (Shelter shelter : fullData) {
            if (this.filter.filter(shelter)) {
                allowed.add(shelter);
            }
        }
        return allowed;
    }

    public void setFilter(@Nullable ShelterFilter filter) {
        this.filter = filter;
        clear();
        if (priorityItem != null) {
            add(priorityItem);
        }
        if (this.filter == null) {
            addAll(fullData);
        } else {
            addAll(getFilteredData());
        }
        notifyDataSetChanged();
    }

    public void sort(@NonNull final ShelterSort sorter) {
        this.sort = sorter;
        Collections.sort(fullData, new Comparator<Shelter>() {
            @Override
            public int compare(Shelter shelter, Shelter t1) {
                return sorter.compare(shelter, t1);
            }
        });
        setFilter(filter);
    }

    public void setPriorityItem(@Nullable Shelter item) {
        if (item != null && !fullData.contains(item)) {
            throw new IllegalArgumentException("Cannot set a priority item if the item is not in the list");
        }

        if (item == null) {
            if (priorityItem == null) {
                return;
            }
            fullData.add(priorityItem);
            sort(sort);
        } else {
            if (priorityItem != null) {
                setPriorityItem(null);
            }
            fullData.remove(item);
            priorityItem = item;
            sort(sort);
        }
    }
}
