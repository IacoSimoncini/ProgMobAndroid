package it.iacorickvale.progettoprogmob.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersFunctions {

    public static void deleteUser(String ref){
        FirebaseFirestore.getInstance().collection("users")
                .document(ref).delete();
        FirebaseAuth.getInstance().getCurrentUser().delete();
    }

    public static void modifyUserName(String ref, String newName){
        FirebaseFirestore.getInstance().collection("users")
                .document(ref).update("firstname" , newName);
    }
    public static void modifyUserSurname(String ref, String newSurname){
        FirebaseFirestore.getInstance().collection("users")
                .document(ref).update("lastname" , newSurname);
    }
    public static void modifyUserEmail(String ref, String newEmail){
        FirebaseFirestore.getInstance().collection("users")
                .document(ref).update("email" , newEmail);
        FirebaseAuth.getInstance().getCurrentUser().updateEmail(newEmail);
    }
    public static void modifyUserPhone(String ref, String newPhone){
        FirebaseFirestore.getInstance().collection("users")
                .document(ref).update("phone" , newPhone);
    }
}

