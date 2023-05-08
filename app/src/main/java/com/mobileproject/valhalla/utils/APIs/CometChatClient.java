package com.mobileproject.valhalla.utils.APIs;

// this class is for managing the CometChat data to be used in the app
// NOTE: all the processes take place in the activities,
// those are just boilerplate data for managing the state across the app
public class CometChatClient {

    private static final String API_KEY = "8f8fd36518acf02b0ef49bae739ec60323dee691";
    private static final String APP_ID = "207160abf799e44b";
    private static final String REGION = "eu";

    private static final String groupIDForCod = "callofduty";
    private static final String groupIDForValo = "valorant";
    private static final String groupIDForOW = "overwatch";
    private static final String groupIDForFort = "fortnite";

    public static String getGroupIDForCod() {
        return groupIDForCod;
    }

    public static String getGroupIDForValo() {
        return groupIDForValo;
    }

    public static String getGroupIDForOW() {
        return groupIDForOW;
    }

    public static String getGroupIDForFort() {
        return groupIDForFort;
    }


    public static String getAPIKey() {
        return API_KEY;
    }

    public static String getAppID() {
        return APP_ID;
    }

    public static String getRegion() {
        return REGION;
    }

    public static String getRegisterUrl(){
        return String.format("https://api-%s.cometchat.io/v2/users", REGION);
    }
}
