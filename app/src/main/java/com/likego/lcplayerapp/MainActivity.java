package com.likego.lcplayerapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private String TAG = "MainActivity xmlc";

    private String[] mPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE };

    private int READ_EXTERNAL_STORAGE_CONSTANT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");

        getPermissions();

    }

    private void getPermissions(){
        int permission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: 表示已经授权,进入列表");
            Intent startList = new Intent("com.likego.xmlc.Player.ListActivity");
            startActivity(startList);
            finish();
        }else {
            Log.d(TAG, "onCreate: 表示权限被否认或首次请求权限，当前申请权限，并监听申请结果");
            ActivityCompat.requestPermissions(MainActivity.this,mPermissions,
                    READ_EXTERNAL_STORAGE_CONSTANT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CONSTANT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "onRequestPermissionsResult: 监听到是当前申请的权限，且SDK的版本>=AndroidM(6.0)");
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.d(TAG, "权限被拒绝，进入设置打开权限");
                AskForPermission();
            }else {
                Log.d(TAG, "已获得权限，打开列表");
                Intent startList = new Intent("com.likego.xmlc.Player.ListActivity");
                startActivity(startList);
                finish();
            }
        }
    }

    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("需要权限!");
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this,mPermissions,
                        READ_EXTERNAL_STORAGE_CONSTANT);
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }
        });
        builder.create().show();
    }

}
