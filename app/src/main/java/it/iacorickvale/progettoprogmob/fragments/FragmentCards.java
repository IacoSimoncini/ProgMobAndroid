package it.iacorickvale.progettoprogmob.fragments;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.adapters.CardsAdapter;
import it.iacorickvale.progettoprogmob.firebase.CardsFunctions;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.utilities.Cards;

public class FragmentCards extends Fragment  {
    
    private RecyclerView recyclerView;
    private CardsAdapter cardsAdapter;
    private ArrayList<Cards> listCards = new ArrayList<Cards>();
    private ArrayList<String> days = new ArrayList<String>();
    private Integer ndays = 0;
    private ImageButton btnAdd;
    private Boolean aux;
    private RecyclerView recyclerViewCalendary;
    private String currentDay;
    private String current_uid;
    private Boolean whichSet;
    private String ABC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_cards, container, false);
        String docref_user = null;
        assert this.getArguments() != null;
        ABC =  this.getArguments().getString("ABC");
        String control = this.getArguments().getString("type");
        if(control.equals("admin")){ current_uid = this.getArguments().getString("u_id"); }
        if(!this.getArguments().getString("SelectedDay").equals("all")){
            currentDay = this.getArguments().getString("SelectedDay");
            whichSet = true;
        }else{
            whichSet = false;
        }
        recyclerView = view.findViewById(R.id.rv_cards);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        days = fillDays(days, ndays);
        // Fill listCards with collections path
        try {
            if (control.equals("admin")) {
                docref_user = new String(this.getArguments().getString("u_id"));
                aux = true;
            } else {
                docref_user = new String(DatabaseReferences.getUser().getUid());
                aux = false;
            }
            final String doc = docref_user;
            Log.d("NON ADMIN" , doc);
            if(whichSet){
                DatabaseReferences.listCards(doc, currentDay)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(ABC == null) {
                                            Cards card = new Cards(document.getId(), doc, document.get("type").toString());
                                            listCards.add(card);
                                        }else{
                                            if (ABC.equals(document.get("type").toString())){
                                                Log.d("IN QUESTO CAZZO DI CASO", document.get("type").toString());
                                                Cards card = new Cards(document.getId(), doc, document.get("type").toString());
                                                listCards.add(card);
                                            };
                                        }
                                    }
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        cardsAdapter.notifyDataSetChanged();
                    }
                });
            }else{
                for (String d : days){
                    DatabaseReferences.listCards(doc, d)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Cards card = new Cards(document.getId(), doc, document.get("type").toString());
                                            listCards.add(card);
                                        }
                                    }
                                }
                            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            cardsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        } catch (Exception e) {

    }

        cardsAdapter = new CardsAdapter(getContext(), listCards, aux, currentDay);
        recyclerView.setAdapter(cardsAdapter);

        listCards.clear();
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

