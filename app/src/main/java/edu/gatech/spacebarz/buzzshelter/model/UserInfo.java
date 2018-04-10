package edu.gatech.spacebarz.buzzshelter.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserInfo {
    private String uid, name, phone, currentReservation;
    private UserRole role;

    /** Required for use with Firebase, should not be used by the app */
    public UserInfo() {

    }

    public UserInfo(String u, String n, String p, String reservation, UserRole r) {
        uid = u;
        name = n;
        phone = p;
        currentReservation = reservation;
        role = r;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public UserRole getRole() {
        return role;
    }

    public String getCurrentReservation() {
        return currentReservation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setCurrentReservation(String reservation) {
        this.currentReservation = reservation;
    }

    public enum UserRole {
        USER("User"), ADMINISTRATOR("Administrator"), SHELTER_EMPLOYEE("Shelter Employee");

        UserRole(String s) {
            label = s;
        }
        private final String label;

        @Override
        public String toString() {
            return label;
        }

        public static UserRole findUserRole(String id) {
            try {
                UserRole role = UserRole.valueOf(id);
                if (role != null) {
                    return role;
                }
            } catch (IllegalArgumentException e) {
                // Intentional fallthrough
            }

            for (UserRole r : UserRole.values()) {
                if (r.label.equals(id)) {
                    return r;
                }
            }

            return null;
        }
    }
}