package edu.gatech.spacebarz.buzzshelter.model;

import android.content.Context;
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

    public ShelterListAdapter(Context context, ArrayList<Shelter> shelters) {
        super(context, 0, shelters);
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
