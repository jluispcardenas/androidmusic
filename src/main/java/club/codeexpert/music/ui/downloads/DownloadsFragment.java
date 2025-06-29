package club.codeexpert.music.ui.downloads;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import club.codeexpert.music.MyItemRecyclerViewAdapter;
import club.codeexpert.music.R;
import club.codeexpert.music.data.db.Song;
import club.codeexpert.music.managers.ApiManager;
import com.google.android.material.snackbar.Snackbar;
import dagger.android.support.AndroidSupportInjection;


public class DownloadsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private View rootView;
    
    private ArrayList<Song> mItems = new ArrayList<Song>();
    private MyItemRecyclerViewAdapter mAdapter = new MyItemRecyclerViewAdapter(mItems);
    private static Bundle savedState;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    DownloadsViewModel downloadsViewModel;

    @Inject
    ApiManager apiManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        AndroidSupportInjection.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_downloads, container, false);
        
        initViews();
        setupRecyclerView();
        setupSwipeRefresh();
        restoreState(savedInstanceState);
        loadDownloads();

        return rootView;
    }
    
    private void initViews() {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        swipeRefresh = rootView.findViewById(R.id.swipe_refresh);
        progressBar = rootView.findViewById(R.id.progress_bar);
        emptyState = rootView.findViewById(R.id.empty_state);
    }
    
    private void setupRecyclerView() {
        mAdapter.setApiManager(apiManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::loadDownloads);
        swipeRefresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent
        );
    }
    
    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null || savedState != null) {
            Bundle sInstance = savedInstanceState != null ? savedInstanceState : savedState;
            ArrayList<Song> sItems = sInstance.getParcelableArrayList("mItems");
            if (sItems != null && sItems.size() > 0) {
                refreshItems(sItems);
                return;
            }
        }
    }

    private void loadDownloads() {
        showLoading(true);
        
        executorService.execute(() -> {
            List<Song> downloads = downloadsViewModel.getAll();
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    showLoading(false);
                    swipeRefresh.setRefreshing(false);
                    
                    if (downloads != null && !downloads.isEmpty()) {
                        refreshItems(downloads);
                        showSnackbar(getString(R.string.loading_downloads) + " - " + downloads.size() + " songs");
                    } else {
                        showEmptyState();
                    }
                });
            }
        });
    }
    
    private void refreshItems(List<Song> songs) {
        mItems.clear();
        mItems.addAll(songs);
        mAdapter.notifyDataSetChanged();
        
        recyclerView.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
        }
    }
    
    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }
    
    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mItems", mItems);
        super.onSaveInstanceState(outState);
        savedState = outState;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onSaveInstanceState(new Bundle());
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh downloads when returning to fragment
        loadDownloads();
    }
}