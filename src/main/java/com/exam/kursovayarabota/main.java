package com.exam.kursovayarabota;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by -- on 09.11.2015.
 */
public class main extends AppCompatActivity implements View.OnClickListener,Runnable {


    private static final String massage = "Start";
    private static final String key = "Save_tele";
    private static final String key2 = "Send_time";
    private static final String key5 = "Send_date";
    private static final String key3 = "Save_time";
    private static final String key4 = "Save_date";
    PendingIntent sentPI;
    boolean flag = true;
    SmsManager sms;
    MediaPlayer player;
    Thread t1;
    SharedPreferences share;
    Button btn2;
    EditText editText;
    ImageButton imbtn1;
    String teleNumber = ""; //Telephone number

    private EditText editText1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.for_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        t1 = new Thread(this,"ChildThread");
        btn2 = (Button) findViewById(R.id.button2);
        editText = (EditText) findViewById(R.id.editText);
        imbtn1 = (ImageButton) findViewById(R.id.imageButton);

        imbtn1.setImageResource(R.drawable.pic);
        imbtn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        try {
           Intent intent6 = getIntent();
            String sms_body = intent6.getStringExtra("key");
            if (sms_body.equals("Start")) {
                voice(R.raw.first);
                share = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = share.edit();
                ed.putInt("key10", 2);
                ed.commit();
            } else if(sms_body.equals("water")) {
                voice(R.raw.water);
                share = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor ed = share.edit();
                ed.putInt("key10",2);
                ed.commit();
            }
        }catch(Exception ex) { System.out.println("Fatal Error!"); }
        loadTele();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_downloarder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        t1 = new Thread(this,"ChildThread");
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            voice(R.raw.shutdown2);
            finish();
            overridePendingTransition(0,R.anim.rotatediag2);
        }
        if(id==R.id.action_clear) {
            voice(R.raw.statclear);
            share = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = share.edit();
            ed.clear().commit();
            Toast.makeText(main.this, "Статистика успешно очищена!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        t1 = new Thread(this,"ChildThread");
        switch (v.getId()) {
            case R.id.button2:
                Intent intent2 = new Intent(this, Statistica.class);
                intent2.putExtra(key2,share.getString(key3,""));
                intent2.putExtra(key5,share.getString(key4,""));
                startActivity(intent2);
                overridePendingTransition(R.anim.rotatediag, R.anim.rotatediag2);
                break;
            case R.id.imageButton:
                flag = true;
                getter(teleNumber);
                break;
        }
    }

    public String getter(String Number) { //Check tele number
        Number = editText.getText().toString();
        if (Number.length() != 11 || Number == null) {
            Toast.makeText(main.this, R.string.uncorrect_number, Toast.LENGTH_SHORT).show();
            voice(R.raw.badnumber);
            editText.setText(null);
            return null;
        } else {
            teleNumber = Number;
            sendSms(teleNumber);
            return teleNumber;
        }
    }

    private void successEvent(String teleNumber) {   //Doing all changes in Activity
        imbtn1.setImageResource(R.drawable.pic2);
        imbtn1.setEnabled(false);
        stopService(new Intent(this, MyService.class));
        voice(R.raw.smssuccess);
        Toast.makeText(main.this, R.string.success, Toast.LENGTH_SHORT).show();
        dateTime(teleNumber);
    }

    private void sendSms(final String teleNumber) { //Send SMS
        sms = SmsManager.getDefault();
        String SENT = "SMS_SENT";
        try {
            sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            t1 = new Thread(main.this, "ChildThread");
            registerReceiver(new BroadcastReceiver() {
                int g = 1;

                @Override
                public void onReceive(Context context, Intent intent) {
                    if (g == 1) {
                        try {
                        t1 = new Thread(main.this, "ChildThread");
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                successEvent(teleNumber);
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Thread.sleep(2700);
                                voice(R.raw.badsignal);
                                g++;
                                Toast.makeText(main.this, "Слишком слабый сигнал", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Thread.sleep(2000);
                                voice(R.raw.lowbalance);
                                g++;
                                Toast.makeText(main.this, "Не хватает средств на телефоне", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Thread.sleep(2700);
                                voice(R.raw.planeon);
                                g++;
                                Toast.makeText(main.this, "Выключите режим в самолёте", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Thread.sleep(2700);
                                voice(R.raw.nosms);
                                g++;
                                Toast.makeText(main.this, "Sim карта не найдена", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }catch(Exception ex) { System.out.println("Fatal Error!");}
                    }
                }
            }, new IntentFilter(SENT));
            sms.sendTextMessage(teleNumber, null, massage, sentPI, null);
            Toast.makeText(main.this, "Отправка сообщения...", Toast.LENGTH_LONG).show();
            voice(R.raw.wait);
        }catch (Exception ex) { System.out.println("Error in Phone!");}
    }

    private void saveTele(String teleNumber,String time,String date) {
        share = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = share.edit();
        ed.putString(key, teleNumber);
        ed.putInt("key10",5);
        ed.putString(key3, time);
        ed.putString(key4, date);
        ed.commit();
    }

    private void loadTele() { // Download strings, picture
        t1 = new Thread(this,"ChildThread");
        share = getPreferences(MODE_PRIVATE);
        startService(new Intent(this, MyService.class));
        editText.setText(share.getString(key, ""));
        if(editText.getText().toString().equals("")
                || editText.getText().toString().equals(null)) {
            try {
                t1.sleep(2000);
            } catch (InterruptedException e) {}
            voice(R.raw.startnumber);
        }
         if(share.getInt("key10", 0)==5) {
             imbtn1.setImageResource(R.drawable.pic2);
           imbtn1.setEnabled(false);
        } else;

    }
    private void dateTime(String teleNumber) {  // Get current Time and Date
        share = getPreferences(MODE_PRIVATE);
        String date = new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()); // Date
        date = date +"                 "+ share.getString(key4,"");
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String time = String.valueOf(format.format(d));  //Time
        time = time +"                 "+ share.getString(key3,"");
        saveTele(teleNumber, time, date);
    }
    public void voice(int a) {
        player = MediaPlayer.create(main.this,a);
        t1.start();
    }

    @Override
    public void run()  {
        try {
            player.start();
        }catch (Exception ex) { System.out.println("Error!"); }
    }
}
