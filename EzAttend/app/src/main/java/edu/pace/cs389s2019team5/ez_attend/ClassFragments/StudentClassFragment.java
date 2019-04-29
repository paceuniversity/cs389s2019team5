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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import javax.annotation.Nullable;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
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

    private edu.pace.cs389s2019team5.ez_attend.Firebase.View view;
    private Controller controller;

    public class SessionAttendanceAdapter extends FirestoreRecyclerAdapter<ClassSession, SessionViewHolder> {

        public SessionAttendanceAdapter(@NonNull FirestoreRecyclerOptions<ClassSession> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull final SessionViewHolder holder,
                                        final int position,
                                        @NonNull final ClassSession model) {

            Log.v(TAG, "onBindViewHolder");
            holder.m_sessionTime.setText(model.getStartTime().toString());

            // This caches the result in attendees so it doesn't reload every time the user
            // scrolls through the attendance records

            holder.m_sessionStatus.setText(R.string.attendance_loading);

            edu.pace.cs389s2019team5.ez_attend.Firebase.View view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();

            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (holder.m_listener != null) {
                Log.d(TAG, "Removing listener");
                holder.m_listener.remove();
            }

            holder.m_listener = view.listenForMarking(classId,
                    model.getId(),
                    userId,
                    new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {

                            if (snapshot == null) {
                                Log.e(TAG, "Snapshot is null in the listener");
                                return;
                            }

                            if (e != null) {
                                Log.e(TAG, "Error from listening for attendee");
                                return;
                            }

                            if (!snapshot.exists()) {
                                holder.m_sessionStatus.setText(R.string.attendance_absent);
                            } else {
                                Attendee attendee = Attendee.SNAPSHOTPARSER.parseSnapshot(snapshot);
                                if (attendee.getStudentTimeStamp() == null)
                                        return;
                                if (holder.getAdapterPosition() == position)
                                    // This is equivalent to 10 minutes
                                    holder.m_sessionStatus.setText(
                                            attendee.getAttendeeStatus(StudentClassFragment.this.getActivity(),
                                                    model.getStartTime(),
                                                    600000));
                            }
                        }
                    });
            Log.d(TAG, "Adding listener in bind view");
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull SessionViewHolder holder) {
            if (holder.m_listener != null) {
                Log.d(TAG, "Detaching listener in onViewDetachedFromWindow");
                holder.m_listener.remove();
                holder.m_listener = null;
            }
            super.onViewDetachedFromWindow(holder);
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
        private ListenerRegistration m_listener;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);

            this.m_sessionTime = itemView.findViewById(R.id.txtSessionStartTime);
            this.m_sessionStatus = itemView.findViewById(R.id.txtSessionAttendanceStatus);
            this.m_listener = null;
        }

    }

    public StudentClassFragment() {
        this.controller = new Controller();
        this.view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
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

        Query query = view.getClassSessionsQuery(this.classId);

        FirestoreRecyclerOptions<ClassSession> options = new FirestoreRecyclerOptions.Builder<ClassSession>()
                .setQuery(query, ClassSession.SNAPSHOTPARSER)
                .build();

        this.mAdapter = new SessionAttendanceAdapter(options);
        this.recyclerView.setLayoutManager(layoutManager);

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
        Log.v(TAG, "onStart()");
        super.onStart();
        this.recyclerView.setAdapter(this.mAdapter);
        ((FirestoreRecyclerAdapter) this.mAdapter).startListening();
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop()");
        super.onStop();
        this.recyclerView.setAdapter(null);
        ((FirestoreRecyclerAdapter) this.mAdapter).stopListening();
    }


}
