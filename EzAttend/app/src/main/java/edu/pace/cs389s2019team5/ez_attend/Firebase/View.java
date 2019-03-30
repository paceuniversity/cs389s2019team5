package edu.pace.cs389s2019team5.ez_attend.Firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class View {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View() { }

    /**
     * Loads the students in the class. It only loads the ids, and not the student info
     * @param onSuccessListener what to do with the students ids
     * @param onFailureListener if it fails, what should happen
     */
    public void getStudents(OnSuccessListener<ArrayList<String>> onSuccessListener, OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
        // todo implement
    }

    /**
     * Loads an entire student object from the student id. This includes the students first name,
     * last name, and mac address
     * @param id the id of the student
     * @param onSuccessListener the callback when the student info is loaded
     * @param onFailureListener the callback when the student info load fails
     */
    public void getStudent(String id, OnSuccessListener<Student> onSuccessListener, OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
        // todo implement
    }

    /**
     * Loads the id's of all the sessions that have taken place in chronological order by start time
     * @param onSuccessListener the callback for when we get this info
     * @param onFailureListener the callback for failing to get the info
     */
    public void getSessions(OnSuccessListener<ArrayList<String>> onSuccessListener, OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
        // todo implement
    }

    /**
     * Loads the session info for a provided session. The id for the session must be provided.
     * Using this info it pulls the attendance for that specific session
     * @param id the id for the session that we are interested in
     * @param onSuccessListener the callback for getting this information
     * @param onFailureListener the failure callback for this info
     */
    public void getSessionDetails(String id, OnSuccessListener<ArrayList<ClassSession>> onSuccessListener, OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
        // todo implement
    }

}
