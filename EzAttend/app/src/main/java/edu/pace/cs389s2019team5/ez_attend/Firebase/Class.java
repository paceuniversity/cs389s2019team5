package edu.pace.cs389s2019team5.ez_attend.Firebase;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Iterator;
import java.util.List;

public class Class {

    private final static String TAG = Class.class.getName();
    public final static String SESSIONS = "sessions";

    private String id;
    private String teacherId;
    private List<String> studentIds;

    public Class(String id, String teacherId, List<String> studentIds) {
        this.id = id;
        this.teacherId = teacherId;
        this.studentIds = studentIds;
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

    @Override
    public String toString() {
        return "Class{" +
                "id='" + id + '\'' +
                ", teacherId='" + teacherId + '\'' +
                ", studentIds=" + studentIds +
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

    /**
     * Generates a class object from a document snapshot
     * @param snapshot the document snapshot generally returned by calls to firebase
     * @return the newly created class object
     */
    public static Class fromSnapshot(DocumentSnapshot snapshot) {

        String id = snapshot.getId();
        String teacherId = snapshot.getString("teacherId");
        List<String> students = (List<String>) snapshot.get("students");

        return new Class(id, teacherId, students);
    }

}
