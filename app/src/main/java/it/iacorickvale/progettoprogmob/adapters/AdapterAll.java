package it.iacorickvale.progettoprogmob.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.firebase.ExerciseFunctions;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

public class AdapterAll extends RecyclerView.Adapter<AdapterAll.CViewHolder> {
    private ArrayList<Esercizi> struttura;
    private LayoutInflater inflater;
    private String defName;
    private String defDesc;
    private String defDiff;

    public AdapterAll(Context ctx, ArrayList<Esercizi> struttura) {
        this.inflater = LayoutInflater.from(ctx);
        this.struttura = struttura;
    }
    public class CViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button btnDelete;
        Button btnModify;
        ImageView img;
        LinearLayout parentLayout;

        public CViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.all_ex_name);
            img = itemView.findViewById(R.id.img);
            btnModify = itemView.findViewById(R.id.btn_modify);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    @NonNull
    @Override
    public AdapterAll.CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater. from(parent.getContext()).inflate(R.layout.all_ex,parent,false);
        return new CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAll.CViewHolder holder, final int position) {
        holder.name.setText(struttura.get(position).getName());
        //holder.name.setText(struttura.get(position).getDescription());
        String difficulty =struttura.get(position).getDifficulty();
        holder.img.setImageResource(ExerciseFunctions.setImageEx(difficulty));
        holder.name.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
                View dialogView = inflater.inflate(R.layout.description_ex, null);
                alertDialog.setView(dialogView);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //alertDialog.setMessage(struttura.get(position).getDescription());
                TextView descText = dialogView.findViewById(R.id.text_descrizione);
                descText.setGravity(Gravity.CENTER);
                descText.setText(struttura.get(position).getDescription());
                Button btn = dialogView.findViewById(R.id.btn_dlg);
                btn.setGravity(Gravity.CENTER);
                alertDialog.show();
            }
        });

        holder.btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Update Exercise");
                View dialogView = inflater.inflate(R.layout.dialog_ex_mod, null);
                alertDialog.setView(dialogView);
                final EditText name = dialogView.findViewById(R.id.name_ex_mod);
                final EditText description = dialogView.findViewById(R.id.desc_ex_mod);
                final Spinner difficulty = dialogView.findViewById(R.id.diff_ex_mod);
                Log.d("KKKKKKK: id " , struttura.get(position).getName());
                DatabaseReferences.getExById(struttura.get(position).getName()).get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                defName = new String(task.getResult().get("Nome").toString());
                                defDesc = new String(task.getResult().get("Descrizione").toString());
                                defDiff = new String(task.getResult().get("Difficoltà").toString());
                                Log.d("1)" , defName+ defDesc + defDiff);
                            }
                        }
                );
                alertDialog.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if( name.getText().toString().length() > 20)
                                {
                                    Toast.makeText(v.getContext(), "Impossible to Update:" + "\nName must be <20", Toast.LENGTH_SHORT).show();
                                }
                                else if( description.getText().toString().length() > 50)
                                {
                                    Toast.makeText(v.getContext(), "Impossible to Update:" + "\nDescription must be <20", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                Log.d("2)" , "controllo se è già presente");
                                if(ExerciseFunctions.verifyName(name.getText().toString()) && !name.equals(defName)){
                                    Log.d( "1)" , "lo Modifico");
                                    String newName;
                                    if (name.getText().toString().equals("")) {
                                        newName = new String(defName);
                                    } else {
                                        newName = new String(name.getText().toString());
                                    }

                                    String newDesc;
                                    if (description.getText().toString().equals("")) {
                                        newDesc = new String(defDesc);
                                    } else {
                                        newDesc = new String(description.getText().toString());
                                    }

                                    String newDiff;
                                    if (difficulty.getSelectedItem().toString().equals("")) {
                                        newDiff = new String(defDiff);
                                    } else {
                                        newDiff = new String(difficulty.getSelectedItem().toString());
                                    }
                                    Log.d("AA: ", newDiff);
                                    ExerciseFunctions.deleteEx(struttura.get(position).getName());
                                    ExerciseFunctions.createEx(newDesc , newDiff , newName);
                                    Log.d("mmm", "k");
                                    ExerciseFunctions.updateExCard(defName , newName, newDesc, newDiff);
                                    Log.d("mmm", "h");
                                    struttura.set(position , new Esercizi(newDesc , newDiff , newName));
                                    notifyItemChanged(position , new Esercizi(newDesc , newDiff , newName));
                                    notifyDataSetChanged();
                                }else{
                                    Log.d( "onClick: " , "non lo Modifico");
                                }
                                notifyDataSetChanged();

                                }
                            }
                        });
                alertDialog.setNegativeButton("Negate",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();
                notifyDataSetChanged();
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());//Here I have to use v.getContext() istead of just cont.
                alertDialog.setTitle("Delete the Exercise?");
                View dialogView = inflater.inflate(R.layout.delete_ex, null);
                alertDialog.setView(dialogView);
                alertDialog.setPositiveButton( "Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReferences.getExercises()
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for(DocumentSnapshot document : task.getResult()){
                                            if(document.get("Nome").toString().equals(struttura.get(position).getName())){
                                                Log.d("esercizio rimosso: ", struttura.get(position).getName());
                                                DatabaseReferences.getExById(document.getId())
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
                    public void onClick(DialogInterface dialog, int which) {
                        // Close dialog
                    }
                });

                alertDialog.create().show();

            }
    });

    }
    @Override
    public int getItemCount() {
        return struttura.size();
    }

    public void removeAt(int position){
        struttura.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, struttura.size());
    }

}
