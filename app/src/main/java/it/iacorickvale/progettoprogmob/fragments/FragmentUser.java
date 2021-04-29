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
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;

import static android.app.Activity.RESULT_CANCELED;
import static it.iacorickvale.progettoprogmob.fragments.FragmentAll.isDoubleOrInt;

public class FragmentUser extends Fragment {

    private TextView fullName, email, phone;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;
    private ImageView profileImage;
    private StorageReference storageReference;
    private Button modify;
    private String mod_name;
    private String mod_surname;
    private String mod_phone;
    private String mod_uri;
    private String mod_totcal;
    private String mod_goal;
    private String mod_admin;
    private String mod_email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        fullName = view.findViewById(R.id.user_mfullname);
        email = view.findViewById(R.id.user_memail);
        phone = view.findViewById(R.id.user_mphone);
        modify = view.findViewById(R.id.mod_btn);

        profileImage = view.findViewById(R.id.user_image);

        mod_name = this.getArguments().getString("firstname");
        mod_surname = this.getArguments().getString("lastname");
        mod_phone = this.getArguments().getString("phone");
        mod_admin = this.getArguments().getString("admin");
        mod_uri = this.getArguments().getString("uri");
        mod_email = this.getArguments().getString("email");
        mod_goal = this.getArguments().getString("goal");
        mod_totcal = this.getArguments().getString("tot_cal");
        Log.d("TOTCAL", mod_totcal);
        Log.d("GOAL", mod_goal);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        try {
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
                fStore.collection("users")
                        .document(userId)
                        .update("uri", uri.toString());
            }
        });
        } catch (Exception e){
            Log.d("Do nothing", "");
        }

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference docRef = fStore.collection("users").document(userId);
        try {
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot != null ? documentSnapshot.exists() : false){
                        phone.setText(documentSnapshot.getString("phone"));
                        fullName.setText(documentSnapshot.getString("firstname") + " " + documentSnapshot.getString("lastname"));
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

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Update your data");
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_user, null);

                    builder.setView(dialogView);

                    final EditText name = dialogView.findViewById(R.id.name_user);
                    final EditText surname = dialogView.findViewById(R.id.surname_user);
                    final EditText phone = dialogView.findViewById(R.id.phone_user);

                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if( name.getText().toString().equals("")||
                                    surname.getText().toString().equals("")||
                                    phone.getText().toString().equals("")

                            ) {
                                Toast.makeText(getContext(), "Impossible to Modify:" + "\nNo empty spaces", Toast.LENGTH_SHORT).show();
                            }
                            else if(name.getText().toString().length() > 20)
                            {
                                Toast.makeText(getContext(), "Impossible to Create:" + "\nName must be <=20", Toast.LENGTH_SHORT).show();
                            }

                            else if(surname.getText().toString().length() > 25)
                            {
                                Toast.makeText(getContext(), "Impossible to Create:" + "\nSurname must be <=25", Toast.LENGTH_SHORT).show();
                            }

                            else if(phone.getText().toString().length() < 8)
                            {
                                Toast.makeText(getContext(), "Impossible to Create:" + "\nPhone is wrong, must be >= 8", Toast.LENGTH_SHORT).show();
                            }

                            else if(isDoubleOrInt(phone.getText().toString())== -1)
                            {
                                Toast.makeText(getContext(), "Impossible to Create:" + "\nPhone must be an int", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.d("mod_name:", mod_name);
                                Log.d("name", name.getText().toString());
                                Log.d("mod_surname", mod_surname);
                                Log.d("surname", surname.getText().toString());
                                Log.d("mod_phone", mod_phone);
                                Log.d("phone", phone.getText().toString());
                                Log.d("tot_cal", mod_totcal);
                                Log.d("goal", mod_goal);

                                final Map<String, Object> mp = new HashMap<>();
                                    mp.put("firstname", name.getText().toString());
                                    mp.put("lastname", surname.getText().toString());
                                    mp.put("phone", phone.getText().toString());
                                    mp.put("uri", mod_uri);
                                    mp.put("email", mod_email);
                                    mp.put("admin", mod_admin);
                                    mp.put("goal", Integer.parseInt(mod_goal));
                                    mp.put("tot_cal" , Integer.parseInt(mod_totcal));

                                    DatabaseReferences.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(mp);
                                    Toast.makeText(getContext(), "Update Done", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNegativeButton("Delete",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();

                    } catch (Exception e){
                    Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {
        if(requestCode == 1000) {
            try{
                assert data != null;
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            } catch(Exception e){
                Toast.makeText(getContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }

        }
        }else{
            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
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
