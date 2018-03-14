package edu.gatech.spacebarz.buzzshelter.model;

public class Reservation {

    /** Required for firebase, should not be used by the actual app */
    public Reservation() {}

    public Reservation(String cID, String sID, int s) {
        setCreatorID(cID);
        setShelterID(sID);
        setSize(s);
    }

    public String getCreatorID() {
        return creatorID;
    }
    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }
    public String getShelterID() {
        return shelterID;
    }
    public void setShelterID(String shelterID) {
        this.shelterID = shelterID;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    private String creatorID, shelterID;
    private int size;
}