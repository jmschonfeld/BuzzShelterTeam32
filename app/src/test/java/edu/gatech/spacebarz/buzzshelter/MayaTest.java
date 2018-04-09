package edu.gatech.spacebarz.buzzshelter;

import org.junit.Before;
import org.junit.Test;

import edu.gatech.spacebarz.buzzshelter.model.Shelter;
import edu.gatech.spacebarz.buzzshelter.model.UserInfo;

import static org.junit.Assert.*;

/**
 * Created by mayaholikatti on 4/8/18.
 */

public class MayaTest {

    @Before
    public void setup() {

    }

    @Test
    public void testFindUserRole() {
//        assertEquals(UserInfo.UserRole.findUserRole(null), null);
        assertEquals(UserInfo.UserRole.findUserRole("abc"), null);
        assertEquals(UserInfo.UserRole.findUserRole("Administrator"), UserInfo.UserRole.ADMINISTRATOR);

    }
}

