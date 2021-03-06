package tmnt.example.onedaily.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import tmnt.example.onedaily.R;
import tmnt.example.onedaily.ui.common.BaseActivity;
import tmnt.example.onedaily.ui.douban.fragment.BookPageFragment;
import tmnt.example.onedaily.ui.douban.listener.OnBookRetrunListener;
import tmnt.example.onedaily.ui.main.fragment.MsgFragment;
import tmnt.example.onedaily.ui.zhihu.fragment.ZhihuFregment;
import tmnt.example.onedaily.util.SharedPreferencesUtil;
import tmnt.example.onedaily.weight.BottomNavigation.BottomNavigationLayout;
import tmnt.example.onedaily.weight.BottomNavigation.Controller;
import tmnt.example.onedaily.weight.BottomNavigation.OnTabItemSelectListener;
import tmnt.example.onedaily.weight.BottomNavigation.TabItem;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    @Bind(R.id.bottom_na)
    BottomNavigationLayout mBottom;

    private Controller controller;
    private SharedPreferencesUtil util;
    private int mIndex;

    private static OnBookRetrunListener mOnBookRetrunListener;

    @Override
    public void initData(Bundle savedInstanceState) {
        util = SharedPreferencesUtil.getInstance(this);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        TabItem tabItem = new TabItem.Builder(this)
                .setDefaultColor(0xFFACACAC)
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setTest("首页")
                .setRes(R.drawable.ic_tab_home)
                .setTag("home")
                .build();

        TabItem tabItem1 = new TabItem.Builder(this)
                .setDefaultColor(0xFFACACAC)
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setTest("影音")
                .setRes(R.drawable.ic_tab_movie)
                .setTag("rr")
                .build();

        TabItem tabItem2 = new TabItem.Builder(this)
                .setDefaultColor(0xFFACACAC)
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setRes(R.drawable.ic_tab_write)
                .setTag("write")
                .build();

        TabItem tabItem3 = new TabItem.Builder(this)
                .setDefaultColor(0xFFACACAC)
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setTest("聊天")
                .setRes(R.drawable.ic_tab_chat)
                .setTag("notification")
                .build();

        TabItem tabItem4 = new TabItem.Builder(this)
                .setDefaultColor(0xFFACACAC)
                .setSelectColor(getResources().getColor(R.color.colorPrimary))
                .setTest("我的")
                .setRes(R.drawable.ic_tab_person)
                .setTag("person")
                .build();

        controller = mBottom.create()
                .addTabItem(tabItem)
                .addTabItem(tabItem1)
                .addTabItem(tabItem2)
                .addTabItem(tabItem3)
                .addTabItem(tabItem4)
                .build();

        Fragment zhihu = ZhihuFregment.getInstance();
        setFragment(R.id.main_contain, zhihu);
    }

    @Override
    public void initOperation() {

        controller.setSelect(0);
        controller.addTabItemClickListener(new OnTabItemSelectListener() {
            @Override
            public void onSelected(int index, Object tag) {
                Log.i(TAG, "onSelected: " + index);
                switch (index) {
                    case 0:
                        Fragment zhihu = ZhihuFregment.getInstance();
                        mIndex = index;
                        toFragment(R.id.main_contain, zhihu);
                        break;
                    case 1:
                        Fragment douban = BookPageFragment.getInstance();
                        mIndex = index;
                        if (douban.isVisible() && douban.isAdded()) {
                            if (mOnBookRetrunListener != null) {
                                mOnBookRetrunListener.onReturn();
                            }
                        } else {
                            toFragment(R.id.main_contain, douban);
                        }

                        break;
                    case 2:
                        toActivity(WriteArticleActivity.class);
                        break;
                    case 3:
                        break;
                    case 4:
                        Fragment msg = MsgFragment.getInstance();
                        mIndex = index;
                        toFragment(R.id.main_contain, msg);
                        break;
                }
            }

            @Override
            public void onRepeatClick(int index, Object tag) {

            }
        });
    }

    @Override
    public void loadData() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mBottom.setSelect(mIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = mFragmentManager.findFragmentById(R.id.main_contain);
        if (fragment instanceof MsgFragment) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void setOnBookRetrunListener(OnBookRetrunListener onBookRetrunListener) {
        mOnBookRetrunListener = onBookRetrunListener;
    }
}
