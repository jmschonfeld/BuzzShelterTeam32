package edu.gatech.spacebarz.buzzshelter;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import edu.gatech.spacebarz.buzzshelter.model.Reservation;
import edu.gatech.spacebarz.buzzshelter.model.Shelter;
import edu.gatech.spacebarz.buzzshelter.util.TestDatabase;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ShelterTest {
    private Shelter testShelter;

    @Before
    public void setUp() {
        testShelter = new Shelter("-1", "testShelter", "10", "none", "123 Test Way", "Test shelter", "5555555555", 10, 0, 0, Shelter.Gender.ALL, Shelter.AgeRest.ALL, false, null);
    }

    @Test
    public void testNullRes() {
        testShelter.setReservationIDs(null);
        assertEquals("null reservations incorrect", testShelter.getCapacityNum(), testShelter.getVacancyNum());
    }

    @Test
    public void testEmptyRes() {
        testShelter.setReservationIDs(new ArrayList<String>());
        assertEquals("empty reservations incorrect", testShelter.getCapacityNum(), testShelter.getVacancyNum());
    }

    @Test
    public void testNormalRes() {
        ArrayList<Reservation> res = new ArrayList<>();
        res.add(new Reservation("testuser01", "-1", 1, "test01"));
        res.add(new Reservation("testuser02", "-1", 3, "test02"));
        res.add(new Reservation("testuser03", "-1", 5, "test03"));
        TestDatabase.setReservations(res);

        ArrayList<String> rid = new ArrayList<>();
        rid.add("test01");
        rid.add("test02");
        rid.add("test03");
        testShelter.setReservationIDs(rid);


    }
}