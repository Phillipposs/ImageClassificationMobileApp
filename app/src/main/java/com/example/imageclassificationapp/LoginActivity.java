package com.example.imageclassificationapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.imageclassificationapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private String TAG = "Login Activity";
    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.usernNameText)
    EditText usernNameText;
    @BindView(R.id.passwordText)
    EditText passwordText;
    String token;
    User user;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       // turnOffDozeMode(getApplicationContext());
        ButterKnife.bind(this, getWindow().getDecorView());
         user = new User();
         progressBar.setVisibility(View.VISIBLE);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                         token = task.getResult().getToken();
                       // getSharedPreferences("_", MODE_PRIVATE).edit().putString("deviceToken",token).apply();
                        progressBar.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        System.out.println(token);
                        user.setDeviceToken(token);
                        // Log and toast
                        String msg = token;
                        Log.d(TAG, msg);

                    }
                });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
                user.setName(usernNameText.getText().toString());
                user.setPassword(passwordText.getText().toString());
                getSharedPreferences("_", MODE_PRIVATE).edit().putString("userName",usernNameText.getText().toString()).apply();
                APICalls.userLogin(user, new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        int x = 0;
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        int y = 0;
                    }
                });
            }
        });
    }

    private void startMainActivity() {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(myIntent);
        this.finish();
    }
    public void turnOffDozeMode(Context context){  //you can use with or without passing context
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName)) // if you want to desable doze mode for this package
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else { // if you want to enable doze mode
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            context.startActivity(intent);
        }
    }


}
