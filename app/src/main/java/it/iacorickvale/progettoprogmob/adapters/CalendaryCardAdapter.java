package it.iacorickvale.progettoprogmob.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.iacorickvale.progettoprogmob.MainActivity;
import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.firebase.CardsFunctions;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.fragments.FragmentCountdown;
import it.iacorickvale.progettoprogmob.fragments.FragmentEsercizi;
import it.iacorickvale.progettoprogmob.utilities.Cards;

import static it.iacorickvale.progettoprogmob.firebase.DatabaseReferences.getUser;

public class CalendaryCardAdapter extends RecyclerView.Adapter<CalendaryCardAdapter.CViewHolder> {
    private ArrayList<Cards>cardsDay;
    private ArrayList<String>days = new ArrayList<String>(Arrays.asList("MON", "TUE" ,"WED" , "THU" , "FRI" , "SAT"));
    private LayoutInflater inflater;
    private Context context;


    public CalendaryCardAdapter(Context ctx) {
        this.inflater = LayoutInflater.from(ctx);
        this.context = inflater.getContext();
    }

    public class CViewHolder extends RecyclerView.ViewHolder {
        Button nameDay;
        LinearLayout parentLayout;

        public CViewHolder(@NonNull View itemView) {
            super(itemView);
            nameDay =  itemView.findViewById(R.id.dayName);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
    @NonNull
    @Override
    public CalendaryCardAdapter.CViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendary_row, parent, false);
        return new CalendaryCardAdapter.CViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CalendaryCardAdapter.CViewHolder holder, final int position) {
        holder.nameDay.setText(days.get(position));
        holder.nameDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return days.size();
    }


}
