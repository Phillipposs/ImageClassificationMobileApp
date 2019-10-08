package com.example.imageclassificationapp.firebase;


import android.util.Log;
import android.view.View;

import com.example.imageclassificationapp.APICalls;
import com.example.imageclassificationapp.ApiResponseHandler;
import com.example.imageclassificationapp.BroadcastHelper;
import com.example.imageclassificationapp.ViewModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle or display both data and notification FCM messages here.
        // Here is where you can display your own notifications built from a received FCM message.
        super.onMessageReceived(remoteMessage);
        System.out.println("entered onreceive");
/*        APICalls.getResult(getApplicationContext(), new ApiResponseHandler() {
            @Override
            public void onResponse(Object response) {

                ViewModel.getInstance().getPredictions((String)response,getApplicationContext());

            }

            @Override
            public void onError(int code) {
                int x =0;
            }

            @Override
            public void onFailure(Throwable t) {
                int y =0;
            }
        });*/
/*        APICalls.getResult(getBaseContext(), new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                System.out.println("OnReceived Success:"+ response.body());
                ViewModel.getInstance().getPredictions(response.body(),getApplicationContext());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("OnReceived Failure");
                t.printStackTrace();
            }
        });*/
        android.os.Debug.waitForDebugger();
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("remote", remoteMessage.getData().toString()).apply();
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {

/*            System.out.println("data>0");
            String predictions = remoteMessage.getData().values().toArray()[0].toString();
            BroadcastHelper.notificationReceived(getApplicationContext(),predictions);
            getSharedPreferences("_", MODE_PRIVATE).edit().putString("predictions",predictions).apply();
            ViewModel.getInstance().getPredictions(predictions,getApplicationContext());*/
          //  String[] ary = predictions.split(",");

/*            for(int i =0; i < ary.length;i++){
                ary[i]=ary[i].trim();
                ary[i]=ary[i].replace("[","");
                ary[i]=ary[i].replace("]","");
            }*/
           // PredictionResponse mainResponse = gson.fromJson(predictions, PredictionResponse.class);
           //

                    //  for(int i=0;i<remoteMessage.getData().get(0).t)
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // displayNotification(remoteMessage.getNotification().getBody());
    }


}