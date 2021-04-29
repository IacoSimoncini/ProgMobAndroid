package it.iacorickvale.progettoprogmob.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
    private TextView end;
    private TextView countdown;
    private TextView currentExName;
    private TextView currentExDiff;
    private Button StartAndStopBtn;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 0;
    private long timePauseInMilliseconds = 0;
    private boolean timerRunning;
    private ArrayList<Esercizi> listEserciziScheda = new ArrayList<>();
    private boolean inPause;
    private boolean firstTime = true;
    private int index;
    private int lenght;
    private String currentDay;
    private String exCountdown[];
    private String diffCountdown[];
    private ProgressBar progressBar;
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
        end = view.findViewById(R.id.end_txt);
        StartAndStopBtn = view.findViewById(R.id.StartAndStopBtn);
        progressBar = view.findViewById(R.id.progress_bar);

        assert this.getArguments() != null;
        timeLeftInMilliseconds = this.getArguments().getLong("timeLeftInMilliseconds");
        timePauseInMilliseconds = this.getArguments().getLong("timePauseInMilliseconds");
        k = 60000/timeLeftInMilliseconds;
        h = 60000/timePauseInMilliseconds;
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
                                            document.get("name").toString(),
                                            document.get("cal").toString(),
                                            document.get("uri").toString())
                                    );
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
                    index = -1;
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
                                    //progressBar.setMax((int) ((hok*timeLeftInMilliseconds/1000)));
                                    //progressBar.setProgress(1000);
                                    //progressBar.setProgress(Integer.parseInt(String.valueOf(timeLeftInMilliseconds)));
                                    index += 1;
                                    currentExDiff.setText("Current Type: " + diffCountdown[index]);
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
            Toast.makeText(getContext(), "Pause: new Max!" , Toast.LENGTH_SHORT).show();
            progressBar.setMax((int) ((hok*timeLeftInMilliseconds/1000)));
            stopTimer();
        }else{
            //int progress = (int) ((hok*l/1000)-1);
            progressBar.setMax((int) ((hok*timeLeftInMilliseconds/1000)));

            startTimer();
        }
    }

    public void startTimer(){
        //progressBar.setMax((int) ((hok*timeLeftInMilliseconds/1000)));
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds , 1000){
            @Override
            public void onTick(long l){
                int progress = (int) ((hok*l/1000)+1);
                progressBar.setProgress(progress);
                timeLeftInMilliseconds = l;
                updateTimer();
            }
            @Override
            public void onFinish(){
                progressBar.setProgress(0);
                index += 1;
                if ((index < exCountdown.length)) {
                    if(!diffCountdown[index-1].equals(diffCountdown[index]) && !inPause){
                        inPause = true;
                        currentExDiff.setText("BREAK TIME");
                        currentExName.setText("next exercise: " + exCountdown[index]);
                        // currentExName.setText("next exercise: " + diffCountdown[index]);
                        hok = h;
                        index -= 1;
                    }
                    else{
                        inPause = false;
                        hok = k;
                        currentExDiff.setText(diffCountdown[index]);
                        currentExName.setText(exCountdown[index]);
                    }
                    countdown.setText("");
                    timeLeftInMilliseconds = timePauseInMilliseconds;
                    startStop();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setMax(100);
                    progressBar.setProgress(0);
                    countdown.setText("END");
                    end.setVisibility(View.VISIBLE);
                    end.setText("Thank you for training with us!");
                    currentExDiff.setText("");
                    currentExName.setText("Training is ended");
                    StartAndStopBtn.setVisibility(View.INVISIBLE);
                }
            }
        }.start();
        StartAndStopBtn.setText("Pause");
        //timerRunning = f;
    }

    public void stopTimer(){
        StartAndStopBtn.setText("RESUME");
        countDownTimer.cancel();
    }

    public void updateTimer(){
        int seconds = (int) timeLeftInMilliseconds % 60000 / 1000;
        seconds += 1;
        String timeLeftText;
        if(seconds < 10){
            timeLeftText= "0:0"+seconds;
        }else{
            timeLeftText= "0:"+seconds;
        }
        countdown.setText(timeLeftText);
    }

}