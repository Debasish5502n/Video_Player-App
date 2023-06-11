package com.example.videoplayer;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class VideoFilesAdapter extends RecyclerView.Adapter<VideoFilesAdapter.viewHolder> {

    private    ArrayList<MediaFiles> videoList;
    private Context context;
    BottomSheetDialog bottomSheetDialog;

    public VideoFilesAdapter(ArrayList<MediaFiles> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
        return new VideoFilesAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.videoName.setText(videoList.get(position).getTitle());

        String size=videoList.get(position).getSize();
        holder.videoSize.setText(android.text.format.Formatter.formatShortFileSize(context, Long.parseLong(size)));

       // double milisecond= videoList.get(position).getDuration();
        holder.videoDuration.setText(videoList.get(position).getDuration());

        Glide
                .with(context)
                .load(videoList.get(position).getData())
                .into(holder.thumnail);

        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialog = DialogPlus.newDialog(holder.menu_more.getContext())
                        .setContentHolder(new ViewHolder(R.layout.videos_bs_layout))
                        .setExpanded(true,800)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                View myview=dialog.getHolderView();
                LinearLayout play=myview.findViewById(R.id.bs_play);
                LinearLayout rename=myview.findViewById(R.id.bs_rename);
                LinearLayout share=myview.findViewById(R.id.bs_share);
                LinearLayout delete=myview.findViewById(R.id.bs_delete);
                LinearLayout properties=myview.findViewById(R.id.bs_properties);
                dialog.show();

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.itemView.performClick();
                        dialog.dismiss();
                    }
                });
                rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            AlertDialog.Builder alertDilog = new AlertDialog.Builder(context);
                            alertDilog.setTitle("Rename to");
                            EditText editText = new EditText(context);
                            String path =videoList.get(position).getPath();
                            final File file = new File(path);
                            String videoName = file.getName();
                            videoName = videoName.substring(0, videoName.lastIndexOf("."));
                            editText.setText(videoName);
                            alertDilog.setView(editText);
                            editText.requestFocus();

                            alertDilog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (TextUtils.isEmpty(editText.getText().toString())){
                                        Toast.makeText(context, "Can't Rename empty file", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String onlyPath = file.getParentFile().getAbsolutePath();
                                    String ext = file.getAbsolutePath();
                                    ext = ext.substring(ext.lastIndexOf("."));
                                    String newpath =  onlyPath+"/" + editText.getText().toString() + ext;
                                    File newfile = new File(newpath);
                                    boolean rename = file.renameTo(newfile);

                                    if (rename) {
                                        ContentResolver resolver = context.getApplicationContext().getContentResolver();
                                        resolver.delete(MediaStore.Files.getContentUri("external"),
                                                MediaStore.MediaColumns.DATA + "=?", new String[]{file.getAbsolutePath()});
                                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        intent.setData(Uri.fromFile(newfile));
                                        context.getApplicationContext().sendBroadcast(intent);

                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Video Renamed", Toast.LENGTH_SHORT).show();

                                        SystemClock.sleep(200);
                                        ((Activity) context).recreate();
                                    } else {
                                        Toast.makeText(context, "Process Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            alertDilog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDilog.create().show();
                            dialog.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri= Uri.parse(videoList.get(position).getPath());
                        Intent shreintent=new Intent(Intent.ACTION_SEND);
                        shreintent.setType("video/*");
                        shreintent.putExtra(Intent.EXTRA_STREAM,uri);
                        context.startActivity(Intent.createChooser(shreintent,"Share Video Via"));
                        dialog.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDilog=new AlertDialog.Builder(context);
                        alertDilog.setTitle("Delete");
                        alertDilog.setMessage("Do you want to delete this video?");
                        alertDilog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri contentUri= ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                        videoList.get(position).getId());
                                File file=new File(videoList.get(position).getPath());
                                boolean delete=file.delete();
                                if (delete){
                                    context.getContentResolver().delete(contentUri,null,null);
                                    videoList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,videoList.size());
                                    Toast.makeText(context, "Video Deleted", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "can't Delete", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alertDilog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDilog.show();
                        dialog.dismiss();
                    }
                });
                properties.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDilog=new AlertDialog.Builder(context);
                        alertDilog.setTitle("Properties");

                        String one ="File-"+ videoList.get(position).getTitle();

                        String path=videoList.get(position).getPath();
                        int lastIndex=path.lastIndexOf("/");
                        String two="Path-"+path.substring(0,lastIndex);

                        String three="Size-"+android.text.format.Formatter.formatShortFileSize(context, Long.parseLong(videoList.get(position).getSize()));

                     //   String four="Length"+timeconversion(videoList.get(position).getDuration());

                        String namewithformat=videoList.get(position).getTitle();
                        int index=namewithformat.lastIndexOf(".");
                        String format= String.valueOf(namewithformat.lastIndexOf(index+1));
                        String five="Format-"+format;

                        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(videoList.get(position).getPath());
                        String hight=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                        String width=mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                        String six="Resolution-"+width+"x"+hight;
                        alertDilog.setMessage(one+"\n\n"+two+"\n\n"+three+"\n\n"+"Length-"+videoList.get(position).getDuration()+"\n\n"+five+"\n\n"+six);
                        alertDilog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDilog.show();
                        dialog.dismiss();
                    }
                });


            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,VideoPlayerActivity.class);
                intent.putExtra("position",videoList.get(position).getId());
                intent.putExtra("video_name",videoList.get(position).getTitle());
                Bundle bundle=new Bundle();
                bundle.putParcelableArray("videoArrayList", bundle.getParcelableArray(String.valueOf(videoList)));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView thumnail,menu_more;
        TextView videoName,videoSize,videoDuration;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            thumnail=itemView.findViewById(R.id.thumnail);
            menu_more=itemView.findViewById(R.id.video_menu_more);
            videoName=itemView.findViewById(R.id.video_name);
            videoSize=itemView.findViewById(R.id.video_size);
            videoDuration=itemView.findViewById(R.id.video_duration);
        }
    }
    public String timeconversion(long value){
        String videoTime;
        int duration=(int) value;
        int sec=(duration /1000)%60;
        int min=(duration /(1000*60))%60;
        int hrs=duration /(1000*60*60);

        if (hrs==0) {
            videoTime = String.valueOf(min).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
        }else {
            videoTime = String.valueOf(hrs).concat(":".concat(String.format(Locale.UK, "%02d", min).concat(":".concat(String.format(Locale.UK,"%02d",sec)))));
        }
        return  videoTime;
    }

    void  updatrVideoFiles(ArrayList<MediaFiles> files){
        videoList=new ArrayList<>();
        videoList.addAll(files);
        notifyDataSetChanged();
    }
}
