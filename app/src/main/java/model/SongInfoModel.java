package model;

import java.io.Serializable;

/**
 * Created by Reiky on 2015/7/12.
 */
public class SongInfoModel implements Serializable {
    Integer db_id;
    String songName;
    Integer songID;
    String artist;
    String albumName;
    String albumCoverUrl;
    String songUrl;
    Integer songScore;

    int songLength;

    public SongInfoModel() {
    }

    public SongInfoModel(int db_id,
                         String songName,
                         int songID,
                         String songUrl,
                         String artist,
                         String albumName,
                         String albumCoverUrl,
                         int songScore,
                         int songLength) {
        this.albumCoverUrl = albumCoverUrl;
        this.albumName = albumName;
        this.artist = artist;
        this.db_id = db_id;
        this.songID = songID;
        this.songName = songName;
        this.songScore = songScore;
        this.songUrl = songUrl;
        this.songLength = songLength;
    }

    public Integer getDb_id() {
        return db_id;
    }

    public void setDb_id(Integer db_id) {
        this.db_id = db_id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Integer getSongID() {
        return songID;
    }

    public void setSongID(Integer songID) {
        this.songID = songID;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumCoverUrl() {
        return albumCoverUrl;
    }

    public void setAlbumCoverUrl(String albumCoverUrl) {
        this.albumCoverUrl = albumCoverUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public Integer getSongScore() {
        return songScore;
    }

    public void setSongScore(Integer songScore) {
        this.songScore = songScore;
    }

    public int getSongLength() {
        return songLength;
    }

    public void setSongLength(int songLength) {
        this.songLength = songLength;
    }

    @Override
    public String toString() {
        return "SongInfoModel{" +
                "db_id=" + db_id +
                ", songName='" + songName + '\'' +
                ", songID=" + songID +
                ", artist='" + artist + '\'' +
                ", albumName='" + albumName + '\'' +
                ", albumCoverUrl='" + albumCoverUrl + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", songScore=" + songScore +
                ", songLength=" + songLength +
                '}';
    }
}
