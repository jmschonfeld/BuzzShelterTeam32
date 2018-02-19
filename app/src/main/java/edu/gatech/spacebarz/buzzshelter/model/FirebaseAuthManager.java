package edu.gatech.spacebarz.buzzshelter.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

/**
 * Created by jmschonfeld on 2/19/18.
 */

public class FirebaseAuthManager {

    private static FirebaseAuth auth;

    public static void initialize() {
        auth = FirebaseAuth.getInstance();
    }

    public static boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static void signupUser(String email, String password, @Nullable final Runnable completion) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail: success");
                            if (completion != null) {
                                completion.run();
                            }
                        } else {
                            Log.w(TAG, "createUserWithEmail: failure", task.getException());
                            if (completion != null) {
                                completion.run();
                            }
                        }
                    }
                });
    }

    public static void signin(String email, String password, @Nullable final Runnable completion) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail: success");
                            if (completion != null) {
                                completion.run();
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail: failure", task.getException());
                            if (completion != null) {
                                completion.run();
                            }
                        }
                    }
                });
    }

    public static void signout() {
        auth.signOut();
    }

    public static void signinAnonymous() {
        auth.signInAnonymously();
    }

    public static void sendRecoveryEmail(String email) {
        auth.sendPasswordResetEmail(email);
    }

    public static String getUserDisplayName() {
        if (auth.getCurrentUser() == null)
            return null;

        return auth.getCurrentUser().getDisplayName();
    }

    public static String getUserEmail() {
        if (auth.getCurrentUser() == null)
            return null;

        return auth.getCurrentUser().getEmail();
    }

    public static String getUserPhone() {
        if (auth.getCurrentUser() == null)
            return null;

        return auth.getCurrentUser().getPhoneNumber();
    }

    public static String getUserID() {
        if (auth.getCurrentUser() == null)
            return null;

        return auth.getCurrentUser().getUid();
    }

}
