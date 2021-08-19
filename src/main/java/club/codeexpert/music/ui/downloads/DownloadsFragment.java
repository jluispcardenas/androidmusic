package club.codeexpert.music.ui.downloads;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.codeexpert.music.MyItemRecyclerViewAdapter;
import club.codeexpert.music.R;
import club.codeexpert.music.data.db.Song;
import club.codeexpert.music.managers.ApiManager;
import dagger.android.support.AndroidSupportInjection;


public class DownloadsFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Song> mItems = new ArrayList<Song>();
    MyItemRecyclerViewAdapter mAdapter = new MyItemRecyclerViewAdapter(mItems);

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

        View view = inflater.inflate(R.layout.fragment_list_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        TextView searchView = (TextView) view.findViewById(R.id.search);
        searchView.setVisibility(View.INVISIBLE);

        Context context = view.getContext();

        //downloadsViewModel = ViewModelProviders.of(this).get(DownloadsViewModel.class);

        mAdapter.setApiManager(apiManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);

        new GetDownloads().execute();

        return view;
    }

    public class GetDownloads extends AsyncTask<String, Integer, List<Song>> {
        @Override
        protected List<Song> doInBackground(String... urlParams) {
            return downloadsViewModel.getAll();
        }

        @Override
        protected void onPostExecute(List<Song> songs) {
            super.onPostExecute(songs);
            mItems.clear();
            mItems.addAll(songs);
            mAdapter.notifyDataSetChanged();
        }
    }
}