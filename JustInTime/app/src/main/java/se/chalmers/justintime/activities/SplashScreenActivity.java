package se.chalmers.justintime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import se.chalmers.justintime.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 2500;

    private Handler mSplashHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mSplashHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();


        mSplashHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                SplashScreenActivity.this.startActivity(intent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_SCREEN_DELAY);

        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade_in);
        final ImageView iv = (ImageView) findViewById(R.id.imageButton3);
        final TextView tv = (TextView) findViewById(R.id.textView3);

        tv.setAnimation(an);
        iv.startAnimation(an);

        an.setAnimationListener(new Animation.AnimationListener() {


            @Override
            public void onAnimationStart(Animation animation) {
                /*
                Context context = getApplicationContext();
                CharSequence text = "Loading...";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                */
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });


    }


    @Override
    protected void onPause() {

        super.onPause();
        mSplashHandler.removeCallbacksAndMessages(null);

    }



    public void removeSplash(View view){

        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SplashScreenActivity.this.startActivity(intent);
        SplashScreenActivity.this.finish();

    }


}
