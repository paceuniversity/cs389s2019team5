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

                        if (list.isEmpty()) {
                            Log.i(TAG, "There are no sessions created");
                            onFailureListener.onFailure(new NullPointerException("No sessions created"));
                            return;
                        }
                        final DocumentSnapshot sessionSnapShot = (DocumentSnapshot) list.get(0);

                        db.collection("sessions/" + sessionSnapShot.getId() + "/attendees")
                                .document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot studentSnapshot) {
                                if (studentSnapshot.exists() && studentSnapshot.get("timeStamp") != null) {
                                    // the student has already checked in to this session
                                    Log.i(TAG, "Student has already been marked previously for this class");
                                    onSuccessListener.onSuccess(null);
                                } else {
                                    // the student hasn't checked in to this session
                                    Map<String, Object> userTimeStamp = new HashMap<>();
                                    userTimeStamp.put("timeStamp", FieldValue.serverTimestamp());

                                    Log.i(TAG, "Marking student present for most recent session");
                                    db.collection("sessions/" + sessionSnapShot.getId() + "/attendees")
                                            .document(id).set(userTimeStamp).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Couldn't check if the user exists in mark present", e);
                                onFailureListener.onFailure(e);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Couldn't not get most recent session", e);
                        onFailureListener.onFailure(e);
                    }
                });

    }

    /**
     * Used by the teacher to mark a student present. This is specifically used by bluetooth support.
     * As opposed to markPresent which is used by a student to mark themselves present, this adds
     * a new field to the document called teacherTimestamp which marks when the teacher device
     * detected the student.
     * @param id the id of the student that the teacher wishes to mark present
     * @param onSuccessListener the callback when the student is successfully marked present
     * @param onFailureListener the callback when marking the student present fails
     */
    public void teacherMarkPresent(final String id, final OnSuccessListener<Void> onSuccessListener, final OnFailureListener onFailureListener) {

        db.collection("sessions")
                .orderBy("startTime", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List list = queryDocumentSnapshots.getDocuments();

                        if (list.isEmpty()) {
                            Log.i(TAG, "There are no sessions created");
                            onFailureListener.onFailure(new NullPointerException("No sessions created"));
                            return;
                        }
                        final DocumentSnapshot sessionSnapShot = (DocumentSnapshot) list.get(0);

                        db.collection("sessions/" + sessionSnapShot.getId() + "/attendees")
                                .document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot studentSnapshot) {
                                if (studentSnapshot.exists() && studentSnapshot.get("teacherTimestamp") != null) {
                                    // the student has already checked in to this session
                                    Log.i(TAG, "Student has already been marked previously for this class");
                                    onSuccessListener.onSuccess(null);
                                } else {
                                    // the student hasn't checked in to this session
                                    Map<String, Object> userTimeStamp = new HashMap<>();
                                    userTimeStamp.put("teacherTimestamp", FieldValue.serverTimestamp());

                                    Log.i(TAG, "Marking student present for most recent session");
                                    db.collection("sessions/" + sessionSnapShot.getId() + "/attendees")
                                            .document(id).set(userTimeStamp).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Couldn't check if the user exists in mark present", e);
                                onFailureListener.onFailure(e);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Couldn't not get most recent session", e);
                        onFailureListener.onFailure(e);
                    }
                });
    }
}
