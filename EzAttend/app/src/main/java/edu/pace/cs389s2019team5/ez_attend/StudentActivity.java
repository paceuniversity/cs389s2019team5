package edu.pace.cs389s2019team5.ez_attend;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {

    private final static String TAG = StudentActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
    }

    public void studentSignIn(View view) {
        // Check that the first name and last name are entered
        EditText txtFirstName = findViewById(R.id.txtFirstName);
        EditText txtLastName = findViewById(R.id.txtLastName);

        final String userFirstName = txtFirstName.getText().toString().trim();
        final String userLastName = txtLastName.getText().toString().trim();

        if (userFirstName.trim().equals("") || userLastName.trim().equals("")) {
            Toast.makeText(this, "You must enter a first and last name", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference studentsRef = db.collection("students");
        final Query query = studentsRef
                .whereEqualTo("firstName", userFirstName)
                .whereEqualTo("lastName", userLastName);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                if (documents.size() == 0) {
                    // Create a new user
                    Map<String, Object> user = new HashMap<>();
                    user.put("firstName", userFirstName);
                    user.put("lastName", userLastName);

                    if (BluetoothAdapter.getDefaultAdapter() == null) {
                        Log.w(TAG, "No Bluetooth connected");
                        user.put("macAddress", "noMac");
                    } else {
                        String macAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                        user.put("macAddress", macAddress);
                    }

                    studentsRef.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            launchStudentCheckInActivity(documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Couldn't create a new user", e);
                        }
                    });

                } else {
                    // log in with the user
                    DocumentSnapshot snap = documents.get(0);
                    String id = snap.getId();
                    launchStudentCheckInActivity(id);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Could not connect", e);
            }
        });

    }

    private void launchStudentCheckInActivity(String id) {
        Intent intent = new Intent(this, StudentClassActivity.class);
        intent.putExtra(StudentClassActivity.STUDENT_ID_EXTRA_TAG, id);
        startActivity(intent);
    }

}
