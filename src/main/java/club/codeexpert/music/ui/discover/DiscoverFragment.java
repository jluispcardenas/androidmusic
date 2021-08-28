package club.codeexpert.music.ui.discover;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.codeexpert.music.MyItemRecyclerViewAdapter;
import club.codeexpert.music.R;
import club.codeexpert.music.data.db.Song;
import club.codeexpert.music.managers.ApiManager;
import dagger.android.support.AndroidSupportInjection;

public class DiscoverFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Song> mItems = new ArrayList<Song>();
    MyItemRecyclerViewAdapter mAdapter = new MyItemRecyclerViewAdapter(mItems);

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
        View view = inflater.inflate(R.layout.fragment_list_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        Context context = view.getContext();

        mAdapter.setApiManager(apiManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        //discoverViewModel = ViewModelProviders.of(this).get(DiscoverViewModel.class);

        SearchView editText = (SearchView) view.findViewById(R.id.search);
        editText.setIconifiedByDefault(false);
        editText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable runnable;

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        discoverViewModel.getResults("search?k=" + s).observe(DiscoverFragment.this, new Observer<List<Song>>() {
                            @Override
                            public void onChanged(List<Song> songs) {
                                refreshItems(songs);
                            }
                        });
                    }
                };
                handler.postDelayed(runnable, 1000);
                return false;
            }
        });

        discoverViewModel.getResults("search?k=music").observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                    refreshItems(songs);
            }
        });

        return view;
    }

    void refreshItems(List<Song> items) {
        mItems.clear();
        mItems.addAll(items);
        mAdapter.notifyDataSetChanged();

    }

}