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
        assertEquals("null reservations vacancy calculation incorrect", testShelter.getCapacityNum(), testShelter.getVacancyNum());
    }

    @Test
    public void testEmptyRes() {
        testShelter.setReservationIDs(new ArrayList<String>());
        assertEquals("empty reservations vacancy calculation incorrect", testShelter.getCapacityNum(), testShelter.getVacancyNum());
    }

    @Test
    public void testNormalRes() {
        ArrayList<Reservation> res = new ArrayList<>();
        res.add(new Reservation("testuser01", "-1", 1));
        res.get(0).setReservationID("test01");
        res.add(new Reservation("testuser02", "-1", 3));
        res.get(1).setReservationID("test02");
        res.add(new Reservation("testuser03", "-1", 5));
        res.get(2).setReservationID("test03");

        TestDatabase.setReservations(res);

        ArrayList<String> rid = new ArrayList<>();
        rid.add("test01");
        rid.add("test02");
        rid.add("test03");
        testShelter.setReservationIDs(rid);

        testShelter.useTestDB();

        assertEquals("non empty reservations vacancy calculation incorrect", 1, testShelter.getVacancyNum());
    }
}