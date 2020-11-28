package club.codeexpert.musica.ui.discover;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import club.codeexpert.musica.MyItemRecyclerViewAdapter;
import club.codeexpert.musica.R;
import club.codeexpert.musica.data.db.Song;
import club.codeexpert.musica.presentation.DiscoverPresentation;
import club.codeexpert.musica.ui.notifications.NotificationsViewModel;

public class DiscoverFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Song> mItems = new ArrayList<Song>();
    MyItemRecyclerViewAdapter mAdapter = new MyItemRecyclerViewAdapter(mItems);
    //DiscoverPresentation presentation = new DiscoverPresentation();
    DiscoverViewModel discoverViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        Context context = view.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(mAdapter);

        discoverViewModel = ViewModelProviders.of(this).get(DiscoverViewModel.class);

        EditText editText = (EditText)view.findViewById(R.id.search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(final Editable s) {
                discoverViewModel.getResults("search?k=" + s).observe(DiscoverFragment.this, new Observer<List<Song>>() {
                    @Override
                    public void onChanged(List<Song> songs) {
                        refreshItems(songs);
                    }
                });
            }
        });

        discoverViewModel.getResults("").observe(this, new Observer<List<Song>>() {
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