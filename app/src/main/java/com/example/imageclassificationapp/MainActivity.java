package com.example.imageclassificationapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imageclassificationapp.model.Photo;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_MULTIPLE = 99;
    @BindView(R.id.uploadButton)
    Button uploadButton;
    private int column_index;
    private String imagePath;
    public static final int REQUEST_PERMISSION_MULTIPLE = 0;
    public static final int REQUEST_WRITE_EXTERNAL = 3;
    public static final int REQUEST_READ_EXTERNAL = 4;

    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    String imageEncoded;
    List<String> imagesEncodedList;
    private ViewModel viewModel;
    private String TAG = "Main Activity";
    String token;
    List<Photo> photos = new ArrayList<>();
    ResultAdapter resultAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndRequestPermissions(this);
        setContentView(R.layout.activity_main);
        viewModel = ViewModel.getInstance();
        recyclerView =findViewById(R.id.recyclerView);
        resultAdapter = new ResultAdapter(getApplicationContext());
        resultAdapter.setItems(photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(resultAdapter);
   /*     BackgroundUpdatesRunningReceiver backgroundUpdatesRunningReceiver = new BackgroundUpdatesRunningReceiver();
        IntentFilter filter = new IntentFilter(BroadcastConst.RECEIVED_PUSH_NOTIFICATION*//*BackgroundUpdateService.class.getName()*//*);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        getApplicationContext().registerReceiver(backgroundUpdatesRunningReceiver, filter);*/
        Log.d("Firebase", "token "+ FirebaseInstanceId.getInstance().getToken());

        ViewModelProviders.of(this).get(ViewModel.class);
        ButterKnife.bind(this, getWindow().getDecorView());
        observePredictions();



        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Thread t1 = new Thread(new Runnable() {
                    public void run()
                    {
                        while (1>0)
                        {
                            APICalls.getResult(getApplicationContext(), new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    if(response.body() != null){
                                        String[] results =  response.body().split("`");
                                        for(int i=0;i<results.length;i++){
                                            String[] ary = results[i].split(",");
                                            for(int j =0; j < ary.length;j++){
                                               // ary[j]=ary[j].trim();
                                                ary[j]=ary[j].replace("[","");
                                                ary[j]=ary[j].replace("]","");
                                            }
                                            if(ary.length>2){
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Photo photo = new Photo();
                                                SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("_", MODE_PRIVATE);
                                                String userName = mPrefs.getString("userName", null);
                                                photo.highestScore=ary[0];
                                                photo.secondHighestScore=ary[1];
                                                photo.thirdHighestScore=ary[2];
                                                photo.photoName=ary[3];
                                                photo.userName=userName;
                                                photos.add(photo);
                                                resultAdapter.addItem(photo);
                                                resultAdapter.notifyDataSetChanged();
                                        }

/*                                            highestScore.setText(ary[0]);
                                            if(ary.length>1)
                                            secondHighestScore.setText(ary[1]);
                                            if(ary.length>2)
                                            thirdHighestScore.setText(ary[2]);*/
                                        }

                                    }

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }});
                t1.start();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
/*                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE );*/
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();

                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                try {
                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                        APICalls.sendImageForClassification(filePath, getApplicationContext(), new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                   int x =0;
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });
                    } else {
                        //NOT IN REQUIRED FORMAT
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
    }
   /*@Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       try {
           // When an Image is picked
           if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                   && null != data) {
               // Get the Image from data

               String[] filePathColumn = { MediaStore.Images.Media.DATA };
               imagesEncodedList = new ArrayList<String>();
               if(data.getData()!=null){

                   Uri mImageUri=data.getData();
                   String filePath = getPath(mImageUri);
                   // Get the cursor
                   Cursor cursor = getContentResolver().query(mImageUri,
                           filePathColumn, null, null, null);
                   // Move to first row
                   cursor.moveToFirst();

                   int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                   imageEncoded  = cursor.getString(columnIndex);
                   cursor.close();

               } else {
                   if (data.getClipData() != null) {
                       ClipData mClipData = data.getClipData();
                       ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                       for (int i = 0; i < mClipData.getItemCount(); i++) {

                           ClipData.Item item = mClipData.getItemAt(i);
                           Uri uri = item.getUri();
                           mArrayUri.add(uri);
                           // Get the cursor
                           Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                           // Move to first row
                           cursor.moveToFirst();
                           String filePath = getPath(uri);
                           int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                           imageEncoded  = cursor.getString(columnIndex);
                           imagesEncodedList.add(filePath);
                           cursor.close();
                           APICalls.sendImagesForClassification(imagesEncodedList, getApplicationContext(), new Callback<String>() {
                               @Override
                               public void onResponse(Call<String> call, Response<String> response) {
                                   int x =0;
                               }

                               @Override
                               public void onFailure(Call<String> call, Throwable t) {
                                   t.printStackTrace();
                               }
                           });
                       }
                       Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                   }
               }
           } else {
               Toast.makeText(this, "You haven't picked Image",
                       Toast.LENGTH_LONG).show();
           }
       } catch (Exception e) {
           Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                   .show();
       }

       super.onActivityResult(requestCode, resultCode, data);
   }*/
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }
    public static boolean checkAndRequestPermissions(Activity activity) {
        System.out.println("PermissionsUtils checkAndRequestPermissions()");

        int permissionReadExternal = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternal = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // Permission List
        List<String> listPermissionsNeeded = new ArrayList<>();

        // Camera Permission
        if (permissionReadExternal != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "Camera Permission is required for this app to run", Toast.LENGTH_SHORT)
                        .show();
            }
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // Read/Write Permission
        if (permissionWriteExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_PERMISSION_MULTIPLE);
            return false;
        }

        return true;
    }

    public static void requestReadStorage(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "LOCATION permission is needed to display location info ", Toast.LENGTH_SHORT)
                        .show();
                // Show an explanation to the user *asynchronously* -- don't
                // block this thread waiting for the user's response! After the
                // user sees the explanation, try again to request the
                // permission.
                ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                        REQUEST_READ_EXTERNAL);

                Toast.makeText(activity, "REQUEST READ EXTERNAL STORAGE", Toast.LENGTH_LONG).show();

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                        REQUEST_READ_EXTERNAL);
                Toast.makeText(activity, "REQUEST READ EXTERNAL STORAGE", Toast.LENGTH_LONG).show();
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            // Permission is granted
        } else {

        }
    }

    public static void requestWriteExternalPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(activity, "Write permission is needed to create Excel file ", Toast.LENGTH_SHORT).show();
                // Show an explanation to the user *asynchronously* -- don't
                // block this thread waiting for the user's response! After the
                // user sees the explanation, try again to request the
                // permission.
                ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        REQUEST_WRITE_EXTERNAL);

                Toast.makeText(activity, "REQUEST LOCATION PERMISSION", Toast.LENGTH_LONG).show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        REQUEST_WRITE_EXTERNAL);

            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    requestReadStorage(this);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    requestWriteExternalPermission(this);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }


    }
    private void observePredictions(){

       viewModel.predicitions.observe(this,new Observer<String[]>() {
           @Override
           public void onChanged(String[] strings) {
                progressBar.setVisibility(View.INVISIBLE);
      /*          highestScore.setText(strings[0]);
                secondHighestScore.setText(strings[1]);
                thirdHighestScore.setText(strings[2]);*/
           }
       });

    }

    public class BackgroundUpdatesRunningReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
   /*         if (intent.getAction().equals(BroadcastConst.RECEIVED_PUSH_NOTIFICATION)) {
                if (intent.hasExtra(BroadcastConst.firebaseData)) {
                  String  predictionsArray = intent.getStringExtra(BroadcastConst.firebaseData);
                    String[] ary = predictionsArray.split(",");
                    for(int i =0; i < ary.length;i++){
                        ary[i]=ary[i].trim();
                        ary[i]=ary[i].replace("[","");
                        ary[i]=ary[i].replace("]","");
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    highestScore.setText(ary[0]);
                    secondHighestScore.setText(ary[1]);
                    thirdHighestScore.setText(ary[2]);
                }

            }*/
        }
    }

/*    public boolean checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE_EXTERNAL_STORAGE added");
        if (!addPermission(permissionsList, android.Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("READ_EXTERNAL_STORAGE added");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = String.format("%s, %s", message, permissionsNeeded.get(i));
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[0]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return false;
            }
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

        //readContacts();
        return true;

        //insertDummyContact();
    }
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            *//*if (!ContextCompat.shouldShowRequestPermissionRationale(permission))
                return false;*//*
        }
        return true;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Please allow all permission");
                    alertDialogBuilder.setPositiveButton("Yes",
                            (arg0, arg1) -> checkPermissions());
                    alertDialogBuilder.setNegativeButton("No", (dialog, which) -> finish());
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }*/
}
