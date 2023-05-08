package com.mobileproject.valhalla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileproject.valhalla.utils.APIs.APIClient;
import com.mobileproject.valhalla.utils.APIs.GoogleClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    // this activity provides a sign in functionality, so it needs a GoogleClient object
    GoogleClient googleClient;

    // this activity provides an API functionality, so it needs a APIClient object
    APIClient APIclient;

    // views needed from the xml file
    Spinner COD_Spinner, FORT_Spinner, VALO_Spinner, OW_Spinner;
    FloatingActionButton fab;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // remove top action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Make sure to run your application only in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // initiate the googleClient object with an instance of this activity
        googleClient = new GoogleClient(this);

        // initiate the APIClient object
        APIclient = new APIClient();

        // get the components from the view
        COD_Spinner = findViewById(R.id.cod_spinner);
        FORT_Spinner = findViewById(R.id.fort_spinner);
        VALO_Spinner = findViewById(R.id.valo_spinner);
        OW_Spinner = findViewById(R.id.over_spinner);

        fab = findViewById(R.id.fab);

        saveBtn = findViewById(R.id.saveBtn);

        //check if user is authenticated
        if (googleClient.isSignedIn()) {
            // if user is authenticated do the following

            // 1. get the data from the api and set the spinners
            getDataFromAPI();

            // 2. add functionality to goBack floating action button
            fab.setOnClickListener(v -> startMainActivity());

            // 3. add functionality to saveBtn Button
            saveBtn.setOnClickListener(v -> sendDataToAPI());

        } else {
            // if user is not authenticated do the following

            // 1. send to Main Activity
            startMainActivity();

        }
    }

    private void getDataFromAPI() {
        // the API url provided from the APIClient class
        String rankURL = APIclient.getRank(googleClient.getUserEmail());

        // build a new request using OkHttp lib
        Request request = new Request.Builder().url(rankURL).build();

        // start a new call to get the data from the api within a new Thread via Callback interface
        new OkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // if the API returned the data put it in the correct spinner, else do nothing
                if (response.isSuccessful()){
                    // working with json might throw a JSONException, so
                    // the next process have to be in a try/catch block
                    try {
                        // parse the response body into a JSON Array object
                        JSONArray json = new JSONArray(response.body().string());

                        if (json.length() > 0){ // if the JSON array contains data process them, else do nothing
                            for (int i=0;i<json.length();i++){
                                // get the current json object out of the json array to extract the data
                                JSONObject object = new JSONObject(json.get(i).toString());

                                // extract the game and the rank from the object
                                String rank = object.get("rank").toString();
                                String game = object.get("game").toString();

                                // using the game and the rank,
                                // select the spinner and the index for the array in the strings.xml
                                int rankIndex = getRankIndex(rank);
                                // since we are working on a different thread,
                                // we have no access to the spinners objects in the main thread
                                // so we will assign the spinner we need to this temp spinner and modify it
                                // thus we can modify the spinner on the UI
                                Spinner tempSpinner = selectSpinner(game);

                                // now use the main UI thread we can change the selected item on the spinner
                                ProfileActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tempSpinner.setSelection(rankIndex);
                                    }
                                });
                            }// end of for loop
                        }// end of if statement
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        // the response need to be closed
                        response.body().close();
                    }
                }else{
                    System.err.println("response not successful");
                }
            }
        });
    }

    // get the index of the corresponded rank for the array in the strings.xml
    private int getRankIndex(String rank) {
        switch (rank){
            case "NA":
                return 0;
            case "Bronze":
                return 1;
            case "Silver":
                return 2;
            case "Gold":
                return 3;
            case "Diamond":
                return 4;
        }
        return -1;
    }

    // select the corresponded spinner based on the game
    private Spinner selectSpinner(String game) {
        switch (game){
            case "cod":
                return COD_Spinner;
            case "valo":
                return VALO_Spinner;
            case "fort":
                return FORT_Spinner;
            case "ow":
                return OW_Spinner;
        }
        return null;
    }

    private void sendDataToAPI() {
        String userName = googleClient.getUserEmail();

        String[] gamesNames = {"cod",       "valo",       "fort",       "ow"};
        Spinner[] spinners = { COD_Spinner, VALO_Spinner, FORT_Spinner, OW_Spinner};

        // on the click of the save button,
        // send the request for every game using the sorted arrays above
        for (int i=0 ; i < 4 ; i++){
            String gameName = gamesNames[i];
            String rank = spinners[i].getSelectedItem().toString();
            startRequest(userName, gameName, rank);
        }
        // then go back the main page
        startMainActivity();
    }

    private void startRequest(String userName, String game, String rank) {
        // the API url provided from the APIClient class
        String postURL = APIclient.setRank();

        // build the request body and add the data provided on the parameter to it
        RequestBody body = new FormBody.Builder()
                .add("rankHolder", userName)
                .add("game", game)
                .add("rank", rank)
                .add("orderRank", rank)
                .build();

        // build the request using the url and the body using the post method
        Request request = new Request.Builder()
                .url(postURL)
                .post(body)
                .build();

        // since we can't run requests on the main UI thread we need to run it on a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // execute the call using the newCall method from the OkHttpClient class to send the data to the API
                    new OkHttpClient().newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startMainActivity() {
        // since the main page do not need any data from this page
        // the intent is empty and the main page will start immediately
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}