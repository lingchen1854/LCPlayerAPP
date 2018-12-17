package com.likego.lcplayerapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends Activity {

    private static final String TAG = "ListActivity xmlc";
    private static HashMap<String,String> MediaHash;
    private ProgressBar mProgressBar;
    private Button mButtonAll;
    private Button mButtonDownload;
    private Button mButtonSdcard;
    private static final int MSG_VISIBLE = 1;
    private static final int MSG_GONE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list);

        mProgressBar = findViewById(R.id.progressBarLarge);
        mHandler.sendEmptyMessage(MSG_GONE);
        mButtonAll = findViewById(R.id.all);
        mButtonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ScanSdcard("/storage/emulated/0/").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mButtonAll.setVisibility(View.GONE);
                mButtonDownload.setVisibility(View.GONE);
                mButtonSdcard.setVisibility(View.GONE);

            }
        });
        mButtonDownload = findViewById(R.id.dowload);
        mButtonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ScanSdcard("/storage/emulated/0/Download/").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mButtonAll.setVisibility(View.GONE);
                mButtonDownload.setVisibility(View.GONE);
                mButtonSdcard.setVisibility(View.GONE);
            }
        });
        mButtonSdcard = findViewById(R.id.sdcard);
        mButtonSdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ScanSdcard("/storage/external_storage/sdcard1").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                mButtonAll.setVisibility(View.GONE);
                mButtonDownload.setVisibility(View.GONE);
                mButtonSdcard.setVisibility(View.GONE);
            }
        });




    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case MSG_VISIBLE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case MSG_GONE:
                    mProgressBar.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /***
     * 根据播放路径设置缩略图
     * @param filePath 视频资源的路径
     * @return 返回缩略图的Bitmap对象
     */
    public Bitmap getVideoThumbNail(String filePath) {
        long startTime = System.currentTimeMillis();
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();//60*1000*1000

        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        if (bitmap != null){
//            bitmap = Bitmap.createScaledBitmap(bitmap, 640, 360, true);
            Log.d(TAG, filePath+"  时间 : "+ runTime + " ms "+""+bitmap.getWidth()+" * "+bitmap.getHeight());
            return bitmap;
        }else {
            Resources res=getResources();
            Bitmap nullBitmap = BitmapFactory.decodeResource(res, R.drawable.bitmap_null);
            Log.d(TAG, filePath+"  时间 : "+ runTime + " ms "+""+nullBitmap.getWidth()+" * "+nullBitmap.getHeight());
            return nullBitmap;
        }
    }

    public class ScanSdcard extends AsyncTask<Void, Void, Void> {

        private static final String TAG = "ScanSdcard xmlc";
        private long mStartTime = System.currentTimeMillis();
        private ArrayList MediaNameList;

        private List<MediaInfo> MediaList = new ArrayList<MediaInfo>();

        public ScanSdcard(String path) {
            super();
            scanSdcard(path);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "----------onPreExecute: onCreate Scan Sdcard----------");
            MediaNameList = new ArrayList();
            MediaHash = new HashMap<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ->>>>>--");
            mHandler.sendEmptyMessage(MSG_VISIBLE);
            initMediaImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: -----doInBackground over , You can update UI-----" +
                    "\n ----------视频文件数量 = "+MediaNameList.size());
            if (MediaNameList.size() == 0){
                mButtonAll.setVisibility(View.VISIBLE);
                mButtonDownload.setVisibility(View.VISIBLE);
                mButtonSdcard.setVisibility(View.VISIBLE);
            }
            long endTime = System.currentTimeMillis();
            long runTime = endTime - mStartTime;
            Log.d(TAG, "方法使用时间 : "+ runTime + " ms");
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            StaggeredGridLayoutManager layoutManager = new
                    StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            MediaAdapter adapter = new MediaAdapter(MediaList,ListActivity.this);
            recyclerView.setAdapter(adapter);

        }

        private void scanSdcard(String path){
            File sdcardFile = new File(path);
            File[] sdcardFileList = sdcardFile.listFiles();
            if (sdcardFileList == null) {
                Log.d(TAG, "f is null");
                return;
            }
            for (File f : sdcardFileList) {
                if (f.isFile()){
                    if (f.getName().toLowerCase().endsWith(".mp4")
                            || f.getName().toLowerCase().endsWith(".mkv")){
                        MediaNameList.add(f.getName());
                        MediaHash.put(f.getName(),f.getAbsolutePath());
                        Log.d(TAG, "scanSdcard: "+f.getName());
                    }
                }else if (f.isDirectory()) {
                    scanSdcard(f.getPath());
                    Log.d(TAG, "scanSdcard: "+f.getName());
                }
            }
        }

        private void initMediaImage(){
            mHandler.sendEmptyMessage(MSG_VISIBLE);
            for (int i = 0; i < MediaNameList.size();i++){
                Bitmap bitmap = getVideoThumbNail(MediaHash.get(MediaNameList.get(i)));
                MediaInfo info = new MediaInfo(MediaNameList.get(i).toString(),
                        bitmap);
                MediaList.add(info);
            }
            mHandler.sendEmptyMessage(MSG_GONE);
        }

    }

    public static HashMap<String, String> getMediaHash() {
        return MediaHash;
    }

}
