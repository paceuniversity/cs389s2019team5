package edu.pace.cs389s2019team5.ez_attend.ClassListFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Random;

import edu.pace.cs389s2019team5.ez_attend.ClassFragments.StudentClassFragment;
import edu.pace.cs389s2019team5.ez_attend.Firebase.Class;
import edu.pace.cs389s2019team5.ez_attend.R;


public class StudentFragment extends Fragment {


    public StudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseFirestore.getInstance().collection("chats").whereArrayContains("students",user);
        FirestoreRecyclerOptions<Class> options = new FirestoreRecyclerOptions.Builder<Class>().setQuery(query, Class.class).build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Class, StudentFragment.ClassHolder>(options) {
            @Override
            public void onBindViewHolder(StudentFragment.ClassHolder holder, int position, final Class model) {
                Button classSelection = holder.classSelection;
                classSelection.setText(model.getId());

                Drawable a = getResources().getDrawable(R.drawable.fui_idp_button_background_anonymous);
                Drawable b = getResources().getDrawable(R.drawable.fui_idp_button_background_email);
                Drawable c = getResources().getDrawable(R.drawable.fui_idp_button_background_facebook);
                Drawable d = getResources().getDrawable(R.drawable.fui_idp_button_background_github);
                Drawable e = getResources().getDrawable(R.drawable.fui_idp_button_background_google);
                Drawable f = getResources().getDrawable(R.drawable.fui_idp_button_background_phone);
                Drawable g = getResources().getDrawable(R.drawable.fui_idp_button_background_twitter);
                Drawable[] arr = {a,b,c,d,e,f,g};
                Random rand = new Random();
                int num = rand.nextInt(7);//background could be changed from random to choose depending on class name

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

        adapter.startListening();
        return inflater.inflate(R.layout.fragment_student, container, false);
    }

    public void openClass(String classID) {
        StudentClassFragment fragment = new StudentClassFragment();
        Bundle bundle = new Bundle();
        bundle.putString("classID", classID);
        getFragmentManager().beginTransaction().replace(R.id.fragment_content, fragment).commit();
    }
    public class ClassHolder extends RecyclerView.ViewHolder {
        public Button classSelection;
        public ClassHolder(View itemView) {
            super(itemView);
            classSelection = itemView.findViewById(R.id.classSelection);
        }
    }
}
