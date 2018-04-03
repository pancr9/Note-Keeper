package builderspace.notekeeper.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import builderspace.notekeeper.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int SPLASH_TIMEOUT = 750;
        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash.this, MainActivity.class));
            finish();
        }, SPLASH_TIMEOUT);
    }
}
