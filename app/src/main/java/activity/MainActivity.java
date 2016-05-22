package activity;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.reikyz.oncefm.R;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.Config;
import data.EventConfig;
import data.Prefs;
import db.SongDAOImpl;
import io.fabric.sdk.android.Fabric;
import model.SongStatusInfo;
import service.PlaySongService;
import utils.AnimHelper;
import utils.BitmapUtils;
import utils.FileUtils;
import utils.PermissionHelper;
import utils.Utils;
import view.BadgeView;
import view.CircleImageMoveableView;
import view.CircleImageView;
import view.CircleProgressBar;
import widget.AlertHelper;

public class MainActivity extends Activity implements CircleImageMoveableView.MoveListener {

    final static String TAG = "===MainActivity===";


    SongDAOImpl songDAO = SongDAOImpl.getInstance(this);


    @Bind(R.id.menu_drawer)
    DrawerLayout drawerLayout;

    @Bind(R.id.song_name)
    TextView tvSongName;
    @Bind(R.id.song_artist)
    TextView tvSongArtist;
    @Bind(R.id.song_album)
    TextView tvSongAlbum;

    @Bind(R.id.iv_cache)
    ImageView ivCache;

    @Bind(R.id.iv_album_bg)
    ImageView ivAlbumBg;
    @Bind(R.id.iv_logo)
    ImageView ivLogo;
    @Bind(R.id.iv_pan)
    CircleImageMoveableView civPan;
    @Bind(R.id.progressBar)
    CircleProgressBar progressBar;

    @Bind(R.id.btn_star)
    CircleImageView civStar;
    @Bind(R.id.btn_play)
    CircleImageView civPlay;
    @Bind(R.id.btn_next)
    CircleImageView civNext;

    @Bind(R.id.btn_noti_list)
    ImageView ivNotiList;
    @Bind(R.id.tv_noti_list)
    TextView tvNotiList;
    BadgeView bvSongNum;

    @OnClick(R.id.btn_noti_list)
    void listOnClick() {
        closeDrawer();
        openSongList();
    }


    @OnClick(R.id.btn_noti_response)
    void responseOnClick() {
        Utils.showToast(this, "btn_noti_response");
    }

    @OnClick(R.id.btn_noti_advice)
    void feedbackOnClick() {
        Utils.showToast(this, "btn_noti_advice");
    }

    @OnClick(R.id.btn_noti_install)
    void installOnClick() {
        Utils.showToast(this, "btn_noti_install");
    }

    @OnClick(R.id.btn_noti_setting)
    void notifyOnClick() {
        Utils.showToast(this, "btn_noti_setting");
    }

    @OnClick(R.id.btn_timer)
    void timerOnClick() {
        Utils.showToast(this, "btn_timer");
    }

    @OnClick(R.id.btn_shutdown)
    void shutdownOnClick() {
        Utils.showToast(this, "btn_shutdown");
    }

    @OnClick(R.id.btn_star)
    void btnStarOnClick() {
        updateStarStatus();
    }

    boolean pendingPlay = false;

    @OnClick(R.id.btn_play)
    void btnPlayOnClick() {
        if (songDAO.hasData() == 0) {
            pendingPlay = true;
            Utils.showToast(this, getString(R.string.downloading));
            return;
        } else {
            pendingPlay = false;
        }

        if (onceSong != null && !PlaySongService.isPlaying) {
            EventBus.getDefault().post(onceSong, EventConfig.MEDIA_PLAY);
            onceSong = null;
        } else {
            if (PlaySongService.isPlaying) {
                EventBus.getDefault().post(0, EventConfig.MEDIA_PAUSE);
            } else {
                // pausing
                if (PlaySongService.progress > 0 || PlaySongService.progress < 99) {
                    EventBus.getDefault().post(0, EventConfig.MEDIA_CONTINUE);
                } else {
                    EventBus.getDefault().post(new SongStatusInfo(null), EventConfig.MEDIA_PLAY);
                }
            }
        }
    }

    @OnClick(R.id.btn_next)
    void btnNextOnclick() {
        if (songDAO.hasData() > 2 &&
                PlaySongService.nextable) {
            EventBus.getDefault().post(new SongStatusInfo(null), EventConfig.MEDIA_PLAY);
            PlaySongService.nextable = false;
        }
    }

    SongStatusInfo onceSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initViews();
        onceSong = songDAO.getRandomOne(0);

        if (PermissionHelper.checkPermission(this, "WRITE_EXTERNAL_STORAGE")) {
            initPlayService();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionHelper.PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initPlayService();
                } else {
                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                    this.finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (onceSong != null && !PlaySongService.isPlaying) {
            updateViews(onceSong);
        } else {
            updateViews(PlaySongService.songStatusInfo);
        }
    }

    private void initPlayService() {
        Intent intentPlay = new Intent(this, PlaySongService.class);
        startService(intentPlay);
    }

