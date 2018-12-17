package com.likego.lcplayerapp;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class MediaPlayerActivity extends Activity {
    private  String TAG = "MediaPlayerActivity xmlc";
    private MediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_player);

        mSurfaceView = findViewById(R.id.SurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        Log.d(TAG, "onCreate: "+mSurfaceHolder.toString());
        mSurfaceHolder.addCallback(callback);

    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                Intent intent = getIntent();
                String path = intent.getStringExtra("path");
                Log.d(TAG, "surfaceCreated: " + path);
                Uri uri = Uri.parse(path);
                mMediaPlayer = MediaPlayer.create(MediaPlayerActivity.this,uri);
                mMediaPlayer.setDisplay(mSurfaceHolder);
//                mMediaPlayer.prepareAsync();
                mMediaPlayer.start();

                mediaPlayerListener();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            Log.d(TAG, "surfaceChanged: ");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "surfaceDestroyed: ");
        }
    };





    private void mediaPlayerListener(){
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onCompletion: 播放结束");
                if (mMediaPlayer != null){
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.e(TAG, "onError: 播放错误"+i+","+i1);
                if (i == 1 && i1 == -1){
                    Log.d(TAG, "onError: 1");
                }else if (i == 100 && i1 == 0){
                    Log.d(TAG, "onError: 错误代码（100，0），该视频路径对应的存储设备不存在，无法缓存");
                }else if (i == -38 && i1 == 0){
                    Log.d(TAG, "onError: -38，0.播U盘1，切音乐，拔U盘1，切视频，空路径请求下一曲/突然拔U盘也会");
                }else if (i == 1 && i1 == -2147483648){
                    Log.d(TAG, "onError: Play方法出错，reset,缓存");
                }else if (i == 1 && i1 == -33554449){
                    Log.d(TAG, "onError: 拔U盘触发的");
                }else if (i == 1 && i1 == -33554485){
                    Log.d(TAG, "onError: 不支持的文件格式");
                }else {
                    Log.d(TAG, "onError: else");
                }
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
