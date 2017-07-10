package com.example.rui.androidstudy;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;
import com.example.rui.androidstudy.adapter.WaterfallFlowAdapter;
import com.example.rui.androidstudy.auxiliary.ItemTouchHelperCallback;
import com.example.rui.androidstudy.figure.ShapesActivity;
import com.example.rui.androidstudy.info.FunctionInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<FunctionInfo> list = new ArrayList<FunctionInfo>();
    private WaterfallFlowAdapter waterfallFlowAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private FunctionInfo functionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        creatData();
        waterfallFlowAdapter = new WaterfallFlowAdapter(this, list);
        recyclerView.setHasFixedSize(true);
        //设置StaggeredGridLayoutManager布局管理器，参数分别是列数，方向。
        if (isScreenChange()) {
            layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        } else {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }
        //位置发生变化
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layoutManager);
        //设置布局管理器
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelperCallback(list, waterfallFlowAdapter));
        helper.attachToRecyclerView(recyclerView);
        //设置添加,移除item的动画,DefaultItemAnimator为默认的
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(waterfallFlowAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //防止第一行到顶部有空白区域
                layoutManager.invalidateSpanAssignments();
            }
        });
        //设置RecyclerView的每一项的点击事件
        waterfallFlowAdapter.setOnItemClickListener(new WaterfallFlowAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0: {
                        startActivity(new Intent(MainActivity.this, SlidingMenuActivity.class));
                    }
                    break;
                    case 1: {
                        startActivity(new Intent(MainActivity.this, ShapesActivity.class));
                    }
                    break;
                }
                Toast.makeText(MainActivity.this, "点击事件：" + list.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        //设置RecyclerView的每一项的长按事件
        waterfallFlowAdapter.setOnItemLongClickListener(new WaterfallFlowAdapter.onRecyclerItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "长按事件：" + list.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void creatData() {
        setFunctionInfo("抽屉功能", R.drawable.test2);
        setFunctionInfo("画画功能", R.drawable.test2);
        setFunctionInfo("抽屉功能", R.drawable.test2);
        setFunctionInfo("抽屉功能", R.drawable.test2);
        setFunctionInfo("抽屉功能", R.drawable.test2);
    }

    private void setFunctionInfo(String name, int pricture) {
        functionInfo = new FunctionInfo();
        functionInfo.setName(name);
        functionInfo.setPricture(pricture);
        list.add(functionInfo);
    }

    public boolean isScreenChange() {
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            return true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏
            return false;
        }
        return false;
    }
}
