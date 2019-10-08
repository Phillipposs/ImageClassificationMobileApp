package com.example.imageclassificationapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.imageclassificationapp.firebase.MyFirebaseMessagingService;
import com.example.imageclassificationapp.model.User;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static android.content.Context.MODE_PRIVATE;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private static ViewModel viewModel;
    public MutableLiveData<String[]> predicitions = new MutableLiveData<>();
    private User user;

    public static ViewModel getInstance(){
        if(viewModel== null)
            viewModel = new ViewModel();
        return viewModel;
    }

    public void getPredictions(String s, Context context){
        SharedPreferences mPrefs = context.getSharedPreferences("_", MODE_PRIVATE); //add key
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        System.out.println("entered view model - get predictions");
        String predictionsArray = mPrefs.getString("predictions", null);

        String[] ary = predictionsArray.split(",");
        for(int i =0; i < ary.length;i++){
            ary[i]=ary[i].trim();
            ary[i]=ary[i].replace("[","");
            ary[i]=ary[i].replace("]","");
        }
        predicitions.postValue(ary);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
