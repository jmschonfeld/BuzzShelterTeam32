package edu.gatech.spacebarz.buzzshelter.util;

import java.util.ArrayList;

import edu.gatech.spacebarz.buzzshelter.model.Reservation;

public class TestDatabase {
    public static Reservation retrReservation(String rid) {
        Reservation temp = null;

        for (Reservation r: reservations)
            if (r.getReservationID().equals(rid))
                temp = r;

        return temp;
    }

    public static void setReservations(ArrayList<Reservation> res) {
        reservations = res;
    }

    private static ArrayList<Reservation> reservations;
}
