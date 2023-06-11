package com.example.videoplayer;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.AlarmManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class allowacessActivity extends AppCompatActivity {
    Button allowbtn;
    public static final int STORAGE_PERMISSION=1;
    public static final int REQUEST_PERMISSION_SETTING=12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allowacess);
        allowbtn=findViewById(R.id.allowbtn);

//        SharedPreferences preferences=getSharedPreferences("Allow access",MODE_PRIVATE);
//
//        String value=preferences.getString("Allow","");
//        if (value.equals("OK")){
//            Intent intent=new Intent(allowacessActivity.this,MainActivity.class);
//            startActivity(intent);
//            finish();
//
//        }else {
//            SharedPreferences.Editor editor=preferences.edit();
//            editor.putString("Allow","OK");
//            editor.apply();
//        }

        allowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)==getPackageManager().PERMISSION_GRANTED){
                   Intent intent=new Intent(allowacessActivity.this,MainActivity.class);
                   startActivity(intent);
                   finish();
               }
               else {
                   ActivityCompat.requestPermissions(allowacessActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION);
               }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==STORAGE_PERMISSION){
            for (int i=0;i<permissions.length;i++){
                String per=permissions[i];
                if (grantResults[i]== PackageManager.PERMISSION_DENIED){
                    boolean showretional=shouldShowRequestPermissionRationale(per);
                    if (!showretional){
                        AlertDialog.Builder builder=new AlertDialog.Builder(this);
                        builder.setTitle("App Permission")
                                .setMessage("For playing videos, You nust allow to access video file on your device"+"\n\n"+
                                        "Now follow the below steps"+"\n\n"+
                                        "Open setting for the bellow button"+"\n"+
                                        "Click on permission"+"\n"+"Allow access to for storage")
                                .setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri =Uri.fromParts("package",getPackageName(),null);
                                        intent.setData(uri);
                                        startActivityForResult(intent,REQUEST_PERMISSION_SETTING);
                                    }
                                }).create().show();
                    }
                    else {
                        ActivityCompat.requestPermissions(allowacessActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION);
                    }
                }else {
                    Intent intent=new Intent(allowacessActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            Intent intent=new Intent(allowacessActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}