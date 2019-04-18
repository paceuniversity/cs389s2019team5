package edu.pace.cs389s2019team5.ez_attend.AttendanceFragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Random;

import edu.pace.cs389s2019team5.ez_attend.ClassFragments.StudentClassFragment;
import edu.pace.cs389s2019team5.ez_attend.Firebase.ClassSession;
import edu.pace.cs389s2019team5.ez_attend.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SessionsFragment extends Fragment {

    private static final String TAG = SessionsFragment.class.getName();

    private String classId;

    private RecyclerView rv;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public SessionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_sessions, container, false);
        createAdapter();
        this.rv = v.findViewById(R.id.rvSessions);
        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.rv.setLayoutManager(this.layoutManager);
        this.rv.setAdapter(this.adapter);
        return v;
    }
    public void setClassId(String classId)
    {
        this.classId = classId;
    }
    private void createAdapter() {
        Query query = FirebaseFirestore.getInstance().collection("classes").document(classId).collection("sessions");
        FirestoreRecyclerOptions<ClassSession> options = new FirestoreRecyclerOptions.Builder<ClassSession>().setQuery(query, ClassSession.SNAPSHOTPARSER).build();

        this.adapter = new FirestoreRecyclerAdapter<ClassSession, SessionsFragment.ClassHolder>(options) {
            @Override
            public void onBindViewHolder(SessionsFragment.ClassHolder holder, int position, final ClassSession model) {
                Button classSelection = holder.classSelection;
                classSelection.setText(model.getStartTime().toString());

                Drawable a = getResources().getDrawable(R.drawable.fui_idp_button_background_anonymous);
                Drawable b = getResources().getDrawable(R.drawable.fui_idp_button_background_email);
                Drawable c = getResources().getDrawable(R.drawable.fui_idp_button_background_facebook);
                Drawable f = getResources().getDrawable(R.drawable.fui_idp_button_background_phone);
                Drawable g = getResources().getDrawable(R.drawable.fui_idp_button_background_twitter);
                Drawable[] arr = {a,b,c,f,g};//d and e were scrapped
                Random rand = new Random();
                int num = rand.nextInt(5);//background could be changed from random to choose depending on class name

                classSelection.setBackground(arr[num]);
                classSelection.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        openSession(model);
                    }
                });
            }

            @Override
            public SessionsFragment.ClassHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.holder_class, group, false);
                return new SessionsFragment.ClassHolder(view);
            }
        };
    }

    private void openSession(ClassSession session)
    {
        SessionAttendanceFragment fragment = new SessionAttendanceFragment();
        fragment.setClassId(this.classId);
        fragment.setSession(session);
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).addToBackStack(TAG).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.adapter.stopListening();
    }


    public class ClassHolder extends RecyclerView.ViewHolder {
        public Button classSelection;
        public ClassHolder(View itemView) {
            super(itemView);
            classSelection = itemView.findViewById(R.id.classSelection);
        }
    }
}
