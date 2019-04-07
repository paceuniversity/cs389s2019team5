package edu.pace.cs389s2019team5.ez_attend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;

public class TeacherActivity extends AppCompatActivity {

    private static final String TAG = TeacherActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
    }

    public void launchAttendance (View view) {
        Controller session = new Controller();
        session.beginClassSession(new OnSuccessListener<ClassSession>() {
            @Override
            public void onSuccess(ClassSession classSession) {
                Toast.makeText(TeacherActivity.this,
                        "New class created with Id: " + classSession.getId(),
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Successfully taking attendance");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeacherActivity.this,
                        "Failed to begin taking attendance",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error when attempting to begin attendance", e);
            }
        });
    }
}
