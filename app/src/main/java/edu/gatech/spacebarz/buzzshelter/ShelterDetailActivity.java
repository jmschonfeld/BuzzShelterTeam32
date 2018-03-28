package edu.gatech.spacebarz.buzzshelter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import edu.gatech.spacebarz.buzzshelter.util.FirebaseAuthManager;
import edu.gatech.spacebarz.buzzshelter.util.FirebaseDBManager;
import edu.gatech.spacebarz.buzzshelter.model.Shelter;

public class ShelterDetailActivity extends AppCompatActivity {

    private Shelter shelter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_detail);

        shelter = (Shelter) getIntent().getSerializableExtra("shelter");
        final String shelterUID = getIntent().getStringExtra("shelterUID");
        final ConstraintLayout loadingLayout = findViewById(R.id.shelter_detail_loading_layout);
        final LinearLayout mainLayout = findViewById(R.id.shelter_detail_main_layout);

        if (shelter != null) {
            loadingLayout.setVisibility(View.GONE);
            this.setupViewWith(shelter);
        } else {
            mainLayout.setVisibility(View.GONE);
            final Handler uiHandler = new Handler();
            new Thread() {
                @Override
                public void run() {
                    shelter = FirebaseDBManager.retrieveShelterInfo(shelterUID);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.setVisibility(View.VISIBLE);
                            setupViewWith(shelter);
                            loadingLayout.setVisibility(View.GONE);
                        }
                    });
                }
            }.start();
        }
    }

    private void setupViewWith(final Shelter shelter) {
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
        resButton = findViewById(R.id.shelter_reservation_button);
        
//      Crashes on condition, not sure why
//        if (FirebaseDBManager.retrieveCurrentUserInfo().getCurrentReservation().equals(shelter.getUID()))
//            btnRes();
//        else {
//            if (shelter.getVacancyNum() == 0)
//                btnNoVac();
//            else
//                btnNoRes();
//        }

//      Debug
//        btnRes();
        btnNoVac();
//        btnNoRes();

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

//  Put these in methods just in case we do something like offline action queueing
    private void btnRes() {
        resButton.setText(getResources().getString(R.string.shelter_cancel));
        resButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 3/27/18 handle DB res cancel and relaunch activity
            }
        });
    }

    private void btnNoVac() {
        resButton.setText(getResources().getString(R.string.shelter_no_vacancies));
        resButton.setEnabled(false);
    }

    private void btnNoRes() {
        resButton.setText(getResources().getString(R.string.shelter_reserve));
        resButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//              Show picker dialog
                final Dialog d = new Dialog(ShelterDetailActivity.this);
                d.setTitle(getResources().getString(R.string.shelter_res_beds));

//              Need to find a better bgcolor for dialog
//                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                d.setContentView(R.layout.dialog_reservation_beds);
                Button btn_ok = d.findViewById(R.id.reservation_okay);
                Button btn_cancel = d.findViewById(R.id.reservation_cancel);
                final NumberPicker np = (NumberPicker) d.findViewById(R.id.reservation_picker);
                np.setMinValue(1);
                np.clearFocus();
//              Causes crash, see Shelter
//                np.setMaxValue(shelter.getVacancyNum());
                np.setMaxValue(20);
                np.setWrapSelectorWheel(false);
//              Debug
                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        Log.i("Reservation", "New num: " + newVal);
                    }
                });
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 3/27/18 Create reservation in DB, possibly conformation toast/ dialog, reload screen on complete
                        d.dismiss();
                    }
                });
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), R.string.toast_reservation_canceled, Toast.LENGTH_LONG).show();
                        d.dismiss();
                    }
                });
                d.show();
            }
        });
    }
    
    private Button resButton;
}
