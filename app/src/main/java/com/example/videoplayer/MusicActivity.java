package com.example.videoplayer;


import androidx.appcompat.app.AppCompatActivity;

        import android.Manifest;
        import android.content.ClipData;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Environment;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.BaseAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.karumi.dexter.Dexter;
        import com.karumi.dexter.DexterBuilder;
        import com.karumi.dexter.MultiplePermissionsReport;
        import com.karumi.dexter.PermissionToken;
        import com.karumi.dexter.listener.PermissionDeniedResponse;
        import com.karumi.dexter.listener.PermissionGrantedResponse;
        import com.karumi.dexter.listener.PermissionRequest;
        import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
        import com.karumi.dexter.listener.single.PermissionListener;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.List;

public class MusicActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        getSupportActionBar().setTitle("Music Player");
        listView = findViewById(R.id.listView);
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        songlist();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if(songs !=null){
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview=getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textSong=myview.findViewById(R.id.textSong);
            textSong.setSelected(true);
            textSong.setText(items[i]);

            return myview;
        }
    }
    public void songlist(){
        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
        items = new String[mySongs.size()];
        for(int i=0;i<mySongs.size();i++){
            items[i] = mySongs.get(i).getName().replace(".mp3", "");

        }

        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
        // listView.setAdapter(adapter);
        customAdapter customAdapter=new customAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent=new Intent(MusicActivity.this,playSong.class);
                String currentSong = (String) listView.getItemAtPosition(i);
                intent.putExtra("songList",mySongs);
                intent.putExtra("currentSong",currentSong);
                intent.putExtra("position",i);
                startActivity(intent);
            }
        });
    }

}