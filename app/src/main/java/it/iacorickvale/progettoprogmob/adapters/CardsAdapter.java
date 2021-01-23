package it.iacorickvale.progettoprogmob.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Console;
import java.util.ArrayList;

import it.iacorickvale.progettoprogmob.MainActivity;
import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.CardsFunctions;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.fragments.FragmentCalendary;
import it.iacorickvale.progettoprogmob.fragments.FragmentCountdown;
import it.iacorickvale.progettoprogmob.fragments.FragmentEsercizi;
import it.iacorickvale.progettoprogmob.utilities.Cards;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

import static it.iacorickvale.progettoprogmob.firebase.DatabaseReferences.getUser;


public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CViewHolder>{
    private Context context;
    private FragmentEsercizi fragmentEsercizi;
    private FragmentCountdown fragmentCountdown;
    private FragmentCalendary fragmentCalendary;
    private ArrayList<Cards> struttura ;
    private LayoutInflater inflater;
    private Boolean control_ifAd;
    private long timeLeftInMilliseconds = 0;
    private long timePauseInMilliseconds = 0;
    private long timeIntLeft = 0;
    private long timeIntPause = 0;
    private String currentDay;
    private String ABC;

    public CardsAdapter(Context ctx, ArrayList<Cards> struttura , Boolean aux, String currentDay, String ABC) {
        this.inflater = LayoutInflater.from(ctx);
        this.struttura = struttura;
        this.control_ifAd = aux;
        this.currentDay = currentDay;
        this.context = inflater.getContext();
        this.ABC = ABC;
    }

    public class CViewHolder extends RecyclerView.ViewHolder{
            TextView textPath;
            LinearLayout parentLayout;
            Button btnDel;
            Button btnMod;
            Button btnPlay;

        public CViewHolder(@NonNull View itemView) {
            super(itemView);
            textPath = itemView.findViewById(R.id.text_path);
            btnDel = itemView.findViewById(R.id.btnCard_delete);
            btnMod = itemView.findViewById(R.id.btnCard_modify);
            btnPlay = itemView.findViewById(R.id.playCard);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
    @NonNull
    @Override
    public CardsAdapter.CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_row, parent, false);
        return new CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CViewHolder holder, final int position) {
        holder.textPath.setText(struttura.get(position).getPath());
        if(control_ifAd){
            Log.d("è dunque " , "Admin");
            holder.btnMod.setVisibility(View.VISIBLE);
            holder.btnMod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                    alertDialog.setTitle("Enter new card name");
                    final EditText name = new EditText(v.getContext());
                    alertDialog.setView(name);
                    alertDialog.setPositiveButton("confirm",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if( name.getText().toString().equals("")){
                                        Toast.makeText(context, "Impossible to Update:" + "\nInsert new name", Toast.LENGTH_SHORT).show();
                                    }
                                    else if( name.getText().toString().length() > 20)
                                    {
                                        Toast.makeText(context, "Impossible to Update:" + "\nName must be <20", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        String newName = name.getText().toString();
                                        String type = struttura.get(position).getType();
                                        String ref = struttura.get(position).getRef();

                                        CardsFunctions.modifyCard(ref, struttura.get(position).getPath(), newName, type, currentDay);

                                        struttura.set(position, new Cards(newName, ref, type));
                                        notifyItemChanged(position, new Cards(newName, ref, type));
                                        notifyDataSetChanged();
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
                    notifyDataSetChanged();
                }
            });
            holder.btnDel.setVisibility(View.VISIBLE);
            holder.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String uid = struttura.get(position).getRef();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                    alertDialog.setTitle("Delete the Card?");
                    View dialogView = inflater.inflate(R.layout.delete_ex, null);
                    alertDialog.setView(dialogView);
                    alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                    //holder.btnDel.
                    CardsFunctions.deleteCard(struttura.get(position).getRef() , struttura.get(position).getPath(), currentDay);
                    struttura.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    notifyDataSetChanged();
                    if (struttura.isEmpty()){
                        fragmentCalendary = new FragmentCalendary();
                        Bundle args = new Bundle();
                        if(control_ifAd) {
                            args.putString("type", "admin");
                            args.putString("u_id" , uid);
                        }else{
                            args.putString("type" , "noadmin");
                        }
                        args.putString("ABC", ABC);
                        fragmentCalendary.setArguments(args);
                        FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment_container, fragmentCalendary).addToBackStack(null).commit();
                    }

                }
            });
                    alertDialog.setNegativeButton("Negate", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close dialog
                        }
                    });
                    alertDialog.create().show();
                }

            });

            holder.btnPlay.setVisibility(View.INVISIBLE);
        } else {
            Log.d("è dunque " , "NON Admin");
            holder.btnDel.setVisibility(View.INVISIBLE);
            holder.btnMod.setVisibility(View.INVISIBLE);
            holder.btnPlay.setVisibility(View.VISIBLE);
            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
            if(struttura.size() > 0){
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View dialogView = inflater.inflate(R.layout.difficulty_card, null);
                builder.setTitle("CHOOSE DIFFICULTY:");
                builder.setView(dialogView);
                final Button bEasy = dialogView.findViewById(R.id.easy_btn);
                final Button bMedium = dialogView.findViewById(R.id.medium_btn);
                final Button bHard = dialogView.findViewById(R.id.hard_btn);
                /*final Button bGoto = dialogView.findViewById(R.id.goto_btn);*/
                bEasy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeLeftInMilliseconds = 5000;
                        timePauseInMilliseconds = 15000;
                        bEasy.setBackground(ResourcesCompat.getDrawable(v.getResources(), R.drawable.whiteline_background, null));
                        bHard.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                        bMedium.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                    }
                });
                bMedium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeLeftInMilliseconds = 10000;
                        timePauseInMilliseconds = 10000;
                        bEasy.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                        bMedium.setBackground(ResourcesCompat.getDrawable(v.getResources(), R.drawable.whiteline_background, null));
                        bHard.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                    }
                });
                bHard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeLeftInMilliseconds = 15000;
                        timePauseInMilliseconds = 5000;
                        bEasy.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                        bMedium.setBackgroundColor(v.getResources().getColor(android.R.color.white));
                        bHard.setBackground(ResourcesCompat.getDrawable(v.getResources(), R.drawable.whiteline_background, null));
                    }
                });

                builder.setPositiveButton("confirm",
                new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (timePauseInMilliseconds!=0 && timeLeftInMilliseconds!=0){
                        try {
                            fragmentCountdown = new FragmentCountdown();
                            String path_scheda = struttura.get(position).getPath();
                            String ref= struttura.get(position).getRef();
                            Bundle args = new Bundle();
                            Log.d("1 left: "+String.valueOf(timeIntLeft), "pause: "+String.valueOf(timeIntPause));
                            args.putLong("timeTot",  timeIntLeft);
                            args.putLong("timeLeftInMilliseconds",  timeLeftInMilliseconds);
                            args.putLong("timePauseInMilliseconds", timePauseInMilliseconds);
                            args.putString("path", path_scheda);
                            args.putString("ref", ref);
                            args.putString("currentDay", currentDay);
                            fragmentCountdown.setArguments(args);
                            FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.fragment_container, fragmentCountdown).addToBackStack(null).commit();
                        }catch (Exception e){
                            Toast.makeText(context.getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                });
                builder.setNegativeButton("Negate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                                           // Close dialog
                    }
                });

                builder.show();

            }
        }
    });


        }
        holder.textPath.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    fragmentEsercizi = new FragmentEsercizi();
                    String path_scheda = struttura.get(position).getPath();
                    String ref= struttura.get(position).getRef();
                    Bundle args = new Bundle();
                    args.putString("path", path_scheda);
                    args.putString("ref", ref);
                    if (control_ifAd) {
                        args.putString("type", "admin");
                    }else {
                        args.putString("type", "noadmin");
                    }
                    args.putString("currentDay", currentDay);
                    fragmentEsercizi.setArguments(args);
                    FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_container, fragmentEsercizi).addToBackStack(null).commit();
                }catch (Exception e){
                    Toast.makeText(context.getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return struttura.size();
    }

    public void modifyCurrentDay(String newDay){
        currentDay = new String(newDay);
        notify();
        notifyDataSetChanged();
        notifyAll();
    }
}