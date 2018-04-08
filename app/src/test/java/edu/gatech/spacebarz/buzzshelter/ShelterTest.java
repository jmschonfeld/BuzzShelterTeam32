package edu.gatech.spacebarz.buzzshelter;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import edu.gatech.spacebarz.buzzshelter.model.Shelter;

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
        testShelter = new Shelter("-1", "testShelter", "10", "none", "123 Test Way", "Test shelter", "5555555555", 10, 0, 0, Shelter.Gender.ALL, Shelter.AgeRest.ALL, false, new ArrayList<String>());
    }
}