package edu.pace.cs389s2019team5.ez_attend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;

public class StudentClassActivity extends AppCompatActivity {

    private final static String TAG = StudentClassActivity.class.getName();
    public final static String STUDENT_ID_EXTRA_TAG = "student_id";

    private Controller controller;
    private String classId;
    private String studentId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<ClassSession> m_classSessions;
        private Attendee[] attendees;

        public MyAdapter(ArrayList<ClassSession> classSessions) {
            if (classSessions != null) {
                this.m_classSessions = classSessions;
                this.attendees = new Attendee[classSessions.size()];
            } else {
                Log.e(TAG, "Cannot create a new adapter with null data");
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            private TextView m_sessionTime;
            private TextView m_sessionStatus;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                this.m_sessionTime = itemView.findViewById(R.id.txtSessionStartTime);
                this.m_sessionStatus = itemView.findViewById(R.id.txtSessionAttendanceStatus);

            }
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.student_attendance_item_view, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyAdapter.MyViewHolder myViewHolder, final int i) {

            final ClassSession session = this.m_classSessions.get(i);

            myViewHolder.m_sessionTime.setText(session.getStartTime().toString());

            // This caches the result in attendees so it doesn't reload every time the user
            // scrolls through the attendance records
            if (attendees[i] != null) {
                String status = attendees[i].getAttendeeStatus(StudentClassActivity.this,
                        session.getStartTime(),
                        60000);
                myViewHolder.m_sessionStatus.setText(status);
            } else {
                // todo add this to the strings resources file
                myViewHolder.m_sessionStatus.setText("Loading...");

                edu.pace.cs389s2019team5.ez_attend.Firebase.View view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();

                view.getAttendee(classId,
                        session.getId(),
                        studentId,
                        new OnSuccessListener<Attendee>() {
                            @Override
                            public void onSuccess(Attendee attendee) {
                                if (attendee == null) {
                                    myViewHolder.m_sessionStatus.setText(R.string.attendance_absent);
                                } else {
                                    // cache the result for this attendee
                                    attendees[i] = attendee;

                                    // Before modifying the content of the adapter, we should make
                                    // sure it is still holding the right data
                                    if (myViewHolder.getAdapterPosition() == i)
                                        // This is equivalent to 10 minutes
                                        myViewHolder.m_sessionStatus.setText(
                                                attendee.getAttendeeStatus(StudentClassActivity.this,
                                                        session.getStartTime(),
                                                        600000));
                                }
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error adding to view holder", e);
                            }
                        });
            }
        }

        @Override
        public int getItemCount() {
            return m_classSessions.size();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_class);

        Intent intent = getIntent();
        String studentId = intent.getStringExtra(STUDENT_ID_EXTRA_TAG);

        // TODO get the class id too. This should come from the ClassListActivity
        this.classId = Controller.DEBUG_CLASS_ID;
        this.studentId = studentId;
        Log.i(TAG, "Student with id " + studentId + " viewing attendance for " + classId);

        controller = new Controller();

        recyclerView = (RecyclerView) findViewById(R.id.student_attendance_recycler);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Load the data from the view
        edu.pace.cs389s2019team5.ez_attend.Firebase.View view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        view.getSessions(this.classId,
                new OnSuccessListener<ArrayList<ClassSession>>() {
                    @Override
                    public void onSuccess(ArrayList<ClassSession> classSessions) {
                        if (classSessions != null) {
                            StudentClassActivity.this.mAdapter = new MyAdapter(classSessions);
                            StudentClassActivity.this.recyclerView
                                    .setAdapter(StudentClassActivity.this.mAdapter);
                            Log.i(TAG, "Set the adapter for the recycler view");
                        }
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Couldn't load the student attendance data", e);
                    }
                });

    }

    public void checkIn(View view) {
        Log.i(TAG, "Student checking in");
        controller.markPresent(Controller.DEBUG_CLASS_ID, studentId, new OnSuccessListener<Void>() {
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
