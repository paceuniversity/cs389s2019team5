package edu.pace.cs389s2019team5.ez_attend.ClassFragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import edu.pace.cs389s2019team5.ez_attend.AttendanceFragments.SessionsFragment;
import edu.pace.cs389s2019team5.ez_attend.BluetoothAdapter;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Class;
import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Model;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Student;
import edu.pace.cs389s2019team5.ez_attend.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherClassFragment extends Fragment {

    private static final String TAG = TeacherClassFragment.class.getName();
    private String classID;
    private Controller controller;
    private edu.pace.cs389s2019team5.ez_attend.Firebase.View view;
    private BluetoothAdapter bluetoothAdapter;

    public TeacherClassFragment() {
        this.controller = new Controller();
        this.view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
    }
    public void setClass(String classID) {
        this.classID = classID;
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

        Button showAttendance = v.findViewById(R.id.attendanceRecordsButton);
        showAttendance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAttendance();
            }
        });

        TextView txt = v.findViewById(R.id.textViewClassID);
        txt.setText(this.classID);

        edu.pace.cs389s2019team5.ez_attend.Firebase.View view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        view.getClass(classID, new OnSuccessListener<Class>() {
            @Override
            public void onSuccess(Class aClass) {
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(aClass.getClassName());
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to get class name", e);
            }
        });

        bluetoothAdapter = new BluetoothAdapter(TeacherClassFragment.this.getActivity(), BluetoothAdapter.Role.TEACHER, controller);

        return v;
    }
    public void showAttendance()
    {
        SessionsFragment fragment = new SessionsFragment();
        fragment.setClassId(classID);
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).addToBackStack(TAG).commit();
    }
    public void launchAttendance() {

        controller.beginClassSession(this.classID, new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String sessionId) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "Successfully taking attendance: " + sessionId,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Successfully taking attendance");

            if (!Model.BLUETOOTH)
                return;

            view.getClass(classID,
                    new OnSuccessListener<Class>() {
                        @Override
                        public void onSuccess(Class aClass) {
                            bluetoothAdapter.beginTakingAttendance(aClass,
                                    new OnSuccessListener<Student>() {
                                        @Override
                                        public void onSuccess(Student student) {
                                            Log.i(TAG, "Student marked present " + student);
                                        }
                                    }, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Failed to mark a student present", e);
                                        }
                                    }
                            );
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error getting the class");
                        }
                    });

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
            String[] PermissionToWrite = { Manifest.permission.WRITE_EXTERNAL_STORAGE};
            int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(), PermissionToWrite, 1
                );

            } else if (isExternalStorageWritable()) {

                String name = "EzAttendanceRecord " + Calendar.getInstance().getTime()+".csv";
                name = name.replaceAll(":","-");
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), name);
                CSVWriter writer = new CSVWriter(new FileWriter(file));

                Log.i(TAG, "Log:" + name);

                String[] record = {"Class Session ID", "Class Session Date", "Class Session Attendees"};
                writer.writeNext(record);


                for (ClassSession i : sessionsNew.keySet()) {
                    String id = i.getId();
                    Date date = i.getStartTime();
                    String students = "";
                    ArrayList<Attendee> sessionAttendees = sessionsNew.get(i);
                    for (Attendee attendee : sessionAttendees) {
                        students += attendee.getId();
                        students += "&";
                    }
                    if (students.length() != 0)
                        students = students.substring(0, students.length() - 1);
                    String[] entry = {id, date.toString(), students};
                    writer.writeNext(entry);
                }
                writer.close();
                openEmail(file, FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error when attempting to export attendance", e);
        }
    }
    private void openEmail(File f, String email)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Attendance");
        Uri uri = FileProvider.getUriForFile(getActivity(), "edu.pace.cs389s2019team5.ez_attend.fileprovider", f);
        getActivity().grantUriPermission("edu.pace.cs389s2019team5.ez_attend.ClassFragments", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Create email"));
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Model.BLUETOOTH)
            bluetoothAdapter.stopTakingAttendance();
    }
}
