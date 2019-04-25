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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Student;

public class StudentActivity extends AppCompatActivity {

    private final static String TAG = StudentActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
    }

    public void studentSignIn(View view) {
        // Check that the first name and last name are entered
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        final String userId = auth.getCurrentUser().getUid();
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
        final DocumentReference docRef = studentsRef.document(userId);

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", userFirstName);
        user.put("lastName", userLastName);
        final String macAddress;

        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Log.w(TAG, "No Bluetooth connected");
            macAddress = "noMac";
        } else {
            macAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
        }

        user.put("macAddress", macAddress);

        docRef.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Student student = new Student(userId, userFirstName, userLastName, macAddress);
                        launchClassList(student);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Couldn't create a new user", e);
            }
        });

    }

    private void launchClassList(Student student) {
        Intent intent = new Intent(this, ClassListActivity.class);
        intent.putExtra(ClassListActivity.CURRENT_USER_TAG, student);
        startActivity(intent);
        finish();
    }

}
