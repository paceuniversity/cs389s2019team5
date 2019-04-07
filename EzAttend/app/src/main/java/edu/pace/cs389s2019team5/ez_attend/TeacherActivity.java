package edu.pace.cs389s2019team5.ez_attend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.opencsv.CSVWriter;

import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Student;

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

    private ArrayList<ClassSession> sessionsOld;
    private ArrayList<ClassSession> sessionsNew;
    private ClassSession[] sessionTemp = new ClassSession[1];
    public void exportAttendance (View view) {
        final edu.pace.cs389s2019team5.ez_attend.Firebase.View v = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        sessionsOld = new ArrayList<>();

        sessionsNew = new ArrayList<>();
        v.getSessions(new OnSuccessListener<ArrayList<ClassSession>>() {
            @Override
            public void onSuccess(ArrayList<ClassSession> classSessions) {
                sessionsOld = classSessions;
                addAttendees();
                Log.i(TAG, "Successful");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TeacherActivity.this,
                        "Failed to export attendance",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error when attempting to export attendance", e);
            }
        });
    }
    private void addAttendees() {
        final edu.pace.cs389s2019team5.ez_attend.Firebase.View v = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        for(ClassSession i:sessionsOld)
        {
            v.getSessionAttendance(i,new OnSuccessListener<ClassSession>() {
                @Override
                public void onSuccess(ClassSession classSession) {
                    sessionTemp[0]=classSession;
                    sessionsNew.add(sessionTemp[0]);
                    if(sessionsNew.size() == sessionsOld.size())
                        export();
                    Log.i(TAG, "Successful");
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    sessionTemp[0]=null;
                    Toast.makeText(TeacherActivity.this,
                            "Failed to export attendance",
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error when attempting to export attendance", e);
                }
            });
        }

    }
    private void export() {
        try {
            String directory = getFilesDir().getAbsolutePath()+"/records.csv";
            File file = new File(directory);
            CSVWriter writer = new CSVWriter(new FileWriter(file));

            Log.i(TAG, "Log:"+ directory);

            String[] record = {"Class Session ID", "Class Session Date", "Class Session Attendees"};
            writer.writeNext(record);


            for(ClassSession i:sessionsNew)
            {
                String id = i.getId();
                Date date = i.getStartTime();
                String students = "";
                Iterator<Student> s= i.getAttendeeIterator();
                while(s.hasNext())
                {
                    students+=s.next().getId();
                    students+="&";
                }
                if(students.length()!=0)
                    students = students.substring(0, students.length() - 1);
                String[] entry = {id, date.toString(), students};
                writer.writeNext(entry);
            }
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, "Error when attempting to export attendance", e);
        }

        Toast.makeText(TeacherActivity.this,
                "Successfully Exported",
                Toast.LENGTH_SHORT).show();
    }
}
