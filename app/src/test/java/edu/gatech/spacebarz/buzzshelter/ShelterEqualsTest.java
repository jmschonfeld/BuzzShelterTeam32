package edu.gatech.spacebarz.buzzshelter;

import org.junit.Before;
import org.junit.Test;
import edu.gatech.spacebarz.buzzshelter.model.Shelter;

import static org.junit.Assert.*;

public class ShelterEqualsTest {

    private Shelter nullShelter;
    private Shelter shelter1;
    private Shelter shelter2;

    @Before
    public void setup() {
        nullShelter = new Shelter();
        shelter1 = new Shelter();
        shelter1.setUID("abc");
        shelter2 = new Shelter();
        shelter2.setUID("def");
    }

    @Test
    public void testNullShelter() {
        assertTrue(nullShelter.equals(new Shelter()));
        assertFalse(nullShelter.equals(shelter1));
        assertFalse(nullShelter.equals(shelter2));
    }

    @Test
    public void testShelterEqualsSame() {
        assertFalse(nullShelter.equals(null));
        assertTrue(nullShelter.equals(nullShelter));
        assertTrue(shelter1.equals(shelter1));
        assertTrue(shelter2.equals(shelter2));
    }

    @Test
    public void testShelterEqualsDifferent() {
        assertFalse(nullShelter.equals(shelter1));
        assertFalse(shelter1.equals(shelter2));
        assertFalse(nullShelter.equals(shelter2));
    }




    @Test
    public void testShelterEqualsDifferentVarSameContent() {
        assertTrue(nullShelter.equals(new Shelter()));
        Shelter shelter1clone = new Shelter();
        shelter1clone.setUID(shelter1.getUID());
        assertTrue(shelter1.equals(shelter1clone));
        Shelter shelter2clone = new Shelter();
        shelter1clone.setUID(shelter2.getUID());
        assertFalse(shelter2.equals(shelter2clone));
    }
}
