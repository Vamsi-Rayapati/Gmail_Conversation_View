package com.example.gmmail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.services.gmail.Gmail;

import java.io.IOException;

import static com.example.gmmail.MainActivity.ctxt;

public class Welcome extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener  {
    private static final String GMAIL_SCOPE = "oauth2:https://www.googleapis.com/auth/gmail.readonly";
    private GoogleApiClient googleApiClient;
    private  static  final int REQ_CODE=9001;
    private String name;
    private String email;
    private static Gmail mailService;
    private SharedPreferences sharedPrefs;
    private SignInButton sin;
    private  static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        sharedPrefs = this.getSharedPreferences("GMmaild", 0);
        sin=(SignInButton)findViewById(R.id.google_button);
        sin.setOnClickListener(this);
        System.out.println("Gmail sign in required");

        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();
        intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private  void signresult(GoogleSignInResult isLogin)
    {
        if(isLogin.isSuccess())
        {
            GoogleSignInAccount account=isLogin.getSignInAccount();
            name=account.getDisplayName();
            email=account.getEmail();
            System.out.println(email);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("gmid", email);
            editor.commit();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
            {
                new WelcomeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else // Below Api Level 13
            {
                new WelcomeTask().execute();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signresult(result);
        }
        else if(requestCode==12345)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
            {
                new WelcomeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else // Below Api Level 13
            {
                new WelcomeTask().execute();
            }
        }
        else
        {

        }
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(intent,REQ_CODE);
    }
    class WelcomeTask extends AsyncTask<Void, Void, Void>
    {

        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                token = GoogleAuthUtil.getToken(Welcome.this, email, GMAIL_SCOPE);
                Intent intent = new Intent(ctxt, LoadData.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
            catch (UserRecoverableAuthException e)
            {
                startActivityForResult(e.getIntent(),12345);

            }
            catch (IOException e)
            {
                System.out.println(e+"");
            }
            catch (GoogleAuthException e)
            {
                System.out.println(e+"");
            }
            return null;
        }
    }
}

