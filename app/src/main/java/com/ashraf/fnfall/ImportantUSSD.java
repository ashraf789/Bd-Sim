package com.ashraf.fnfall;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class ImportantUSSD extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_ussd);

        addIniTialiZe();
        String operator = getIntent().getStringExtra("operatorName");
        switch (operator){
            case "gp":
                operator = "grameenphone";
                break;
            case "bl":
                operator = "banglalink";
                break;
            case "tl":
                operator = "teletalk";
                break;
            case "rb":
                operator = "robi";
                break;
            case "air":
                operator = "airtel";
                break;
        }
        mWebView = (WebView) findViewById(R.id.web_internet);
        mWebView.loadUrl("file:///android_asset/www/"+operator+"/ussd.html");
        mWebView.setWebViewClient(new WebViewClient());
    }
    private void addIniTialiZe() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2122789248840144~8578449717");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
    public void home(View v){
        startActivity(new Intent(this,MainActivity.class));
    }
}
