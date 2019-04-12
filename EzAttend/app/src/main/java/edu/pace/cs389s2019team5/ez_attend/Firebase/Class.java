package edu.pace.cs389s2019team5.ez_attend.Firebase;

import java.util.ArrayList;
import java.util.Iterator;

public class Class {

    private final static String TAG = Class.class.getName();
    public final static String SESSIONS = "sessions";

    private String id;
    private String teacherId;
    private ArrayList<String> studentIds;

    public Class(String id, String teacherId, ArrayList<String> studentIds) {
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

}
