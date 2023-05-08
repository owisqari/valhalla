package com.mobileproject.valhalla;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileproject.valhalla.utils.APIs.APIClient;
import com.mobileproject.valhalla.utils.APIs.CometChatClient;
import com.mobileproject.valhalla.utils.APIs.GoogleClient;
import com.mobileproject.valhalla.utils.Adapters.LeaderboardRankAdapter;
import com.mobileproject.valhalla.utils.Models.Rank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LeaderboardActivity extends AppCompatActivity {

    // this activity provides a sign in functionality, so it needs a GoogleClient object
    GoogleClient googleClient;

    // this activity provides an API functionality, so it needs a APIClient object
    APIClient APIclient;

    //  a holder for the game name since this activity have dynamic content based on the game name
    String gameName;

    // an array that holds objects of the Rank class (from utils) for the RecyclerView
    ArrayList<Rank> ranks;

    // get the components from the view
    RecyclerView recyclerViewForLeaderboard;
    FloatingActionButton fab;
    ImageView leaderboardPic;
    TextView leaderGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // remove top action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Make sure to run your application only in portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // initiate the googleClient object with an instance of this activity
        googleClient = new GoogleClient(this);

        // initiate the APIClient object
        APIclient = new APIClient();

        // get the game name from the intent
        gameName = getIntent().getStringExtra("GAMENAME");

        // initiate the array of ranks
        ranks = new ArrayList();

        // get the components from the view
        recyclerViewForLeaderboard = findViewById(R.id.recyclerViewForLeaderboard);
        fab = findViewById(R.id.fab);
        leaderboardPic = findViewById(R.id.leaderboard_pic);
        leaderGameName = findViewById(R.id.leader_game_name);


        //check if user is authenticated
        if (googleClient.isSignedIn()) {
            // if user is authenticated do the following

            // 1. change the image and the title of the page based on the game name
            changeLeaderboardHeader();

            // 2. get the data from the api
            getData();

            // 3. add functionality to chat floating action button
            fab.setOnClickListener(v -> directToChatPage());

        } else {
            // if user is not authenticated do the following

            // 1. send to Main Activity
            startMainActivity();
        }

    }

    // based on the game name from the intent,
    // set the image and the title of the page to the corresponded values
    private void changeLeaderboardHeader(){
        switch (gameName){
            case "cod":
                leaderboardPic.setImageResource(R.drawable.cod);
                leaderGameName.setText("Call of Duty");
                break;
            case "valo":
                leaderboardPic.setImageResource(R.drawable.valo);
                leaderGameName.setText("Valorant");
                break;
            case "fort":
                leaderboardPic.setImageResource(R.drawable.fn);
                leaderGameName.setText("Fortnite");
                break;
            case "ow":
                leaderboardPic.setImageResource(R.drawable.overwatch);
                leaderGameName.setText("Overwatch");

                break;
        }
    }

    private void getData(){
        // the API url provided from the APIClient class
        String leaderboardURL = APIclient.getLeaderboardURL(gameName);

        // build a new request using OkHttp lib
        Request request = new Request.Builder().url(leaderboardURL).build();

        // start a new call to get the data from the api within a new Thread via Callback interface
        new OkHttpClient().newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // if the API returned the data put it in the recycler view, else do nothing
                if (response.isSuccessful()){
                    // working with json might throw a JSONException, so
                    // the next process have to be in a try/catch block
                    try {
                        // parse the response body into a JSON Array object
                        JSONArray json = new JSONArray(response.body().string());

                        if (json.length() > 0){ // if the JSON array contains data process them, else do nothing

                            for (int i=0;i<json.length();i++) {
                                // get the current json object out of the json array to extract the data
                                JSONObject object = new JSONObject(json.get(i).toString());

                                // extract the game and the rank from the object
                                String rank = object.get("rank").toString();
                                String username = object.get("rankHolder").toString();

                                // since the API uses the email to store the ranks
                                // we need to slice the @domain.com
                                int atPos = username.indexOf('@');
                                username = username.substring(0, atPos);

                                // add the new rank to the ranks array
                                ranks.add(new Rank(username, rank));

                            }// end of for loop

                            // now use the main UI thread to update the recycler view with the data
                            LeaderboardActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setRecyclerViewForLeaderboard();
                                }
                            });
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

    private void setRecyclerViewForLeaderboard(){
        // create an object from the LeaderboardRankAdapter class (from utils) and pass the ranks array to it
        LeaderboardRankAdapter leaderboardRankAdapter = new LeaderboardRankAdapter(ranks);

        // create a linear layout manager to set the layout of the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        // set the layout and the animator and the adapter for the recycler view component
        recyclerViewForLeaderboard.setLayoutManager(layoutManager);
        recyclerViewForLeaderboard.setItemAnimator(new DefaultItemAnimator());
        recyclerViewForLeaderboard.setAdapter(leaderboardRankAdapter);
    }

    // given the gameName, return the corresponded group ID from the CometChatClint
    // this group ID will be used to identify the chat and mix the messages
    private String getChatGroupID() {
        switch (gameName){
            case "cod":
                return CometChatClient.getGroupIDForCod();
            case "valo":
                return CometChatClient.getGroupIDForValo();
            case "fort":
                return CometChatClient.getGroupIDForFort();
            case "ow":
                return CometChatClient.getGroupIDForOW();
        }
        return null;
    }

    private void directToChatPage(){
        // since the chat page need only the group id from this page
        // the intent will save the group id and start the chat page
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("GROUP_ID", getChatGroupID());
        startActivity(intent);
    }

    private void startMainActivity() {
        // since the main page do not need any data from this page
        // the intent is empty and the main page will start immediately
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}