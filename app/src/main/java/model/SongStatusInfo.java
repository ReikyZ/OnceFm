package model;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/7/14.
 */
public class SongStatusInfo implements Serializable {

    Integer db_id, status, stared, finished;
    String songUri, picUri, songName, artist, albumName;

    public SongStatusInfo() {
        super();
    }

    public SongStatusInfo(Integer db_id) {
        this.db_id = db_id;
    }


    public SongStatusInfo(int db_id, String songUri, String picUri, String songName, String artist, String albumName, int status, int stared, int finished) {
        this.db_id = db_id;
        this.status = status;
        this.stared = stared;
        this.finished = finished;
        this.songUri = songUri;
        this.picUri = picUri;
        this.songName = songName;
        this.artist = artist;
        this.albumName = albumName;
    }


    @Override
    public String toString() {
        return "SongStatusInfo{" +
                "db_id=" + db_id +
                ", songUri='" + songUri + '\'' +
                ", picUri='" + picUri + '\'' +
                ", songName='" + songName + '\'' +
                ", artist='" + artist + '\'' +
                "albumName='" + albumName + '\'' +
                ", status=" + status +
                ", stared=" + stared +
                ", finished=" + finished +
                '}';
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUri() {
        return songUri;
    }

    public void setSongUri(String songUri) {
        this.songUri = songUri;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
