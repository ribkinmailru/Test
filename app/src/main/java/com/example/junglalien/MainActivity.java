package com.example.junglalien;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.onesignal.OneSignal;

import java.net.InetAddress;

import bolts.AppLinks;

import static com.facebook.FacebookSdk.setAutoLogAppEventsEnabled;

public class MainActivity extends AppCompatActivity {
    private static final String ONESIGNAL_APP_ID = "d4f2a56e-c488-4557-a01f-6286a2efe0bd";
    WebView test;
    private String connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        setContentView(R.layout.activity_main);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        if(!checkconnection()){
            game();
            Log.d("404", "connection");
        }
        SharedPreferences preg = getSharedPreferences("link",MODE_PRIVATE);
        connection = preg.getString("test","https://scnddmn.com/7vZTBtvQ");  //получаю ссылку

        test = findViewById(R.id.web);
        test.setWebViewClient(new MyWebViewClient());
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        if (appLinkData != null) {
                            Log.i("FB_DEEP", appLinkData.getTargetUri().toString()); //sub
                            if(connection.equals("https://scnddmn.com/7vZTBtvQ")) {
                                connection = connection + "?" + appLinkData.getTargetUri().toString().split("\\?")[1];
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                test.loadUrl(connection);
                                Log.d("sdasdad",connection);
                            }
                        });
                    }
                }
        );
    }


    private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
        @Override
        public void onReceivedHttpError (WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            game(); // если 404
            Log.d("404", "404");
        }
    }

    public boolean checkconnection(){
        if(isNetworkOnline()){
            return true;
        }
        return false;
    }


    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public void game(){
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);     //заглушка
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        if(test.canGoBack()) {
            test.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void setFullscreen(){
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStop() {
        super.onStop();
        savelink();
    }

    public void savelink(){
        SharedPreferences pref = getSharedPreferences("link", MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putString("test", test.getUrl());
        ed.apply();
    }



}