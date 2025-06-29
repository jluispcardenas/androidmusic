package club.codeexpert.music;

import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import club.codeexpert.music.data.db.Song;
import club.codeexpert.music.managers.ApiManager;
import com.google.android.material.snackbar.Snackbar;

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
                // Add click animation
                animateView(holder.mDetail, 0.95f);
                
                MainActivity.mService.setList((ArrayList<Song>)mValues);
                MainActivity.mService.setSong(position);
                MainActivity.mService.playPauseSong();

                int oldValue = mTrackPlaying;
                setTrackPlaying(position);
                
                notifyItemChanged(position);
                notifyItemChanged(oldValue);
                
                // Show snackbar feedback
                showSnackbar(holder.mView, context.getString(R.string.playing_song) + ": " + holder.mItem.title);
            }
        });

        // Set download button state with animation
        updateDownloadButtonState(holder, MyItemRecyclerViewAdapter.this.apiManager.isDownloaded(holder.mItem.id));

        // Animate text size changes for playing track
        if (position == mTrackPlaying) {
            animateTextSize(holder.mTitleView, 20f);
            holder.mTitleView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            animateTextSize(holder.mTitleView, 16f);
            holder.mTitleView.setTextColor(context.getResources().getColor(R.color.text_color_primary));
        }

        // Download button actions with improved feedback
        holder.mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add click animation
                animateView(holder.mBtnDownload, 0.9f);
                
                Song item = mValues.get(position);
                if (!MyItemRecyclerViewAdapter.this.apiManager.isDownloaded(item.id)) {
                    // Starting download
                    showSnackbar(holder.mView, context.getString(R.string.download_started) + ": " + item.title);
                    updateDownloadButtonState(holder, true);
                    MyItemRecyclerViewAdapter.this.apiManager.requestDownloadFile(item);
                } else {
                    // Removing download
                    showSnackbar(holder.mView, context.getString(R.string.file_removed) + ": " + item.title);
                    updateDownloadButtonState(holder, false);
                    MyItemRecyclerViewAdapter.this.apiManager.deleteDownload(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void updateDownloadButtonState(ViewHolder holder, boolean isDownloaded) {
        int drawableRes = isDownloaded ? android.R.drawable.ic_delete : android.R.drawable.stat_sys_download;
        
        // Animate the state change
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(holder.mBtnDownload, "scaleX", 1f, 0.8f);
        scaleDown.setDuration(100);
        scaleDown.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                holder.mBtnDownload.setCompoundDrawablesWithIntrinsicBounds(drawableRes, 0, 0, 0);
                ObjectAnimator scaleUp = ObjectAnimator.ofFloat(holder.mBtnDownload, "scaleX", 0.8f, 1f);
                scaleUp.setDuration(100);
                scaleUp.start();
            }
        });
        scaleDown.start();
    }
    
    private void animateView(View view, float scale) {
        view.animate()
            .scaleX(scale)
            .scaleY(scale)
            .setDuration(100)
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }
    
    private void animateTextSize(TextView textView, float targetSize) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "textSize", textView.getTextSize(), targetSize);
        animator.setDuration(200);
        animator.start();
    }
    
    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
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