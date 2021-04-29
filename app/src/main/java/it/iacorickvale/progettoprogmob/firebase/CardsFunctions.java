package it.iacorickvale.progettoprogmob.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import it.iacorickvale.progettoprogmob.utilities.Cards;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

public class CardsFunctions {



    public static void deleteCard(String ref, String path, String newDay){
        FirebaseFirestore.getInstance().collection("users")
                .document(ref)
                .collection(newDay).document(path).delete();
    }

    public static void modifyCard(final String ref , final String path, final String newName, final String type, final String currentDay){
        CardsFunctions.createCard(newName, type, ref, currentDay);
        String ID = DatabaseReferences.getCard(ref,path, currentDay).getId();
        DatabaseReferences.listExCard(ref, path, currentDay).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for ( QueryDocumentSnapshot documentSnapshots : task.getResult()){
                        Esercizi e = new Esercizi(documentSnapshots.get("description").toString(), documentSnapshots.get("difficulty").toString(), documentSnapshots.get("name").toString(), documentSnapshots.get("cal").toString(), documentSnapshots.get("uri").toString());
                        ExerciseFunctions.addExToCard(ref,newName,e,currentDay);
                    }
                }
            }
        });
       DatabaseReferences.getCard(ref,path,currentDay).delete();
    }

    public static void createCard(String path , String type , String ref, String currentDay) {
        DatabaseReferences.listCards(ref , currentDay).document(path).set(new Cards(path , ref , type));
    }

    public static Boolean controlTypeDay( final String ref, final String currentDay, final String type) {
        final Boolean[] ret = new Boolean[1];
            ret[0] = false;
        DatabaseReferences.listCards(ref, currentDay).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.get("type").toString().equals(type)) {
                                Log.d("TRUE", document.get("type").toString());
                                ret[0] = true;
                            }else{
                                Log.d("UN" , " C");
                            }
                        }
                    }
                }
            }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ret[0] = ret[0];
                }
        });
        if(ret[0]){
            Log.d("RET" , " VALE");
            return ret[0];
        }else{
            Log.d("RET" , "NON VALE");
            return false;
        }
    }
}
