package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import api.impl.ApiImpl;
import app.MyApp;
import data.Config;
import data.EventConfig;
import data.Prefs;
import db.SongDAOImpl;
import model.SongInfoModel;
import model.SongStatusInfo;
import model.response.ApiResponse;
import utils.FileUtils;
import utils.Utils;
import utils.async.ResponseSimpleNetTask;

/**
 * Created by Reiky on 2016/5/20.
 */
public class PlaySongService extends Service implements MediaPlayer.OnCompletionListener {

    final static String TAG = "===PlaySongService===";
    private static final int SONG_STORAGE_SIZE = 150;

    ApiImpl api = new ApiImpl();
    SongDAOImpl songDAO = SongDAOImpl.getInstance(this);
    public static SongStatusInfo songStatusInfo = null;

    private MediaPlayer mediaPlayer = null;
    public static float progress = 0;
    int currentID = 0;
    public static boolean isPlaying = false;
    private int playPoint = 0;
    public static boolean nextable = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerBC();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        Log.e("sssss", TAG + "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("sssss", TAG + "onStartCommand");
        getSongList();
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscriber(tag = EventConfig.MEDIA_PLAY)
    private void playMusic(SongStatusInfo setSong) {
        nextable = true;
        EventBus.getDefault().post(new SongStatusInfo(null), EventConfig.UPDATE_SONG_NUMBER);
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();

        if (setSong == null || setSong.getDb_id() == null) {
            Log.e("ssss", TAG + Utils.getLineNumber(new Exception()));
            songStatusInfo = songDAO.getRandomOne(currentID);
        } else {
            Log.e("ssss", TAG + Utils.getLineNumber(new Exception()));
            songStatusInfo = setSong;
        }
        if (songStatusInfo != null
                && FileUtils.existFile(songStatusInfo.getDb_id())) {
            try {
                currentID = songStatusInfo.getDb_id();
                mediaPlayer.reset();
                Uri songUri = Uri.parse(MyApp.DOWNLOAD_DIR + songStatusInfo.getSongUri());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(this, songUri);
                mediaPlayer.prepare();//真机测试用
//            mediaPlayer.prepareAsync();//虚拟机测试用
                mediaPlayer.start();
                songDAO.updateStatus(-1, songStatusInfo.getDb_id());
                isPlaying = true;

                TimerTask mTimerTask = new
                        TimerTask() {
                            @Override
                            public void run() {
                                progress = ((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration() * 100);
                                EventBus.getDefault().post(progress, EventConfig.CURRENT_PROGRESS);
                            }
                        };
                mTimer.schedule(mTimerTask, 0, 700);
                EventBus.getDefault().post(songStatusInfo, EventConfig.UPDATE_VIEWS);

            } catch (Exception e) {
                Log.e("ssss", TAG + Utils.getLineNumber(new Exception()));
                if (songDAO.hasData() != 0)
                    playMusic(songStatusInfo);
            }
        } else {
            Log.e("ssss", TAG + Utils.getLineNumber(new Exception()));

            if (songDAO.hasData() != 0)
                playMusic(null);
        }
    }

    @Subscriber(tag = EventConfig.MEDIA_PAUSE)
    private void pauseMusic(int i) {
        isPlaying = false;
        EventBus.getDefault().post(0, EventConfig.PLAY_STATUS);
        playPoint = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
    }

    @Subscriber(tag = EventConfig.MEDIA_CONTINUE)
    private void continueMusic(int i) {
        isPlaying = true;
        EventBus.getDefault().post(0, EventConfig.PLAY_STATUS);
        mediaPlayer.seekTo(playPoint);
        mediaPlayer.start();
    }

    Timer mTimer = null;

    private void registerBC() {
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ACTION_CLOSE_MAIN);
        registerReceiver(receiver, filter);
    }

    int index = 0;

    private void getSongList() {
        Log.e("sssss", TAG + "BEGIN DOWNLOAD");
        index++;
        if (index > 9) {
            index = 0;
        }
        if (songDAO.hasData() < SONG_STORAGE_SIZE) {
            new ResponseSimpleNetTask(this, false) {

                @Override
                protected ApiResponse doInBack() throws Exception {
                    return api.getSongList(getUrl(index));
                }

                @Override
                protected void onSucceed(String result) throws Exception {
                    List<SongInfoModel> songInfoModelList = new ArrayList<SongInfoModel>();
                    try {
                        songInfoModelList = JSON.parseArray(result, SongInfoModel.class);
                    } catch (JSONException e) {
                        getSongList();
                    }
                    if (songInfoModelList.size() == 0) {
                        Log.e("sssss", TAG + "getSong NONE" + Utils.getLineNumber(new Exception()));
                        getSongList();
                    } else
                        downloadSong(songInfoModelList.get((int) (Math.random() * songInfoModelList.size())), true);
                }

                @Override
                protected void onFailure() {
                    Log.e("sssss", TAG + "getSong ERROR" + Utils.getLineNumber(new Exception()));
                    getSongList();
                }
            }.execute();
        }

    }


    private void downloadSong(final SongInfoModel songInfo, final boolean keepDown) {
        if (!Utils.isWifiConnected(this) &&
                !Prefs.getBoolean(Config.ALLOW_DOWNLOAD_WIZOUT_WIFI, true))
            return;
        MyApp.downloadingList.add(songInfo.getDb_id());
        new ResponseSimpleNetTask(this, false) {

            @Override
            protected ApiResponse doInBack() throws Exception {
                return api.downloadSong(songInfo.getSongUrl(), String.valueOf(songInfo.getDb_id()));
            }

            @Override
            protected void onSucceed(String result) throws Exception {
                if (!songDAO.isExist(songInfo.getDb_id())) {
                    SongStatusInfo song = new SongStatusInfo(
                            songInfo.getDb_id(),
                            "m" + songInfo.getDb_id() + ".mp3",
                            songInfo.getAlbumCoverUrl(),
                            songInfo.getSongName(),
                            songInfo.getArtist(),
                            songInfo.getAlbumName(),
                            0, 0, 0);

                    checkMusic(song);
                }
                if (keepDown) getSongList();

            }

            @Override
            protected void onFailure() {
                if (keepDown) getSongList();
            }
        }.execute();
    }

    MediaPlayer checkMedia = new MediaPlayer();

    private void checkMusic(SongStatusInfo song) {
        checkMedia.reset();
        Uri songUri = Uri.parse(MyApp.DOWNLOAD_DIR + song.getSongUri());

        try {
            checkMedia.setDataSource(this, songUri);
            checkMedia.prepare();//真机测试用
            checkMedia.getDuration();
        } catch (Exception e) {
            return;
        } finally {
            songDAO.insertSongStatus(song);
            EventBus.getDefault().post(song, EventConfig.UPDATE_SONG_NUMBER);
        }
    }

    private String getUrl(int key) {
//        int key = (int) (Math.random() * 10);
        String urlPop = "http://yanwenzi.net/serve_song.php/?action=pop";
        String urlRandom = "http://yanwenzi.net/serve_song.php/?action=random";
        switch (key) {
            case 0:
                return urlRandom;
            case 1:
            case 2:
                return urlPop;
            case 3:
                if (songDAO.getfinishedAlbum() != null)
                    return "http://yanwenzi.net/serve_song.php/?action=album&key=" + songDAO.getfinishedAlbum();
                else return urlRandom;
            case 4:
            case 5:
                if (songDAO.getFinishedArtist() != null)
                    return "http://yanwenzi.net/serve_song.php/?action=artist&key=" + songDAO.getFinishedArtist();
            case 6:
                if (songDAO.getStaredAlbum() != null)
                    return "http://yanwenzi.net/serve_song.php/?action=album&key=" + songDAO.getStaredAlbum();
                else return urlRandom;
            case 7:
            case 8:
            case 9:
                if (songDAO.getStaredArtist() != null)
                    return "http://yanwenzi.net/serve_song.php/?action=artist&key=" + songDAO.getStaredArtist();
                else return urlRandom;
            default:
                return urlPop;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        EventBus.getDefault().unregister(this);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Config.ACTION_CLOSE_MAIN.equals(intent.getAction())) {
                System.exit(0);
            }
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        isPlaying = false;
        EventBus.getDefault().post(0, EventConfig.PLAY_STATUS);
        if (songStatusInfo != null)
            songDAO.onFinished(songStatusInfo.getDb_id());
        progress = 0;
        EventBus.getDefault().post(progress, EventConfig.CURRENT_PROGRESS);
        Log.e("sssss", TAG + "onCompletion==" + Utils.getLineNumber(new Exception()));
        playMusic(null);
        getSongList();
    }
}
