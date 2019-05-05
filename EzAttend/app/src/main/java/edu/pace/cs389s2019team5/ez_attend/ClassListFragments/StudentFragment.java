package edu.pace.cs389s2019team5.ez_attend.ClassListFragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

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
    private AutoCompleteTextView textView;

    private edu.pace.cs389s2019team5.ez_attend.Firebase.View view;
    private Controller controller;

    public StudentFragment() {
        this.user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.view = new edu.pace.cs389s2019team5.ez_attend.Firebase.View();
        this.controller = new Controller();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_student, container, false);
        Button joinClass = v.findViewById(R.id.joinClassButton);
        textView = v.findViewById(R.id.classInput);
        joinClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String classID = textView.getText().toString().trim();
                joinClass(classID);
                textView.setText("");
            }
        });
        createAdapter();
        this.rv = v.findViewById(R.id.rvStudent);
        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.rv.setLayoutManager(this.layoutManager);
        this.rv.setAdapter(this.adapter);

        Log.d(TAG, "Setting up the get classes with prefix");


        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // THIS IS NOT NEEDED RIGHT NOW
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // THIS IS NOT NEEDED RIGHT NOW
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*
                    Whenever the user types something into the text box we update the items in the
                    adapter. This doesn't sound very efficient but we do not expect users to be
                    typing very quickly since the ids are generally difficult to remember. For that
                    reason, in practice this should suffice without the overhead of retrieving all
                    the document ids.
                 */
                if (editable.toString().trim().equals("")) {
                    textView.setAdapter(null);
                    return;
                }

                view.getClassesWithIdPrefix(editable.toString(), 3,
                        new OnSuccessListener<ArrayList<Class>>() {
                            @Override
                            public void onSuccess(ArrayList<Class> classes) {

                                Log.d(TAG, "On Success in get classes with prefix:" + classes.size());
                                String[] stringArr = new String[classes.size()];
                                int i = 0;
                                for (Class currClass : classes) {
                                    Log.d(TAG, "Adding document to string adapter " + currClass.getId());
                                    stringArr[i++] = currClass.getId();
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(StudentFragment.this.getContext(), android.R.layout.simple_list_item_1, stringArr);
                                textView.setAdapter(adapter);
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error when getting class ids");
                            }
                        });
            }
        });

        return v;
    }

    private void createAdapter() {
        Query query = view.getStudentClassesQuery(this.user);
        FirestoreRecyclerOptions<Class> options = new FirestoreRecyclerOptions.Builder<Class>().setQuery(query, Class.SNAPSHOTPARSER).build();

        this.adapter = new FirestoreRecyclerAdapter<Class, StudentFragment.ClassHolder>(options) {
            @Override
            public void onBindViewHolder(StudentFragment.ClassHolder holder, int position, final Class model) {
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
