package com.example.videoplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class VolumeDilog extends AppCompatDialogFragment {

    ImageView close;
    SeekBar seekBar;
    TextView textView;
    AudioManager audioManager;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.vol_dilo_item,null);
        builder.setView(view);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        close=view.findViewById(R.id.vol_close);
        seekBar=view.findViewById(R.id.seekbar);
        textView=view.findViewById(R.id.vol_number);

        audioManager=(AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        int maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int mediaVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        double volper=Math.ceil((((double) mediaVolume/(double) maxVolume )* (double) 100));
        textView.setText(""+volper);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                int maxVolume1=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int mediaVolume1=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                double volper=Math.ceil((((double) mediaVolume1/(double) maxVolume1 )* (double) 100));
                textView.setText(""+volper);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }
}
