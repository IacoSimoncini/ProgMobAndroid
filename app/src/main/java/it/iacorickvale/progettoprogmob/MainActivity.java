package it.iacorickvale.progettoprogmob;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.iacorickvale.progettoprogmob.fragments.FragmentAdmin;
import it.iacorickvale.progettoprogmob.fragments.FragmentAll;
import it.iacorickvale.progettoprogmob.fragments.FragmentCalendary;
import it.iacorickvale.progettoprogmob.fragments.FragmentCards;
import it.iacorickvale.progettoprogmob.fragments.FragmentInfo;
import it.iacorickvale.progettoprogmob.fragments.FragmentUser;

public class MainActivity extends AppCompatActivity {
    Window window;
    private ActionBar actionBar;

    private FragmentInfo fragmentInfo;
    private FragmentUser fragmentUser;
    private FragmentCards fragmentCards;
    private FragmentAdmin fragmentAdmin;
    private FragmentAll fragmentAll;
    private FragmentCalendary fragmentCalendary;
    private BottomNavigationView bottomNav;
    private Context context;
    private FirebaseAuth fAuth;
    private String userId;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        //COLORE ACTION BAR
        actionBar=getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01b6a5")));

        //COLORE STATUS BAR
        if(Build.VERSION.SDK_INT>=21){
            window=this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
            //window.setStatusBarColor(this.getResources().getColor(R.color.primaryDark));
        }

        fragmentCards = new FragmentCards();
        fragmentUser = new FragmentUser();
        fragmentInfo = new FragmentInfo();
        fragmentAdmin = new FragmentAdmin();
        fragmentAll = new FragmentAll();
        fragmentCalendary = new FragmentCalendary();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();




        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task ) {
                if(task.isComplete()){
                    final String user_type = String.valueOf(task.getResult().get("admin"));
                    final String user_goal = String.valueOf(task.getResult().get("tot_cal"));
                    final String user_name = String.valueOf(task.getResult().get("firstname"));
                    final String user_lastname = String.valueOf(task.getResult().get("lastname"));
                    final String user_phone = String.valueOf(task.getResult().get("phone"));
                    final String user_uri = String.valueOf(task.getResult().get("uri"));
                    final String user_real_goal = String.valueOf(task.getResult().get("goal"));
                    final String user_email = String.valueOf(task.getResult().get("email"));

                    if((user_type.equals("admin"))){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentAdmin).commit();
                        bottomNav = findViewById(R.id.bottom_nav);
                        bottomNav.inflateMenu(R.menu.bottom_menu_admin);
                        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                Fragment selectedFragment = null;
                                switch(item.getItemId()) {
                                    case R.id.menu_cards:
                                        selectedFragment = fragmentAll;
                                        break;
                                    case R.id.menu_admin:
                                        selectedFragment = fragmentAdmin;
                                        break;
                                }
                                if(selectedFragment != null) {
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
                                }
                                return true;
                            }
                        });
                    } else {
                        Bundle args = new Bundle();
                        args.putString("type", "noadmin");
                        args.putString("ABC", "A");
                        args.putString("SelectedDay", "all");
                        fragmentCalendary.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentCalendary).commit();
                        bottomNav = findViewById(R.id.bottom_nav);
                        bottomNav.inflateMenu(R.menu.bottom_menu);
                        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                Fragment selectedFragment = null;
                                switch(item.getItemId()) {
                                    case R.id.menu_calendary:
                                        selectedFragment = fragmentCalendary;
                                        break;
                                    case R.id.menu_user:
                                        selectedFragment = fragmentUser;
                                        break;
                                    case R.id.menu_info:
                                        selectedFragment = fragmentInfo;
                                }
                                if(selectedFragment != null) {
                                    Bundle args = new Bundle();

                                    args.putString("admin", user_type);
                                    args.putString("type", "noadmin");
                                    args.putString("ABC", "A");
                                    args.putString("goal", user_real_goal);
                                    args.putString("tot_cal", user_goal);
                                    args.putString("firstname", user_name);
                                    args.putString("lastname", user_lastname);
                                    args.putString("uri", user_uri);
                                    args.putString("phone", user_phone);
                                    args.putString("email", user_email);

                                    selectedFragment.setArguments(args);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack(null).commit();
                                }
                                return true;
                            }
                        });
                    }

                } else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });



    }


    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.menu_logout) {
            try {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    public Context getCtx() {
        return getApplicationContext();
    }


}
