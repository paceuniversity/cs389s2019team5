package edu.pace.cs389s2019team5.ez_attend.ClassFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherClassFragment extends Fragment {

    private static final String TAG = TeacherClassFragment.class.getName();
    private String classID;
    public TeacherClassFragment() {
        this.classID = getArguments().getString("classID");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_teacher_class, container, false);

        Button launchAttendance = v.findViewById(R.id.takeAttendanceButton);
        launchAttendance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchAttendance();
            }
        });

        Button exportAttendance = v.findViewById(R.id.exportRecordsButton);
        exportAttendance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exportAttendance();
            }
        });

//        Button showAttendance = v.findViewById(R.id.attendanceRecordsButton);
//        showAttendance.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                showAttendance();
//            }
//        });

        return v;
    }

    public void launchAttendance() {
        Controller session = new Controller();

        this.classID = Controller.DEBUG_CLASS_ID;//DELETE. USE ONLY FOR TESTING!!!!!

        session.beginClassSession(this.classID, new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String sessionId) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "New class created with Id: " + sessionId,
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Successfully taking attendance");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Failed to begin taking attendance",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error when attempting to begin attendance", e);
            }
        });
    }




    private ArrayList<ClassSession> sessionsOld;
    private Hashtable<ClassSession, ArrayList<Attendee>> sessionsNew;

    public void exportAttendance() {

        this.classID = Controller.DEBUG_CLASS_ID;//DELETE. USE ONLY FOR TESTING!!!!!

        final edu.pace.cs389s2019team5.ez_attend.Firebase.View v = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        sessionsOld = new ArrayList<>();
        sessionsNew = new Hashtable<>();
        v.getSessions(this.classID, new OnSuccessListener<ArrayList<ClassSession>>() {
            @Override
            public void onSuccess(ArrayList<ClassSession> classSessions) {
                sessionsOld = classSessions;
                for (ClassSession session : classSessions) {
                    addAttendees(session);
                }
                Log.i(TAG, "Successful");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Failed to export attendance",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error when attempting to export attendance", e);
            }
        });
    }
    private void addAttendees(final ClassSession session) {
        final edu.pace.cs389s2019team5.ez_attend.Firebase.View v = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        v.getSessionAttendance(this.classID, session,new OnSuccessListener<ArrayList<Attendee>>() {
            @Override
            public void onSuccess(ArrayList<Attendee> attendees) {
                sessionsNew.put(session, attendees);
                if(sessionsNew.size() == sessionsOld.size()) {
                    Log.d(TAG, sessionsNew.size() + ", " + sessionsOld.size());
                    export();
                }
                Log.i(TAG, "Successful");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Failed to export attendance",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error when attempting to export attendance", e);
            }
        });
    }
    private void export() {
        try {
            String directory = getContext().getFilesDir().getAbsolutePath()+"/records.csv";
            File file = new File(directory);
            CSVWriter writer = new CSVWriter(new FileWriter(file));

            Log.i(TAG, "Log:"+ directory);

            String[] record = {"Class Session ID", "Class Session Date", "Class Session Attendees"};
            writer.writeNext(record);


            for(ClassSession i:sessionsNew.keySet())
            {
                String id = i.getId();
                Date date = i.getStartTime();
                String students = "";
                ArrayList<Attendee> sessionAttendees = sessionsNew.get(i);
                for (Attendee attendee : sessionAttendees)
                {
                    students+=attendee.getId();
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
    }
}
