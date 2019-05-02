package edu.pace.cs389s2019team5.ez_attend.ClassListFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import edu.pace.cs389s2019team5.ez_attend.ClassFragments.StudentClassFragment;
import edu.pace.cs389s2019team5.ez_attend.ClassFragments.TeacherClassFragment;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Class;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Controller;
import edu.pace.cs389s2019team5.ez_attend.R;

public class RecentFragment extends Fragment {

    private static final String TAG = RecentFragment.class.getName();
    private String user;
    private FirestoreRecyclerAdapter adapter1;
    private FirestoreRecyclerAdapter adapter2;
    private RecyclerView rv1;
    private RecyclerView rv2;
    private RecyclerView.LayoutManager layoutManager1;
    private RecyclerView.LayoutManager layoutManager2;

    private edu.pace.cs389s2019team5.ez_attend.Firebase.View view;
    private Controller controller;

    public RecentFragment() {
        this.user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        this.controller = new Controller();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_recent, container, false);
        createAdapters();
        this.rv1 = v.findViewById(R.id.rvRecentTeacher);
        this.rv2 = v.findViewById(R.id.rvRecentStudent);
        this.layoutManager1 = new LinearLayoutManager(this.getActivity());
        this.layoutManager2 = new LinearLayoutManager(this.getActivity());
        this.rv1.setLayoutManager(this.layoutManager1);
        this.rv2.setLayoutManager(this.layoutManager2);
        if(this.adapter1!=null)
            this.rv1.setAdapter(this.adapter1);
        if(this.adapter2!=null)
            this.rv2.setAdapter(this.adapter2);
        return v;
    }

    public void openClass(String classID, boolean teacher) {
        if(teacher)
        {
            TeacherClassFragment fragment = new TeacherClassFragment();
            fragment.setClass(classID);
            getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).addToBackStack(TAG).commit();
        }
        else
        {
            StudentClassFragment fragment = new StudentClassFragment();
            fragment.setClassID(classID);
            getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).addToBackStack(TAG).commit();
        }
    }


    private void createAdapters() {
        Query query = view.getTeacherClassesQuery(this.user).orderBy("mostRecent", Query.Direction.DESCENDING).limit(3);
        FirestoreRecyclerOptions<Class> options = new FirestoreRecyclerOptions.Builder<Class>().setQuery(query, Class.SNAPSHOTPARSER).build();
        this.adapter1 = createAdapter(options);

        Query query2 = view.getStudentClassesQuery(this.user).orderBy("mostRecent", Query.Direction.DESCENDING).limit(3);
        FirestoreRecyclerOptions<Class> options2 = new FirestoreRecyclerOptions.Builder<Class>().setQuery(query2, Class.SNAPSHOTPARSER).build();
        this.adapter2 = createAdapter(options2);
    }

    private FirestoreRecyclerAdapter createAdapter(FirestoreRecyclerOptions<Class> options) {
        if(options==null)
            return null;
        FirestoreRecyclerAdapter a = new FirestoreRecyclerAdapter<Class, RecentFragment.ClassHolder>(options) {
            @Override
            public void onBindViewHolder(RecentFragment.ClassHolder holder, int position, final Class model) {
                Button classSelection = holder.classSelection;
                classSelection.setText(model.getClassName());

                Drawable a = getResources().getDrawable(R.drawable.fui_idp_button_background_anonymous);
                Drawable b = getResources().getDrawable(R.drawable.fui_idp_button_background_email);
                Drawable c = getResources().getDrawable(R.drawable.fui_idp_button_background_facebook);
                Drawable f = getResources().getDrawable(R.drawable.fui_idp_button_background_phone);
                Drawable g = getResources().getDrawable(R.drawable.fui_idp_button_background_twitter);
                Drawable h = getResources().getDrawable(R.drawable.fui_idp_button_background_google);

                Drawable color = h;
                char letter = model.getClassName().charAt(0);
                if((letter>='A' && letter<='E')||(letter>='a' && letter<='e'))
                    color = a;
                else if((letter>='F' && letter<='J')||(letter>='f' && letter<='j'))
                    color = b;
                else if((letter>='K' && letter<='O')||(letter>='k' && letter<='o'))
                    color = c;
                else if((letter>='P' && letter<='T')||(letter>='p' && letter<='t'))
                    color = f;
                else if((letter>='U' && letter<='Z')||(letter>='u' && letter<='z'))
                    color = g;

                classSelection.setBackground(color);
                classSelection.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(model.getTeacherId().equals(user))
                            openClass(model.getId(), true);
                        else
                            openClass(model.getId(), false);
                    }
                });
            }

            @Override
            public RecentFragment.ClassHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.holder_class, group, false);
                return new RecentFragment.ClassHolder(view);
            }
        };
        return a;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.adapter1!=null)
            this.adapter1.startListening();
        if(this.adapter2!=null)
            this.adapter2.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(this.adapter1!=null)
            this.adapter1.stopListening();
        if(this.adapter2!=null)
            this.adapter2.stopListening();
    }


    public class ClassHolder extends RecyclerView.ViewHolder {
        public Button classSelection;
        public ClassHolder(View itemView) {
            super(itemView);
            classSelection = itemView.findViewById(R.id.classSelection);
        }
    }
}
