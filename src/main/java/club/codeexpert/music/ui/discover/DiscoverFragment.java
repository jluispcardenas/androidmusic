package club.codeexpert.music.ui.discover;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import club.codeexpert.music.MyItemRecyclerViewAdapter;
import club.codeexpert.music.R;
import club.codeexpert.music.data.db.Song;
import club.codeexpert.music.managers.ApiManager;
import com.google.android.material.snackbar.Snackbar;
import dagger.android.support.AndroidSupportInjection;

public class DiscoverFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private LinearLayout errorState;
    private EditText searchEditText;
    private Button searchButton;
    private Button retryButton;
    
    private ArrayList<Song> mItems = new ArrayList<Song>();
    private MyItemRecyclerViewAdapter mAdapter = new MyItemRecyclerViewAdapter(mItems);
    private static Bundle savedState;
    private View rootView;

    @Inject
    ApiManager apiManager;

    @Inject
    DiscoverViewModel discoverViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        AndroidSupportInjection.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        
        initViews();
        setupRecyclerView();
        setupSearch();
        setupSwipeRefresh();
        restoreState(savedInstanceState);
        
        return rootView;
    }
    
    private void initViews() {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        swipeRefresh = rootView.findViewById(R.id.swipe_refresh);
        progressBar = rootView.findViewById(R.id.progress_bar);
        emptyState = rootView.findViewById(R.id.empty_state);
        errorState = rootView.findViewById(R.id.error_state);
        searchEditText = rootView.findViewById(R.id.search_edit_text);
        searchButton = rootView.findViewById(R.id.search_button);
        retryButton = rootView.findViewById(R.id.retry_button);
    }
    
    private void setupRecyclerView() {
        mAdapter.setApiManager(apiManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
    }
    
    private void setupSearch() {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable;
        
        View.OnClickListener searchClickListener = v -> performSearch();
        searchButton.setOnClickListener(searchClickListener);
        
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
        
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    handler.removeCallbacks(runnable);
                    Runnable newRunnable = () -> searchMusic(s.toString());
                    handler.postDelayed(newRunnable, 1000);
                }
            }
        });
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(this::refreshContent);
        swipeRefresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent
        );
    }
    
    private void restoreState(Bundle savedInstanceState) {
        boolean restored = false;
        if (savedInstanceState != null || savedState != null) {
            Bundle sInstance = savedInstanceState != null ? savedInstanceState : savedState;
            ArrayList<Song> sItems = sInstance.getParcelableArrayList("mItems");
            if (sItems != null && sItems.size() > 0) {
                refreshItems(sItems);
                restored = true;
            }
            CharSequence k = sInstance.getCharSequence("k");
            if (k != null) {
                searchEditText.setText(k);
            }
        }
        
        if (!restored) {
            loadDefaultContent();
        }
        
        retryButton.setOnClickListener(v -> refreshContent());
    }
    
    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (query.length() > 2) {
            searchMusic(query);
        } else {
            showSnackbar(getString(R.string.search));
        }
    }
    
    private void searchMusic(String query) {
        if (!isNetworkAvailable()) {
            showErrorState(getString(R.string.error_network_title), getString(R.string.error_network_subtitle));
            return;
        }
        
        showLoading(true);
        discoverViewModel.getResults("search?k=" + query).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                showLoading(false);
                if (songs != null && !songs.isEmpty()) {
                    refreshItems(songs);
                    showSnackbar(getString(R.string.loading_search) + " - " + songs.size() + " results");
                } else {
                    showEmptySearchState();
                }
            }
        });
    }
    
    private void loadDefaultContent() {
        showLoading(true);
        discoverViewModel.getResults("search?k=music").observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                showLoading(false);
                if (songs != null && !songs.isEmpty()) {
                    refreshItems(songs);
                } else {
                    showEmptyState();
                }
            }
        });
    }
    
    private void refreshContent() {
        String query = searchEditText.getText().toString().trim();
        if (query.length() > 2) {
            searchMusic(query);
        } else {
            loadDefaultContent();
        }
        swipeRefresh.setRefreshing(false);
    }

    private void refreshItems(List<Song> items) {
        mItems.clear();
        mItems.addAll(items);
        mAdapter.notifyDataSetChanged();
        
        recyclerView.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
            errorState.setVisibility(View.GONE);
        }
    }
    
    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
        errorState.setVisibility(View.GONE);
    }
    
    private void showEmptySearchState() {
        showEmptyState();
        TextView title = emptyState.findViewById(R.id.empty_state).findViewById(android.R.id.text1);
        TextView subtitle = emptyState.findViewById(R.id.empty_state).findViewById(android.R.id.text2);
        if (title != null) title.setText(R.string.empty_search_title);
        if (subtitle != null) subtitle.setText(R.string.empty_search_subtitle);
    }
    
    private void showErrorState(String title, String subtitle) {
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.VISIBLE);
        
        TextView errorTitle = errorState.findViewById(R.id.error_title);
        TextView errorSubtitle = errorState.findViewById(R.id.error_subtitle);
        
        if (errorTitle != null) errorTitle.setText(title);
        if (errorSubtitle != null) errorSubtitle.setText(subtitle);
    }
    
    private void showSnackbar(String message) {
        if (rootView != null) {
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) 
            getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mItems", mItems);
        if (searchEditText != null) {
            outState.putCharSequence("k", searchEditText.getText());
        }
        super.onSaveInstanceState(outState);
        savedState = outState;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onSaveInstanceState(new Bundle());
    }
}