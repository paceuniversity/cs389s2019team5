package edu.pace.cs389s2019team5.ez_attend.Firebase;

/*
These are package private to discourage use outside of the firebase package. All accesses to firebase
should be done indirectly through the firebase package.
 */
public class Model {

    // True sets the attendance based on student pressing check in
    // false sets the attendance based on bluetooth detection
    public final static boolean BLUETOOTH = false;

    // The root collections for the application
    final static String STUDENTS = "students";
    final static String CLASSES = "classes";

}
