package tmnt.example.onedaily.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tmnt on 2017/4/11.
 */

public abstract class BaseFragment extends Fragment implements BaseFunc {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = setContentView(inflater, container, savedInstanceState);

        initView();
        initOperation();
        loadData();

        return view;


    }

    /**
     * 设置布局
     *
     * @return
     */
    protected abstract View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 跳转activity
     *
     * @param clazz
     */
    protected void toActivity(Class<? extends BaseActivity> clazz) {
        toActivity(clazz, null);
    }

    protected void toActivity(Class<? extends BaseActivity> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}
