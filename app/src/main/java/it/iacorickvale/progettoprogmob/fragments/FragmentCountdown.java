package it.iacorickvale.progettoprogmob.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

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
    private Button StartAndStopBtn;
    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 11000; //600000
    private boolean timerRunning;
    private ArrayList<Esercizi> listEserciziScheda = new ArrayList<>();
    private boolean inPause;
    private boolean firstTime = true;
    private int index;
    private int lenght;
    String exCountdown[];
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.countdown_ex, container, false);
        countdown = view.findViewById(R.id.countdown);
        currentExName = view.findViewById(R.id.currentExName);
        StartAndStopBtn = view.findViewById(R.id.StartAndStopBtn);
        assert this.getArguments() != null;
        path = this.getArguments().getString("path");
        ref = this.getArguments().getString("ref");
        StartAndStopBtn.setText("START");
        timerRunning = true;
        try {
            lenght = 0;
            listExCard(ref , path).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(final QueryDocumentSnapshot document : task.getResult()){
                                    listEserciziScheda.add(new Esercizi(document.get("description").toString(),
                                            document.get("difficulty").toString(),
                                            document.get("name").toString()) );
                                }
                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    exCountdown = new String[listEserciziScheda.size()];
                    int i = 0;
                    for (Esercizi e : orderListByDiff(listEserciziScheda)){
                        exCountdown[i] = e.getName();
                        i+=1;
                    }
                    index = -1;
                    currentExName.setText("Click to Start");
                    countdown.setText("CountDown Timer");

                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        StartAndStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(firstTime){
                    firstTime = false;
                    index += 1;
                    currentExName.setText(exCountdown[index]);
                }
                timerRunning = !timerRunning;
                startStop();
            }
        });
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
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds , 1000){
            @Override
            public void onTick(long l){
                timeLeftInMilliseconds = l;
                updateTimer();

            }
            @Override
            public void onFinish(){
                index += 1;
                if ((index < exCountdown.length)) {
                    if(!controlType(exCountdown[index-1] , exCountdown[index] , orderListByDiff(listEserciziScheda)) && !inPause){
                        inPause = true;
                        index -= 1;
                        currentExName.setText("Time to Break");
                    }
                    else{
                        inPause = false;
                        currentExName.setText(exCountdown[index]);
                    }
                    countdown.setText("");
                    timeLeftInMilliseconds = 11000;
                    startStop();
                }else{
                    countdown.setText("END");
                    currentExName.setText("");
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
        String timeLeftText;
        timeLeftText= "0:"+seconds;
        if(seconds < 6){
            //countdown.setTextColor(Integer.parseInt("#cb3234"));
        }else{
            //countdown.setTextColor();
        }
        countdown.setText(timeLeftText);
    }

    public boolean controlType(String a , String b, ArrayList<Esercizi> list){
        String typeA = new String();
        String typeB = new String();
        boolean breakA = false;
        boolean breakB = false;
        for (Esercizi ex : list) {
            if (ex.getName().equals(a)){
                typeA = ex.getDifficulty();
                breakA = true;
            }
            if (ex.getName().equals(b)){
                typeB = ex.getDifficulty();
                breakB = true;
            }
            if (breakA && breakB){
                break;
            }
        }
        if (typeA.equals(typeB)){
            return true;
        }else{
            return false;
        }
    }

}
