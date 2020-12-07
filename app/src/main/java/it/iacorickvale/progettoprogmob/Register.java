package it.iacorickvale.progettoprogmob;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText mfirstname, mlastname, mEmail, mPassword, mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    Window window;
    private ActionBar actionBar;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //COLORE ACTION BAR IN ALTO
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01B6A5")));

        //COLORE DELLA STATUS BAR IN ALTO
        if(Build.VERSION.SDK_INT>=21){
            window=this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.green));
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        mfirstname = findViewById(R.id.firstname);
        mlastname = findViewById(R.id.lastname);
        mPassword = findViewById(R.id.password);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mRegisterBtn = findViewById(R.id.btnRegister);
        mLoginBtn = findViewById(R.id.createText);

        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String firstname = mfirstname.getText().toString().trim();
                final String lastname = mlastname.getText().toString().trim();
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    mPassword.setError("Password must be >= 6 characters");
                    return;
                }
                if(password.length() < 6 ) {
                    mPassword.setError("Password must be >= 6 characters");
                    return;
                }
                if(firstname.length() > 15 ) {
                    mPassword.setError("Name must be <= 15 characters");
                    return;
                }
                if(lastname.length() > 20 ) {
                    mPassword.setError("Surname must be <= 20 characters");
                    return;
                }
                if(email.length() > 40 ) {
                    mPassword.setError("Email must be <= 40 characters");
                    return;
                }

                //Register authentication in firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            final FirebaseUser user = fAuth.getCurrentUser();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mfirstname + " " + mlastname)
                                    .build();
                            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    writeUserToDb(firstname, lastname, phone, email, user.getUid());
                                    Intent intent = new Intent();
                                    intent.putExtra("firstname", mfirstname.getText().toString());
                                    intent.putExtra("lastname", mlastname.getText().toString());
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }

    private void writeUserToDb(String firstname, String lastname, String phone, String email, final String uid) {
        final Map<String, Object> user = new HashMap<>();
        user.put("firstname", firstname);
        user.put("lastname", lastname);
        user.put("phone", phone);
        user.put("email", email);
        user.put("admin", "noadmin");
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference uriStorage = storageRef.child("/users/Default/default.png");
        uriStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("URI", uri.toString());
                user.put("uri", uri.toString());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .document(uid)
                        .set(user);
            }
        });



    }

}
