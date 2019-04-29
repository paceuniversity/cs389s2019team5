package edu.pace.cs389s2019team5.ez_attend.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    private final static String TAG = Controller.class.getName();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // TODO This should no longer be used after sprint2. Current usage is only in the bluetooth adapter
    public final static String DEBUG_CLASS_ID = "123";

    /**
     * Creates a new class session on firebase so that the class attendance can begin
     * getting recorded. It also updates the most recent field of the class
     *
     * @param classId         the id of the class that we wish to create a new session for
     * @param successListener what should be done when the creation of the class session was successful.
     *                        A class session will be passed which can be used to get the id and timestamp
     * @param failureListener the callback if there is some sort of error when attempting to create
     *                        a new class session
     */
    public void beginClassSession(final String classId,
                                  final OnSuccessListener<String> successListener,
                                  final OnFailureListener failureListener) {

        Map<String, Object> session = new HashMap<>();
        session.put("startTime", FieldValue.serverTimestamp());

        db.collection(Model.CLASSES)
                .document(classId)
                .collection(Class.SESSIONS)
                .add(session)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "Session successfully created");
                        successListener.onSuccess(documentReference.getId());

                    }
                })
                .addOnFailureListener(failureListener);

        // This adds the most recent session timestamp to the class itself. With this design,
        // the time might not match the start time of the session exactly. It may be better to do
        // this on Firebase instead but this is good enough for the most part.
        db.collection(Model.CLASSES)
                .document(classId)
                .update("mostRecent", FieldValue.serverTimestamp())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Begin class session: Successfully updated the most recent" +
                                " session");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Failed to update the most recent class session.", e);
            }

        });
    }

    /**
     * Creates a new class on firebase, with the provided teacher id as the teacher.
     *
     * @param teacherId       the id of the teacher that is creating the class
     * @param successListener what should be done when the creation of the class session was successful.
     *                        The id for the newly created class will be passed
     * @param failureListener the callback when there is an error creating the class
     */
    public void createClass(String teacherId,
                            String className,
                            final OnSuccessListener<String> successListener,
                            final OnFailureListener failureListener) {

        Map<String, Object> classMap = new HashMap<>();
        classMap.put("name", className);
        classMap.put("teacherId", teacherId);

        db.collection(Model.CLASSES)
                .add(classMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "Class successfully created");
                        successListener.onSuccess(documentReference.getId());

                    }
                })
                .addOnFailureListener(failureListener);

    }

    /**
     * Adds a student to a specific class that is given
     *
     * @param classId         the id of the class that the student would like to join
     * @param studentId       the id of the student that would like to join the class
     * @param successListener the callback if the student was successfully added to the class
     * @param failureListener the callback if the student couldn't join the class. This could mean
     *                        the class doesn't exist
     */
    public void joinClass(String classId,
                          String studentId,
                          OnSuccessListener<Void> successListener,
                          OnFailureListener failureListener) {

        DocumentReference classReference = db.collection(Model.CLASSES).document(classId);

        classReference.update("students", FieldValue.arrayUnion(studentId))
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);

    }

    /**
     * Ends the session with the given id
     *
     * @return
     */
    public boolean endClassSession() {
        // todo this might not be necessary
        return false;
    }

    /**
     * Given the id of the student, should mark present in the most recent class session if the student
     * is not already marked
     *
     * @param classId           the id of the class we wish to be marked present for
     * @param studentId         the id of the student being marked present
     * @param onSuccessListener the callback if the marking was successful
     * @param onFailureListener the callback if there was an error marking the student present
     */
    public void markPresent(final String classId,
                            final String studentId,
                            final OnSuccessListener<Void> onSuccessListener,
                            final OnFailureListener onFailureListener) {

        db.collection(Model.CLASSES)
                .document(classId)
                .collection(Class.SESSIONS)
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

                        db.collection(Model.CLASSES)
                                .document(classId)
                                .collection(Class.SESSIONS)
                                .document(sessionSnapShot.getId())
                                .collection(ClassSession.ATTENDEES)
                                .document(studentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                    db.collection(Model.CLASSES)
                                            .document(classId)
                                            .collection(Class.SESSIONS)
                                            .document(sessionSnapShot.getId())
                                            .collection(ClassSession.ATTENDEES)
                                            .document(studentId).set(userTimeStamp)
                                            .addOnSuccessListener(onSuccessListener)
                                            .addOnFailureListener(onFailureListener);
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
     *
     * @param classId           the id of the class that the teacher is marking the student present for
     * @param studentId         the id of the student that the teacher wishes to mark present
     * @param onSuccessListener the callback when the student is successfully marked present
     * @param onFailureListener the callback when marking the student present fails
     */
    public void teacherMarkPresent(final String classId,
                                   final String studentId,
                                   final OnSuccessListener<Void> onSuccessListener,
                                   final OnFailureListener onFailureListener) {

        db.collection(Model.CLASSES)
                .document(classId)
                .collection(Class.SESSIONS)
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

                        db.collection(Model.CLASSES)
                                .document(classId)
                                .collection(Class.SESSIONS)
                                .document(sessionSnapShot.getId())
                                .collection(ClassSession.ATTENDEES)
                                .document(studentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                                    db.collection(Model.CLASSES)
                                            .document(classId)
                                            .collection(Class.SESSIONS)
                                            .document(sessionSnapShot.getId())
                                            .collection(ClassSession.ATTENDEES)
                                            .document(studentId).set(userTimeStamp)
                                            .addOnSuccessListener(onSuccessListener)
                                            .addOnFailureListener(onFailureListener);
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
     * Used by the teacher to mark a student present, late, or absent. This is specifically used for
     * manual activation. This allows a teacher to change a student to the specified mark in a given
     * class and class session.
     *
     * @param classId           the id of the class that the teacher is marking the change to
     * @param sessionId         the id of the session that the teacher is marking the change to
     * @param studentId         the id of the student that the teacher wishes to change the mark for
     * @param mark              the type of mark to make
     * @param onSuccessListener the callback when the student is successfully marked present
     * @param onFailureListener the callback when marking the student present fails
     */
    //TODO not sure if this will work for students who were initially absent
    public void markManual(final String classId,
                                  final String sessionId,
                                  final String studentId,
                                  final Enum mark,
                                  final OnSuccessListener<Void> onSuccessListener,
                                  final OnFailureListener onFailureListener) {
        db.collection(Model.CLASSES)
                .document(classId)
                .collection(Class.SESSIONS)
                .document(sessionId)
                .collection(ClassSession.ATTENDEES)
                .document(studentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot snapStudent) {
                        db.collection(Model.CLASSES)
                                .document(classId)
                                .collection(Class.SESSIONS)
                                .document(sessionId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot snapSession) {
                                        Timestamp time = (Timestamp) snapSession.get(ClassSession.STARTTIME);
                                        Date date = time.toDate();
                                        Map<String, Object> update = new HashMap<>();
                                        if (mark == Attendee.Mark.PRESENT) {
                                            update.put(Attendee.TIMESTAMP,date);
                                        }
                                        if (mark == Attendee.Mark.LATE) {
                                            date = new Date(date.getTime() + (11 * 60000));;
                                            update.put(Attendee.TIMESTAMP,date);
                                        }
                                        if (mark == Attendee.Mark.ABSENT) {
                                            update.put(Attendee.TIMESTAMP, FieldValue.delete());
                                        }
                                        db.collection(Model.CLASSES)
                                                .document(classId)
                                                .collection(Class.SESSIONS)
                                                .document(sessionId)
                                                .collection(ClassSession.ATTENDEES)
                                                .document(studentId)
                                                .update(update)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        onSuccessListener.onSuccess(aVoid);
                                                        Log.i(TAG, "Successfully marked " + mark);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "Couldn't mark"+ mark, e);
                                                        onFailureListener.onFailure(e);
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Couldn't mark"+ mark, e);
                                        onFailureListener.onFailure(e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Couldn't mark"+ mark, e);
                        onFailureListener.onFailure(e);
                    }
                });
    }











    /**
     * Creates a new user in firestore based on the provided user information.
     *
     * @param userId            the user id of the signed in user. this is unique for this user
     * @param firstName         the first name of the user
     * @param lastName          the last name of the user to create
     * @param macAddress        the mac address of the users device
     * @param onSuccessListener the callback when the user is successfully created
     * @param onFailureListener the callback when there is an error creating the user
     */
    public void createNewUser(final String userId,
                              final String firstName,
                              final String lastName,
                              final String macAddress,
                              @NonNull final OnSuccessListener<Void> onSuccessListener,
                              @NonNull final OnFailureListener onFailureListener) {

        final CollectionReference studentsRef = db.collection(Model.STUDENTS);
        final DocumentReference docRef = studentsRef.document(userId);

        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("macAddress", macAddress);

        docRef.set(user)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);

    }

}

