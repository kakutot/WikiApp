
package com.example.romatupkalenko.wikiresearcherapp.ui.favArticlesFragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.romatupkalenko.wikiresearcherapp.InjectorUtils;
import com.example.romatupkalenko.wikiresearcherapp.R;
import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.example.romatupkalenko.wikiresearcherapp.ui.MyAdapter;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModel;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModelFactory;
import com.example.romatupkalenko.wikiresearcherapp.databinding.FragmentFavBinding;

public class FavArticlesFragment extends Fragment implements MyAdapter.MyAdapterOnItemClickHandler {
    private final static String LOG_TAG = FavArticlesFragment.class.getSimpleName();

    private MyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private MainViewModel mViewModel;
    private FragmentFavBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_fav,null,false);
        initRecyclerView();
        initMainViewModel();

        binding.setMainViewModel(mViewModel);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.saved_menu, menu);
        MenuItem item = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) item.getActionView();
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
      //  MenuItemCompat.setActionView(item, searchView);
        search(searchView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText!=null){
                    mAdapter.getFilter().filter(newText);
                    return false;
                }

                return true;
            }
        });
    }

    @Override
    public void onItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }

    private void initMainViewModel(){
        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(getActivity());
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);


        //Creates Observer interface onChanged method realization

        mAdapter.setMainViewModel(mViewModel);
        mViewModel.getFavArticles() .observe(getActivity(), newArticles ->
        {
            Log.i(LOG_TAG, "New articles:");
            int i = 0;
            for (ArticleEntry entry :
                    newArticles) {
                Log.i(LOG_TAG, "Articles[" + ++i + "] : " + entry);
            }
            mAdapter.swapArticles(newArticles);
        });

    }

    private void initRecyclerView(){
        mRecyclerView = binding.recyclerviewFavArticles;
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(getActivity(),this);
        mRecyclerView.setAdapter(mAdapter);
    }
}

