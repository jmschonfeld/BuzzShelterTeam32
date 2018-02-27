package edu.gatech.spacebarz.buzzshelter.model;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CountDownLatch;

public class FirebaseDBManager {

    public static UserInfo retrieveUserInfo(String uid) throws DatabaseException {
        RetrieveObjectSynchronousTask<UserInfo> task = new RetrieveObjectSynchronousTask<>(DatabaseKey.USER, uid, UserInfo.class);
        DatabaseException ex = task.run();
        if (ex != null) {
            throw ex;
        }
        return task.getValue();
    }

    public static UserInfo retrieveCurrentUserInfo() {
        if (!FirebaseAuthManager.isLoggedIn()) {
            return null;
        }
        return retrieveUserInfo(FirebaseAuthManager.getCurrentUserUID());
    }

    /*
    public static String insertNewUserInfo(UserInfo user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/" + DatabaseKey.USER);
        DatabaseReference userKey = myRef.push();
        user.uid = userKey.getKey();
        userKey.setValue(user);
        return userKey.getKey();
    }*/

    public static void setUserInfo(UserInfo updated) throws DatabaseException {
        StoreObjectSynchronousTask<UserInfo> task = new StoreObjectSynchronousTask<>(DatabaseKey.USER, updated.getUid(), UserInfo.class);
        DatabaseException ex = task.run(updated);
        if (ex != null) {
            throw ex;
        }
    }

    private static class StoreObjectSynchronousTask<T> {

        private String key;
        private Class<T> type;
        private T value;
        private DatabaseError error;

        private StoreObjectSynchronousTask(DatabaseKey dbKey, String id, Class<T> type) {
            this.key = "/" + dbKey + "/" + id;
            this.type = type;
        }

        private DatabaseException run(T object) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(key);
            final CountDownLatch latch = new CountDownLatch(1);
            myRef.setValue(object, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        error = databaseError;
                    }
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (error == null) {
                return null;
            } else {
                return error.toException();
            }
        }

        private T getValue() {
            return value;
        }

    }

    private static class RetrieveObjectSynchronousTask<T> {

        private String key;
        private Class<T> type;
        private T value;
        private DatabaseError error;

        private RetrieveObjectSynchronousTask(DatabaseKey dbKey, String id, Class<T> type) {
            this.key = "/" + dbKey + "/" + id;
            this.type = type;
        }

        private DatabaseException run() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(key);
            final CountDownLatch latch = new CountDownLatch(1);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    value = dataSnapshot.getValue(type);
                    latch.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase(Database)", "An error occurred while retrieving an object from Firebase", databaseError.toException());
                    error = databaseError;
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (error == null) {
                return null;
            } else {
                return error.toException();
            }
        }

        private T getValue() {
            return value;
        }

    }

    @IgnoreExtraProperties
    public static class UserInfo {
        private String uid, name, phone;
        private UserRole role;

        /** Required for use with Firebase, should not be used by the app */
        public UserInfo() {

        }

        public UserInfo(String u, String n, String p, UserRole r) {
            uid = u;
            name = n;
            phone = p;
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

        public void setName(String name) {
            this.name = name;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setRole(UserRole role) {
            this.role = role;
        }
    }

    private enum DatabaseKey {
        USER("SSUser"), SHELTER("SSShelter");

        private String key;
        private DatabaseKey(String k) {
            key = k;
        }

        @Override
        public String toString() {
            return key;
        }
    }

    public enum UserRole {
        USER("User"), ADMINISTRATOR("Administrator"), SHELTER_EMPLOYEE("Shelter Employee");

        UserRole(String s) {
            label = s;
        }
        private String label;

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
