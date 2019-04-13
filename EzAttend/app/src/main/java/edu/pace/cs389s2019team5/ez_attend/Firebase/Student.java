package edu.pace.cs389s2019team5.ez_attend.Firebase;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.DocumentSnapshot;

public class Student implements Parcelable {

    private String id;
    private String firstName;
    private String lastName;
    private String macAddress;

    public Student (String id, String firstName, String lastName, String macAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.macAddress = macAddress;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMacAddress(String mac_address) {
        this.macAddress = mac_address;
    }

    /**
     * Given a Firebase firestore snapshot of a student produces a student object that can be
     * manipulated by the rest of the view
     * @param snapshot the firebase snapshot of a student
     * @return the student object based off of the student snapshot that was provided
     * @throws NullPointerException if the snapshot provided was null or the student data was null
     */
    public static Student fromSnapshot(DocumentSnapshot snapshot) {
        if (snapshot == null) {
            throw new NullPointerException("Snapshot cannot be null");
        }

        String firstName = snapshot.getString("firstName");
        String lastName = snapshot.getString("lastName");
        String macAddress = snapshot.getString("macAddress");

        return new Student(snapshot.getId(), firstName, lastName, macAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return student.id.equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.firstName);
        parcel.writeString(this.lastName);
        parcel.writeString(this.macAddress);
    }

    public static final Parcelable.Creator<Student> CREATOR = new Parcelable.Creator<Student>() {

        @Override
        public Student createFromParcel(Parcel parcel) {
            return new Student(parcel.readString(),
                               parcel.readString(),
                               parcel.readString(),
                               parcel.readString());
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }

    };

}
