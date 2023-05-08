package com.mobileproject.valhalla.utils.APIs;

import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// this class is for providing the API urls
// NOTE: the requests process take place in the activity,
// those are just data to be used across the app
public class APIClient {

    // the API take three possible URLs
    // 1. GET  : /api/leaderboard/<game>/ : this url will get all the ranks of a specific game to be displayed in the leaderboard activity
    // 2. GET  : /api/rank/<username>/    : this url will get all the ranks of a specific user to be displayed in the profile activity
    // 3. POST : /api/rank/               : this url will post to the API to update the user current ranks or add new ranks for him, note: the username will be provided in the request body


    private String URL;
    private String leaderboardPath, rankPath;

    public APIClient() {
        this.URL = "https://valhallapi.herokuapp.com/api/";
        this.leaderboardPath = "leaderboard/";
        this.rankPath = "rank/";
    }

    public String getBaseURL(){
        return this.URL;
    }


    // returns /api/leaderboard/<game>/
    public String getLeaderboardURL(String game){
        return this.URL + this.leaderboardPath + game;
    }

    // returns /api/rank/<username>/
    public String getRank(String rankHolder){
        return this.URL + this.rankPath + rankHolder;
    }

    // returns /api/rank/
    public String setRank(){
        return this.URL + this.rankPath;
    }
}
