package net.akmobile.youtubeapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.akmobile.youtubeapp.R;
import net.akmobile.youtubeapp.activities.MainActivity;
import net.akmobile.youtubeapp.adapters.SearchResultsAdapter;
import net.akmobile.youtubeapp.interfaces.OnListItemsClickListener;
import net.akmobile.youtubeapp.interfaces.SearchCallback;
import net.akmobile.youtubeapp.models.SearchedItem;
import net.akmobile.youtubeapp.network.Connection;
import net.akmobile.youtubeapp.network.ResponseHandler;
import net.akmobile.youtubeapp.utils.UrlBuilder;

import java.util.ArrayList;

/**
 * Created by Rahimli Rahim on 28/09/2016.
 * ragim95@gmail.com
 * https://github.com/rahimlis/
 */

public class SearchFragment extends Fragment implements SearchCallback {
    /**
     * The fragment argument representing the search query for this
     * fragment.
     */
    private static final String ARG_QUERY = "query";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageView searchIcon;
    private SearchReceiver receiver;
    private IntentFilter filter;
    private SearchResultsAdapter adapter;
    private ArrayList<SearchedItem> searchedItems;

    public SearchFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new SearchReceiver();
        filter = new IntentFilter(MainActivity.MAKE_SEARCH_INTENT);
        filter.addAction(MainActivity.DOWNLOAD_STATUS_CHANGED);
        getActivity().registerReceiver(receiver, filter);
        //   getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        receiver = new SearchReceiver();
        filter = new IntentFilter(MainActivity.MAKE_SEARCH_INTENT);
        filter.addAction(MainActivity.DOWNLOAD_STATUS_CHANGED);
        getActivity().registerReceiver(receiver, filter);
        super.onResume();
    }


    @Override
    public void onPause() {
        getActivity().unregisterReceiver(receiver);
        super.onPause();
    }

    /**
     * Returns a new instance of this fragment
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_search_videos);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        //   recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        return view;
    }

    /**
     * This function is gets triggered when the fragment receives a
     * broadcast from activity
     *
     * @param query is the keyword searched by user
     */
    public void search(String query) {
        MakeSearch task = new MakeSearch(query);
        task.execute();
    }

    /**
     * This AsyncTask has a purpose of searching the keyword which is entered by user
     * it then takes the result and pushes to the handler which in turn returns a recyclerview adapter
     */
    class MakeSearch extends AsyncTask<Void, Void, String> {
        private String query;

        MakeSearch(String query) {
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) getActivity().findViewById(R.id.search_progress_bar);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),
                    R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
            searchIcon = (ImageView) getActivity().findViewById(R.id.search_icon_large);
            setLoading(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            Connection connection = new Connection(UrlBuilder.getUrlForSearchQuery(query));
            if (connection.connectForJSON()) {
                return connection.getStringFromServer();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            ResponseHandler handler = new ResponseHandler((MainActivity) getActivity());
            searchedItems = handler.handleVideos(result);
            adapter = new SearchResultsAdapter( searchedItems);
            adapter.setListener((OnListItemsClickListener) getActivity());

            if (adapter != null)
                recyclerView.setAdapter(adapter);
            setLoading(false);
        }


        private void setLoading(boolean isLoading) {
            if (isLoading) {
                recyclerView.setVisibility(View.INVISIBLE);
                searchIcon.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * This broadcast receiver listens to the activity generated broadcasts
     * it performs a search whenever user type query in toolbar and hits Go.
     */
    public class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(MainActivity.MAKE_SEARCH_INTENT)) {
                String query = intent.getStringExtra(ARG_QUERY);
                search(query);
            }

            if (intent.getAction().equals(MainActivity.DOWNLOAD_STATUS_CHANGED)) {
                int position = intent.getIntExtra("position", -1);
                boolean isDownloading = intent.getBooleanExtra("isDownloading", false);
                adapter.setDownloading(position,isDownloading);
            }

        }
    }
}
