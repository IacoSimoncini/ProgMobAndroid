package it.iacorickvale.progettoprogmob.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.adapters.AdapterAll;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.firebase.ExerciseFunctions;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

public class FragmentAll extends Fragment {

    private RecyclerView recyclerView;
    private AdapterAll adapterAll;
    private ArrayList<Esercizi> listEsercizi = new ArrayList<>();
    private ImageButton btnAdd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all, container, false);

        recyclerView = view.findViewById(R.id.rv_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        adapterAll = new AdapterAll(getContext(), listEsercizi);
        recyclerView.setAdapter(adapterAll);

        try {
            DatabaseReferences.getExercises()
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    DatabaseReferences.getExById(document.getId())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    String name = new String(document.get("Nome").toString());
                                                    String desc = new String(document.get("Descrizione").toString());
                                                    String diff = new String(document.get("Difficoltà").toString());
                                                    listEsercizi.add(new Esercizi(desc, diff, name));
                                                    adapterAll.notifyItemInserted(listEsercizi.size());
                                                }
                                            });
                                }
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    adapterAll.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        btnAdd = view.findViewById(R.id.button_add);
        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creation of a new exercise in the database
                try {
                    // Creation of AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setTitle("Enter exercise name");
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_ex, null);

                    builder.setView(dialogView);

                    final EditText name_ex = dialogView.findViewById(R.id.name_ex);
                    final EditText desc_ex = dialogView.findViewById(R.id.desc_ex);
                    final Spinner diff_ex =  (Spinner) dialogView.findViewById(R.id.diff_ex);

                    // Affirmative case
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if( name_ex.getText().toString().equals("")||
                                desc_ex.getText().toString().equals("")||
                                diff_ex.getSelectedItem().toString().equals("")
                            ) {
                                Toast.makeText(getContext(), "Impossible to Create:" + "\nNo empty spaces", Toast.LENGTH_SHORT).show();
                            }
                            else if(name_ex.getText().toString().length() > 15)
                            {
                                Toast.makeText(getContext(), "Impossible to Create:" + "\nName must be <=15", Toast.LENGTH_SHORT).show();
                            }

                            else if(desc_ex.getText().toString().length() > 50)
                            {
                                Toast.makeText(getContext(), "Impossible to Create:" + "\nDescription must be <=50", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                boolean aux = false;
                                for (Esercizi e : listEsercizi) {
                                    if (e.getName().equals(name_ex.getText().toString())) {
                                        aux = true;
                                    }
                                }
                                if (!aux) {
                                    try {
                                        // Write to db the new exercise
                                        ExerciseFunctions.createEx(desc_ex.getText().toString(),  diff_ex.getSelectedItem().toString() ,name_ex.getText().toString());
                                        Toast.makeText(getContext(), "The exercise has been created", Toast.LENGTH_SHORT).show();
                                        listEsercizi.add(new Esercizi (desc_ex.getText().toString() , diff_ex.getSelectedItem().toString(), name_ex.getText().toString())) ;
                                        adapterAll.notifyItemInserted(listEsercizi.size());
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                     Toast.makeText(getContext(), "Warning: Exercise's name match " + name_ex.getText().toString() + ", change it!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    // Negative case
                    builder.setNegativeButton("Negate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close dialog
                        }
                    });
                    builder.create().show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapterAll.notifyDataSetChanged();
        listEsercizi.clear();

        return view;
    }
}
