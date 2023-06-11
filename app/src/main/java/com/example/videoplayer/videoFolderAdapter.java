package com.example.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class videoFolderAdapter extends RecyclerView.Adapter<videoFolderAdapter.viewHolder> {
    private ArrayList<MediaFiles> mediaFolders;
    private ArrayList<String> folderPath;
    private Context context;


    public videoFolderAdapter(ArrayList<MediaFiles> mediaFolders, ArrayList<String> folderPath, Context context) {
        this.mediaFolders = mediaFolders;
        this.folderPath = folderPath;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.folder_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        try {
            int indexPath = folderPath.get(position).lastIndexOf("/");
            String nameOFFolder = folderPath.get(position).substring(indexPath + 1);
            holder.folderName.setText(nameOFFolder);
            holder.folder_path.setText(folderPath.get(position));
            holder.noOfFolder.setText(noOfFiles(folderPath.get(position)) + " videos");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VideoFilesActivity.class);
                    intent.putExtra("folderName", nameOFFolder);
                    context.startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return folderPath.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView folderName,folder_path,noOfFolder;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            folderName=itemView.findViewById(R.id.foldername);
            folder_path=itemView.findViewById(R.id.folderpath);
            noOfFolder=itemView.findViewById(R.id.nooffolder);
        }
    }
    int noOfFiles(String name_folder) {
            int filesno = 0;
                for (MediaFiles mediaFolder : mediaFolders) {
                    if (mediaFolder.getPath().substring(0, mediaFolder.getPath().lastIndexOf("/"))
                            .endsWith(name_folder)) {
                        filesno++;
                    }
                }
            return filesno;
    }
}
