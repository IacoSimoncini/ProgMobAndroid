package it.iacorickvale.progettoprogmob.firebase;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.opencensus.common.ServerStatsFieldEnums;
import it.iacorickvale.progettoprogmob.adapters.CardsAdapter;
import it.iacorickvale.progettoprogmob.utilities.Cards;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

public class CardsFunctions {

    private FirebaseFirestore db;// = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth;// = FirebaseAuth.getInstance();

    public static void deleteCard(String ref, String path){
        FirebaseFirestore.getInstance().collection("users")
                .document(ref)
                .collection("Schede").document(path).delete();
    }

    public static void modifyCard(final String ref , final String path, final String newName){
        CardsFunctions.createCard(new Cards(newName , ref) ,ref);
        String ID = DatabaseReferences.getCard(ref,path).getId();
        DatabaseReferences.listExCard(ref, path).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for ( QueryDocumentSnapshot documentSnapshots : task.getResult()){
                        Esercizi e = new Esercizi(documentSnapshots.get("description").toString(), documentSnapshots.get("difficulty").toString(), documentSnapshots.get("name").toString());
                        ExerciseFunctions.addExToCard(ref,newName,e);
                    }
                }
            }
        });
       DatabaseReferences.getCard(ref,path).delete();
        }

    public static void createCard(Cards card , String ref) {
        String name = card.getPath();
        DatabaseReferences.listCards(ref).document(name).set(card);
        }



}
