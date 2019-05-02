package edu.pace.cs389s2019team5.ez_attend.Firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class Attendee {

    private final static String TAG = Attendee.class.getName();
    public enum Mark {
        PRESENT, LATE, ABSENT;
    }

    public final static SnapshotParser<Attendee> SNAPSHOTPARSER = new SnapshotParser<Attendee>() {
        @NonNull
        @Override
        public Attendee parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            Date studentTimeStamp = snapshot.getDate("timeStamp");
            Date teacherTimeStamp = snapshot.getDate("teacherTimestamp");
            return new Attendee(snapshot.getId(), studentTimeStamp, teacherTimeStamp);
        }
    };

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

    public String getAttendeeStatus(Context context, Date startTime, long timeToLate) {

        // Check if student is late
        long startInMillis = startTime.getTime();
        long studentArrival;
        if (!Model.BLUETOOTH)
            studentArrival = this.getStudentTimeStamp().getTime();
        else
            studentArrival = this.getTeacherTimeStamp().getTime();

        startInMillis += timeToLate;

        if (startInMillis < studentArrival) {
            return context.getResources().getString(edu.pace.cs389s2019team5.ez_attend.R.string.attendance_late);
        } else {
            return context.getResources().getString(edu.pace.cs389s2019team5.ez_attend.R.string.attendance_present);
        }

    }

    @Deprecated
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
