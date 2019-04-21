package edu.pace.cs389s2019team5.ez_attend;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import edu.pace.cs389s2019team5.ez_attend.ClassListFragments.RecentFragment;
import edu.pace.cs389s2019team5.ez_attend.ClassListFragments.StudentFragment;
import edu.pace.cs389s2019team5.ez_attend.ClassListFragments.TeacherFragment;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Student;

public class ClassListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = ClassListActivity.class.getName();
    public final static String CURRENT_USER_TAG = "user";

    // Stores the information for the currently signed in user
    private Student currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        this.currentUser = getIntent().getParcelableExtra(CURRENT_USER_TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //for now the app will just display recent classes as the default
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new RecentFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.class_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager f = getSupportFragmentManager();
        for(int i = 0; i < f.getBackStackEntryCount(); i++) {
            f.popBackStack();
        }

        int id = item.getItemId();

        if (id == R.id.nav_recent) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new RecentFragment()).commit();
        } else if (id == R.id.nav_teacher) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new TeacherFragment()).commit();
        } else if (id == R.id.nav_student) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_content, new StudentFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
