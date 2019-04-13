package edu.pace.cs389s2019team5.ez_attend.ClassListFragments;

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

import edu.pace.cs389s2019team5.ez_attend.Firebase.Class;
import edu.pace.cs389s2019team5.ez_attend.R;


public class TeacherFragment extends Fragment {


    public TeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseFirestore.getInstance().collection("chats").whereEqualTo("teacherId",user);
        FirestoreRecyclerOptions<Class> options = new FirestoreRecyclerOptions.Builder<Class>().setQuery(query, Class.class).build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Class, ClassHolder>(options) {
            @Override
            public void onBindViewHolder(ClassHolder holder, int position, final Class model) {
                Button classSelection = holder.classSelection;
                classSelection.setText(model.getId());
                classSelection.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        openClass(model.getId());
                    }
                });
            }

            @Override
            public ClassHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.holder_class, group, false);
                return new ClassHolder(view);
            }
        };

        adapter.startListening();

        return inflater.inflate(R.layout.fragment_teacher, container, false);
    }

    public void openClass(String classID) {
        //selecting a class will open a new fragment such as: new TeacherClassFragment(String classID)

        //getFragmentManager().beginTransaction().replace(R.id.fragment_content, new TeacherClassFragment(classID)).commit();
    }
    public class ClassHolder extends RecyclerView.ViewHolder {
        public Button classSelection;
        public ClassHolder(View itemView) {
            super(itemView);
            classSelection = itemView.findViewById(R.id.classSelection);
        }
    }
}
