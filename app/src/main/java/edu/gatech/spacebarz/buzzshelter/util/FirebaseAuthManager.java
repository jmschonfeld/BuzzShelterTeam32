package edu.gatech.spacebarz.buzzshelter.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import static android.content.ContentValues.TAG;

public class FirebaseAuthManager {

    public interface FirebaseAuthCallback {
        void callback(boolean success, @Nullable Exception exception);
    }

    private static FirebaseAuth auth;

    public static void initialize() {
        auth = FirebaseAuth.getInstance();
    }

    public static boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public static String getCurrentUserUID() {
        return auth.getCurrentUser().getUid();
    }

    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static void signupUser(String email, String password, @Nullable final FirebaseAuthCallback completion) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "createUserWithEmail: success");
                            if (completion != null) {
                                completion.callback(true, null);
                            }
                        } else {
                            if (task.getException() != null) {
                                Log.i("authFeedback",task.getException().getMessage());
                            }
                            if (completion != null) {
                                completion.callback(false, task.getException());
                            }
                        }
                    }
                });
    }

    public static void signin(String email, String password, @Nullable final FirebaseAuthCallback completion) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail: success");
                            if (completion != null) {
                                completion.callback(true, null);
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail: failure", task.getException());
                            if (completion != null) {
                                completion.callback(false, task.getException());
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

    @Nullable
    public static String getUserDisplayName() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        return auth.getCurrentUser().getDisplayName();
    }

    @Nullable
    public static String getUserEmail() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        return auth.getCurrentUser().getEmail();
    }

    @Nullable
    public static String getUserPhone() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        return auth.getCurrentUser().getPhoneNumber();
    }

    @Nullable
    public static String getUserID() {
        if (auth.getCurrentUser() == null) {
            return null;
        }

        return auth.getCurrentUser().getUid();
    }
}