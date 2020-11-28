package club.codeexpert.musica.ui.downloads;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.codeexpert.musica.MyItemRecyclerViewAdapter;
import club.codeexpert.musica.R;
import club.codeexpert.musica.data.db.DbFactory;
import club.codeexpert.musica.data.db.Song;
import club.codeexpert.musica.data.db.SongDao;
import club.codeexpert.musica.ui.discover.DiscoverViewModel;

public class DownloadsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Song> mItems = new ArrayList<Song>();
    MyItemRecyclerViewAdapter mAdapter = new MyItemRecyclerViewAdapter(mItems);
    DownloadsViewModel downloadsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        Context context = view.getContext();

        downloadsViewModel = ViewModelProviders.of(this).get(DownloadsViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        downloadsViewModel.getAll().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> songs) {
                mItems.clear();
                mItems.addAll(songs);
                mAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
/*
    public class GetDownloads extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urlParams) {
            List<Song> songs = dao.getAll();

            mItems.clear();
            mItems.addAll(songs);
            mAdapter.notifyDataSetChanged();

            return null;
        }
    }*/

}