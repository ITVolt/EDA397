package se.chalmers.justintime.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import se.chalmers.justintime.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2500);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();


        final ImageView iv = (ImageView) findViewById(R.id.imageButton3);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade_in);
        final TextView tv = (TextView) findViewById(R.id.textView3);

        tv.setAnimation(an);

        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

    }

    public void removeSplash(View view){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
}


}
