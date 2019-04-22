package edu.pace.cs389s2019team5.ez_attend.Firebase;

import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Class {

    private final static String TAG = Class.class.getName();
    public final static String SESSIONS = "sessions";
    public final static SnapshotParser<Class> SNAPSHOTPARSER = new SnapshotParser<Class>() {
        @NonNull
        @Override
        public Class parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            return new Class(snapshot.getId(),
                    snapshot.getString("name"),
                    snapshot.getString("teacherId"),
                    (List<String>) snapshot.get("students"),
                    snapshot.getDate("mostRecent"));
        }
    };

    private String id;
    private String className;
    private String teacherId;
    private List<String> studentIds;
    private Date mostRecent;

    public Class(String id, String name,String teacherId, List<String> studentIds, Date mostRecent) {
        this.id = id;
        this.className = name;
        this.teacherId = teacherId;
        this.studentIds = studentIds;
        this.mostRecent = mostRecent;
    }

    public String getId() {
        return id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public Iterator<String> getStudentIdsIterator() {
        if (this.studentIds == null) return null;
        return this.studentIds.iterator();
    }

    public String getClassName() {
        return className;
    }

    public Date getMostRecent() {
        return mostRecent;
    }

    @Override
    public String toString() {
        return "Class{" +
                "id='" + id + '\'' +
                ", className='" + className + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", studentIds=" + studentIds +
                ", mostRecent=" + mostRecent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Class aClass = (Class) o;

        return id.equals(aClass.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Deprecated
    /**
     * Generates a class object from a document snapshot. @Deprecated Use the SNAPSHOTPARSER instead
     * @param snapshot the document snapshot generally returned by calls to firebase
     * @return the newly created class object
     */
    public static Class fromSnapshot(DocumentSnapshot snapshot) {

        String id = snapshot.getId();
        String name = snapshot.getString("name");
        String teacherId = snapshot.getString("teacherId");
        List<String> students = (List<String>) snapshot.get("students");
        Date mostRecent = snapshot.getDate("mostRecent");

        return new Class(id, name, teacherId, students, mostRecent);
    }

}
