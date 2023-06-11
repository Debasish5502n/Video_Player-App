package com.example.videoplayer;

import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;

        import androidx.appcompat.app.AppCompatActivity;

        import android.animation.Animator;
        import android.animation.AnimatorSet;
        import android.animation.ObjectAnimator;
        import android.content.Intent;
        import android.graphics.PorterDuff;
        import android.media.MediaPlayer;
        import android.media.audiofx.Visualizer;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Handler;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

        import java.io.File;
        import java.util.ArrayList;
        import java.util.Timer;
        import java.util.TimerTask;

public class playSong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        if (visualizer !=null){
            visualizer.release();
        }
        super.onDestroy();
        mediaPlayer.stop();
        //   mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView,txtsstart,txtsstop;
    ImageView play, previous, next,backwoard,forwoard;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent,songName;
    int position;
    SeekBar seekBar;
    Thread updateSeek;
    ImageView imageView;
    BarVisualizer visualizer;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        txtsstart = findViewById(R.id.txtsstart);
        txtsstop = findViewById(R.id.txtsstop);
        imageView = findViewById(R.id.imageView);
        visualizer = findViewById(R.id.visualizer);
        backwoard=findViewById(R.id.backwoard);
        forwoard=findViewById(R.id.forwoard);

        if (mediaPlayer !=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        position = intent.getIntExtra("position", 0);
        textView.setSelected(true);
        Uri uri = Uri.parse(songs.get(position).toString());
        songName = songs.get(position).getName();
        textView.setText(songName);
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        updateSeek = new Thread() {

            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        //   seekBar.setMax(mediaPlayer.getDuration());
        updateSeek.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.MULTIPLY);
        // seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.cardview_light_background), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.SRC_IN);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play_arrow_music);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause_music);
                    mediaPlayer.start();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0) {
                    position = position - 1;
                } else {
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause_music);
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                String endtime = creatTime(mediaPlayer.getDuration());
                txtsstop.setText(endtime);
                seekBar.setMax(mediaPlayer.getDuration());
                startAnnimation(imageView,-360);
                int audiosetionId = mediaPlayer.getAudioSessionId();
                if (audiosetionId != 1) {
                    visualizer.setAudioSessionId(audiosetionId);
                    updateSeek = new Thread() {

                        @Override
                        public void run() {
                            int totalDuration = mediaPlayer.getDuration();
                            int currentPosition = 0;

                            while (currentPosition < totalDuration) {
                                try {
                                    sleep(500);
                                    currentPosition = mediaPlayer.getCurrentPosition();
                                    seekBar.setProgress(currentPosition);
                                } catch (InterruptedException | IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    };
                    updateSeek.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                //  position=((position+1)%songs.size());
                if (position != songs.size() - 1) {
                    position = position + 1;
                } else {
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause_music);
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                String endtime = creatTime(mediaPlayer.getDuration());
                txtsstop.setText(endtime);
                seekBar.setMax(mediaPlayer.getDuration());
                startAnnimation(imageView,360);
                int audiosetionId = mediaPlayer.getAudioSessionId();
                if (audiosetionId != 1) {
                    visualizer.setAudioSessionId(audiosetionId);
                    updateSeek = new Thread() {

                        @Override
                        public void run() {
                            int totalDuration = mediaPlayer.getDuration();
                            int currentPosition = 0;

                            while (currentPosition < totalDuration) {
                                try {
                                    sleep(500);
                                    currentPosition = mediaPlayer.getCurrentPosition();
                                    seekBar.setProgress(currentPosition);
                                } catch (InterruptedException | IllegalStateException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    };
                    updateSeek.start();
                }
            }

        });

        backwoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
                //   Toast.makeText(getApplicationContext(), "+10", Toast.LENGTH_SHORT).show();
            }
        });

        forwoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
                //   Toast.makeText(getApplicationContext(), "-10", Toast.LENGTH_SHORT).show();
            }
        });

        String endtime = creatTime(mediaPlayer.getDuration());
        txtsstop.setText(endtime);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    String currentTime1 = creatTime(mediaPlayer.getCurrentPosition());
                    txtsstart.setText(currentTime1);
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        int audiosetionId = mediaPlayer.getAudioSessionId();
        if (audiosetionId != 1) {
            visualizer.setAudioSessionId(audiosetionId);
        }

    }

    public String creatTime(int duration) {
        String time = " ";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        time += min + ":";
        if (sec < 10) {
            time += "0";
        }
        time += sec;
        return time;
    }
    public void startAnnimation(View view,float degree){
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",0f,degree);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }

}