    private void initViews() {
        bvSongNum = new BadgeView(this, ivNotiList);
        updateSongNumber(PlaySongService.songStatusInfo);
        civPan.setMoveListener(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        FileUtils.clearFiles(SongDAOImpl.getInstance(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = EventConfig.CURRENT_PROGRESS)
    void updateProgress(float progress) {
        progressBar.setProgress(progress);

        if (refreshViews && progress > 0.5f && progress < 2) {
            refreshViews = false;
            updateViews(PlaySongService.songStatusInfo);
        }
        if (progress > 5) refreshViews = true;
    }

    boolean refreshViews = true;

    @Subscriber(tag = EventConfig.UPDATE_VIEWS)
    void updateViews(SongStatusInfo info) {
        closeDrawer();
        if (info != null) {
            ivLogo.setVisibility(View.GONE);
            tvSongAlbum.setText(info.getAlbumName());
            tvSongArtist.setText(info.getArtist());
            tvSongName.setText(info.getSongName());
            BitmapUtils.displayImage(info.getPicUri(), civPan);
            BitmapUtils.displayImageWithBlur(info.getPicUri(), ivAlbumBg);
            setStarStatus();
            setPlayStatus(0);
        }
    }

    @Subscriber(tag = EventConfig.PLAY_STATUS)
    private void setPlayStatus(int i) {
        if (PlaySongService.isPlaying) {
            civPlay.setImageResource(R.mipmap.pause_black);
        } else {
            civPlay.setImageResource(R.mipmap.play_black);
        }
    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    private void openSongList() {
        Intent intent = new Intent(this, SongListActvity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_horizonal_entry, R.anim.activity_half_horizonal_exit);
    }

    private void setStarStatus() {
        if (PlaySongService.songStatusInfo != null) {
            if (songDAO.isStared(PlaySongService.songStatusInfo.getDb_id())) {
                civStar.setImageResource(R.mipmap.stared_black);
            } else {
                civStar.setImageResource(R.mipmap.star_black);
            }
        } else {
            civStar.setImageResource(R.mipmap.star_black);
        }
    }

    private void updateStarStatus() {
        if (PlaySongService.songStatusInfo != null) {
            if (songDAO.isStared(PlaySongService.songStatusInfo.getDb_id())) {
                civStar.setImageResource(R.mipmap.star_black);
                songDAO.markStarCancel(PlaySongService.songStatusInfo.getDb_id());
            } else {
                civStar.setImageResource(R.mipmap.stared_black);
                songDAO.markStar(PlaySongService.songStatusInfo.getDb_id());
                AnimHelper.showPopScaleAnimation(civStar);
            }
        } else {
            civStar.setImageResource(R.mipmap.star_black);
        }
    }

    @Subscriber(tag = EventConfig.UPDATE_SONG_NUMBER)
    void updateSongNumber(SongStatusInfo songStatusInfo) {
        if (Prefs.getBoolean(Config.DOWNLOAD_ALERT_SHOW, true) &&
                !Utils.isWifiConnected(this)) {
            AlertHelper.askDownload(this);
            Prefs.save(Config.DOWNLOAD_ALERT_SHOW, false);
        }

        if (pendingPlay) {
            pendingPlay = false;
            EventBus.getDefault().post(new SongStatusInfo(null), EventConfig.MEDIA_PLAY);
        }

        if (songStatusInfo != null && songStatusInfo.getPicUri() != null)
            BitmapUtils.displayImage(songStatusInfo.getPicUri(), ivCache);

        if (songDAO.hasData() > 2) {
            ivNotiList.setVisibility(View.VISIBLE);
            tvNotiList.setVisibility(View.VISIBLE);
            if (songDAO.hasData() > 99) {
                bvSongNum.setText("99+");
            } else {
                bvSongNum.setText(songDAO.hasData() + "");
            }
            bvSongNum.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            bvSongNum.show();

            civNext.setImageResource(R.mipmap.next_black);
        } else {
            ivNotiList.setVisibility(View.GONE);
            tvNotiList.setVisibility(View.GONE);
            bvSongNum.setVisibility(View.GONE);
            // no more song to play
            civNext.setImageResource(R.mipmap.next_white);
        }
    }

    @Override
    public void moveLeft() {
        if (songDAO.hasData() > 2)
            openSongList();
    }

    @Override
    public void moveRight() {
        Log.e("sssss", TAG + Utils.getLineNumber(new Exception()));
    }

    long keyDown = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else {
                    if ((System.currentTimeMillis() - keyDown) > 1500) {
                        Utils.showToast(this, "再次点击退出");
                        keyDown = System.currentTimeMillis();
                        return true;
                    }
                }
                break;
            //play by wire control
            case 79:
            case 126:
            case 127:

                break;
            //star by wire contrl
            case 88:
                break;
            //next bt wire control
            case 87:

                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
