package tmnt.example.onedaily.ui.zhihu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tmnt.example.onedaily.R;
import tmnt.example.onedaily.bean.zhihu.Story;
import tmnt.example.onedaily.bean.zhihu.TopStories;
import tmnt.example.onedaily.bean.zhihu.ZhihuInfo;
import tmnt.example.onedaily.ui.common.BaseViewHolder;
import tmnt.example.onedaily.ui.zhihu.listener.OnZhihuItemClickListener;
import tmnt.example.onedaily.ui.zhihu.viewHolder.HeaderViewHolder;
import tmnt.example.onedaily.ui.zhihu.viewHolder.NewsViewHolder;
import tmnt.example.onedaily.ui.zhihu.viewHolder.ViewHolderFactory;

/**
 * Created by tmnt on 2017/4/25.
 */

public class ZhihuAdapter extends RecyclerView.Adapter {

    private List<Story> mStories;
    private List<TopStories> mTopStories;
    private Context mContext;
    private OnZhihuItemClickListener mOnZhihuItemClickListener;

    public static final int IS_HEADER = 2;
    public static final int IS_NORMAL = 1;

    public ZhihuAdapter(List<Story> stories, List<TopStories> topStories, Context context) {
        mStories = stories;
        mTopStories = topStories;
        mContext = context;
    }

    public void setOnZhihuItemClickListener(OnZhihuItemClickListener onZhihuItemClickListener) {
        mOnZhihuItemClickListener = onZhihuItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == IS_HEADER) {
            BaseViewHolder<List<TopStories>> header = ViewHolderFactory.create(IS_HEADER, mContext, parent);
            return header;
        } else {
            BaseViewHolder<List<TopStories>> item = ViewHolderFactory.create(IS_NORMAL, mContext, parent);
            return item;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BaseViewHolder baseViewHolder = (BaseViewHolder) holder;
        if (position == 0 && baseViewHolder.type == IS_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) baseViewHolder;
            headerViewHolder.setData(mContext, mTopStories);
        } else {
            NewsViewHolder newsViewHolder = (NewsViewHolder) baseViewHolder;
            newsViewHolder.setData(mContext, mStories.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mStories.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return IS_HEADER;
        } else {
            return IS_NORMAL;
        }
    }
}