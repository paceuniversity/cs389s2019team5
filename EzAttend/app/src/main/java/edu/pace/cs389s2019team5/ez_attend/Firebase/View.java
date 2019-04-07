package edu.pace.cs389s2019team5.ez_attend.Firebase;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class View {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = View.class.getName();

    public View() { }

    /**
     * Loads the students in the class.
     * @param onSuccessListener what to do with the students info
     * @param onFailureListener if it fails, what should happen
     */
    public void getStudents(final OnSuccessListener<ArrayList<Student>> onSuccessListener,
                            OnFailureListener onFailureListener) {

        CollectionReference studentsRef = db.collection("students");

        studentsRef
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ArrayList<Student> students = new ArrayList<>();
                    for (QueryDocumentSnapshot docSnapshot : queryDocumentSnapshots) {
                        try {
                            Student student = getStudentFromDocSnap(docSnapshot);
                            students.add(student);
                        } catch (NullPointerException exc) {
                            Log.e(TAG, "Error parsing student", exc);
                        }
                    }
                    onSuccessListener.onSuccess(students);
                }
            }).addOnFailureListener(onFailureListener);
    }

    /**
     * Loads an entire student object from the student id. This includes the students first name,
     * last name, and mac address
     * @param id the id of the student
     * @param onSuccessListener the callback when the student info is loaded
     * @param onFailureListener the callback when the student info load fails
     */
    public void getStudent(final String id,
                           final OnSuccessListener<Student> onSuccessListener,
                           OnFailureListener onFailureListener) {

        DocumentReference docRef = db.collection("students").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {
                    Student student;
                    try {
                        student = getStudentFromDocSnap(snapshot);
                    } catch (NullPointerException exc) {
                        student = null;
                        Log.w(TAG, "Student with id " + id + " was corrupt ", exc);
                    }
                    onSuccessListener.onSuccess(student);
                } else {
                    Log.e(TAG, "Student with id " + id + " doesn't exit");
                    onSuccessListener.onSuccess(null);
                }
            }
        }).addOnFailureListener(onFailureListener);

    }

    /**
     * Loads the id's of all the sessions that have taken place in chronological order by start time
     * @param onSuccessListener the callback for when we get this info
     * @param onFailureListener the callback for failing to get the info
     */
    public void getSessions(final OnSuccessListener<ArrayList<ClassSession>> onSuccessListener,
                            OnFailureListener onFailureListener) {

        CollectionReference sessionsRef = db.collection("sessions");

        sessionsRef
                .orderBy("startTime")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<ClassSession> sessions = new ArrayList<>();
                        for (QueryDocumentSnapshot docSnapshot : queryDocumentSnapshots) {
                            try {
                                ClassSession session = getSessionFromDocSnap(docSnapshot);
                                sessions.add(session);
                            } catch (NullPointerException exc) {
                                Log.e(TAG, "Error parsing session", exc);
                            }
                        }
                        onSuccessListener.onSuccess(sessions);
                    }
                }).addOnFailureListener(onFailureListener);
    }

    /**
     * Loads the session info for a provided session. The id for the session must be provided.
     * Using this info it pulls the attendance for that specific session
     * @param session the session that we are interested in getting the attendance for
     * @param onSuccessListener the callback for getting this information
     * @param onFailureListener the failure callback for this info
     */
    public void getSessionAttendance(final ClassSession session,
                                     final OnSuccessListener<ClassSession> onSuccessListener,
                                     OnFailureListener onFailureListener) {

        CollectionReference attendeesCollection = db.collection("sessions")
                .document(session.getId())
                .collection("attendees");

        attendeesCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                HashMap<Student, Date> mMap = new HashMap<>();
                for (QueryDocumentSnapshot snap : snapshot) {
                    mMap.put(new Student(snap.getId()), snap.getDate("timeStamp"));
                }

                session.setAttendees(mMap);
                onSuccessListener.onSuccess(session);
            }
        }).addOnFailureListener(onFailureListener);

    }

    public ListenerRegistration listenForMarking(String courseId,
                                                 String studentId,
                                                 EventListener<DocumentSnapshot> eventListener) {

        final DocumentReference docRef = db.collection("sessions/ " + courseId + "/attendees").document(studentId);
        return docRef.addSnapshotListener(eventListener);

    }

    /**
     * Given a Firebase firestore snapshot of a student produces a student object that can be
     * manipulated by the rest of the view
     * @param snapshot the firebase snapshot of a student
     * @return the student object based off of the student snapshot that was provided
     * @throws NullPointerException if the snapshot provided was null or the student data was null
     */
    private Student getStudentFromDocSnap(DocumentSnapshot snapshot) {

        if (snapshot == null) {
            throw new NullPointerException("Snapshot cannot be null");
        }

        Student student = new Student(snapshot.getId());

        student.setFirstName(snapshot.getString("firstName"));
        student.setLastName(snapshot.getString("lastName"));
        student.setMacAddress(snapshot.getString("macAddress"));

        return student;

    }

    /**
     * Given a Firebase firestore snapshot of a session produces a session object that can be
     * manipulated by the rest of the view
     * @param snapshot the firebase snapshot of a session
     * @return the session object based off of the session snapshot that was provided
     * @throws NullPointerException if the snapshot provided was null or the session data was null
     */
    private ClassSession getSessionFromDocSnap(DocumentSnapshot snapshot) {

        if (snapshot == null) {
            throw new NullPointerException("Snapshot cannot be null");
        }
        ClassSession session = new ClassSession(snapshot.getId());
        session.setStartTime(snapshot.getDate("startTime"));
        return session;

    }

}
