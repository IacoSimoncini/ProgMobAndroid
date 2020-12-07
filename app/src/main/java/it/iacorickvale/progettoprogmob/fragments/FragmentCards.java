package it.iacorickvale.progettoprogmob.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private ImageButton btnAdd;
    private Boolean aux;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        String docref_user = null;
        String current_uid = null;
        assert this.getArguments() != null;
        String control = this.getArguments().getString("type");
        if(control.equals("admin")){ current_uid = this.getArguments().getString("u_id"); }

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_cards);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        // Fill listCards with collections path
        try {
            if(control.equals("admin")) {
                docref_user = new String ( current_uid);
                aux = true;
            }else{
                final FirebaseUser user = DatabaseReferences.getUser();
                docref_user = new String(user.getUid());
                aux = false;
            }
            final String doc = docref_user;
            DatabaseReferences.listCards(doc)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Cards card = new Cards(document.getId(), doc);;
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
        }catch(Exception e){
            Toast.makeText(getContext(), "Cards need Name", Toast.LENGTH_SHORT).show();
        }

        cardsAdapter = new CardsAdapter(getContext(), listCards , aux);
        recyclerView.setAdapter(cardsAdapter);

        if(control.equals("admin")) {

            btnAdd = view.findViewById(R.id.button_add);
            btnAdd.setVisibility(View.VISIBLE);
            final String finalDocref_user = docref_user;
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Creation of a new card in the database
                    try {
                        final EditText name = new EditText(getContext());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Enter card name");
                        builder.setView(name);

                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(name.getText().toString().length() > 30)
                                {
                                    Toast.makeText(getContext(), "Impossible to Create:" + "\nCard's Name must be <=18", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                try {
                                    boolean aux = false;
                                    for(Cards c : listCards){
                                        if(c.getPath().equals(name.getText().toString())){
                                            aux = true;
                                        }
                                    }
                                    if(!aux) {
                                        Log.d("1)"+name.getText().toString(), "2)" +finalDocref_user);
                                        Cards card = new Cards(name.getText().toString(), finalDocref_user);
                                        CardsFunctions.createCard(card, finalDocref_user);
                                        Toast.makeText(getContext(), "Your card has been created", Toast.LENGTH_SHORT).show();
                                        listCards.add(card);
                                        cardsAdapter.notifyDataSetChanged();
                                    }else { Toast.makeText(getContext(), "This card already exists", Toast.LENGTH_SHORT).show();}
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                }
                            }
                        });
                        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Close dialog
                            }
                        });

                        builder.create().show();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    cardsAdapter.notifyDataSetChanged();
                }
            });
        } else {
            btnAdd = view.findViewById(R.id.button_add);
            btnAdd.setVisibility(View.GONE);
        }

        listCards.clear();
        return view;
    }

}
