package com.example.videoplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayBackIconAdapter extends RecyclerView.Adapter<PlayBackIconAdapter.ViewHolder> {
    private ArrayList<IconModel> iconModels=new ArrayList<>();
    private Context context;
    public onitenClickedListener mlistner;

    public interface onitenClickedListener{
        void onItemClicked(int position);
    }
    public void setOnItemClickedListner(onitenClickedListener listner){
        mlistner=listner;
    }

    public PlayBackIconAdapter(ArrayList<IconModel> iconModels, Context context) {
        this.iconModels = iconModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.playback_iconlayout,parent,false);
        return new ViewHolder(view,mlistner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.iconName.setText(iconModels.get(position).getImageTitle());
        holder.icon.setImageResource(iconModels.get(position).getImageView());
    }

    @Override
    public int getItemCount() {
        return iconModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView iconName;
        ImageView icon;
        public ViewHolder(@NonNull View itemView,onitenClickedListener listner) {
            super(itemView);
            iconName=itemView.findViewById(R.id.icon_title);
            icon=itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listner !=null){
                        int position=getAdapterPosition();
                        if (position !=RecyclerView.NO_POSITION){
                            listner.onItemClicked(position);
                        }
                    }
                }
            });
        }
    }
}
