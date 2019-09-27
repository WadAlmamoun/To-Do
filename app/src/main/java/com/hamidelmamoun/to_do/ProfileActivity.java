package com.hamidelmamoun.to_do;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.hamidelmamoun.to_do.data.SPHelper;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView emailTv;
    private CallbackManager callbackManager;
    Button logoutBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setViews();
        clickListeners();
    }


    private void initViews(){
        emailTv = findViewById(R.id.email_tv);
        logoutBt = findViewById(R.id.logout_bt);
    }


    private void setViews(){

//        Picasso.with(getApplicationContext()).load(SPHelper.getFacebookPicture(getApplicationContext())).transform(new CircleTransform()).into(profileIv);
        emailTv.setText(SPHelper.getAccountId(getApplicationContext()));
    }

    private void clickListeners(){
        logoutBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SPHelper.getAccountType(ProfileActivity.this) == 2){
                    FirebaseAuth.getInstance().signOut();

                    SPHelper.setAccountType(ProfileActivity.this,0);
                    SPHelper.setAccountId(ProfileActivity.this, SPHelper.NON_LOGGED_IN_USERS_ACCOUNT_ID);
                }

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    private void initFacebookLogin() {
//        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
//
//        callbackManager = CallbackManager.Factory.create();
//        // If you are using in a fragment, call loginButton.setFragment(this);
//
//        // Callback registration
//
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                AccessToken accessToken = loginResult.getAccessToken();
//                useLoginInformation(accessToken);
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//            }
//        });


    }

    private void useLoginInformation(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                SPHelper.setAccountType(ProfileActivity.this,0);
                SPHelper.setAccountId(ProfileActivity.this,"");

                Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
