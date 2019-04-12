package edu.pace.cs389s2019team5.ez_attend.Firebase;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {

    private String id;
    private String firstName;
    private String lastName;
    private String macAddress;

    public Student (String id) {
        this.id = id;
    }

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
