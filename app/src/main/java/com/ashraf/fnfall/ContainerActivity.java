package com.ashraf.fnfall;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class ContainerActivity extends AppCompatActivity implements View.OnClickListener {


    String operator;
    ImageView img;
    Button btnNewFnf,btnFnfList,btnInternet,btnBundle,btnPrepaidPack,btnUssd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        operator = getIntent().getStringExtra("name");
        initializeAll();
        addIniTialiZe();

        switch (operator){
            case "gp":
                img.setBackgroundResource(R.drawable.ic_gp_template);
                break;
            case "bl":
                img.setBackgroundResource(R.drawable.ic_bl_template);
                break;
            case "tl":
                img.setBackgroundResource(R.drawable.ic_tl_template);
                break;
            case "rb":
                img.setBackgroundResource(R.drawable.ic_robi_template);
                break;
            case "air":
                img.setBackgroundResource(R.drawable.ic_airtle_template);
                break;
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ContainerActivity.this,MainActivity.class));
    }

    private void initializeAll() {
        img = (ImageView) findViewById(R.id.img_operator_icon);

        btnNewFnf = (Button) findViewById(R.id.btn_new_fnf);
        btnFnfList = (Button) findViewById(R.id.btn_list_fnf);
        btnInternet = (Button) findViewById(R.id.btn_internet_pack);
        btnPrepaidPack = (Button) findViewById(R.id.btn_package);
        btnBundle = (Button) findViewById(R.id.btn_bundle_offer);
        btnUssd = (Button) findViewById(R.id.btn_ussd);

        btnNewFnf.setOnClickListener(this);
        btnFnfList.setOnClickListener(this);
        btnInternet.setOnClickListener(this);
        btnPrepaidPack.setOnClickListener(this);
        btnBundle.setOnClickListener(this);
        btnUssd.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_new_fnf:
                startActivity(new Intent(ContainerActivity.this,NewFnf.class).putExtra("operatorName",operator));
                break;
            case R.id.btn_list_fnf:
                startActivity(new Intent(ContainerActivity.this,FnfList.class).putExtra("operatorName",operator));
                break;
            case R.id.btn_internet_pack:
                startActivity(new Intent(ContainerActivity.this,Internet.class).putExtra("operatorName",operator));
                break;
            case R.id.btn_package:
                startActivity(new Intent(ContainerActivity.this,PrepaidActivity.class).putExtra("operatorName",operator));
                break;
            case R.id.btn_bundle_offer:
                startActivity(new Intent(ContainerActivity.this,BundleOffer.class).putExtra("operatorName",operator));
                break;
            case R.id.btn_ussd:
                startActivity(new Intent(ContainerActivity.this,ImportantUSSD.class).putExtra("operatorName",operator));
                break;

        }
    }
}
