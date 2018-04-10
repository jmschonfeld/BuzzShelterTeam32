package edu.gatech.spacebarz.buzzshelter.util;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.concurrent.CountDownLatch;

import edu.gatech.spacebarz.buzzshelter.model.Reservation;
import edu.gatech.spacebarz.buzzshelter.model.Shelter;
import edu.gatech.spacebarz.buzzshelter.model.UserInfo;

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

    public static Reservation retrieveReservation(String rid) throws DatabaseException {
        RetrieveObjectSynchronousTask<Reservation> task = new RetrieveObjectSynchronousTask<>(DatabaseKey.RESERVATION, rid, Reservation.class);
        DatabaseException ex = task.run();
        if (ex != null) {
            throw ex;
        }
        return task.getValue();
    }

    public static void setReservation(Reservation updated) throws DatabaseException {
        StoreObjectSynchronousTask<Reservation> task = new StoreObjectSynchronousTask<>(DatabaseKey.RESERVATION, updated.getReservationID());
        DatabaseException ex = task.run(updated);
        if (ex != null) {
            throw ex;
        }
    }

    public static void insertNewReservation(Reservation reservation) throws DatabaseException {
        String uid = generateUID(DatabaseKey.RESERVATION);
        reservation.setReservationID(uid);
        setReservation(reservation);
    }

    public static void deleteReservation(Reservation reservation) throws DatabaseException {
        DeleteObjectSynchronousTask<Reservation> task = new DeleteObjectSynchronousTask<>(DatabaseKey.RESERVATION, reservation.getReservationID());
        DatabaseException ex = task.run();
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
        StoreObjectSynchronousTask<Shelter> task = new StoreObjectSynchronousTask<>(DatabaseKey.SHELTER, updated.getUID());
        DatabaseException ex = task.run(updated);
        if (ex != null) {
            throw ex;
        }
    }

    public static void insertNewShelterInfo(Shelter shelter) throws DatabaseException {
        // TODO: ask TA to determine if the CSV UID scheme needs to remain
        String uid = generateUID(DatabaseKey.SHELTER);
        shelter.setUID(uid);
        updateShelterInfo(shelter);
    }

    private static String generateUID(DatabaseKey key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(key.toString());
        return myRef.push().getKey();
    }

    private static final class DeleteObjectSynchronousTask<T> {

        private final String key;
        private DatabaseError error;

        private DeleteObjectSynchronousTask(DatabaseKey dbKey, String id) {
            this.key = "/" + dbKey + "/" + id;
        }

        private DatabaseException run() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(key);
            final CountDownLatch latch = new CountDownLatch(1);
            myRef.removeValue(new DatabaseReference.CompletionListener() {
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

    private static final class StoreObjectSynchronousTask<T> {

        private final String key;
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

    private static final class RetrieveObjectListSynchronousTask<T> {
        private final String key;
        private final Class<T> type;
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
                @Override @SuppressWarnings("unchecked")
                public void onDataChange(DataSnapshot dataSnapshot) {
                    values = (T[]) Array.newInstance(type, (int)dataSnapshot.getChildrenCount());
                    int i = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
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

    private static final class RetrieveObjectSynchronousTask<T> {

        private final String key;
        private final Class<T> type;
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
        USER("SSUser"), SHELTER("SSShelter"), RESERVATION("SSReservation");

        private final String key;
        DatabaseKey(String k) {
            key = k;
        }

        @Override
        public String toString() {
            return key;
        }
    }

}
