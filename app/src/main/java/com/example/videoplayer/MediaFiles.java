package com.example.videoplayer;

import static java.lang.String.valueOf;

import android.net.Uri;

import android.os.Parcel;
import android.os.Parcelable;


public class MediaFiles implements Parcelable {

    private Long id;
    private String title;
    private String path;
    private String duration;
    private String dateAded;
    private String displayName;
    private String data;
    private String size;

    public MediaFiles(Long id, String title, String path, String duration, String dateAded, String displayName, String size, String data) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.duration = duration;
        this.dateAded = dateAded;
        this.displayName = displayName;
        this.size = size;
        this.data = data;
    }


    protected MediaFiles(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        path = in.readParcelable(Uri.class.getClassLoader());
        duration = in.readString();
        dateAded = in.readString();
        displayName = in.readString();
        size = String.valueOf(in.readInt());
    }

    public static final Creator<MediaFiles> CREATOR = new Creator<MediaFiles>() {
        @Override
        public MediaFiles createFromParcel(Parcel in) {
            return new MediaFiles(in);
        }

        @Override
        public MediaFiles[] newArray(int size) {
            return new MediaFiles[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDateAded() {
        return dateAded;
    }

    public void setDateAded(String dateAded) {
        this.dateAded = dateAded;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static Creator<MediaFiles> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(title);
      //  dest.writeParcelable(path,flags);
        dest.writeString(duration);
        dest.writeString(dateAded);
        dest.writeString(displayName);
        dest.writeString(valueOf(size));
    }
}
