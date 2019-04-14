package edu.pace.cs389s2019team5.ez_attend.ClassListFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Random;

import edu.pace.cs389s2019team5.ez_attend.ClassFragments.StudentClassFragment;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Class;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.R;


public class StudentFragment extends Fragment {

    private static final String TAG = StudentFragment.class.getName();
    private String user;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView rv;
    private RecyclerView.LayoutManager layoutManager;
    public StudentFragment() {
        this.user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student, container, false);
        Button joinClass = v.findViewById(R.id.joinClassButton);
        joinClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String tempClass = "trDCEnUDFTHQ8G9kbR3S";
                joinClass(tempClass);
            }
        });
        createAdapter();
        this.rv = v.findViewById(R.id.rvStudent);
        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.rv.setLayoutManager(this.layoutManager);
        this.rv.setAdapter(this.adapter);
        return v;
    }

    private void createAdapter() {
        Query query = FirebaseFirestore.getInstance().collection("classes").whereArrayContains("students",this.user);
        FirestoreRecyclerOptions<Class> options = new FirestoreRecyclerOptions.Builder<Class>().setQuery(query, Class.SNAPSHOTPARSER).build();

        this.adapter = new FirestoreRecyclerAdapter<Class, StudentFragment.ClassHolder>(options) {
            @Override
            public void onBindViewHolder(StudentFragment.ClassHolder holder, int position, final Class model) {
                Button classSelection = holder.classSelection;
                classSelection.setText(model.getId());

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
                        openClass(model.getId());
                    }
                });
            }

            @Override
            public StudentFragment.ClassHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.holder_class, group, false);
                return new StudentFragment.ClassHolder(view);
            }
        };
    }

    public void joinClass(String classId) {
        Controller newClass = new Controller();
        newClass.joinClass(classId, this.user, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "New class joined",
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Failed joining new class");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Failed joining new class",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error when attempting to join new class", e);
            }
        });
    }

    public void openClass(String classID) {
        StudentClassFragment fragment = new StudentClassFragment();
        fragment.setClassID(classID);
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
