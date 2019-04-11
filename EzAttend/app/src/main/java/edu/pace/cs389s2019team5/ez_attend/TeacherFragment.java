package edu.pace.cs389s2019team5.ez_attend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TeacherFragment extends Fragment {


    public TeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher, container, false);
    }

    public void openClass(String classID) {
        //selecting a class will open a new fragment such as: new TeacherClassFragment(String classID)

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new TeacherClassFragment(classID)).commit();
    }
}
