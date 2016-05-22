package activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;
import com.reikyz.oncefm.R;

import org.simple.eventbus.EventBus;

import adapter.SongListAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import db.SongDAOImpl;
import utils.AnimHelper;

/**
 * Created by Reiky on 2016/5/21.
 */
public class SongListActvity extends Activity {

    final static String TAG = "===SongListActvity===";

    SongDAOImpl songDAO = SongDAOImpl.getInstance(this);

    @Bind(R.id.lv)
    ListView listView;

    SongListAdapter songListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListener();
        songListAdapter = new SongListAdapter(this, songDAO.getSongStatuss());
        listView.setAdapter(songListAdapter);
        AnimHelper.showListLayoutAnim(listView);
    }

    private void initListener() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                for (int i = firstVisibleItem; i < visibleItemCount - 1; i++) {
                    if (i != 0)
                        ((SwipeLayout) (listView.getChildAt(i))).close();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_half_horizonal_entry, R.anim.activity_horizonal_exit);
    }
}
