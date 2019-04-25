package edu.pace.cs389s2019team5.ez_attend.AttendanceFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditAttendanceFragment extends Fragment {

    private String classId;
    private String sessionId;
    private String studentId;
    private String studentName;
    private static final String TAG = EditAttendanceFragment.class.getName();

    public EditAttendanceFragment() {
        // Required empty public constructor
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_attendance, container, false);
        TextView name = v.findViewById(R.id.studentName);
        name.setText(this.studentName);

        Button markPresent = v.findViewById(R.id.markPresentButton);
        markPresent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mark(Attendee.Mark.PRESENT);
            }
        });

        Button markLate = v.findViewById(R.id.markLateButton);
        markLate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mark(Attendee.Mark.LATE);
            }
        });

        Button markAbsent = v.findViewById(R.id.markAbsentButton);
        markAbsent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mark(Attendee.Mark.ABSENT);
            }
        });

        return v;
    }

    private void mark(final Enum mark) {
        Controller c = new Controller();
        c.markManual(mark, this.classId, this.sessionId, this.studentId, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Successfully marked "+ mark,
                        Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
                Log.i(TAG, "Successfully marked "+ mark);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "Error when attempting to mark "+mark, e);
            }
        });
    }
}
