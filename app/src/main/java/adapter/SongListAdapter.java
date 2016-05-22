package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.reikyz.oncefm.R;

import org.simple.eventbus.EventBus;

import java.util.List;

import data.EventConfig;
import db.SongDAOImpl;
import model.SongStatusInfo;
import utils.BitmapUtils;

/**
 * Created by Reiky on 2016/5/21.
 */
public class SongListAdapter extends BaseListAdapter<SongStatusInfo> {

    final static String TAG = "===SongListAdapter===";
    SongDAOImpl songDAO;

    public SongListAdapter(Context context, List<SongStatusInfo> models) {
        super(context, models);
        songDAO = SongDAOImpl.getInstance(context);
    }

    ImageView ivCover;
    TextView tvSong;
    TextView tvArtist;

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        BaseViewHolder holder = BaseViewHolder.getViewHolder(
                context,
                convertView,
                parent, R.layout.item_song_deleteable,
                position
        );

        ivCover = holder.getView(R.id.iv_album_cover);
        tvSong = holder.getView(R.id.tv_song_name);
        tvArtist = holder.getView(R.id.tv_song_artist);

        final SongStatusInfo statusInfo = models.get(position);

        ivCover.setImageResource(R.mipmap.ic_launcher);
        BitmapUtils.displayImage(statusInfo.getPicUri(), ivCover);
        tvSong.setText(statusInfo.getSongName());
        tvArtist.setText(statusInfo.getArtist());

        LinearLayout ll = holder.getView(R.id.ll);
//
//        ll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("ssss", TAG + statusInfo.toString());
//                EventBus.getDefault().post(statusInfo, EventConfig.MEDIA_PLAY);
//                ((Activity) context).finish();
//            }
//        });

        final SwipeLayout swipeLayout = holder.getView(R.id.swipeLayout);
        final TextView tvDelete = (TextView) swipeLayout.findViewById(R.id.tv_trash);

        tvDelete.setVisibility(View.GONE);
        tvDelete.setVisibility(View.VISIBLE);
        swipeLayout.close();
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewWithTag("Bottom"));

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.close();
                models.remove(position);
                notifyDataSetChanged();
                songDAO.deleteSongInfo(statusInfo.getDb_id());
                EventBus.getDefault().post(new SongStatusInfo(null), EventConfig.UPDATE_SONG_NUMBER);
            }
        });

        return holder.getConvertView();
    }
}
