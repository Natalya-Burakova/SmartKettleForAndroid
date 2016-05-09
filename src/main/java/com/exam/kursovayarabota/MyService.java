package com.exam.kursovayarabota;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MyService extends Service implements Runnable {

    Thread t2;
    MediaPlayer player;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread t2 = new Thread(this,"child");
        player = MediaPlayer.create(this,R.raw.timetea);
        t2.start();
        return Service.START_STICKY;
    }
    @Override
    public void run() {
        try {
                t2.sleep(1000*60*60*2);
                player.start();
                t2.sleep(1000*60*60*2);
                player.start();
                t2.sleep(1000*60*60*2);
                player.start();
        }catch(Exception ex) {System.out.println("Doesnt work!");}
    }

}
