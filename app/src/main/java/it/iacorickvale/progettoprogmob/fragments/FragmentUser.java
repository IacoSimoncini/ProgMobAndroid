package it.iacorickvale.progettoprogmob.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.CardsFunctions;
import it.iacorickvale.progettoprogmob.utilities.Cards;

import static it.iacorickvale.progettoprogmob.firebase.UsersFunctions.modifyUserEmail;
import static it.iacorickvale.progettoprogmob.firebase.UsersFunctions.modifyUserName;
import static it.iacorickvale.progettoprogmob.firebase.UsersFunctions.modifyUserPhone;
import static it.iacorickvale.progettoprogmob.firebase.UsersFunctions.modifyUserSurname;

public class FragmentUser extends Fragment {

    private TextView firstName, lastName, email, phone;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private ImageView profileImage;
    private StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        firstName = view.findViewById(R.id.user_mfirstname);
        lastName = view.findViewById(R.id.user_mlastname);
        email = view.findViewById(R.id.user_memail);
        phone = view.findViewById(R.id.user_mphone);
        profileImage = view.findViewById(R.id.user_image);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
                fStore.collection("users")
                        .document(userId)
                        .update("uri", uri.toString());
            }
        });

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference docRef = fStore.collection("users").document(userId);
        try {
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot != null ? documentSnapshot.exists() : false){
                        phone.setText(documentSnapshot.getString("phone"));
                        firstName.setText(documentSnapshot.getString("firstname"));
                        lastName.setText(documentSnapshot.getString("lastname"));
                        email.setText(documentSnapshot.getString("email"));
                    } else {
                        Log.d("tag", "onEvent: Document do not exists");
                    }
                }
            });
        } catch (Exception e){
            Log.d("Do nothing", "");
        }

        profileImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });


        firstName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Change Name");
                final EditText name = new EditText(v.getContext());
                alertDialog.setView(name);
                alertDialog.setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if( name.getText().toString().equals("")){
                                    Toast.makeText(getContext(), "Impossible to Update:" + "\nInsert new name", Toast.LENGTH_SHORT).show();
                                }
                                else if( name.getText().toString().length() > 20)
                                {
                                    Toast.makeText(getContext(), "Impossible to Update:" + "\nName must be <20", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String newName = name.getText().toString();
                                    modifyUserName(userId,newName);
                                }
                            }
                        }
                );
                alertDialog.setNegativeButton("delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();
            }
        });

        lastName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Change Surname");
                final EditText name = new EditText(v.getContext());
                alertDialog.setView(name);
                alertDialog.setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if( name.getText().toString().equals("")){
                                    Toast.makeText(getContext(), "Impossible to Update:" + "\nInsert new name", Toast.LENGTH_SHORT).show();
                                }
                                else if( name.getText().toString().length() > 20)
                                {
                                    Toast.makeText(getContext(), "Impossible to Update:" + "\nSurname must be <20", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String newName = name.getText().toString();
                                    modifyUserSurname(userId,newName);
                                }
                            }
                        }
                );
                alertDialog.setNegativeButton("delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();
            }
        });
        phone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Change Number");
                final EditText name = new EditText(v.getContext());
                alertDialog.setView(name);
                alertDialog.setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if( name.getText().toString().equals("")){
                                    Toast.makeText(getContext(), "Impossible to Update:" + "\nInsert new Phone Number", Toast.LENGTH_SHORT).show();
                                }
                                else if( name.getText().toString().length() > 20)
                                {
                                    Toast.makeText(getContext(), "Impossible to Update:" + "\nNumber must be <20", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String newName = name.getText().toString();
                                    modifyUserPhone(userId,newName);
                                }
                            }
                        }
                );
                alertDialog.setNegativeButton("delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();
            }
        });
        email.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Change email");
                final EditText name = new EditText(v.getContext());
                alertDialog.setView(name);
                alertDialog.setPositiveButton("confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if( name.getText().toString().equals("")){
                                    Toast.makeText(getContext(), "Impossible to Update:" + "\nInsert new email", Toast.LENGTH_SHORT).show();
                                }
                                else if( name.getText().toString().length() > 20)
                                {
                                    Toast.makeText(getContext(), "Impossible to Update:" + "\nemail must be <20", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String newName = name.getText().toString();
                                    modifyUserEmail(userId,newName);
                                }
                            }
                        }
                );
                alertDialog.setNegativeButton("delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            try{
                assert data != null;
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            } catch(Exception e){
                Toast.makeText(getContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void uploadImageToFirebase(Uri imageUri) {
        // upload image to firebase storage
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
