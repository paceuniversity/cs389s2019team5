package edu.pace.cs389s2019team5.ez_attend.Firebase;

import java.util.ArrayList;
import java.util.Iterator;

public class ClassSession {

    private String id;
    private ArrayList<Student> attendees;

    public ClassSession(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
    }

    public void setAttendees(ArrayList<Student> attendees) {
        this.attendees = attendees;
    }

    public Iterator<Student> getAttendeeIterator() {
        return attendees.iterator();
    }

    public String getId() {
        return id;
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
}
