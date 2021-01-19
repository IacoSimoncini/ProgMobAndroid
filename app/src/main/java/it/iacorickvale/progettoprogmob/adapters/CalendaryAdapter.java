package it.iacorickvale.progettoprogmob.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.iacorickvale.progettoprogmob.MainActivity;
import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.CardsFunctions;
import it.iacorickvale.progettoprogmob.fragments.FragmentCalendary;
import it.iacorickvale.progettoprogmob.fragments.FragmentCards;
import it.iacorickvale.progettoprogmob.utilities.Cards;

import static it.iacorickvale.progettoprogmob.firebase.CardsFunctions.controlTypeDay;

public class CalendaryAdapter extends RecyclerView.Adapter<CalendaryAdapter.CViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private String uid;
    private String controlAd;
    private String type;
    private Boolean[] ifset = new Boolean[28];

    public CalendaryAdapter(Context ctx, String type , Boolean[] ifset , String controlAd , String uid) {
        this.type = type;
        this.controlAd = controlAd;
        this.ifset = ifset;
        this.uid = uid;
        this.context = ctx;
        this.inflater = LayoutInflater.from(ctx);
    }

    public class CViewHolder extends RecyclerView.ViewHolder{

        LinearLayout parentLayout;
        TextView day;
        Button btnDay;

        public CViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            day = itemView.findViewById(R.id.day);
            btnDay = itemView.findViewById(R.id.btnDay);
        }
    }

    @NonNull
    @Override
    public CalendaryAdapter.CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendary_row, parent, false);
        return new CalendaryAdapter.CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendaryAdapter.CViewHolder holder, final int position) {
        holder.day.setText(String.valueOf(position+1));
        if(!(ifset[position]== null) && ifset[position]){
            holder.btnDay.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.barbell, null));
            holder.btnDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentCards fragmentCards = new FragmentCards();
                    Bundle args = new Bundle();
                    args.putString("ABC",  type);
                    args.putString("type",  controlAd);
                    args.putString("SelectedDay", (new Integer(position+1).toString()));
                    args.putString("u_id", uid);
                    fragmentCards.setArguments(args);
                    FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_container, fragmentCards).addToBackStack(null).commit();
                }
            });
        }else{
            if (controlAd.equals("admin")){
                holder.btnDay.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.add, null));
                holder.btnDay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Creation of a new card in the database
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setTitle("Create New Card");
                                View dialogView = inflater.inflate(R.layout.dialog_card_mod, null);

                                builder.setView(dialogView);

                                final EditText name_card = dialogView.findViewById(R.id.rename_card);

                                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(name_card.getText().toString().length() > 30)
                                        {
                                            Toast.makeText(context, "Impossible to Create:" + "\nCard's Name must be <=18", Toast.LENGTH_SHORT).show();
                                        }

                                        else{
                                            try {
                                                CardsFunctions.createCard(name_card.getText().toString(), type , uid, new Integer(position+1).toString());
                                                ifset[position] = true;
                                                notifyItemChanged(position,true);
                                                notifyItemChanged(position);
                                                notifyDataSetChanged();
                                                Toast.makeText(context, "Your card has been created", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                Toast.makeText(context , "path " + name_card.getText().toString() + " type " + type + " uid" + uid + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Close dialog
                                    }
                                });

                                builder.create().show();

                            } catch (Exception e) {
                                Toast.makeText(context, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



            }
            else {
                holder.btnDay.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        }

    }

    @Override
    public int getItemCount() {
        return 28;
    }
}
