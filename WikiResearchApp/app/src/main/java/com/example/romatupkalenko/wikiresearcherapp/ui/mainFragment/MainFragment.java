
package com.example.romatupkalenko.wikiresearcherapp.ui.mainFragment;

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
import com.example.romatupkalenko.wikiresearcherapp.data.paging.DataLoadingState;
import com.example.romatupkalenko.wikiresearcherapp.ui.MyAdapter;
import com.example.romatupkalenko.wikiresearcherapp.ui.MyPagedAdapter;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.UtilViewModel;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModel;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModelFactory;
import com.example.romatupkalenko.wikiresearcherapp.databinding.FragmentMainBinding;

public class MainFragment extends Fragment implements MyAdapter.MyAdapterOnItemClickHandler,
        MyPagedAdapter.MyAdapterOnItemClickHandler{
    private final static String LOG_TAG = MainFragment.class.getSimpleName();

    private MyAdapter mAdapter;
    private MyPagedAdapter myPagedAdapter;
    private RecyclerView mRecyclerView;
    private MainViewModel mViewModel;
    private UtilViewModel mUtilViewModel;
    private boolean firstLoad;
    private FragmentMainBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(LOG_TAG,"onCreateView");
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,null,false);

        initRecyclerView();

        initViewModelData();

        initUtilViewModel();

        binding.setMainModel(mViewModel);

        binding.setUtilModel(mUtilViewModel);

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
        SearchView searchView = (SearchView) item.getActionView();;
        searchView.setIconifiedByDefault(false);

        search(searchView);
    }
    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(LOG_TAG,"Search is done");

                myPagedAdapter = new MyPagedAdapter(getActivity(),MainFragment.this::onItemClick);
                mViewModel.setCurrentTitle(query);

                myPagedAdapter.setMainViewModel(mViewModel);
                mRecyclerView.setAdapter(myPagedAdapter);
                mViewModel.getRepoResult().observe(getActivity(),
                        (newRepoList)->
                        {
                            newRepoList.getPagedListLiveData().observe(getActivity(),(list)->{
                                    myPagedAdapter.submitList(list);
                            });
                            newRepoList.getDataLoadingStateLiveData().observe(getActivity(),(dataState)->{
                                myPagedAdapter.setNetworkState(dataState);
                            });
                            newRepoList.getInitialdataLoadingStateLiveData().observe(getActivity(),(dataState)->{
                                if(dataState.getStatus()== DataLoadingState.Status.LOADED){
                                    visRecyclerView();
                                }
                                else
                                    invisRecyclerView();
                            });
                        });


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void initViewModelData(){

        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(getActivity());
        mViewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);

        //Creates Observer interface onChanged method realization

        mAdapter.setMainViewModel(mViewModel);

    }
    private void initUtilViewModel(){
        mUtilViewModel = ViewModelProviders.of(getActivity()).get(UtilViewModel.class);
        mUtilViewModel.getNetworkState().observe(this, aBoolean -> {
            Log.i(LOG_TAG, "Broadcast receiver is online" + "| Network State : " + aBoolean);
            binding.setUtilModel(mUtilViewModel);
            if(aBoolean){
                mViewModel.getArticles() .observe(getActivity(), newArticles ->
                {
                    Log.i(LOG_TAG, "New articles:");
                    if(newArticles!=null && newArticles.size()>0) {
                        Log.i(LOG_TAG, "VIS REC VIEW");
                        int i = 0;
                        for (ArticleEntry entry :
                                newArticles) {
                            Log.i(LOG_TAG, "Articles[" + ++i + "] : " + entry);
                        }
                        visRecyclerView();
                        mAdapter.swapArticles(newArticles);
                    }
                    else {
                        Log.i(LOG_TAG, "INVIS REC VIEW");
                        invisRecyclerView();
                    }
                });
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

    mAdapter = new MyAdapter(getActivity(),this);

    mRecyclerView.setAdapter(mAdapter);
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
