package edu.pace.cs389s2019team5.ez_attend.Firebase;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class ClassSession {

    private String id;
    private Date startTime;
    private HashMap<Student, Date> attendees;

    public ClassSession(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
    }

    public void setAttendees(HashMap<Student, Date> attendees) {
        this.attendees = attendees;
    }

    public Iterator<Student> getAttendeeIterator() {
        return attendees.keySet().iterator();
    }

    public Date getStudentArrivalTime(Student student) {
        return attendees.get(student);
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "ClassSession{" +
                "id='" + id + '\'' +
                ", startTime=" + startTime +
                ", attendees=" + attendees +
                '}';
    }
}
