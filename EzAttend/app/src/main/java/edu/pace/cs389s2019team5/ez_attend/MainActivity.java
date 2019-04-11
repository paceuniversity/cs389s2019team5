package edu.pace.cs389s2019team5.ez_attend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchStudent(View view) {
        System.out.println("Launching student");
        Intent intent = new Intent(this, ClassListActivity.class);//temporarily changed to open new main menu
        startActivity(intent);
    }

    public void openTeacherActivity(View v){
        Intent i = new Intent(MainActivity.this, TeacherActivity.class);
        startActivity(i);
    }

}
