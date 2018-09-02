
package com.example.romatupkalenko.wikiresearcherapp.ui.mainFragment;

import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.util.StringUtil;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
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
import com.example.romatupkalenko.wikiresearcherapp.data.paging.DataLoadingState;
import com.example.romatupkalenko.wikiresearcherapp.data.repository.Listing;
import com.example.romatupkalenko.wikiresearcherapp.ui.MyAdapter;
import com.example.romatupkalenko.wikiresearcherapp.ui.MyPagedAdapter;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.UtilViewModel;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModel;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModelFactory;
import com.example.romatupkalenko.wikiresearcherapp.databinding.FragmentMainBinding;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainFragment extends Fragment implements MyAdapter.MyAdapterOnItemClickHandler,
        MyPagedAdapter.MyAdapterOnItemClickHandler{
    private final static String LOG_TAG = MainFragment.class.getSimpleName();

    private MyPagedAdapter myPagedAdapter;
    private RecyclerView mRecyclerView;
    private MainViewModel mViewModel;
    private UtilViewModel mUtilViewModel;
    private FragmentMainBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG,"onCreateView");
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,null,false);

        initRecyclerView();

        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(getActivity());
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        initViewModelData();

        initSearch();

        mUtilViewModel = ViewModelProviders.of(this).get(UtilViewModel.class);
        initUtilViewModel();

        binding.setMainModel(mViewModel);

        return binding.getRoot();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        Log.i(LOG_TAG,"onActivityCreated");

    }


    @Override
    public void onItemClick(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.main_search_item);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                mViewModel.setCurrentTitle("");
                return true;
            }
        });
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setIconifiedByDefault(false);
        search(searchView);
    }
    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(LOG_TAG,"Search is done");

                mViewModel.setCurrentTitle(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void initViewModelData(){


        myPagedAdapter.setMainViewModel(mViewModel);

    }
    private void initUtilViewModel(){


        mUtilViewModel.getNetworkState().observe(this, aBoolean -> {
            Log.i(LOG_TAG, "Broadcast receiver is online" + "| Network State : " + aBoolean);
            binding.setUtilModel(mUtilViewModel);
            if(aBoolean){
                mViewModel.getCurrentTitle().observe(this,newTitle->{
                    Log.i(LOG_TAG,"Current title :" + newTitle);
                        mViewModel.getArticles().observe(this, (articles)->{
                            if(articles!=null && articles.size()>0){
                                Log.i(LOG_TAG,"Current articles number :" + articles.size());
                                Listing<ArticleEntry> listing = mViewModel.getRandomRepoResult();
                                final boolean[] isFirst = {false};
                                listing.getPagedListLiveData().observe(this,(list)->{
                                    if(!list.getDataSource().isInvalid()&& !isFirst[0])
                                    {
                                        if(mViewModel.getCurrentTitle().getValue().isEmpty()){
                                            isFirst[0] = true;
                                            Log.i(LOG_TAG,"New list submitted");
                                            myPagedAdapter.submitList(list);
                                        }
                                    }
                                    if(articles.size()>=myPagedAdapter.getItemCount()){
                                        myPagedAdapter.setNetworkState(DataLoadingState.LOADING); }
                                });
                                listing.getDataLoadingStateLiveData().observe(this,(dataState)->{
                                    Log.i(LOG_TAG,"Current data status:" + dataState.getStatus());
                                    myPagedAdapter.setNetworkState(dataState);
                                });
                                listing.getInitialdataLoadingStateLiveData().observe(this,(dataState)->{
                                    if(dataState.getStatus()== DataLoadingState.Status.LOADED){
                                        visRecyclerView();
                                        Log.i(LOG_TAG,"Init data load status : LOADED");
                                    }
                                    else{
                                        invisRecyclerView();
                                        Log.i(LOG_TAG,"Init data load status : LOADING");
                                    }

                                });
                            }
                        });
                    });
                }
                else if(!aBoolean && myPagedAdapter.getItemCount()>0){
                    myPagedAdapter.setNetworkState(DataLoadingState.LOADING);
                    binding.recyclerviewArticles.setVisibility(View.VISIBLE);
                    binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
                     }
                 else{
                invisRecyclerView();
                binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
            }
            });
        }
    private void initRecyclerView(){

    mRecyclerView = binding.recyclerviewArticles;
    LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    mRecyclerView.setLayoutManager(layoutManager);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */

    mRecyclerView.setHasFixedSize(true);

  //  mAdapter = new MyAdapter(getActivity(),this);
    myPagedAdapter = new MyPagedAdapter(getActivity(),MainFragment.this::onItemClick);

    mRecyclerView.setAdapter(myPagedAdapter);
    }

    public  void initSearch(){
        mViewModel.setCurrentTitle("");
        mViewModel.getSearchRepoResult().observe(MainFragment.this,
                (newRepoList)->
                {
                    if(newRepoList!=null){
                        newRepoList.getPagedListLiveData().observe(MainFragment.this,(list)->{
                            myPagedAdapter.submitList(list);
                        });
                        newRepoList.getDataLoadingStateLiveData().observe(MainFragment.this,(dataState)->{
                            myPagedAdapter.setNetworkState(dataState);
                        });
                        newRepoList.getInitialdataLoadingStateLiveData().observe(MainFragment.this,(dataState)->{
                            if(dataState.getStatus()== DataLoadingState.Status.LOADED){
                                visRecyclerView();
                            }
                            else
                                invisRecyclerView();
                        });
                    }

                });
    }
    private void invisRecyclerView(){
        binding.recyclerviewArticles.setVisibility(View.INVISIBLE);
        binding.pbLoadingIndicator.setVisibility(View.VISIBLE);
    }
    private void visRecyclerView(){
        binding.recyclerviewArticles.setVisibility(View.VISIBLE);
        binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
    }
}
