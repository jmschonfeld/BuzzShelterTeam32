package edu.gatech.spacebarz.buzzshelter.util;

import android.util.Log;

import edu.gatech.spacebarz.buzzshelter.model.Shelter;

// This must be its own class and NOT an inner class because it needs to be serializable
public class CustomShelterFilter extends ShelterListAdapter.ShelterFilter {
    private String nameText;
    private boolean genderMale, genderFemale, genderAll, ageNewborn, ageChild, ageYA, ageAll, veteran;

    public CustomShelterFilter(String n, boolean gm, boolean gf, boolean ga, boolean an, boolean ac, boolean ay, boolean aa, boolean v) {
        nameText = n;
        genderMale = gm;
        genderFemale = gf;
        genderAll = ga;
        ageNewborn = an;
        ageChild = ac;
        ageYA = ay;
        ageAll = aa;
        veteran = v;
    }


    public boolean filter(Shelter shelter) {
        Log.i("CustomShelterFilter", "Comparison Name = " + nameText.trim().toLowerCase() + " | Checking Name = " + shelter.getName().toLowerCase() + " | b1=" + !nameText.trim().equals("") + " | b2=" + !shelter.getName().toLowerCase().contains(nameText.trim().toLowerCase()));
        if (!nameText.trim().equals("") && !shelter.getName().toLowerCase().contains(nameText.trim().toLowerCase())) {
            Log.i("CustomShelterFilter", "Returning false");
            return false;
        }

        if (genderMale && !shelter.getGender().equals(Shelter.Gender.MALE)) {
            return false;
        }
        if (genderFemale && !shelter.getGender().equals(Shelter.Gender.FEMALE)) {
            return false;
        }

        if (ageNewborn && !shelter.getAgeRest().equals(Shelter.AgeRest.FAMILIESWITHNEWBORNS)) {
            return false;
        }
        if (ageChild && !shelter.getAgeRest().equals(Shelter.AgeRest.CHILDREN)) {
            return false;
        }
        if (ageYA && !shelter.getAgeRest().equals(Shelter.AgeRest.YOUNGADULTS)) {
            return false;
        }

        if (!veteran && shelter.getVeteran()) {
            return false;
        }

        Log.i("CustomShelterFilter", "Returning true");
        return true;
    }
}
