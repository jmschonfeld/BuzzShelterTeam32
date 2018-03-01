package edu.gatech.spacebarz.buzzshelter;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.gatech.spacebarz.buzzshelter.model.Shelter;

public class ShelterDetailActivity extends AppCompatActivity {

    private Shelter shelter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_detail);

        shelter = (Shelter) getIntent().getSerializableExtra("shelter");

        TextView nameView = findViewById(R.id.shelter_detail_name);
        TextView addrView = findViewById(R.id.shelter_detail_address);
        TextView phoneView = findViewById(R.id.shelter_detail_phone);
        TextView capacityView = findViewById(R.id.shelter_detail_capacity);
        TextView restrictionsView = findViewById(R.id.shelter_detail_restrictions);
        TextView genderView = findViewById(R.id.shelter_detail_gender);
        TextView notesView = findViewById(R.id.shelter_detail_notes);

        nameView.setText(shelter.getName());
        addrView.setText(shelter.getAddress());
        phoneView.setText(shelter.getPhone());
        capacityView.setText(getResources().getString(R.string.shelter_capacity, shelter.getCapacityStr()));
        restrictionsView.setText(getResources().getString(R.string.shelter_restrictions, shelter.getRestrictions()));
        genderView.setText(getResources().getString(R.string.shelter_gender, shelter.getGender().name().toLowerCase()));
        notesView.setText(getResources().getString(R.string.shelter_notes, shelter.getNotes()));
        notesView.setSingleLine(false);

        // For debug purposes
        nameView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Snackbar.make(view, "UID: " + shelter.getUID(), Snackbar.LENGTH_LONG).show();
                return true;
            }
        });

        Button callButton = findViewById(R.id.shelter_call);
        Button directionsButton = findViewById(R.id.shelter_directions);

        final Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse("tel:" + shelter.getPhone()));
        if (phoneIntent.resolveActivity(getPackageManager()) != null) {
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(phoneIntent);
                }
            });
        } else {
            callButton.setEnabled(false);
        }

        final Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(Uri.parse("geo:0,0?q=" + shelter.getLat() + "," + shelter.getLon() + "(" + shelter.getName() + ")"));
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            directionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(mapIntent);
                }
            });
        } else {
            directionsButton.setEnabled(false);
        }
    }
}
