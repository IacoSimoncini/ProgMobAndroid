package it.iacorickvale.progettoprogmob.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.CardsFunctions;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.firebase.ExerciseFunctions;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

import static it.iacorickvale.progettoprogmob.firebase.ExerciseFunctions.orderListByDiff;


public class EserciziAdapter extends RecyclerView.Adapter<EserciziAdapter.CViewHolder> {
    LayoutInflater inflater;
    private ArrayList<Esercizi> struttura;
    private Context context;
    String path, ref;
    boolean isAdmin;

    public EserciziAdapter(Context ctx, ArrayList<Esercizi> struttura, String path, String ref , boolean isAdmin) {
        this.inflater = LayoutInflater.from(ctx);
        this.struttura =  orderListByDiff(struttura);
        this.path = path;
        this.ref = ref;
        this.isAdmin = isAdmin;
        this.context = inflater.getContext();
    }

    public class CViewHolder extends RecyclerView.ViewHolder {
        TextView textNome;
        TextView textDescrizione;
        Button btnDel;
        Button btnMod;
        ImageView textDifficoltà;
        LinearLayout parentLayout;


        public CViewHolder(@NonNull View itemView) {
            super(itemView);

            textNome = itemView.findViewById(R.id.text_nome);
            //textDescrizione = itemView.findViewById(R.id.text_descrizione);
            textDifficoltà = itemView.findViewById(R.id.text_difficoltà);
            btnDel = itemView.findViewById(R.id.btnEx_delete);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    @NonNull
    @Override
    public CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.esercizi_row, parent, false);
        return new CViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CViewHolder holder, final int position) {
        holder.textNome.setText(struttura.get(position).getName());
        //holder.textDescrizione.setText(struttura.get(position).getDescription());
        String difficulty = struttura.get(position).getDifficulty();
        holder.textDifficoltà.setImageResource(ExerciseFunctions.setImageEx(difficulty));

        if(isAdmin) {
            holder.btnDel.setVisibility(View.VISIBLE);
            holder.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                    alertDialog.setTitle("Delete the Exercise?");
                    View dialogView = inflater.inflate(R.layout.delete_ex, null);
                    alertDialog.setView(dialogView);

                    alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final CollectionReference colRef = DatabaseReferences.listExCard(ref, path);
                            colRef.get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                            if  (document.getId().equals(struttura.get(position).getName())) {
                                                Log.d("delete:", struttura.get(position).getName());
                                                colRef.document(document.getId())
                                                        .delete();
                                                struttura.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
                                                notifyItemRangeChanged(position, getItemCount());
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
                        public void onClick(DialogInterface dialog, int which) {
                            // Close dialog
                        }
                    });

                    alertDialog.create().show();
                }
            });
        } else {
            holder.btnDel.setVisibility(View.INVISIBLE);
        }

        holder.textNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                    View dialogView = inflater.inflate(R.layout.description_ex, null);
                    alertDialog.setView(dialogView);
                    TextView descText = dialogView.findViewById(R.id.text_descrizione);
                    descText.setGravity(Gravity.CENTER);
                    descText.setText(struttura.get(position).getDescription());
                    Button btn = dialogView.findViewById(R.id.btn_dlg);
                    btn.setGravity(Gravity.CENTER);
                    alertDialog.show();
                }catch(Exception e){
                    Toast.makeText(context.getApplicationContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return struttura.size();
    }


}
