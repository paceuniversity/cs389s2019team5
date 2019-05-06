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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Iterator;

import edu.pace.cs389s2019team5.ez_attend.Firebase.Attendee;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Class;
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

    public class SessionAttendanceAdapter extends RecyclerView.Adapter<AttendeeViewHolder>
    {
        private ArrayList<String> attendeeIds;
        public SessionAttendanceAdapter(ArrayList<String> attendees) {
            this.attendeeIds = attendees;
        }

        @Override
        public int getItemCount() {
            return attendeeIds.size();
        }

        @Override
        public void onBindViewHolder(@NonNull final AttendeeViewHolder holder,
                                     final int position) {

            Log.d(TAG, "Binding view holder");
            edu.pace.cs389s2019team5.ez_attend.Firebase.View v = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();

            if (holder.m_listener != null) {
                Log.d(TAG, "Removing listener");
                holder.m_listener.remove();
            }

            v.getStudent(attendeeIds.get(position), new OnSuccessListener<Student>() {
                @Override
                public void onSuccess(final Student s) {
                    // This caches the result in attendees so it doesn't reload every time the user
                    // scrolls through the attendance records...haha not anymore

                    if (position != holder.getAdapterPosition()) return;

                    holder.m_attendeeId.setText(s.getFirstName() + " " + s.getLastName());

                    holder.m_attendeeStatus.setText(R.string.attendance_loading);

                    holder.m_listener = view.listenForMarking(classId,
                            m_session.getId(),
                            s.getId(),
                            new OnSuccessListener<Attendee>() {
                                @Override
                                public void onSuccess(Attendee attendee) {
                                    if (holder.getAdapterPosition() != position) return;
                                    if (attendee == null) {
                                        holder.m_attendeeStatus.setText(R.string.attendance_absent);
                                    } else {
                                        // This is equivalent to 10 minutes
                                        holder.m_attendeeStatus.setText(
                                                attendee.getAttendeeStatus(SessionAttendanceFragment.this.getActivity(),
                                                        m_session.getStartTime(),
                                                        600000));
                                    }
                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error getting student attendance", e);
                                }
                            });

                    holder.m_attendeeId.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            openStudent(classId, m_session.getId(), s.getId(), s.getFirstName() + " " + s.getLastName());
                        }
                    });

                    holder.m_attendeeStatus.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            openStudent(classId, m_session.getId(), s.getId(), s.getFirstName() + " " + s.getLastName());
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

        @Override
        public void onViewDetachedFromWindow(@NonNull AttendeeViewHolder holder) {
            if (holder.m_listener != null) {
                Log.d(TAG, "Detaching listener in onViewDetachedFromWindow");
                holder.m_listener.remove();
                holder.m_listener = null;
            }
            super.onViewDetachedFromWindow(holder);
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
        private ListenerRegistration m_listener;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);

            this.m_attendeeId = itemView.findViewById(R.id.txtSessionStartTime);
            this.m_attendeeStatus = itemView.findViewById(R.id.txtSessionAttendanceStatus);
            this.m_listener = null;
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

        view.getClass(classId,  new OnSuccessListener<Class>() {
            @Override
            public void onSuccess(final Class c)
            {

                ArrayList<String> list = new ArrayList<>();
                Iterator<String> studentIds = c.getStudentIdsIterator();
                while (studentIds.hasNext()) {
                    list.add(studentIds.next());
                }

                mAdapter = new SessionAttendanceAdapter(list);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(mAdapter);

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "Error when attempting to get class", e);
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.recyclerView.setAdapter(this.mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.recyclerView.setAdapter(null);
    }

}

