package com.exam.kursovayarabota;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        try {
            Bundle intentExtras = intent.getExtras();
            if (intentExtras != null) {
                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                String smsMessageStr = "";
                for (int i = 0; i < sms.length; ++i) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]); // Get SMS text
                    String smsBody = smsMessage.getMessageBody().toString();
                    if (smsBody.equals("Start")) {
                        abortBroadcast();
                        Toast.makeText(context, "Чайник вскипел!", Toast.LENGTH_SHORT).show();
                        Intent mIntent = new Intent(context, Starter.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mIntent.putExtra("key", smsBody);
                        context.startActivity(mIntent);

                    }
                    if (smsBody.equals("water")) {
                        abortBroadcast();
                        Toast.makeText(context, "В чайнике нет воды!", Toast.LENGTH_SHORT).show();
                        Intent mIntent = new Intent(context, Starter.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mIntent.putExtra("key", smsBody);
                        context.startActivity(mIntent);
                    }
                }
            }
        } catch (Exception ex) {System.out.println("Fatal Error!");}
    }
}

