package it.iacorickvale.progettoprogmob.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseReferences {

    public static FirebaseUser getUser(){
       return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static DocumentReference getUserById(String ID){
        return FirebaseFirestore.getInstance().collection("users")
                .document(ID);
    }

    public static CollectionReference getUsers(){
        return FirebaseFirestore.getInstance().collection("users");
    }


    public static DocumentReference getExById(String ID){
        return FirebaseFirestore.getInstance().collection("Esercizi")
                .document(ID);
    }


    public static CollectionReference getExercises(){
        return FirebaseFirestore.getInstance().collection("Esercizi");
    }

    public static CollectionReference listCards(String doc){
        return FirebaseFirestore.getInstance().collection("users")
                .document(doc).collection("Schede");
    }

    public static DocumentReference getCard(String ref, String path){
        return FirebaseFirestore.getInstance().collection("users")
                .document(ref)
                .collection("Schede")
                .document(path);
    }

    public static CollectionReference listExCard(String ref , String path){
        return  FirebaseFirestore.getInstance().collection("users")
                .document(ref)
                .collection("Schede")
                .document(path)
                .collection("ExSchede");
    }


}
