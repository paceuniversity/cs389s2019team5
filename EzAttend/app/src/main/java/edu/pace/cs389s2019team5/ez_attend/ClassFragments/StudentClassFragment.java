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
import com.google.firebase.auth.FirebaseAuth;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.R;
import edu.pace.cs389s2019team5.ez_attend.StudentClassActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentClassFragment extends Fragment {

    private static final String TAG = StudentClassFragment.class.getName();
    private String classID;
    public StudentClassFragment() {
        this.classID = getArguments().getString("classID");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_class, container, false);

        Button checkIn = v.findViewById(R.id.checkInButton);
        checkIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkIn();
            }
        });
//
//        Button showAttendance = v.findViewById(R.id.studentAttendanceRecordsButton);
//        showAttendance.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                showAttendance();
//            }
//        });

        return v;
    }
    public void checkIn() {
        Log.i(TAG, "Student checking in");

        this.classID = Controller.DEBUG_CLASS_ID;//DELETE. USE ONLY FOR TESTING!!!!!

        Controller controller = new Controller();
        String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        controller.markPresent(this.classID, studentId, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Successfully marked present for most recent class",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Successfully marked present for last class");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Failed to mark student present",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Couldn't mark student present", e);
            }
        });
    }
}
