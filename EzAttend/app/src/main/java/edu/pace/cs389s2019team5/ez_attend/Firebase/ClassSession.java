package edu.pace.cs389s2019team5.ez_attend.Firebase;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class ClassSession {

    public final static SnapshotParser<ClassSession> SNAPSHOTPARSER = new SnapshotParser<ClassSession>() {
        @NonNull
        @Override
        public ClassSession parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new ClassSession(snapshot.getId(), snapshot.getDate("startTime"));
        }
    };

    private final static String TAG = ClassSession.class.getName();
    public final static String ATTENDEES = "attendees";
    public final static String STARTTIME = "startTime";

    private String id;
    private Date startTime;

    public ClassSession(String id, Date startTime) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    @Deprecated
    /**
     * Given a Firebase firestore snapshot of a session produces a session object that can be
     * manipulated by the rest of the view
     * @param snapshot the firebase snapshot of a session
     * @return the session object based off of the session snapshot that was provided
     * @throws NullPointerException if the snapshot provided was null or the session data was null
     */
    public static ClassSession fromSnapshot(DocumentSnapshot snapshot) {
        if (snapshot == null) {
            throw new NullPointerException("Snapshot cannot be null");
        }
        return new ClassSession(snapshot.getId(), snapshot.getDate("startTime"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassSession that = (ClassSession) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Date getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "ClassSession{" +
                "id='" + id + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
