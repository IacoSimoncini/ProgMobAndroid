package it.iacorickvale.progettoprogmob.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.adapters.AdminAdapter;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.utilities.Users;

public class FragmentAdmin extends Fragment {
    private RecyclerView recyclerView;
    private AdminAdapter adminAdapter;
    private ArrayList<Users> listUsers = new ArrayList<>();
    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        recyclerView = view.findViewById(R.id.rv_admin);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        // Name current user
        userId = DatabaseReferences.getUser().getUid();

        // Fill listUser with all the Users on firebase
        try {
            DatabaseReferences.getUsers()
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    // The current user's name does not appear in the list
                                    if(!document.getId().equals(userId)) {
                                        DatabaseReferences.getUserById(document.getId())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(!Objects.equals(document.get("admin"), "admin")) {
                                                                Users users = new Users(document.get("firstname").toString(), document.get("lastname").toString(), document.getId(), document.get("uri").toString());
                                                                listUsers.add(users);
                                                                adminAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    adminAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        adminAdapter = new AdminAdapter(getContext(), listUsers);
        recyclerView.setAdapter(adminAdapter);

        adminAdapter.notifyDataSetChanged();
        listUsers.clear();

        return view;
    }
}
