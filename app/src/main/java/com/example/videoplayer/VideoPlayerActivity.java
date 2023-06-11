package com.example.videoplayer;

import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_DEFAULT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.net.SocketImpl;
import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<MediaFiles> mVideoFiles=new ArrayList<>();
    int position;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    int VideoID;
    String videoTitle;
    TextView title;
    private ControlsMode controlsMode;
    public enum ControlsMode{
        LOCK,FUIISCREEN;
    }
    ImageView nextButton,previousButton;
    ImageView videoBack,lock,unLock,scalling;
    RelativeLayout root;

    //RecyclerView Icons
    private ArrayList<IconModel> iconModelArrayList=new ArrayList<>();
    PlayBackIconAdapter playBackIconAdapter;
    RecyclerView recyclerView;
    boolean expand=false;
    View nightmode;
    boolean Dark=false;
    boolean mute=false;
    PlaybackParameters playbackParameters;
    float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenOriatintion();
        fullScreen();
        setContentView(R.layout.activity_video_player);

        initializwView();

        VideoID= (int) getIntent().getExtras().getLong("position");
        videoTitle=getIntent().getStringExtra("video_name");


        nextButton=findViewById(R.id.nextvideo);
        previousButton=findViewById(R.id.exo_prev);
        title=findViewById(R.id.video_title);
        title.setText(videoTitle);
        videoBack=findViewById(R.id.video_back);
        lock=findViewById(R.id.lock);
        unLock=findViewById(R.id.unlock);
        root=findViewById(R.id.root_layout);
        scalling=findViewById(R.id.scaling);
        recyclerView=findViewById(R.id.recycler_icon);
        nightmode=findViewById(R.id.night_mode);

        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        videoBack.setOnClickListener(this);
        lock.setOnClickListener(this);
        unLock.setOnClickListener(this);
        scalling.setOnClickListener(firstListiner);

        iconModelArrayList.add(new IconModel(R.drawable.right,""));
        iconModelArrayList.add(new IconModel(R.drawable.night,"Night"));
        iconModelArrayList.add(new IconModel(R.drawable.mute,"Mute"));
        iconModelArrayList.add(new IconModel(R.drawable.rotate,"Rotate"));

        playBackIconAdapter=new PlayBackIconAdapter(iconModelArrayList,this);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL,true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(playBackIconAdapter);
        playBackIconAdapter.notifyDataSetChanged();

        playBackIconAdapter.setOnItemClickedListner(new PlayBackIconAdapter.onitenClickedListener() {
            @Override
            public void onItemClicked(int position) {
                if (position==0){
                    if (position==0){
                        if (expand){
                            iconModelArrayList.clear();
                            iconModelArrayList.add(new IconModel(R.drawable.right,""));
                            iconModelArrayList.add(new IconModel(R.drawable.night,"Night"));
                            iconModelArrayList.add(new IconModel(R.drawable.mute,"Mute"));
                            iconModelArrayList.add(new IconModel(R.drawable.rotate,"Rotate"));
                            playBackIconAdapter.notifyDataSetChanged();
                            expand=false;
                        }else {
                            if (iconModelArrayList.size() == 4){
                                iconModelArrayList.add(new IconModel(R.drawable.volume,"Volume"));
                                iconModelArrayList.add(new IconModel(R.drawable.brightness,"Brightness"));
                                iconModelArrayList.add(new IconModel(R.drawable.equalizer,"Equalizer"));
                                iconModelArrayList.add(new IconModel(R.drawable.speed,"Speed"));
                                iconModelArrayList.add(new IconModel(R.drawable.subtitles,"Subtitles"));
                            }
                            iconModelArrayList.set(position,new IconModel(R.drawable.left," "));
                            playBackIconAdapter.notifyDataSetChanged();
                            expand=true;
                        }
                    }
                }
                if (position==1){
                    if (Dark){
                        nightmode.setVisibility(View.INVISIBLE);
                        iconModelArrayList.set(position,new IconModel(R.drawable.night,"Night"));
                        playBackIconAdapter.notifyDataSetChanged();
                        Dark=false;
                    }else {
                        nightmode.setVisibility(View.VISIBLE);
                        iconModelArrayList.set(position,new IconModel(R.drawable.day,"Day"));
                        playBackIconAdapter.notifyDataSetChanged();
                        Dark=true;
                    }
                }
                if (position==2){
                    if (mute){
                        player.setVolume(100);
                        iconModelArrayList.set(position,new IconModel(R.drawable.mute,"Mute"));
                        playBackIconAdapter.notifyDataSetChanged();
                        mute=false;
                    }else {
                        player.setVolume(0);
                        iconModelArrayList.set(position,new IconModel(R.drawable.volume,"Un Mute"));
                        playBackIconAdapter.notifyDataSetChanged();
                        mute=true;
                    }
                }
                if (position==3){
                   if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                       playBackIconAdapter.notifyDataSetChanged();
                   }else  if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                       playBackIconAdapter.notifyDataSetChanged();
                   }
                }
                if (position==4){
                    VolumeDilog volumeDilog=new VolumeDilog();
                    volumeDilog.show(getSupportFragmentManager(),"Dialog");
                    playBackIconAdapter.notifyDataSetChanged();
                }
                if (position==5){
                                    BrightnessDilog brightnessDilog=new BrightnessDilog();
                                    brightnessDilog.show(getSupportFragmentManager(),"Dialog");
                                    playBackIconAdapter.notifyDataSetChanged();

                }
                if (position==6){
                    Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    if ((intent.resolveActivity(getPackageManager()) != null)) {
                        startActivityForResult(intent, 100);
                    } else {
                        Toast.makeText(getApplicationContext(), "No equalizer found", Toast.LENGTH_SHORT).show();
                    }
                    playBackIconAdapter.notifyDataSetChanged();
                }
                if (position==7){
                    AlertDialog.Builder builder=new AlertDialog.Builder(VideoPlayerActivity.this);
                    builder.setTitle("Select play back speed").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    String[] items={"0.5x","1x Normal Speed","1.25x","1.5x","2x"};
                    int checkItem=-1;
                    builder.setSingleChoiceItems(items, checkItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    speed=0.5f;
                                    playbackParameters=new PlaybackParameters(speed);
                                    player.setPlaybackParameters(playbackParameters);
                                    break;
                                case 1:
                                    speed=1f;
                                    playbackParameters=new PlaybackParameters(speed);
                                    player.setPlaybackParameters(playbackParameters);
                                    break;
                                case 2:
                                    speed=1.25f;
                                    playbackParameters=new PlaybackParameters(speed);
                                    player.setPlaybackParameters(playbackParameters);
                                    break;
                                case 3:
                                    speed=1.5f;
                                    playbackParameters=new PlaybackParameters(speed);
                                    player.setPlaybackParameters(playbackParameters);
                                    break;
                                case 4:
                                    speed=2f;
                                    playbackParameters=new PlaybackParameters(speed);
                                    player.setPlaybackParameters(playbackParameters);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
                }
            }

        });

    }

    private void initializwView() {
        playerView=findViewById(R.id.playerView);
    }
    private void initilizePlayer(){
        player= new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        Uri videoUri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,VideoID);
        MediaSource mediaSource=buildMediaSource(videoUri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        player.setPlaybackParameters(playbackParameters);

    }

    private MediaSource buildMediaSource(Uri uri){
        DataSource.Factory dataSourceFactory=new DefaultDataSourceFactory(this,getString(R.string.app_name));
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }
    private void releasePlayer(){
        if (player !=null){
            player.release();
            player=null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT>=24){
            initilizePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT<24 || player==null){
            initilizePlayer();
        }
    }

    @Override
    protected void onPause() {
        if (Util.SDK_INT<24){
            releasePlayer();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (Util.SDK_INT>=24){
            releasePlayer();
        }
        super.onStop();
    }
    private void fullScreen() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextvideo:
                try {
                    player.stop();
                    VideoID++;
                    initilizePlayer();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "No next Video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case  R.id.exo_prev:
                try {
                    player.stop();
                    VideoID--;
                    initilizePlayer();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "No next Video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.video_back:
                if (player!=null){
                    player.release();
                }
                finish();
                break;
            case R.id.lock:
                controlsMode=ControlsMode.FUIISCREEN;
                root.setVisibility(View.VISIBLE);
                lock.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Unlocked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unlock:
                controlsMode=ControlsMode.LOCK;
                root.setVisibility(View.INVISIBLE);
                lock.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Locked", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    final View.OnClickListener firstListiner = new View.OnClickListener() {
        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scalling.setImageResource(R.drawable.fullscreen);

            Toast.makeText(getApplicationContext(), "Full Screen", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(SecondListner);
        }
    };
    final View.OnClickListener SecondListner = new View.OnClickListener() {
        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scalling.setImageResource(R.drawable.zoom);

            Toast.makeText(getApplicationContext(), "Zoom", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(ThirdListner);
        }
    };
    final View.OnClickListener ThirdListner = new View.OnClickListener() {
        @SuppressLint("WrongConstant")
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scalling.setImageResource(R.drawable.fit);

            Toast.makeText(getApplicationContext(), "Fit", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(firstListiner);
        }
    };
    private void ScreenOriatintion(){
        try {
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();
            Bitmap bitmap;
            String path=mVideoFiles.get(position).getPath();
            Uri uri=Uri.parse(path);
            retriever.setDataSource(this,uri);
            bitmap=retriever.getFrameAtTime();

            int videoWidth=bitmap.getWidth();
            int videoHight=bitmap.getHeight();
            if (videoWidth>videoHight){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}