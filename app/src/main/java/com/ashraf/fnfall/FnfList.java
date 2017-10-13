package com.ashraf.fnfall;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class FnfList extends AppCompatActivity {

    private String operatorName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnf_list);

        addIniTialiZe();
        operatorName = getIntent().getStringExtra("operatorName");
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

    public void press(View v){

        switch (operatorName){
            case "gp":
                sendRequest("2888","FF");
                break;
            case "bl":
                sendRequest("3300","FF");
                break;
            case "tl":
                sendRequest("363","See");
                break;
            case "rb":
                sendRequest("8363","F");
                break;
            case "air":
                sendRequest("7353","List");
                break;

        }


    }

    public void sendRequest(String number,String message){

        SmsManager smsManager = SmsManager.getDefault();
        try{
            smsManager.sendTextMessage(number,null,message,null,null);

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("You will get shortly a sms of your fnf list");
            alert.setCancelable(true);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(FnfList.this,MainActivity.class));
                }
            });

            AlertDialog dialog = alert.create();
            dialog.show();

        }catch (Exception e){
            Toast.makeText(this,"Sms send Failed",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
