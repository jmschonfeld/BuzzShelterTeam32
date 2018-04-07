package edu.gatech.spacebarz.buzzshelter;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import edu.gatech.spacebarz.buzzshelter.model.Shelter;
import edu.gatech.spacebarz.buzzshelter.util.CustomShelterFilter;
import edu.gatech.spacebarz.buzzshelter.util.ShelterListAdapter;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class JeremyTest {

    private ShelterListAdapter listAdapter;
    private Shelter shelterA, shelterB, shelterC, shelterD;

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getTargetContext();
        shelterA = new Shelter("A", "Shelter A", "Capacity", "Restrictions", "1345 Jeremy Lane", "Note", "123-456-7890", 2, 1.23, 4.56, Shelter.Gender.ALL, Shelter.AgeRest.ALL, false, new ArrayList<String>());
        shelterB = new Shelter("B", "Shelter B", "Capacity", "Restrictions", "1345 Jeremy Lane", "Note", "123-456-7890", 3, 1.23, 4.56, Shelter.Gender.MALE, Shelter.AgeRest.CHILDREN, false, new ArrayList<String>());
        shelterC = new Shelter("C", "Shelter C", "Capacity", "Restrictions", "1345 Jeremy Lane", "Note", "123-456-7890", 4, 1.23, 4.56, Shelter.Gender.FEMALE, Shelter.AgeRest.FAMILIESWITHNEWBORNS, true, new ArrayList<String>());
        shelterD = new Shelter("D", "Shelter D", "Capacity", "Restrictions", "1345 Jeremy Lane", "Note", "123-456-7890", 5, 1.23, 4.56, Shelter.Gender.ALL, Shelter.AgeRest.YOUNGADULTS, true, new ArrayList<String>());
        ArrayList<Shelter> list = new ArrayList<>();
        list.add(shelterA);
        list.add(shelterB);
        list.add(shelterC);
        list.add(shelterD);
        listAdapter = new ShelterListAdapter(context, list);
    }

    @Test
    public void testResetFilter() {
        listAdapter.setFilter(null);
        assertEquals(4, listAdapter.getCount());
    }

    @Test
    public void testFilterName() {
        System.out.println("testFilterName");
        listAdapter.setFilter(new CustomShelterFilter("A", false, false, true, false, false, false, true, true));
        assertArrayEquals("Filtering by 'A' failed", new Shelter[]{shelterA}, getShownShelters());

        listAdapter.setFilter(new CustomShelterFilter("C", false, false, true, false, false, false, true, true));
        assertArrayEquals("Filtering by 'C' failed", new Shelter[]{shelterC}, getShownShelters());

        listAdapter.setFilter(new CustomShelterFilter("Shelter", false, false, true, false, false, false, true, true));
        assertArrayEquals("Filtering by 'Shelter' failed: " + Arrays.toString(getShownShelters()), new Shelter[]{shelterA, shelterB, shelterC, shelterD}, getShownShelters());
    }

    @Test
    public void testFilterGender() {
        listAdapter.setFilter(new CustomShelterFilter("", false, false, true, false, false, false, true, true));
        assertArrayEquals("Filtering by all genders failed", new Shelter[]{shelterA, shelterB, shelterC, shelterD}, getShownShelters());

        listAdapter.setFilter(new CustomShelterFilter("", false, true, false, false, false, false, true, true));
        assertArrayEquals("Filtering by female failed", new Shelter[]{shelterC}, getShownShelters());

        listAdapter.setFilter(new CustomShelterFilter("", true, false, false, false, false, false, true, true));
        assertArrayEquals("Filtering by male failed", new Shelter[]{shelterB}, getShownShelters());
    }

    @Test
    public void testFilterAge() {
        listAdapter.setFilter(new CustomShelterFilter("", false, false, true, false, false, false, true, true));
        assertArrayEquals("Filtering by all ages failed", new Shelter[]{shelterA, shelterB, shelterC, shelterD}, getShownShelters());

        listAdapter.setFilter(new CustomShelterFilter("", false, false, true, false, false, true, false, true));
        assertArrayEquals("Filtering by young adults failed", new Shelter[]{shelterD}, getShownShelters());

        listAdapter.setFilter(new CustomShelterFilter("", false, false, true, false, true, false, false, true));
        assertArrayEquals("Filtering by children failed", new Shelter[]{shelterB}, getShownShelters());

        listAdapter.setFilter(new CustomShelterFilter("", false, false, true, true, false, false, false, true));
        assertArrayEquals("Filtering by families with newborns failed", new Shelter[]{shelterC}, getShownShelters());
    }

    @Test
    public void testFilterVeteran() {
        listAdapter.setFilter(new CustomShelterFilter("", false, false, true, false, false, false, true, true));
        assertArrayEquals("Filtering by veterans failed", new Shelter[]{shelterA, shelterB, shelterC, shelterD}, getShownShelters());

        listAdapter.setFilter(new CustomShelterFilter("", false, false, true, false, false, false, true, false));
        assertArrayEquals("Filtering without veterans failed", new Shelter[]{shelterA, shelterB}, getShownShelters());

    }

    private Shelter[] getShownShelters() {
        int size = listAdapter.getCount();
        Shelter shelters[] = new Shelter[size];
        for (int i = 0; i < size; i++) {
            shelters[i] = listAdapter.getItem(i);
        }
        return shelters;
    }
}
