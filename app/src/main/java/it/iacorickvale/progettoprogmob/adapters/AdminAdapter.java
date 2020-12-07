package it.iacorickvale.progettoprogmob.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import it.iacorickvale.progettoprogmob.MainActivity;
import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.fragments.FragmentCards;
import it.iacorickvale.progettoprogmob.utilities.Users;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.CViewHolder> {
    private ArrayList<Users> struttura;
    private Context context;
    private FragmentCards fragmentCards;
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

        public CViewHolder(@NonNull View itemView) {
            super(itemView);
            firstname = itemView.findViewById(R.id.user_firstname);
            lastname = itemView.findViewById(R.id.user_lastname);
            proPic = itemView.findViewById(R.id.user_propic);
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

        /*holder.btnDelete.setVisibility(View.VISIBLE);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Delete the Card?");
                View dialogView = inflater.inflate(R.layout.delete_ex, null);
                alertDialog.setView(dialogView);

                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //holder.btnDel.
                        UsersFunctions.deleteUser(struttura.get(position).getRef() , struttura.get(position).getPath());
                        struttura.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyDataSetChanged();

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

        });*/

        /*holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setTitle("Delete the User?");
                View dialogView = inflater.inflate(R.layout.delete_us, null);
                alertDialog.setView(dialogView);

                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReferences.getUsers()
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {
                                                if(document.get("Id").toString().equals(struttura.get(position).getId())){
                                                    Log.d("utente rimosso: ", struttura.get(position).getId());
                                                    DatabaseReferences.getUserById(document.getId())
                                                            .delete();
                                                    removeAt(position);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                notifyDataSetChanged();

                            }
                        });
                    }
                });
                alertDialog.setNegativeButton("Negate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                alertDialog.create().show();
            }
        });*/

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragmentCards = new FragmentCards();
                    String id = struttura.get(position).getId();
                    Bundle args = new Bundle();
                    args.putString("u_id", id);
                    args.putString("type", "admin");
                    fragmentCards.setArguments(args);
                    FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.fragment_container, fragmentCards).addToBackStack(null).commit();
                }catch (Exception e){
                    Toast.makeText(context.getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() { return struttura.size(); }

}
