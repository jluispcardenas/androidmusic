package club.codeexpert.musica;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import club.codeexpert.musica.data.db.Song;
import club.codeexpert.musica.managers.ApiManager;

import java.util.ArrayList;
import java.util.List;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<Song> mValues;

    ApiManager apiManager;

    private int mTrackPlaying = -1;

    public MyItemRecyclerViewAdapter(List<Song> items) {
        mValues = items;
    }

    public void setApiManager(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    public void setTrackPlaying(int position) {
        mTrackPlaying = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        holder.mTitleView.setText(holder.mItem.title);
        holder.mContentView.setText("· " + holder.mItem.duration);

        holder.mDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyItemRecyclerViewAdapter.this.notifyDataSetChanged();

                MainActivity.mService.setList((ArrayList<Song>)mValues);

                MainActivity.mService.setSong(position);
                MainActivity.mService.playPauseSong();

                int oldValue = mTrackPlaying;

                setTrackPlaying(position);
                notifyItemChanged(position);
                notifyItemChanged(oldValue);
            }
        });

        if (MyItemRecyclerViewAdapter.this.apiManager.isDownloaded(holder.mItem.id)) {

        }

        if(position == mTrackPlaying) {
            holder.mTitleView.setTextSize(20);
        } else {
            holder.mTitleView.setTextSize(17);
        }

        // Download button actions
        holder.mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song item = mValues.get(position);
                if (!MyItemRecyclerViewAdapter.this.apiManager.isDownloaded(item.id)) {
                    Toast.makeText(context, R.string.file_downloading, Toast.LENGTH_LONG).show();
                    holder.mBtnDownload.setBackgroundColor(Color.GREEN);

                    MyItemRecyclerViewAdapter.this.apiManager.requestDownloadFile(item);
                } else {
                    Toast.makeText(context, R.string.file_remove, Toast.LENGTH_LONG).show();
                    holder.mBtnDownload.setBackgroundColor(Color.TRANSPARENT);

                    MyItemRecyclerViewAdapter.this.apiManager.deleteDownload(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mContentView;
        public final Button mBtnDownload;
        public final View mDetail;
        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.title);
            mContentView = (TextView) view.findViewById(R.id.content);
            mBtnDownload = (Button)view.findViewById(R.id.btn_download);
            mDetail = (View)view.findViewById(R.id.detail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

}