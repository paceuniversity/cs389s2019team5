package edu.pace.cs389s2019team5.ez_attend.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    private final static String TAG = Controller.class.getName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Creates a new class session on firebase so that the class attendance can begin
     * getting recorded.
     * @param successListener what should be done when the creation of the class session was successful.
     *                        A class session will be passed which can be used to get the id and timestamp
     */
    public void beginClassSession(final OnSuccessListener<ClassSession> successListener, final OnFailureListener failureListener) {

        Map<String, Object> session = new HashMap<>();
        session.put("startTime", FieldValue.serverTimestamp());

        db.collection("sessions")
                .add(session)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "Session successfully created");
                        successListener.onSuccess(new ClassSession(documentReference.getId()));

                    }
                })
                .addOnFailureListener(failureListener);
    }

    /**
     * Ends the session with the given id
     * @return
     */
    public boolean endClassSession() {
        // todo this might not be necessary
        return false;
    }

    /**
     * Given the id of the student, should mark present in the most recent class session if the student
     * is not already marked
     */
    public void markPresent(final String id, final OnSuccessListener<Void> onSuccessListener, final OnFailureListener onFailureListener) {

        db.collection("sessions")
                .orderBy("startTime", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List list = queryDocumentSnapshots.getDocuments();
                        DocumentSnapshot snap = (DocumentSnapshot) list.get(0);
                        Map<String, Object> userTimeStamp = new HashMap<>();
                        userTimeStamp.put("timeStamp", FieldValue.serverTimestamp());

                        db.collection("sessions/" + snap.getId() + "/attendees")
                                .document(id)
                                .set(userTimeStamp)
                                .addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to mark student present");
                    }
                });

    }

}
