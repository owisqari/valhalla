package com.mobileproject.valhalla.utils.APIs;

import android.app.Activity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;


// this class is for managing the user state
// NOTE: the login process take place in the activity,
// those are just boilerplate data for managing the state across the app
public class GoogleClient {

    // constant variables for the login process
    public final int SIGNIN_CODE = 9001; // just a random number ( must be unique )

    // firebase and google objects for the login process
    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInClient googleSignInClient;

    public GoogleClient(Activity activity){
        // whenever an activity need to use the user state,
        // it must be passed to this class to initiate the googleSignInClient object

        // configuring the sign in options
        String clientID = "272591306134-ao0av1crtf1ks391lg20do864jppnlpq.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientID)
                .requestEmail()
                .build();

        // initiate the client using the activity and the login options objects
        googleSignInClient = GoogleSignIn.getClient(activity, gso);

        //get an instance from firebase
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public boolean isSignedIn(){
        return firebaseAuth.getCurrentUser() != null;
    }


    // getters

    public GoogleSignInClient getGoogleSignInClient(){
        return googleSignInClient;
    }

    public FirebaseAuth getFirebaseAuth(){
        return firebaseAuth;
    }

    public String getUsername(){
        // the object may throw a null pointer exception
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName();
    }

    public String getUserEmail(){
        // the object may throw a null pointer exception
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
    }
}
