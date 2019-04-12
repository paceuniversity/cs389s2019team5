package edu.pace.cs389s2019team5.ez_attend.Firebase;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Attendee {

    private final static String TAG = Attendee.class.getName();

    private String id;
    private Date studentTimeStamp;
    private Date teacherTimeStamp;

    public Attendee(String id, Date studentTimeStamp, Date teacherTimeStamp) {
        this.id = id;
        this.studentTimeStamp = studentTimeStamp;
        this.teacherTimeStamp = teacherTimeStamp;
    }

    public String getId() {
        return id;
    }

    public Date getStudentTimeStamp() {
        return studentTimeStamp;
    }

    public Date getTeacherTimeStamp() {
        return teacherTimeStamp;
    }

    public static Attendee fromSnapshot(DocumentSnapshot snapshot) {

        if (snapshot == null) {
            throw new NullPointerException("Snapshot cannot be null");
        }

        Date studentTimeStamp = snapshot.getDate("timeStamp");
        Date teacherTimeStamp = snapshot.getDate("teacherTimestamp");

        return new Attendee(snapshot.getId(), studentTimeStamp, teacherTimeStamp);
    }

    @Override
    public String toString() {
        return "Attendee{" +
                "id='" + id + '\'' +
                ", studentTimeStamp=" + studentTimeStamp +
                ", teacherTimeStamp=" + teacherTimeStamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attendee attendee = (Attendee) o;

        return id.equals(attendee.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
