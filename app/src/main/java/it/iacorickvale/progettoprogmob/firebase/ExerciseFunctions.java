package it.iacorickvale.progettoprogmob.firebase;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

public class ExerciseFunctions {



    public static void createEx(String desc, String diff, final String name, String cal, String uri){
        final Map<String, String> ex = new HashMap<>();
        ex.put("description", desc);
        ex.put("difficulty", diff);
        ex.put("name", name);
        ex.put("cal", cal);
        if(!uri.equals("")){
            ex.put("uri", uri);
        }
        else {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference uriStorage = storageRef.child("/exercises/Default/default.mp4");
            uriStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("URI", uri.toString());
                    ex.put("uri", uri.toString());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("Esercizi")
                            .document(name)
                            .set(ex);
                }
            });
        }
        DatabaseReferences.getExercises()
                .document(name)
                .set(ex);
        Log.d("2)" ,"esercizio creato");
    }

    public static void deleteEx(String name){
        DatabaseReferences.getExById(name).delete();
        Log.d("1)" ,"esercizio eliminato");
    }

    public static void addExToCard(String ref, String path , Esercizi ex, String day){
        DatabaseReferences.getUsers()
                .document(ref)
                .collection(day)
                .document(path)
                .collection("ExSchede")
                .document(ex.getName())
                .set(ex);
    }

    public static void modifyEx(String Name, String Desc , String Diff, String Cal, String Uri){
        Esercizi e = new Esercizi(Name, Desc, Diff, Cal, Uri);
        DatabaseReferences.getExercises().document(Name).set(e);
    }

    public static void deleteExCard(String ref, String path, String name, String day){
        DatabaseReferences.listExCard(ref, path, day).document(name).delete();
    }

    public static void updateExCard(final String ID, final String name , final String desc , final String diff , final String cal, final String uri){
        //final ArrayList<String>days = new ArrayList<String>(Arrays.asList("MON", "TUE" ,"WED" , "THU" , "FRI" , "SAT"));
        final ArrayList<String>days = new ArrayList<String>(Arrays.asList("1", "2" ,"3" , "4" , "5" , "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18","19", "20", "21", "22", "23", "24", "25", "26", "27", "28"));
        DatabaseReferences.getUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(final String day : days){
                        for (final QueryDocumentSnapshot documentSnapshots : task.getResult()){
                            final String UserID = new String(documentSnapshots.getId());
                            Log.d("Utente per modifica", UserID);
                            DatabaseReferences.listCards(UserID, day).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task ) {
                                    if (task.isSuccessful()) {
                                        for (final QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                            final String CardID = new String(documentSnapshots.getId());
                                            Log.d("Scheda per modifica", CardID);
                                            DatabaseReferences.listExCard(UserID , CardID, day).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for (final QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                                            Log.d("Esercizio per modifica'",documentSnapshots.getId());
                                                            if (documentSnapshots.getId().equals(ID)){
                                                                Log.d("esercizio da aggiornare", documentSnapshots.getId());
                                                                deleteExCard(UserID , CardID , documentSnapshots.getId().toString(), day);
                                                                addExToCard(UserID , CardID , new Esercizi(desc, diff , name, cal, uri), day);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });
    }

    public static void deleteFromExCard(final String ID, final String name , final String desc , final String diff , final String cal, final String uri){
        //final ArrayList<String>days = new ArrayList<String>(Arrays.asList("MON", "TUE" ,"WED" , "THU" , "FRI" , "SAT"));
        final ArrayList<String>days = new ArrayList<String>(Arrays.asList("1", "2" ,"3" , "4" , "5" , "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18","19", "20", "21", "22", "23", "24", "25", "26", "27", "28"));
        DatabaseReferences.getUsers().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(final String day : days){
                        for (final QueryDocumentSnapshot documentSnapshots : task.getResult()){
                            final String UserID = new String(documentSnapshots.getId());
                            Log.d("Utente per modifica", UserID);
                            DatabaseReferences.listCards(UserID, day).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task ) {
                                    if (task.isSuccessful()) {
                                        for (final QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                            final String CardID = new String(documentSnapshots.getId());
                                            Log.d("Scheda per modifica", CardID);
                                            DatabaseReferences.listExCard(UserID , CardID, day).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for (final QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                                            Log.d("Esercizio per modifica'",documentSnapshots.getId());
                                                            if (documentSnapshots.getId().equals(ID)){
                                                                Log.d("esercizio da aggiornare", documentSnapshots.getId());
                                                                deleteExCard(UserID , CardID , documentSnapshots.getId().toString(), day);
                                                                //addExToCard(UserID , CardID , new Esercizi(desc, diff , name, cal, uri), day);
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });
    }

    public static boolean verifyName (final String name ) {
        final boolean[] aux = new boolean[1];
        aux[0] = false;
        DatabaseReferences.getExercises().get().addOnSuccessListener(
                new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            if (document.getId().equals(name)) {
                                aux[0] = true;
                                Log.d("esercizio uguale: ", document.get("name").toString());
                            } else {
                                Log.d("esercizio diverso2: ", document.get("name").toString());
                            }
                        }
                    }
                });
        return !aux[0];
    }

    public static int setImageEx(final String difficulty){
        int id = 0;
        switch(difficulty){
            case "Warming Up":
                id = R.drawable.warming_up;
                break;
            case "Arms":
                id = R.drawable.arms;
                break;
            case "Back":
                id = R.drawable.back;
                break;
                case "Sit Ups":
                id = R.drawable.sit_ups;
                break;
            case "Hiit and Special":
                id = R.drawable.hiit_and_special;
                break;
            default:
                id = 0;
                break;
        }
        return id;
    }

    public static ArrayList<Esercizi> orderListByDiff(ArrayList<Esercizi> listEsercizi) {
        ArrayList<Esercizi> w_up = new ArrayList<>();
        ArrayList<Esercizi> arms = new ArrayList<>();
        ArrayList<Esercizi> s_ups = new ArrayList<>();
        ArrayList<Esercizi> back = new ArrayList<>();
        ArrayList<Esercizi> h_s = new ArrayList<>();
        for (Esercizi e : listEsercizi) {
            switch (e.getDifficulty().toString()) {
                case "Warming Up":
                    w_up.add(e);
                    break;
                case "Arms":
                    arms.add(e);
                    break;
                case "Back":
                    back.add(e);
                    break;
                case "Sit Ups":
                    s_ups.add(e);
                    break;
                case "Hiit and Special":
                    h_s.add(e);
                    break;
                default:
                    break;
            }
        }
            listEsercizi.clear();
            listEsercizi.addAll(w_up);
            listEsercizi.addAll(arms);
            listEsercizi.addAll(s_ups);
            listEsercizi.addAll(back);
            listEsercizi.addAll(h_s);
        return listEsercizi;
    }

}
