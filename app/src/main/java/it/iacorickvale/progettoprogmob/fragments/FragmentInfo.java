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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

public class FragmentInfo extends Fragment {

    private long progress;
    private Button reset;
    private Button goal;
    private Button choose;
    private String prova;
    private int riprova;
    private TextView goal_view;
    private TextView act_cal;
    private ArrayList<Esercizi> listEserciziAdd = new ArrayList<>();
    private ProgressBar calories_bar;
    private String[] allEsercizi;
    private List<String> exList;
    private int index = 0;
    private int user_cal;
    private int user_goal;
    private String db_cal;
    private DocumentReference reference;
    private String uid;
    boolean[] checkedItemsArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        act_cal = view.findViewById(R.id.actual_calories);
        reset = view.findViewById(R.id.reset_btn);
        goal = view.findViewById(R.id.goal_btn);
        choose = view.findViewById(R.id.choose_btn);
        goal_view = view.findViewById(R.id.goal_view);

        calories_bar = (ProgressBar) view.findViewById(R.id.progress_bar_calories);

        if (riprova == 0){
            user_cal = Integer.parseInt(this.getArguments().getString("tot_cal"));
        }
        else {
            user_cal = riprova;
        }

        user_goal = Integer.parseInt(this.getArguments().getString("goal"));


        Log.d("VERIFICA VALORI PASSATI", this.getArguments().getString("tot_cal"));
        if (prova == null){
            goal_view.setText("Your Goal is: " + this.getArguments().getString("goal") + " calories.");
            calories_bar.setMax(Integer.parseInt(this.getArguments().getString("goal")));
        }
        else {
            goal_view.setText("Your Goal is: " + prova + " calories.");
            calories_bar.setMax(Integer.parseInt(prova));
        }
            act_cal.setText("Curr.Cal.:" + user_cal);
            calories_bar.setProgress(user_cal);

        try {
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Esercizi")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    db.collection("Esercizi")
                                            .document(document.getId())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    Esercizi ex = new Esercizi(document.get("description").toString(),
                                                            document.get("difficulty").toString(),
                                                            document.get("name").toString(),
                                                            document.get("cal").toString(),
                                                            document.get("uri").toString()
                                                    );
                                                    listEserciziAdd.add(ex);
                                                }
                                            });
                                }
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allEsercizi = new String[listEserciziAdd.size()];
                index = 0;
                for (Esercizi e : listEserciziAdd) {
                    allEsercizi[index] = e.getName();
                    index++;
                }
                checkedItemsArray = new boolean[allEsercizi.length];
                exList = Arrays.asList(allEsercizi);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Calculate how many calories you burned on exercises!");
                builder.setView(inflater.inflate(R.layout.dialog_list, null));


                builder.setMultiChoiceItems(allEsercizi, checkedItemsArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        checkedItemsArray[which] = isChecked;
                        String currentItem = exList.get(which);
                        builder.setCancelable(false);
                        builder.setTitle("Choose Exercises to add");
                    }

                });
                builder.setPositiveButton("Calculate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int counter = 0;
                        for (int i = 0; i < checkedItemsArray.length; i++) {
                            boolean checked = checkedItemsArray[i];
                            if (checked){
                                try {
                                    for (final Esercizi ex : listEserciziAdd) {
                                        if(allEsercizi[i].toString().equals(ex.getName())) {
                                            counter = counter + Integer.parseInt(ex.getCal());
                                        }
                                    }
                                }catch (Exception e){
                                    Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        user_cal += counter;
                        riprova = user_cal;
                        calories_bar.setProgress(user_cal);
                        act_cal.setText("Curr.Cal.:" + user_cal);

                        if (isTotCalChanged()){
                            Toast.makeText(getContext(),"Update Done", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(),"No Update", Toast.LENGTH_SHORT).show();
                        }
                    }
                    });

                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });




        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Reset the progress?");
                builder.setView(inflater.inflate(R.layout.reset_alert, null));
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_cal = 0;
                        riprova = user_cal;
                        calories_bar.setProgress(user_cal);
                        act_cal.setText("Curr.Cal.: " + 0);
                        if (isTotCalChanged()){
                            Toast.makeText(getContext(),"Update Done", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(),"No Update", Toast.LENGTH_SHORT).show();
                        }
                        //la funzione che modifica tot_cal sul db

                    }
                    });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                    }
                });
                builder.create().show();
            }
        });

        goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //qui va settato il valore dell'obiettivo di calorie dell'utente
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Set your Goal");
                View dialogView = inflater.inflate(R.layout.goal_alert, null);

                builder.setView(dialogView);

                final EditText cal_goal = dialogView.findViewById(R.id.cal_goal);


                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                        if (cal_goal.getText().toString().equals(""))
                        {
                            Toast.makeText(getContext(), "Impossible to set:" + "\nNo empty values", Toast.LENGTH_SHORT).show();
                        }
                        else if (isDoubleOrInt(cal_goal.getText().toString())== -1)
                        {
                            Toast.makeText(getContext(), "Impossible to set:" + "\nGoal must be an Int", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int calories = Integer.parseInt(cal_goal.getText().toString());
                            //calories_bar.setProgress(0);
                            user_goal= calories;
                            prova = cal_goal.getText().toString();
                            calories_bar.setMax(calories);
                            calories_bar.setProgress(user_cal);
                            goal_view.setText("Your goal is now: " + cal_goal.getText().toString() + " calories.");


                            if (isGoalChanged()){
                                Toast.makeText(getContext(),"Update Done", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getContext(),"No Update", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }catch (Exception e){
                            Toast.makeText(getContext(), "No empty spaces:  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });

                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                    }
                });
                builder.create().show();
            }
            catch (Exception e){
                Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });


        listEserciziAdd.clear();
        return view;
}

    public static int isDoubleOrInt(String num){
        try{
            Integer.parseInt(num);
            return 0;
        }catch(Exception exception){
            try{
                Double.parseDouble(num);
                return 1;
            }catch(Exception e){
                return -1;
            }
        }
    }

    public boolean isTotCalChanged()
    {
        if (!this.getArguments().getString("tot_cal").equals(user_cal)) {
            final Map<String, Object> mp = new HashMap<>();
            mp.put("firstname", this.getArguments().getString("firstname"));
            mp.put("lastname", this.getArguments().getString("lastname"));
            mp.put("phone", this.getArguments().getString("phone"));
            mp.put("uri", this.getArguments().getString("uri"));
            mp.put("email", this.getArguments().getString("email"));
            mp.put("admin", this.getArguments().getString("admin"));
            mp.put("tot_cal" , user_cal);
            if (prova == null){
                mp.put("goal", user_goal);
            }else{
                mp.put("goal", Integer.parseInt(prova));
            }

            DatabaseReferences.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(mp);
            //setTot_cal(String cal_goal)
            return true;
        } else {
            return false;
        }
    }

    public boolean isGoalChanged(){
        if (!this.getArguments().getString("goal").equals(user_goal))  {
            final Map<String, Object> mp = new HashMap<>();
            mp.put("firstname", this.getArguments().getString("firstname"));
            mp.put("lastname", this.getArguments().getString("lastname"));
            mp.put("phone", this.getArguments().getString("phone"));
            mp.put("uri", this.getArguments().getString("uri"));
            mp.put("email", this.getArguments().getString("email"));
            mp.put("admin", this.getArguments().getString("admin"));
            mp.put("goal", user_goal);
            mp.put("tot_cal", riprova);

            DatabaseReferences.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(mp);
            return true;
        }
        else  {
            return false;
        }
    }
   }
