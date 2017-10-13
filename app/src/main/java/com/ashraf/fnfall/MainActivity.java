package com.ashraf.fnfall;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbar;

    Button btnGp,btnBl,btnTl,btnRb,btnAir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        initializeAll();
        addIniTialiZe();
    }
    private void addIniTialiZe() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2122789248840144~8578449717");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    private void initializeAll() {
        btnGp = (Button) findViewById(R.id.btn_gp);
        btnBl = (Button) findViewById(R.id.btn_bl);
        btnTl = (Button) findViewById(R.id.btn_tl);
        btnRb = (Button) findViewById(R.id.btn_rb);
        btnAir = (Button) findViewById(R.id.btn_airtle);

        btnGp.setOnClickListener(this);
        btnBl.setOnClickListener(this);
        btnTl.setOnClickListener(this);
        btnRb.setOnClickListener(this);
        btnAir.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_gp:

                startActivity(new Intent(MainActivity.this,ContainerActivity.class).putExtra("name","gp"));
                break;
            case R.id.btn_bl:
                startActivity(new Intent(MainActivity.this,ContainerActivity.class).putExtra("name","bl"));
                break;
            case R.id.btn_tl:

                startActivity(new Intent(MainActivity.this,ContainerActivity.class).putExtra("name","tl"));
                break;
            case R.id.btn_rb:
                startActivity(new Intent(MainActivity.this,ContainerActivity.class).putExtra("name","rb"));
                break;
            case R.id.btn_airtle:
                startActivity(new Intent(MainActivity.this,ContainerActivity.class).putExtra("name","air"));
                break;

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_update){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Lazy+Coder"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);


    }

    public void home(View v){
        // it is toolbar home onclick method
    }
}
