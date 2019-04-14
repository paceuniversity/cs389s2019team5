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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Class;
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

    public class SessionAttendanceAdapter extends FirestoreRecyclerAdapter<ClassSession, SessionViewHolder> {

        public SessionAttendanceAdapter(@NonNull FirestoreRecyclerOptions<ClassSession> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull final SessionViewHolder holder,
                                        final int position,
                                        @NonNull final ClassSession model) {

            Log.d(TAG, "Binding view holder");
            holder.m_sessionTime.setText(model.getStartTime().toString());

            // This caches the result in attendees so it doesn't reload every time the user
            // scrolls through the attendance records

            // todo add this to the strings resources file
            holder.m_sessionStatus.setText("Loading...");

            edu.pace.cs389s2019team5.ez_attend.Firebase.View view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();

            view.getAttendee(classId,
                    model.getId(),
                    StudentClassActivity.this.studentId,
                    new OnSuccessListener<Attendee>() {
                        @Override
                        public void onSuccess(Attendee attendee) {
                            if (attendee == null) {
                                holder.m_sessionStatus.setText(R.string.attendance_absent);
                            } else {
                                // Before modifying the content of the adapter, we should make
                                // sure it is still holding the right data
                                if (holder.getAdapterPosition() == position)
                                    // This is equivalent to 10 minutes
                                    holder.m_sessionStatus.setText(
                                            attendee.getAttendeeStatus(StudentClassActivity.this,
                                                    model.getStartTime(),
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

        @NonNull
        @Override
        public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.student_attendance_item_view, viewGroup, false);
            return new SessionViewHolder(view);
        }
    }

    public class SessionViewHolder extends RecyclerView.ViewHolder {

        private TextView m_sessionTime;
        private TextView m_sessionStatus;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);

            this.m_sessionTime = itemView.findViewById(R.id.txtSessionStartTime);
            this.m_sessionStatus = itemView.findViewById(R.id.txtSessionAttendanceStatus);
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

        Query query = FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classId)
                .collection(Class.SESSIONS)
                .orderBy("startTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ClassSession> options = new FirestoreRecyclerOptions.Builder<ClassSession>()
                .setQuery(query, ClassSession.SNAPSHOTPARSER)
                .build();

        this.mAdapter = new SessionAttendanceAdapter(options);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(this.mAdapter);

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

    @Override
    protected void onStart() {
        super.onStart();
        ((FirestoreRecyclerAdapter) this.mAdapter).startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((FirestoreRecyclerAdapter) this.mAdapter).stopListening();
    }

}
