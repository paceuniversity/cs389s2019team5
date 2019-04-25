package edu.pace.cs389s2019team5.ez_attend.ClassFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Class;
import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Model;
import edu.pace.cs389s2019team5.ez_attend.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentClassFragment extends Fragment {

    private static final String TAG = StudentClassFragment.class.getName();

    private String classId;

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

            holder.m_sessionStatus.setText(R.string.attendance_loading);

            edu.pace.cs389s2019team5.ez_attend.Firebase.View view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();

            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            view.getAttendee(classId,
                    model.getId(),
                    userId,
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
                                            attendee.getAttendeeStatus(StudentClassFragment.this.getActivity(),
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


    public StudentClassFragment() {

    }

    public void setClassID(@NonNull String classID) {
        this.classId = classID;
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

        Controller controller = new Controller();

        recyclerView = (RecyclerView) v.findViewById(R.id.student_attendance_recycler);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        Query query = FirebaseFirestore.getInstance()
                .collection(Model.CLASSES)
                .document(classId)
                .collection(Class.SESSIONS)
                .orderBy("startTime", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ClassSession> options = new FirestoreRecyclerOptions.Builder<ClassSession>()
                .setQuery(query, ClassSession.SNAPSHOTPARSER)
                .build();

        this.mAdapter = new SessionAttendanceAdapter(options);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(this.mAdapter);

        return v;
    }
    public void checkIn() {
        Log.i(TAG, "Student checking in");

        Controller controller = new Controller();
        String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        controller.markPresent(this.classId, studentId, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),
                        "Successfully marked present for most recent class",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Successfully marked present for last class");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),
                        "Failed to mark student present",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Couldn't mark student present", e);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((FirestoreRecyclerAdapter) this.mAdapter).startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((FirestoreRecyclerAdapter) this.mAdapter).stopListening();
    }


}
