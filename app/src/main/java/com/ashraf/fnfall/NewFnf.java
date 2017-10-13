package com.ashraf.fnfall;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Collections;

import static android.R.attr.name;
import static android.R.attr.type;
import static com.ashraf.fnfall.R.drawable.contact;


public class NewFnf extends AppCompatActivity {

    EditText editContact;
    TextView tv;
    LinearLayout ll;

    String fnfType;
    String contactID;
    String contactNumber,contactName;
    String finalFnfNumber="";
    ListView lv;
    ArrayList<String > personName = new ArrayList<>();
    ArrayList<String > personPhone = new ArrayList<>();

    RadioGroup radioGroup;
    RadioButton rbAdd,rbSuper,rbDelete;

    private String operatorName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_fnf);

        initializeAll();
        addIniTialiZe();

        editContact.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String number;
                number = editContact.getText().toString();
                int len = number.length();
                if (len == 11){
                    editContact.setText("");
                    dataUpdate("0",number);
                }else{
                    Toast.makeText(NewFnf.this,"Number Must Be 11 character",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int possition, long l) {

                Toast.makeText(getApplicationContext(),personPhone.get(possition),Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int possition, long l) {
                dataDelete(possition);
                return false;
            }
        });


    }
    private void addIniTialiZe() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2122789248840144~8578449717");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void initializeAll() {
        editContact = (EditText) findViewById(R.id.edit_phone);
        lv = (ListView) findViewById(R.id.list_item);
        radioGroup = (RadioGroup) findViewById(R.id.radio_select);

        rbAdd = (RadioButton) findViewById(R.id.radio_add);
        rbSuper = (RadioButton) findViewById(R.id.radio_super);
        rbDelete = (RadioButton) findViewById(R.id.radio_remove);

        tv = (TextView) findViewById(R.id.text_warning);
        ll = (LinearLayout) findViewById(R.id.layout_linear);
        operatorName = getIntent().getStringExtra("operatorName");
    }


    public void updateListView(){

        if (personPhone.size() < 1){
            tv.setVisibility(View.GONE);
        }else
            tv.setVisibility(View.VISIBLE);

        ArrayAdapter<String > adapter = new ArrayAdapter<String>(this,R.layout.list_view_item,R.id.lv_item, personName);
        lv.setAdapter(adapter);
    }
    public void reverse(ArrayList<String > reversable){
        Collections.reverse(reversable);
    }

    public void selectContact(View v){
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),1);
    }

    public void confirm(View v){

        int id = radioGroup.getCheckedRadioButtonId();

        if (id == R.id.radio_add){
            fnfType = "add";
            if (makeFinalNumber()){
                opCheckAndAction();
                radioGroup.clearCheck();

            }

        }else if (id == R.id.radio_super){
            fnfType = "super";
            if (makeFinalNumber()){
                opCheckAndAction();
                radioGroup.clearCheck();
            }
        }else if (id == R.id.radio_remove){
            fnfType = "delete";
            if (makeFinalNumber()){
                opCheckAndAction();
                radioGroup.clearCheck();
            }
        }else
            Toast.makeText(getApplicationContext(),"Not Select Any",Toast.LENGTH_SHORT).show();
    }


    public void dataUpdate(String newContactName,String newContactPhone){

        boolean exist = false;
        for (int i = 0; i <personPhone.size(); i++){

            if (personPhone.get(i).equals(newContactPhone)) {
                exist = true;
                break;
            }
        }

        if (!exist){

            reverse(personName);
            reverse(personPhone);


            if (!newContactName.equals("0")){
                personName.add(newContactName);
                personPhone.add(newContactPhone);

            }else
            {
                personName.add(newContactPhone);
                personPhone.add(newContactPhone);

            }
            reverse(personName);
            reverse(personPhone);

            updateListView();
        }else{
            showToast("Contact number Already Added");
            exist = false;
        }

    }

    public void dataDelete(int indexNo){
        personName.remove(indexNo);
        personPhone.remove(indexNo);

        updateListView();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // first need to get contact id then contact number by contact id
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){

            // get contact id
            Uri contactData = data.getData();
            Cursor cursorID = getContentResolver().query(contactData,new String []{ContactsContract.Contacts._ID},
                    null,null,null);

            if (cursorID.moveToFirst()){
                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            }

            cursorID.close();


            // now contact number
            Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String []{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? and "+
                            ContactsContract.CommonDataKinds.Phone.TYPE +" = "+
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    new String []{contactID},null);

            if (cursorPhone.moveToFirst()){
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            cursorPhone.close();

            // Contact Name
            Cursor cursorName = getContentResolver().query(contactData,null,null,null,null);
            if (cursorName.moveToFirst()){

                contactName = cursorName.getString(cursorName.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }

            cursorName.close();

            if (!contactName.equals("0")){

                dataUpdate(contactName,contactNumber);
                contactName = "0";
            }else
            {
                dataUpdate("0",contactNumber);
            }
        }
    }

    public void opCheckAndAction(){

        switch (operatorName){
            case "gp":
                gp();
                break;
            case "bl":
                bl();
                break;
            case "tl":
                tl();
                break;
            case "rb":
                rb();
                break;
            case "air":
                air();
                break;

        }
    }

    public boolean makeFinalNumber(){

        if (personPhone.isEmpty()){
            showToast("FnF number can,t be empty");
            return false;
        }else{
            for (int i = 0; i < personPhone.size(); i++){
                finalFnfNumber += " "+personPhone.get(i);
            }
            return true;
        }


    }

    public void sendMessage(String sendNumber,String fnfNumber,String type){
        SmsManager smsManager = SmsManager.getDefault();

        AlertDialog.Builder alert = new  AlertDialog.Builder(this);
        try {
            smsManager.sendTextMessage(sendNumber,null,(type+fnfNumber),null,null);
            alert.setMessage("Fnf Message Send Successfully, Shortly You Will Got A Confirmation Message");
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(NewFnf.this,ContainerActivity.class).putExtra("name",operatorName));
                }
            });

        }catch (Exception e){
            alert.setMessage("SMS send failed, Please try again later!");
            alert.setPositiveButton("TryAgain", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(NewFnf.this,ContainerActivity.class));
                }
            });
            e.printStackTrace();
        }
        AlertDialog dialog = alert.create();
        alert.show();
    }

    public void gp(){
        switch (fnfType){
            case "add":
                sendMessage("2888",finalFnfNumber," ");
                break;
            case "super":
                sendMessage("2888",finalFnfNumber,"SF");
                break;
            case "delete":
                sendMessage("2888",finalFnfNumber,"D");
                break;
        }
    }

    public void bl(){
        switch (fnfType){
            case "add":
                sendMessage("3300",finalFnfNumber,"Add");
                break;
            case "super":
                sendMessage("3300",finalFnfNumber,"Sadd");
                break;
            case "delete":
                sendMessage("3300",finalFnfNumber,"Rem");
                break;
        }
    }
    public void tl(){
        switch (fnfType){
            case "add":
                sendMessage("363",finalFnfNumber,"Add");
                break;
            case "super":
                showToast("Sorry Teletalk Have No Super Fnf");
                break;
            case "delete":
                sendMessage("363",finalFnfNumber,"Del");
                break;
        }
    }
    public void rb(){
        switch (fnfType){
            case "add":
                sendMessage("8363",finalFnfNumber,"A");
                break;
            case "super":
                sendMessage("8363",finalFnfNumber,"P");
                break;
            case "delete":
                sendMessage("8363",finalFnfNumber,"D");
                break;
        }
    }
    public void air(){
        switch (fnfType){
            case "add":
                sendMessage("7353",finalFnfNumber,"Add");
                break;
            case "super":
                sendMessage("7353",finalFnfNumber,"Gang");
                break;
            case "delete":
                sendMessage("7353",finalFnfNumber,"Delete");
                break;
        }
    }

    public void showToast(String str){
        Toast.makeText(getApplicationContext(),str+"",Toast.LENGTH_SHORT).show();
    }
    public void home(View v){
        startActivity(new Intent(this,MainActivity.class));
    }
}
