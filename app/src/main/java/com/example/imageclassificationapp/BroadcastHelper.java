package com.example.imageclassificationapp;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

public class BroadcastHelper {

    public static void notificationReceived(Context context, String data) {
        Intent i = new Intent();
        i.setAction(BroadcastConst.RECEIVED_PUSH_NOTIFICATION);
        i.putExtra(BroadcastConst.firebaseData, data);
        context.sendBroadcast(i);
    }}
