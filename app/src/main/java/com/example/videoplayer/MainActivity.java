package com.example.videoplayer;

import static com.example.videoplayer.allowacessActivity.REQUEST_PERMISSION_SETTING;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ArrayList<MediaFiles> mediaFiles=new ArrayList<>();
    private ArrayList<String> allVideoList=new ArrayList<>();
    RecyclerView recyclerView;
    videoFolderAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    FloatingActionButton fb;


    @SuppressLint("UseSupportActionBar")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri =Uri.fromParts("package",getPackageName(),null);
            intent.setData(uri);
            startActivityForResult(intent,REQUEST_PERMISSION_SETTING);
        }
        swipeRefreshLayout=findViewById(R.id.swiperefresh_layout);
        recyclerView=findViewById(R.id.recycler_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));

        fb=findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MusicActivity.class));
            }
        });

            VideoFolders();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VideoFolders();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void VideoFolders() {
        mediaFiles=fetchMedia();
        adapter=new videoFolderAdapter(mediaFiles,allVideoList,this);
        recyclerView.setAdapter(adapter);
        Log.e("video", String.valueOf(adapter));
        adapter.notifyDataSetChanged();
    }
    @SuppressLint("Range")
    public ArrayList<MediaFiles> fetchMedia(){
        ArrayList<MediaFiles> mediaFolderArrayList=new ArrayList<>();
        Uri uri=MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor=getContentResolver().query(uri,null,null,null,null);
        if (cursor !=null && cursor.moveToNext()) {
            do {
                Long id = Long.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String duration = String.valueOf(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
                String path =cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String dataAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                String size = String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

                MediaFiles mediaFolder = new MediaFiles(id, title,path,duration, displayName , dataAdded, size,data);


                int index = path.lastIndexOf("/");

                String subString = path.substring(0, index);
                if (!allVideoList.contains(subString)) {
                    allVideoList.add(subString);
                }
                mediaFolderArrayList.add(mediaFolder);
            }
                while (cursor.moveToNext()) ;
            }return  mediaFolderArrayList;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_folder,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.rateus:
                Uri uri=Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                break;
            case R.id.refresh:
                finish();
                startActivity(getIntent());
                break;
            case R.id.share:
                Intent share_intent=new Intent();
                share_intent.setAction(Intent.ACTION_SEND);
                share_intent.putExtra(Intent.EXTRA_TEXT,"Check this app via\n"+
                        "https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
                share_intent.setType("text/plain");
                startActivity(Intent.createChooser(share_intent,"Share app via"));
                break;
        }
        return true;
    }
}

