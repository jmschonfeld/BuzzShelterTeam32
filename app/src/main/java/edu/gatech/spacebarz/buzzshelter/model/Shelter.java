package edu.gatech.spacebarz.buzzshelter.model;

import android.location.Location;

/**
 * Created by mayaholikatti on 2/19/18.
 */

public class Shelter {

    public enum Age {
        FamiliesWithNewborns, Children, YoungAdult, Anyone;
    }

    public enum Gender {
        MALE, FEMALE;
    }

    private int capacity;
    private String shelterName;
    private boolean hasFamily;
    private boolean isVet;
    private Gender gender;
    private Age age;
    private String key;
    private String phoneNum;
    private String specialNotes;
    private Location address;

    /** Required for use with Firebase, should not be used by the actual app */
    public Shelter() {

    }


    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getShelterName() {
        return shelterName;
    }

    public void setShelterName(String shelterName) {
        this.shelterName = shelterName;
    }

    public boolean isHasFamily() {
        return hasFamily;
    }

    public void setHasFamily(boolean hasFamily) {
        this.hasFamily = hasFamily;
    }

    public boolean isVet() {
        return isVet;
    }

    public void setVet(boolean vet) {
        isVet = vet;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public String getKey() {
        return key;
    }

    void setKey(String key) {
        this.key = key;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhonNum(String phonNum) {
        this.phoneNum = phonNum;
    }

    public String getSpecialNotes() {
        return specialNotes;
    }

    public void setSpecialNotes(String specialNotes) {
        this.specialNotes = specialNotes;
    }

    public Location getAddress() {
        return address;
    }

    public void setAddress(Location address) {
        this.address = address;
    }
}
