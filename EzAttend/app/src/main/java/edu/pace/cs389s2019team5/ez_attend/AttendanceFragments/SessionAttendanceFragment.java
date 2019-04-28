package edu.pace.cs389s2019team5.ez_attend.AttendanceFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Student;
import edu.pace.cs389s2019team5.ez_attend.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionAttendanceFragment extends Fragment {

    private static final String TAG = SessionAttendanceFragment.class.getName();
    private String classId;
    private ClassSession m_session;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private edu.pace.cs389s2019team5.ez_attend.Firebase.View view;
    private Controller controller;

    public class SessionAttendanceAdapter extends FirestoreRecyclerAdapter<Attendee, AttendeeViewHolder> {

        public SessionAttendanceAdapter(@NonNull FirestoreRecyclerOptions<Attendee> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull final AttendeeViewHolder holder,
                                        final int position,
                                        @NonNull final Attendee model) {

            Log.d(TAG, "Binding view holder");
            edu.pace.cs389s2019team5.ez_attend.Firebase.View v = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
            v.getStudent(model.getId(), new OnSuccessListener<Student>() {
                @Override
                public void onSuccess(final Student s) {

                    // This caches the result in attendees so it doesn't reload every time the user
                    // scrolls through the attendance records

                    holder.m_attendeeId.setText(s.getFirstName()+" "+ s.getLastName());
                    holder.m_attendeeStatus.setText(
                            model.getAttendeeStatus(SessionAttendanceFragment.this.getContext(),
                                    m_session.getStartTime(),
                                    600000));


                    holder.m_attendeeId.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            openStudent(classId, m_session.getId(), s.getId(), s.getFirstName()+" "+ s.getLastName());
                        }
                    });

                    holder.m_attendeeStatus.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            openStudent(classId, m_session.getId(), s.getId(), s.getFirstName()+" "+ s.getLastName());
                        }
                    });

                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.e(TAG, "Error when attempting to display attendee", e);
                }
            });





        }

        @NonNull
        @Override
        public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.student_attendance_item_view, viewGroup, false);
            return new AttendeeViewHolder(view);
        }
    }

    private void openStudent(String classId, String sessionId, String studentId, String studentName) {
        EditAttendanceFragment fragment = new EditAttendanceFragment();
        fragment.setClassId(classId);
        fragment.setSessionId(sessionId);
        fragment.setStudentId(studentId);
        fragment.setStudentName(studentName);
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).addToBackStack(TAG).commit();
    }




    public class AttendeeViewHolder extends RecyclerView.ViewHolder {

        private TextView m_attendeeId;
        private TextView m_attendeeStatus;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);

            this.m_attendeeId = itemView.findViewById(R.id.txtSessionStartTime);
            this.m_attendeeStatus = itemView.findViewById(R.id.txtSessionAttendanceStatus);
        }

    }

    public SessionAttendanceFragment() {
        this.view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        this.controller = new Controller();
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setSession(ClassSession session) {
        this.m_session = session;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_session_attendance, container, false);

        recyclerView = v.findViewById(R.id.student_attendance_recycler);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        Query query = view.getAttendeesQuery(this.classId, this.m_session.getId());

        FirestoreRecyclerOptions<Attendee> options = new FirestoreRecyclerOptions.Builder<Attendee>()
                .setQuery(query, Attendee.SNAPSHOTPARSER)
                .build();

        this.mAdapter = new SessionAttendanceAdapter(options);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(this.mAdapter);

        return v;
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

