package it.iacorickvale.progettoprogmob.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.iacorickvale.progettoprogmob.MainActivity;
import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.firebase.UsersFunctions;
import it.iacorickvale.progettoprogmob.fragments.FragmentCalendary;
import it.iacorickvale.progettoprogmob.fragments.FragmentCards;
import it.iacorickvale.progettoprogmob.utilities.Users;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.CViewHolder> {
    private ArrayList<Users> struttura;
    private Context context;
    private FragmentCalendary fragmentCalendary;
    private LayoutInflater inflater;

    public AdminAdapter(Context ctx, ArrayList<Users> struttura) {
        this.inflater = LayoutInflater.from(ctx);
        this.struttura = struttura;
        this.context = inflater.getContext();
    }

    public class CViewHolder extends RecyclerView.ViewHolder {
        TextView firstname, lastname;
        ImageView proPic;
        LinearLayout parentLayout;
        Button btnDelete;

        public CViewHolder(@NonNull View itemView) {
            super(itemView);
            firstname = itemView.findViewById(R.id.user_firstname);
            lastname = itemView.findViewById(R.id.user_lastname);
            proPic = itemView.findViewById(R.id.user_propic);
            btnDelete = itemView.findViewById(R.id.btnUser_delete);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }

    }

    @NonNull
    @Override
    public AdminAdapter.CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_row,parent,false);
        return new CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAdapter.CViewHolder holder, final int position) {
        holder.firstname.setText(struttura.get(position).getFirstname());
        holder.lastname.setText(struttura.get(position).getLastname());
        if(struttura.get(position).getUri() != null) { Glide.with(context).load(struttura.get(position).getUri()).into(holder.proPic); }



       holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle("Delete the User?");
                View dialogView = inflater.inflate(R.layout.delete_us, null);
                alertDialog.setView(dialogView);

                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UsersFunctions.deleteUser(struttura.get(position).getId());
                        struttura.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    }
                });
                alertDialog.setNegativeButton("Negate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                alertDialog.create().show();
            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragmentCalendary = new FragmentCalendary();
                    String id = struttura.get(position).getId();
                    Bundle args = new Bundle();
                    args.putString("ABC", "A");
                    args.putString("u_id", id);
                    args.putString("type", "admin");
                    fragmentCalendary.setArguments(args);
                    FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_container, fragmentCalendary).addToBackStack(null).commit();
                }catch (Exception e){
                    Toast.makeText(context.getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() { return struttura.size(); }

}
