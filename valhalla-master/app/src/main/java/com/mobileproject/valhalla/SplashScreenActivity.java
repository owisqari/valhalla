package com.mobileproject.valhalla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.mobileproject.valhalla.utils.APIs.APIClient;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// this activity is the start activity
// it's just a splash screen to initiate and load the app
// it will automatically go the main activity
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // remove top action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Make sure to run your application only in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initiateAPI();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);
    }

    // since our API is hosted on a free server
    // the server is shut down until it receive a request then turn on and when the requests stops it's going to shut down again
    // so this method will send an empty request for the server to turn it on so the data on the app will be served fast
    private void initiateAPI(){
        String rankURL = new APIClient().getBaseURL();
        Request request = new Request.Builder().url(rankURL).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}
            @Override
            public void onResponse(Call call, Response response) throws IOException {}
        });
    }
}