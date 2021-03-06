package tmnt.example.onedaily.ui.zhihu.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.Bind;
import butterknife.ButterKnife;
import tmnt.example.onedaily.R;
import tmnt.example.onedaily.bean.zhihu.Story;
import tmnt.example.onedaily.bean.zhihu.TopStories;
import tmnt.example.onedaily.bean.zhihu.ZhihuInfo;
import tmnt.example.onedaily.ui.common.BaseFragment;
import tmnt.example.onedaily.ui.zhihu.activity.ZhihuDetailActivity;
import tmnt.example.onedaily.ui.zhihu.adapter.ZhihuAdapter;
import tmnt.example.onedaily.ui.zhihu.listener.OnLoadingListener;
import tmnt.example.onedaily.ui.zhihu.listener.OnZhihuItemClickListener;
import tmnt.example.onedaily.ui.zhihu.model.ZhihuModel;
import tmnt.example.onedaily.ui.zhihu.presenter.ZhihuPresentor;
import tmnt.example.onedaily.ui.zhihu.viewHolder.NewsViewHolder;
import tmnt.example.onedaily.util.DateFormatUtil;
import tmnt.example.onedaily.util.SharedPreferencesUtil;

/**
 * Created by tmnt on 2017/4/24.
 */

public class ZhihuFregment extends BaseFragment implements tmnt.example.onedaily.mvp.View<ZhihuInfo> {

    @Bind(R.id.rv_zhihu)
    RecyclerView mRvZhihu;
    @Bind(R.id.img_zhihu_empty)
    ImageView mImgZhihuEmpty;
    @Bind(R.id.spl_zhihu)
    SwipeRefreshLayout mSplZhihu;


    private List<TopStories> mTopStories;
    private List<TopStories> mTopStoriesCopy;
    private List<Story> mStories;
    private ZhihuPresentor mZhihuPresentor;
    private ZhihuAdapter mZhihuAdapter;
    private ZhihuModel model;
    private SharedPreferencesUtil mSharedPreferencesUtil;
    private android.os.Handler mHandler;

    private int page = 0;
    private Bundle mBundle;

    public static final String ZHIHU_ID = "zhihu_id";
    public static final String ZHIHU_TITLE = "zhihu_title";

    private static final String TAG = "ZhihuFregment";

    @Override
    protected View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zhihu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTopStories = new ArrayList<>();
        mTopStoriesCopy = new ArrayList<>();
        mStories = new ArrayList<>();
        model = new ZhihuModel();
        mZhihuPresentor = new ZhihuPresentor(model, this);
        mSharedPreferencesUtil = SharedPreferencesUtil.getInstance(getActivity());
        mBundle = new Bundle();

    }

    @Override
    public void initView() {

    }

    @Override
    public void initOperation() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRvZhihu.setLayoutManager(linearLayoutManager);
        mZhihuAdapter = new ZhihuAdapter(mStories, mTopStories, getActivity());
        mRvZhihu.setAdapter(mZhihuAdapter);

        mZhihuAdapter.setOnZhihuItemClickListener(new OnZhihuItemClickListener() {
            @Override
            public void onItemCardClick(View v, int position) {
                createBundle(String.valueOf(mStories.get(position).getId())
                        , mStories.get(position).getTitle());
            }

            @Override
            public void onItemSlideClick(View v, int position) {
                createBundle(String.valueOf(mTopStoriesCopy.get(position - 1).getId())
                        , mTopStoriesCopy.get(position - 1).getTitle());
            }
        });

        mSplZhihu.setColorSchemeColors(new int[]{Color.parseColor("#26f913"), Color.parseColor("#ef4054")
                , Color.parseColor("#e9ec56"), Color.parseColor("#009dff")});

        mSplZhihu.setOnRefreshListener(() ->
                mZhihuPresentor.handleData());

        mRvZhihu.setOnScrollListener(new OnLoadingListener(linearLayoutManager) {
            @Override
            public void onLoading(android.os.Handler handler) {
                mHandler = handler;
                mHandler.sendEmptyMessage(OnLoadingListener.LOAD);
                String s = DateFormatUtil.dateFormatForSub(page);
                page++;
                mZhihuPresentor.handleLoad(s);
                Log.i(TAG, "onLoading: " + page);
            }
        });

    }

    @Override
    public void loadData() {
        mZhihuPresentor.handleData();
    }

    private void createBundle(String value, String title) {
        mBundle.putString(ZHIHU_ID, value);
        mBundle.putString(ZHIHU_TITLE, title);
        toActivity(ZhihuDetailActivity.class, mBundle);
    }

    @Override
    public void showData(ZhihuInfo datas) {
        Log.i(TAG, "showData: " + datas.getTop_stories().size());
        mSplZhihu.setRefreshing(false);
        if (datas.getTop_stories() != null) {
            mTopStories.addAll(datas.getTop_stories());
            mTopStoriesCopy.addAll(mTopStories);
        }
        if (datas.getDate() != null) {
            mZhihuAdapter.setDate(datas.getDate(), true);
        }
        mStories.addAll(datas.getStories());
        // mZhihuAdapter.notifyDataSetChanged();
        mZhihuAdapter.notityData();
    }

    @Override
    public void showLoadData(ZhihuInfo datas) {
        Log.i(TAG, "showLoadData: " + datas);
        if (datas.getDate() != null) {
            mZhihuAdapter.setDate(datas.getDate(), true);
        }
        mStories.addAll(datas.getStories());
        mHandler.sendEmptyMessage(OnLoadingListener.LOAD_OVER);
        mZhihuAdapter.notityData();
    }

    @Override
    public void showRefreshData(ZhihuInfo datas) {

    }

    @Override
    public void showError(Throwable throwable) {
        Log.i(TAG, "showError: " + throwable.toString());
        mImgZhihuEmpty.setVisibility(View.VISIBLE);
        mSplZhihu.setVisibility(View.GONE);
    }

    public static Fragment getInstance() {
        ZhihuFregment fregment = new ZhihuFregment();
        return fregment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        mSharedPreferencesUtil.removeData(NewsViewHolder.DATE);
    }
}
