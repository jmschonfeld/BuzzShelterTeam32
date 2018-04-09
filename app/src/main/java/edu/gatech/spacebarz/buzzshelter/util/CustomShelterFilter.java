package edu.gatech.spacebarz.buzzshelter.util;

import android.util.Log;

import edu.gatech.spacebarz.buzzshelter.model.Shelter;

// This must be its own class and NOT an inner class because it needs to be serializable
public class CustomShelterFilter extends ShelterListAdapter.ShelterFilter {
    private final String nameText;
    private final boolean genderMale;
    private final boolean genderFemale;
    private final boolean ageNewborn;
    private final boolean ageChild;
    private final boolean ageYA;
    private final boolean veteran;

    public CustomShelterFilter(String n, boolean gm, boolean gf, boolean an, boolean ac, boolean ay, boolean v) {
        nameText = n;
        genderMale = gm;
        genderFemale = gf;
        ageNewborn = an;
        ageChild = ac;
        ageYA = ay;
        veteran = v;
    }

    @Override
    public boolean filter(Shelter shelter) {
        if (!nameText.trim().equals("") && !shelter.getName().toLowerCase().contains(nameText.trim().toLowerCase())) {
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

        return true;
    }
}
