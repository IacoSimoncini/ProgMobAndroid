package it.iacorickvale.progettoprogmob.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import it.iacorickvale.progettoprogmob.MainActivity;
import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.adapters.CalendaryAdapter;
import it.iacorickvale.progettoprogmob.adapters.CardsAdapter;
import it.iacorickvale.progettoprogmob.firebase.CardsFunctions;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.utilities.Cards;

import static it.iacorickvale.progettoprogmob.firebase.CardsFunctions.controlTypeDay;
import static it.iacorickvale.progettoprogmob.firebase.CardsFunctions.createCard;

public class FragmentCalendary extends Fragment {
    private RecyclerView recyclerView;
    private Button changeType;
    private CalendaryAdapter calendaryAdapter;
    private ArrayList<Integer> days = new ArrayList<Integer>();
    private ArrayList<Cards> struttura = new ArrayList<Cards>();
    private String controlIfAd;
    private String uid;
    private String type;
    private int nday = 0;
    private Boolean[] ifset = new Boolean[28];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_calendary, container, false);
        controlIfAd = this.getArguments().getString("type");
        if( controlIfAd.equals("admin")){ uid = this.getArguments().getString("u_id"); }
        else{
            uid = DatabaseReferences.getUser().getUid();
            Log.d("TAG" , uid);
        }

        type = this.getArguments().getString("ABC");
        days = fillDays(days, nday);

        for(final Integer d : days){
            DatabaseReferences.listCards(uid, String.valueOf(d+1)).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ATTUALE CICLO " + String.valueOf(d+1) , " document: " + document.getData());
                                if(document.get("type").toString().equals(type)) {
                                    ifset[d] = true;
                                    break;
                                }else{
                                    ifset[d] = false;
                                }
                            }
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        calendaryAdapter.notifyDataSetChanged();
                    }
                });
            }
        changeType = view.findViewById(R.id.changeABC);
        recyclerView = view.findViewById(R.id.rv_calendary);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext() , 7));
        calendaryAdapter = new CalendaryAdapter(getContext(), type , ifset , this.getArguments().getString("type"), uid) ;
        recyclerView.setAdapter(calendaryAdapter);



        changeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View dialogView = inflater.inflate(R.layout.switch_type, null);
                builder.setTitle("CHOOSE TYPE");
                final Bundle args = new Bundle();
                args.putString("type", controlIfAd);
                args.putString("u_id", uid);
                args.putString("ABC" , type);
                final FragmentCalendary fragmentCalendary = new FragmentCalendary();
                final Button A = dialogView.findViewById(R.id.A);
                final Button B = dialogView.findViewById(R.id.B);
                final Button C = dialogView.findViewById(R.id.C);
                A.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                A.setBackground(ResourcesCompat.getDrawable(v.getResources(), R.drawable.whiteline_background, null));
                                B.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                                C.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                                args.putString("ABC", "A");
                            }
                        }
                );
                B.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                A.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                                B.setBackground(ResourcesCompat.getDrawable(v.getResources(), R.drawable.whiteline_background, null));
                                C.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                                args.putString("ABC", "B");
                            }
                        }
                );
                C.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                A.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                                B.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                                C.setBackground(ResourcesCompat.getDrawable(v.getResources(), R.drawable.whiteline_background, null));
                                args.putString("ABC", "C");
                            }
                        }
                );
                builder.setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    fragmentCalendary.setArguments(args);
                                    FragmentManager fm = (getActivity()).getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.fragment_container, fragmentCalendary);
                                    ft.commit();
                                }catch (Exception e){
                                    Toast.makeText(getContext().getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                builder.setNegativeButton("delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.setView(dialogView);
                builder.show();
            }
        });
        return view;
    }

    public ArrayList<Integer> fillDays(ArrayList<Integer> day , Integer nday){
        nday = 0;
        day.clear();
        while(nday < 28){
            day.add(nday);
            nday += 1;
        }
        return day;
    }


}
