package edu.gatech.spacebarz.buzzshelter.model;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
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

    public static void setUserInfo(UserInfo updated) throws DatabaseException {
        StoreObjectSynchronousTask<UserInfo> task = new StoreObjectSynchronousTask<>(DatabaseKey.USER, updated.getUid());
        DatabaseException ex = task.run(updated);
        if (ex != null) {
            throw ex;
        }
    }

    public static Shelter[] retrieveAllShelters() throws DatabaseException {
        RetrieveObjectListSynchronousTask<Shelter> task = new RetrieveObjectListSynchronousTask<>(DatabaseKey.SHELTER, Shelter.class);
        DatabaseException ex = task.run();
        if (ex != null) {
            throw ex;
        }
        return task.getValues();
    }

    public static Shelter retrieveShelterInfo(String uid) throws DatabaseException {
        RetrieveObjectSynchronousTask<Shelter> task = new RetrieveObjectSynchronousTask<>(DatabaseKey.SHELTER, uid, Shelter.class);
        DatabaseException ex = task.run();
        if (ex != null) {
            throw ex;
        }
        return task.getValue();
    }

    public static void updateShelterInfo(Shelter updated) throws DatabaseException {
        StoreObjectSynchronousTask<Shelter> task = new StoreObjectSynchronousTask<>(DatabaseKey.SHELTER, updated.getKey());
        DatabaseException ex = task.run(updated);
        if (ex != null) {
            throw ex;
        }
    }

    public static void insertNewShelterInfo(Shelter shelter) throws DatabaseException {
        String uid = generateUID(DatabaseKey.SHELTER);
        shelter.setKey(uid);
        updateShelterInfo(shelter);
    }

    private static String generateUID(DatabaseKey key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(key.toString());
        return myRef.push().getKey();
    }

    private static class StoreObjectSynchronousTask<T> {

        private String key;
        private DatabaseError error;

        private StoreObjectSynchronousTask(DatabaseKey dbKey, String id) {
            this.key = "/" + dbKey + "/" + id;
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

    }

    private static class RetrieveObjectListSynchronousTask<T> {

        private String key;
        private Class<T> type;
        private T[] values;
        private DatabaseError error;

        private RetrieveObjectListSynchronousTask(DatabaseKey dbKey, Class<T> type) {
            this.key = "/" + dbKey;
            this.type = type;
        }

        private DatabaseException run() {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(key);
            final CountDownLatch latch = new CountDownLatch(1);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("Num Children:", ""+ dataSnapshot.getChildrenCount());
                    values = (T[]) Array.newInstance(type, (int)dataSnapshot.getChildrenCount());
                    int i = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Log.i("Map: ", data.getValue().toString());
                        values[i] = data.getValue(type);
                        i++;
                    }
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

        private T[] getValues() {
            return values;
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

}
