package it.iacorickvale.progettoprogmob.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.adapters.EserciziAdapter;
import it.iacorickvale.progettoprogmob.firebase.ExerciseFunctions;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

import static it.iacorickvale.progettoprogmob.firebase.DatabaseReferences.listExCard;


public class FragmentEsercizi extends Fragment {
    private String path;
    private String ref;
    private RecyclerView recyclerView;
    private EserciziAdapter EserciziAdapter;
    private ArrayList<Esercizi> listEserciziScheda = new ArrayList<>();
    private ArrayList<Esercizi> listEserciziAdd = new ArrayList<>();
    private ImageButton btnAdd;
    private String[] allEsercizi;
    private List<String> exList;
    private boolean isAdmin;
    private String admin;
    private String currentDay;
    private Boolean whichSet;
    private int index = 0;
    private ArrayList<String> days = new ArrayList<String>();
    private Integer ndays = 0;
    boolean[] checkedItemsArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_esercizi, container, false);

        recyclerView = view.findViewById(R.id.rv_esercizi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        assert this.getArguments() != null;
        path = this.getArguments().getString("path");
        ref = this.getArguments().getString("ref");
        admin = this.getArguments().getString("type");
        btnAdd = view.findViewById(R.id.button_add);
        whichSet = (this.getArguments().getString("currentDay") != null);
        isAdmin = admin.equals("admin");

        if(isAdmin){
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setVisibility(View.INVISIBLE);
        }

        if(whichSet) {
            currentDay= this.getArguments().getString("currentDay");
            try {
                listExCard(ref, path, currentDay).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (final QueryDocumentSnapshot document : task.getResult()) {
                                        Map mp = new HashMap();
                                        mp = document.getData();
                                        Esercizi ex = new Esercizi(mp.get("description").toString(),
                                                mp.get("difficulty").toString(),
                                                mp.get("name").toString(),
                                                mp.get("cal").toString(),
                                                mp.get("uri").toString()

                                        );
                                        listEserciziScheda.add(ex);
                                        EserciziAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        EserciziAdapter.notifyDataSetChanged();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            days = fillDays(days, ndays);
            for(String s : days) {
                try {
                    listExCard(ref, path, currentDay).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                            Map mp = new HashMap();
                                            mp = document.getData();
                                            Esercizi ex = new Esercizi(mp.get("description").toString(),
                                                    mp.get("difficulty").toString(),
                                                    mp.get("name").toString(),
                                                    mp.get("cal").toString(),
                                                    mp.get("uri").toString()
                                            );
                                            listEserciziScheda.add(ex);
                                            EserciziAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            EserciziAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        EserciziAdapter = new EserciziAdapter(getContext(), listEserciziScheda, path, ref, isAdmin, currentDay);
        recyclerView.setAdapter(EserciziAdapter);

        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Esercizi")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection("Esercizi")
                                            .document(document.getId())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Esercizi ex = new Esercizi(document.get("description").toString(),
                                                            document.get("difficulty").toString(),
                                                            document.get("name").toString(),
                                                            document.get("cal").toString(),
                                                            document.get("uri").toString()
                                                    );
                                                    boolean aux = false;
                                                        listEserciziAdd.add(ex);
                                                        EserciziAdapter.notifyDataSetChanged();
                                                }
                                            });
                                }
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    EserciziAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        EserciziAdapter.notifyDataSetChanged();

        btnAdd.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                    allEsercizi = new String[listEserciziAdd.size()];
                    index = 0;
                    for (Esercizi e : listEserciziAdd) {
                        allEsercizi[index] = e.getName();
                        index++;
                    }
                    checkedItemsArray = new boolean[allEsercizi.length];
                    exList = Arrays.asList(allEsercizi);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Choose Exercises to add");
                    builder.setView(inflater.inflate(R.layout.dialog_list, null));
                    builder.setMultiChoiceItems(allEsercizi, checkedItemsArray, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            checkedItemsArray[which] = isChecked;
                            String currentItem = exList.get(which);
                            builder.setCancelable(false);
                            builder.setTitle("Choose Exercises to add");

                        }

                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < checkedItemsArray.length; i++) {
                                boolean checked = checkedItemsArray[i];
                                if (checked) {
                                    for (final Esercizi ex : listEserciziAdd) {
                                        if (ex.getName().equals(allEsercizi[i])) {
                                            boolean aux = false;
                                            for (Esercizi e : listEserciziScheda) {
                                                if (e.getName().equals(ex.getName())) {
                                                    aux = true;
                                                }
                                            }
                                            if(!aux){
                                                try {
                                                    ExerciseFunctions.addExToCard(ref, path, ex, currentDay );
                                                    listEserciziScheda.add(ex);
                                                    EserciziAdapter.notifyDataSetChanged();
                                                } catch (Exception e) {
                                                    Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(getContext(), "This Exercise already exists", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close dialog window
                        }
                    });
                    builder.create().show();
                }
        });

        EserciziAdapter.notifyDataSetChanged();
        listEserciziScheda.clear();
        listEserciziAdd.clear();
        return view;
    }

    public ArrayList<String> fillDays(ArrayList<String> day , Integer nday){
        nday = 0;
        day.clear();
        while(nday < 28){
            nday += 1;
            day.add(nday.toString());

        }
        return day;
    }


}