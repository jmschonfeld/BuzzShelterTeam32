package edu.gatech.spacebarz.buzzshelter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.gatech.spacebarz.buzzshelter.util.CustomShelterFilter;
import edu.gatech.spacebarz.buzzshelter.util.ShelterListAdapter.ShelterFilter;
import edu.gatech.spacebarz.buzzshelter.util.UIUtil;

public class FilterSheltersActivity extends AppCompatActivity {

    private boolean finishedFilter = false;

    private EditText nameFilter;
    private RadioButton genderMaleRadio, genderFemaleRadio, genderAllRadio, ageNewbornRadio, ageChildRadio, ageYARadio, ageAllRadio;
    private RadioGroup groupGender, groupAge;
    private CheckBox veteranBox;

    @Override
    public void finish() {
        if (!finishedFilter) {
            setResult(RESULT_CANCELED);
        }

        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_shelters);
        nameFilter = findViewById(R.id.shelter_filter_text);
        genderMaleRadio = findViewById(R.id.shelter_filter_gender_radio1);
        genderFemaleRadio = findViewById(R.id.shelter_filter_gender_radio2);
        ageAllRadio = findViewById(R.id.shelter_filter_age_radio1);
        genderAllRadio = findViewById(R.id.shelter_filter_gender_radio3);
        ageNewbornRadio = findViewById(R.id.shelter_filter_age_radio2);
        ageChildRadio = findViewById(R.id.shelter_filter_age_radio3);
        ageYARadio = findViewById(R.id.shelter_filter_age_radio4);
        veteranBox = findViewById(R.id.shelter_filter_veteran);
        final Button filterButton = findViewById(R.id.shelter_filter_button);

        groupGender = findViewById(R.id.shelter_filter_gender_group);
        groupAge = findViewById(R.id.shelter_filter_age_group);

        nameFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameFilter.setCursorVisible(true);
            }
        });
        final Context context = this;
        nameFilter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    UIUtil.closeSoftKeyboard(nameFilter, context);
                }
                return false;
            }
        });

        groupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onNonTextSelect();
            }
        });

        groupAge.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onNonTextSelect();
            }
        });

        veteranBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean state) {
                onNonTextSelect();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });
    }

    private void onButtonClick() {
        final String nameText = nameFilter.getText().toString();
        final boolean genderMale = genderMaleRadio.isChecked();
        final boolean genderFemale = genderFemaleRadio.isChecked();
        final boolean genderAll = genderAllRadio.isChecked();
        final boolean ageNewborn = ageNewbornRadio.isChecked();
        final boolean ageChild = ageChildRadio.isChecked();
        final boolean ageYA = ageYARadio.isChecked();
        final boolean ageAll = ageAllRadio.isChecked();
        final boolean veteran = veteranBox.isChecked();

        if (!genderMale && !genderFemale && !genderAll) {
            Toast.makeText(this, "You must select a gender", Toast.LENGTH_LONG).show();
            return;
        }
        if (!ageNewborn && !ageChild && !ageYA && !ageAll) {
            Toast.makeText(this, "You must select an age", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("filter", new CustomShelterFilter(nameText, genderMale, genderFemale, genderAll, ageNewborn, ageChild, ageYA, ageAll, veteran););
        finishedFilter = true;
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onNonTextSelect() {
        UIUtil.closeSoftKeyboard(getCurrentFocus(), getBaseContext());
        nameFilter.setCursorVisible(false);
    }
}
