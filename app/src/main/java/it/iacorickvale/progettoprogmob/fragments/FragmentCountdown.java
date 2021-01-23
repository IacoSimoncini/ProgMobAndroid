package it.iacorickvale.progettoprogmob.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

import static it.iacorickvale.progettoprogmob.firebase.DatabaseReferences.listExCard;
import static it.iacorickvale.progettoprogmob.firebase.ExerciseFunctions.orderListByDiff;

public class FragmentCountdown extends Fragment {
    private String path;
    private String ref;
    private Button start;
    private Button pause;
    private TextView countdown;
    private TextView currentExName;
    private TextView currentExDiff;
    private Button StartAndStopBtn;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 0;
    private long timePauseInMilliseconds = 0;
    private long time = 0;
    private boolean timerRunning;
    private ArrayList<Esercizi> listEserciziScheda = new ArrayList<>();
    private boolean inPause;
    private boolean firstTime = true;
    private int index = 0;
    private int lenght;
    private String currentDay;
    private String exCountdown[];
    private String diffCountdown[];
    private  ProgressBar progressBar;
    private double h=0;
    private double k=0;
    private double hok = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.countdown_ex, container, false);
        countdown = view.findViewById(R.id.countdown);
        currentExDiff = view.findViewById(R.id.currentExDiff);
        currentExName = view.findViewById(R.id.currentExName);
        StartAndStopBtn = view.findViewById(R.id.StartAndStopBtn);
        progressBar = view.findViewById(R.id.progress_bar);

        assert this.getArguments() != null;
        timeLeftInMilliseconds = this.getArguments().getLong("timeLeftInMilliseconds");
        timePauseInMilliseconds = this.getArguments().getLong("timePauseInMilliseconds");
        k = 100000/timeLeftInMilliseconds;
        h = 100000/timePauseInMilliseconds;
        hok = k;
        path = this.getArguments().getString("path");
        ref = this.getArguments().getString("ref");
        currentDay = this.getArguments().getString("currentDay");
        StartAndStopBtn.setText("START");
        timerRunning = true;
        try {
            lenght = 0;
            listExCard(ref , path,currentDay).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(final QueryDocumentSnapshot document : task.getResult()){
                                    listEserciziScheda.add(new Esercizi(Objects.requireNonNull(document.get("description")).toString(),
                                            document.get("difficulty").toString(),
                                            document.get("name").toString()) );
                                }
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    exCountdown = new String[listEserciziScheda.size()];
                    diffCountdown = new String[listEserciziScheda.size()];
                    int i = 0;
                    for (Esercizi e : orderListByDiff(listEserciziScheda)){
                        exCountdown[i] = e.getName();
                        diffCountdown[i] = e.getDifficulty();
                        Log.d(exCountdown[i], "onSuccess: ");
                        i+=1;
                    }
                    currentExDiff.setText("");
                    currentExName.setText("READY?");
                    countdown.setText("");
                    if (listEserciziScheda.size()!=0){
                        StartAndStopBtn.setVisibility(View.VISIBLE);
                        StartAndStopBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (firstTime) {
                                    firstTime = false;
                                    progressBar.setVisibility(View.VISIBLE);
                                    progressBar.setProgress(1000);
                                    time = timeLeftInMilliseconds;
                                    currentExDiff.setText(diffCountdown[index]);
                                    currentExName.setText(exCountdown[index]);
                                }
                                timerRunning = !timerRunning;
                                startStop();
                            }
                        });
                    }else {
                        currentExName.setText("NO EXERCISE");
                        StartAndStopBtn.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    public void startStop(){
        if(timerRunning) {
            stopTimer();
        }else{
            startTimer();
        }
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(time, 1000){
            @Override
            public void onTick(long l){
                time = l;
                int progress = (int) ((hok*l)/1000);
                progressBar.setProgress(progress);
                updateTimer();
            }
            @Override
            public void onFinish(){
                progressBar.setProgress(0);
                index += 1;
                if ((index < exCountdown.length)) {
                    if(!diffCountdown[index-1].equals(diffCountdown[index]) && !inPause){
                        hok = h;
                        inPause = true;
                        Log.d("in pausa ", String.valueOf(hok));
                        time = timePauseInMilliseconds;
                        currentExDiff.setText("NOW BREAK");
                        currentExName.setText("next: " + diffCountdown[index]);
                        index -= 1;
                    }
                    else{
                        hok = k;
                        inPause = false;
                        time = timeLeftInMilliseconds;
                        Log.d("NON in pausa ", String.valueOf(hok));
                        currentExDiff.setText(diffCountdown[index]);
                        currentExName.setText(exCountdown[index]);
                    }
                    countdown.setText("");
                    startStop();
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    countdown.setText("END");
                    currentExDiff.setText("");
                    currentExName.setText("");
                    StartAndStopBtn.setVisibility(View.INVISIBLE);
                }
            }
        }.start();
        StartAndStopBtn.setText("Pause");
    }

    public void stopTimer(){
        StartAndStopBtn.setText("RESUME");
        countDownTimer.cancel();
    }

    public void updateTimer(){
        int seconds = (int) time % 60000 / 1000;
        String timeLeftText;
        if(seconds < 9){
            timeLeftText= "0:0"+(seconds+1);
        }else{
            timeLeftText= "0:"+(seconds+1);
        }
        countdown.setText(timeLeftText);
    }

}