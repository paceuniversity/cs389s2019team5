package edu.pace.cs389s2019team5.ez_attend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Student;

public class MainActivity extends AppCompatActivity {

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 773;
    private static final String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            checkSignIn();
        } else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(TAG, "Request Code: " + requestCode);
        Log.v(TAG, "Result Code: " + resultCode);

        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Successfully logged in");
                checkSignIn();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.d(TAG, "User did not sign in");
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.d(TAG, "No internet connection");
                    return;
                }

                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void checkSignIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            edu.pace.cs389s2019team5.ez_attend.Firebase.View view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();

            view.getStudent(auth.getCurrentUser().getUid(), new OnSuccessListener<Student>() {
                @Override
                public void onSuccess(Student student) {
                    if (student == null) {
                        Log.d(TAG, "First time student signs in to this app");
                        Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(MainActivity.this, ClassListActivity.class);
                        intent.putExtra(ClassListActivity.CURRENT_USER_TAG, student);
                        startActivity(intent);
                        finish();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Couldn't check if student exists", e);
                    // todo tell them we couldn't sign them in and try again later?
                }
            });

        } else {
            // This should never happen
            NullPointerException exc = new NullPointerException("User signed in but null user");
            Log.e(TAG, "User attempted to sign in but auth wasn't initialized", exc);
            throw exc;
        }
    }
}
