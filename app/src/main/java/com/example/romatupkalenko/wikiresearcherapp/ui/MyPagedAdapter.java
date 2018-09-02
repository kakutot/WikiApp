
package com.example.romatupkalenko.wikiresearcherapp.ui;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.romatupkalenko.wikiresearcherapp.R;
import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import com.example.romatupkalenko.wikiresearcherapp.data.paging.DataLoadingState;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModel;
import com.example.romatupkalenko.wikiresearcherapp.databinding.ArticleLayoutDatabBinding;
import com.example.romatupkalenko.wikiresearcherapp.databinding.DataLoadStateBinding;

public class MyPagedAdapter  extends PagedListAdapter<ArticleEntry, RecyclerView.ViewHolder> {
    private final MyAdapterOnItemClickHandler mClickHandler;
    private MainViewModel mainViewModel;
    private LayoutInflater layoutInflater;
    private ArticleLayoutDatabBinding mArticleBinding;
    private DataLoadStateBinding mDataStateBinding;
    private DataLoadingState dataLoadingState;

    public DataLoadingState getDataLoadingState() {
        return dataLoadingState;
    }

    public void setNetworkState(DataLoadingState newDataLoadingState) {
        DataLoadingState previousState = this.dataLoadingState;//LOaded
        boolean previousExtraRow = hasExtraRow();//false
        this.dataLoadingState = newDataLoadingState;//loading
        boolean newExtraRow = hasExtraRow();//true
        if (previousExtraRow != newExtraRow) {//true
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());//true
            }
        } else if (newExtraRow && previousState != newDataLoadingState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void setMainViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public MyPagedAdapter(@NonNull Context context,MyAdapterOnItemClickHandler clickHandler) {
        super(DIFF_CALLBACK);
        mClickHandler = clickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(layoutInflater==null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType==R.layout.article_layout_datab) {
            mArticleBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.article_layout_datab,parent,false);
            return new ArticleViewHolder(mArticleBinding);
        } else if (viewType == R.layout.data_load_state) {
             mDataStateBinding = DataBindingUtil.inflate(layoutInflater,
                     R.layout.data_load_state,parent,false);
            return new NetworkStateViewHolder(mDataStateBinding);
        } else {
            throw new IllegalArgumentException("unknown view type");
        }

    }
    private boolean hasExtraRow() {
        if (dataLoadingState != null && dataLoadingState.getStatus()
                != DataLoadingState.Status.LOADED) { return true;
        } else {
            return false;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.article_layout_datab:
                ((ArticleViewHolder) holder).setModel(getItem(position));
                break;
            case R.layout.data_load_state:
                ((NetworkStateViewHolder) holder).bindView(dataLoadingState);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.data_load_state;
        } else {
            return R.layout.article_layout_datab;
        }
    }
    public static DiffUtil.ItemCallback<ArticleEntry> DIFF_CALLBACK = new DiffUtil.ItemCallback<ArticleEntry>() {
        @Override
        public boolean areItemsTheSame(ArticleEntry oldItem,ArticleEntry newItem) {
            return oldItem.getPageId()==newItem.getPageId();
        }

        @Override
        public boolean areContentsTheSame(ArticleEntry oldItem, ArticleEntry newItem) {
            return oldItem.isFaved()==newItem.isFaved() && oldItem.getTitle()==newItem.getTitle();
        }
    };
    class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,FavClickListener{
        final ArticleLayoutDatabBinding binding;
        ArticleViewHolder(ArticleLayoutDatabBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            binding.setClicker(this);

           binding.setFavClicker(this::mOnClick);
        }
        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onItemClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */


        @Override
        public void onClick(View v) {
            String url = getCurrentList().get(getAdapterPosition()).getPageUrl();
            mClickHandler.onItemClick(url);
        }

        @Override
        public void mOnClick(View view) {
            ArticleEntry adapterArticle = getCurrentList().get( getAdapterPosition());
            boolean curVal;
            if(adapterArticle.isFaved()){
                curVal = false;
            }
            else{
                curVal = true;
            }
            if(curVal) {
                mainViewModel.makeFavArticle(adapterArticle);
            }
            else {
                mainViewModel.unFavArticle(adapterArticle.getPageId());
            }
            adapterArticle.setFaved(curVal);
            setModel(adapterArticle);

        }

        void setModel(ArticleEntry model) {
             binding.setArticle(model);
        }
    }

    static class NetworkStateViewHolder extends RecyclerView.ViewHolder {

        final DataLoadStateBinding binding;

        public NetworkStateViewHolder(DataLoadStateBinding binding) {
            super(binding.getRoot());

            this.binding = binding;


          /*  button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listItemClickListener.onRetryClick(view, getAdapterPosition());
                }
            });*/

}

        public void bindView(DataLoadingState networkState) {
            if (networkState != null && networkState.getStatus() == DataLoadingState.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == DataLoadingState.Status.FAILED) {
                binding.errorMsg.setVisibility(View.VISIBLE);
                binding.errorMsg.setText(networkState.getMsg());
            } else {
                binding.errorMsg.setVisibility(View.GONE);
            }
        }
    }

    public interface  FavClickListener{
         void  mOnClick(View view);
    }
    /*
     * The interface that receives onItemClick messages.
     */

    public interface MyAdapterOnItemClickHandler {
        void onItemClick(String url);
    }
}
