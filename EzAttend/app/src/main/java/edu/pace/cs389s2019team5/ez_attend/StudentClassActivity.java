package edu.pace.cs389s2019team5.ez_attend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;

public class StudentClassActivity extends AppCompatActivity {

    private final static String TAG = StudentClassActivity.class.getName();
    public final static String STUDENT_ID_EXTRA_TAG = "student_id";

    private Controller controller;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class);

        Intent intent = getIntent();
        String studentId = intent.getStringExtra(STUDENT_ID_EXTRA_TAG);

        this.studentId = studentId;
        Log.i(TAG, "Student with id " + studentId + " signing in");

        controller = new Controller();
    }

    public void checkIn(View view) {
        Log.i(TAG, "Student checking in");
        controller.markPresent(studentId, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(StudentClassActivity.this,
                        "Successfully marked present for most recent class",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Successfully marked present for last class");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentClassActivity.this,
                        "Failed to mark student present",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Couldn't mark student present", e);
            }
        });
    }
}
