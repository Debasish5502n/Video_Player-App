package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;

public class VideoFilesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String MY_PREF ="my pref" ;
    RecyclerView recyclerView;
    private ArrayList<MediaFiles> videofilesArrayList=new ArrayList<>();
    static   VideoFilesAdapter videoFilesAdapter;
    String folder_name;
    SwipeRefreshLayout swipeRefreshLayout;
    String sortOder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folder_name=getIntent().getStringExtra("folderName");
        getSupportActionBar().setTitle(folder_name);
        setContentView(R.layout.activity_video_files);
        recyclerView=findViewById(R.id.video_list);
        swipeRefreshLayout=findViewById(R.id.swipe_layout);
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
        videofilesArrayList = fetchMedia(folder_name);
        videoFilesAdapter =new VideoFilesAdapter(videofilesArrayList,this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,1));
        recyclerView.setAdapter(videoFilesAdapter);
        videoFilesAdapter.notifyDataSetChanged();
    }
    @SuppressLint("Range")
    public ArrayList<MediaFiles> fetchMedia(String folderName) {
        SharedPreferences sharedPreferences=getSharedPreferences(MY_PREF,MODE_PRIVATE);
        String sort_value=sharedPreferences.getString("sort","abcd");

        ArrayList<MediaFiles> videoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        if (sort_value.equals("sortName")){
            sortOder=MediaStore.MediaColumns.DISPLAY_NAME+" ASC";
        }else if (sort_value.equals("sortSize")){
            sortOder=MediaStore.MediaColumns.SIZE+" DESC";
        }else if (sort_value.equals("sortDate")){
            sortOder=MediaStore.MediaColumns.DATE_ADDED+" DESC";
        } else  {
            sortOder=MediaStore.MediaColumns.DURATION+" DESC";
        }
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArg = new String[]{"%" + folderName + "%"};

        Cursor cursor=getContentResolver().query(uri,null,selection,selectionArg,sortOder);

        if (cursor!= null && cursor.moveToNext()){
            do {
                Long id = Long.valueOf(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String dataAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                String size = String.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))));
                String data=String.valueOf(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,id));

                String duration_formated;
                int sec=(duration /1000)%60;
                int min=(duration /(1000*60))%60;
                int hrs=duration /(1000*60*60);

                if (hrs==0) {
                    duration_formated = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                }else {
                    duration_formated = String.valueOf(hrs).concat(":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK,"%02d",sec)))));
                }
                MediaFiles mediaFolder = new MediaFiles(id, title,path,duration_formated, displayName , dataAdded, size,data);

                videoFiles.add(mediaFolder);
            }while (cursor.moveToNext());
        }
        return videoFiles;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input=newText.toLowerCase();
        ArrayList<MediaFiles> mediaFiles=new ArrayList<>();
        for (MediaFiles media:videofilesArrayList){
            if (media.getTitle().toLowerCase().contains(input)){
                mediaFiles.add(media);
            }
        }
        VideoFilesActivity.videoFilesAdapter.updatrVideoFiles(mediaFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences=getSharedPreferences(MY_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        int id=item.getItemId();
        switch (id){
            case R.id.refresh:
                finish();
                startActivity(getIntent());
                break;
            case R.id.sortbyorder:
                AlertDialog.Builder alertDilog=new AlertDialog.Builder(this);
                alertDilog.setTitle("Sort by");
                alertDilog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.apply();
                        finish();
                        startActivity(getIntent());
                        dialog.dismiss();

                    }
                });
                String[] items={"Name (A to Z)","Size (Big to Small)","Date (New to Old)","Length (Long to Sort)"};
                alertDilog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                editor.putString("sort","sortName");
                                break;
                            case 1:
                                editor.putString("sort","sortSize");
                                break;
                            case 2:
                                editor.putString("sort","sortDate");
                                break;
                            case 3:
                                editor.putString("sort","sortLength");
                                break;
                        }
                    }
                });
                alertDilog.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}