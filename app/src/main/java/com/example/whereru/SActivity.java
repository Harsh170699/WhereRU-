package com.example.whereru;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Harsh on 09-03-2018.
 */

public class SActivity extends AppCompatActivity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s);

        iv=(ImageView)findViewById(R.id.iv);

        Animation anim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        iv.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity();
            }
        }, 3000);
    }

    private void startActivity() {
        String email = new PrefManager(this).getEmail();
        if(email != null && !email.isEmpty()) {
            Intent intent = new Intent(this, NaviActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }
    }
}




