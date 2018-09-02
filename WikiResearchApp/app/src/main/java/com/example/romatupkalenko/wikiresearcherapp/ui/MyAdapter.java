package com.example.romatupkalenko.wikiresearcherapp.ui;

import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.romatupkalenko.wikiresearcherapp.R;
import com.example.romatupkalenko.wikiresearcherapp.data.database.ArticleEntry;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import com.example.romatupkalenko.wikiresearcherapp.viewmodel.MainViewModel;
import java.util.ArrayList;
import java.util.List;
import com.example.romatupkalenko.wikiresearcherapp.databinding.ArticleLayoutDatabBinding;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyAdapterViewHolder> implements Filterable {
    private static final String LOG_TAG = MyAdapter.class.getName();
    private final Context mContext;

    private final MyAdapterOnItemClickHandler mClickHandler;

    private List<ArticleEntry> mArticles;

    private List<ArticleEntry> mFilterArticles;

    private MainViewModel mainViewModel;

    private LayoutInflater layoutInflater;

    public void setMainViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public MyAdapter(@NonNull Context context, MyAdapterOnItemClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                FilterResults filterResults = new FilterResults();
                if (charString.isEmpty()) {
                   filterResults.values = mArticles;
                } else {
                List<ArticleEntry> articles = new ArrayList<ArticleEntry>();
                int length = charSequence.length();
                    for (ArticleEntry entry : mArticles) {
                        if (entry.getTitle().toLowerCase().substring(0,length).equals(charSequence)||
                        entry.getTitle().toUpperCase().substring(0,length).equals(charSequence))
                        {
                            articles.add(entry);
                        }
                    }
                    filterResults.values = articles;
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
               if(null!=(List<ArticleEntry>)filterResults.values){
                   mFilterArticles = (List<ArticleEntry>)filterResults.values;
                   notifyDataSetChanged();
               }
            }
        };
    }

    /*
     * The interface that receives onItemClick messages.
     */

    public interface MyAdapterOnItemClickHandler {
        void onItemClick(String url);
    }
    @Override
    public MyAdapter.MyAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       // return new AdapterFavFragment.AdapterFavFragmentViewHolder(
             //   ArticleLayoutDatabBinding.inflate(LayoutInflater.from(mContext),parent, false));

        if(layoutInflater==null)
            layoutInflater = LayoutInflater.from(mContext);
        ArticleLayoutDatabBinding binding = DataBindingUtil.inflate(layoutInflater,
               R.layout.article_layout_datab,parent,false);

       return new MyAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.MyAdapterViewHolder holder, int position) {
        holder.setModel(mFilterArticles.get(position));

        Log.i(LOG_TAG,"Current article : " + mFilterArticles.get(position ));


    }
    @Override
    public int getItemCount() {
        if(mFilterArticles!=null)
            return mFilterArticles.size();
        else
            return 0;
    }

    public void swapArticles(final List<ArticleEntry> newArticles) {
        // If there was no forecast data, then recreate all of the list
        if (mArticles == null) {
            mFilterArticles = mArticles = newArticles;
            notifyDataSetChanged();
        } else {
            /*
             * Otherwise we use DiffUtil to calculate the changes and update accordingly. This
             * shows the four methods you need to override to return a DiffUtil callback. The
             * old list is the current list stored in mForecast, where the new list is the new
             * values passed in from the observing the database.
             */

           DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mFilterArticles.size();
                }

                @Override
                public int getNewListSize() {
                    return newArticles.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mFilterArticles.get(oldItemPosition).getPageId() ==
                            newArticles.get(newItemPosition).getPageId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ArticleEntry newArticle = newArticles.get(newItemPosition);
                    ArticleEntry oldArticle = mFilterArticles.get(oldItemPosition);
                    return newArticle.isFaved() == oldArticle.isFaved()
                            && newArticle.getTitle().equals(oldArticle.getTitle());
                }
            });

            result.dispatchUpdatesTo(this);

            mFilterArticles = mArticles = newArticles;
        }
    }



    class MyAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,FavClickListener{
        final ArticleLayoutDatabBinding binding;
        MyAdapterViewHolder(ArticleLayoutDatabBinding binding) {
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
        **/

        @Override
        public void onClick(View v) {
            String url = mFilterArticles.get(getAdapterPosition()).getPageUrl();
            mClickHandler.onItemClick(url);
        }

        @Override
        public void mOnClick(View view) {
            ArticleEntry articleEntry = mFilterArticles.get( getAdapterPosition());
            boolean curVal;
            if(articleEntry.isFaved()){
                curVal = false;
            }
            else{
                curVal = true;
            }
            if(curVal) mainViewModel.makeFavArticle(articleEntry);
            else mainViewModel.unFavArticle(articleEntry.getId());
        }

        void setModel(ArticleEntry model) {
           binding.setArticle(model);
        }
    }

    public interface  FavClickListener{
        void  mOnClick(View view);
    }
}
