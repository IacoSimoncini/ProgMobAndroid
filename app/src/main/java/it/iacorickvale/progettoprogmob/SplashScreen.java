package it.iacorickvale.progettoprogmob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    FirebaseAuth fAuth;
    private static int SPLASH_TIME_OUT = 4000;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

       fAuth = FirebaseAuth.getInstance();

            new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashScreen.this,Login.class));
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }
}
