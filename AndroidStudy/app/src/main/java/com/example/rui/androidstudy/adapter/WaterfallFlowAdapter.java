package com.example.rui.androidstudy.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rui.androidstudy.R;
import com.example.rui.androidstudy.auxiliary.ItemTouchHelperViewHolder;
import com.example.rui.androidstudy.info.FunctionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;

/**
 * Created by zonelue003 on 2017/6/21.
 */

public class WaterfallFlowAdapter extends RecyclerView.Adapter<WaterfallFlowAdapter.WaterfallFlowViewHolder> implements ItemTouchHelperViewHolder {
    Context context;

    private View view;
    private List<Integer> heightList;//装产出的随机数
    private List<FunctionInfo> list = new ArrayList<FunctionInfo>();//名称
    private OnRecyclerItemClickListener mOnItemClickListener;//单击事件
    private onRecyclerItemLongClickListener mOnItemLongClickListener;//长按事件
    Random rand = new Random();

    public WaterfallFlowAdapter(Context context, List<FunctionInfo> list) {
        this.context = context;
        this.list = list;
        //记录为每个控件产生的随机高度,避免滑回到顶部出现空白
        heightList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int height = new Random().nextInt(200) + 300;//[100,300)的随机数
            heightList.add(height);
        }
    }

    @Override
    public WaterfallFlowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_waterfall_flow, null);
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mOnItemClickListener != null) {
                    //注意这里使用getTag方法获取数据
                    mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
                }
            }
        });*/
        return new WaterfallFlowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WaterfallFlowViewHolder holder, int position) {
        //to do bind view
        //把数据绑到每个itemView上；
        holder.itemView.setTag(position);
        //由于需要实现瀑布流的效果,所以就需要动态的改变控件的高度了
        ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
        params.height = heightList.get(position);
        holder.cardView.setLayoutParams(params);
        holder.tv_functionName.setText(list.get(position).getName());
        holder.ib_functionPicture.setImageResource(list.get(position).getPricture());
        //设置单击事件
        if (mOnItemClickListener != null) {
            holder.ib_functionPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里是为textView设置了单击事件,回调出去
                    //这里需要获取布局中的position,不然乱序
                    mOnItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
            //长按事件
            holder.ib_functionPicture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //回调出去
                    mOnItemLongClickListener.onItemLongClick(v, holder.getLayoutPosition());
                    return true;//不返回true,松手还会去执行单击事件
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemSelected() {

    }

    @Override
    public void onItemClear() {

    }

    class WaterfallFlowViewHolder extends RecyclerView.ViewHolder {
        ImageButton ib_functionPicture;
        TextView tv_functionName;
        LinearLayout linearLayout;
        CardView cardView;

        public WaterfallFlowViewHolder(View itemView) {
            super(itemView);
            ib_functionPicture = (ImageButton) itemView.findViewById(R.id.ib_functionPicture);
            tv_functionName = (TextView) itemView.findViewById(R.id.functionName);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }

    /**
     * 处理item的点击事件,因为recycler没有提供单击事件,所以只能自己写了
     */
    public interface OnRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 长按事件
     */
    public interface onRecyclerItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * 暴露给外面的设置单击事件
     */
    public void setOnItemClickListener(OnRecyclerItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * 暴露给外面的长按事件
     */
    public void setOnItemLongClickListener(onRecyclerItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }


    /**
     * 向指定位置添加元素
     */
    public void addItem(int position, String value) {
        if (position > list.size()) {
            position = list.size();
        }
        if (position < 0) {
            position = 0;
        }
        /**
         * 使用notifyItemInserted/notifyItemRemoved会有动画效果
         * 而使用notifyDataSetChanged()则没有
         */
        //list.add(position, value);//在集合中添加这条数据
        heightList.add(position, new Random().nextInt(200) + 100);//添加一个随机高度,会在onBindViewHolder方法中得到设置
        notifyItemInserted(position);//通知插入了数据
    }

    /**
     * 移除指定位置元素
     */
    public String removeItem(int position) {
        if (position > list.size() - 1) {
            return null;
        }
        heightList.remove(position);//删除添加的高度
        String value = list.remove(position).getName();//所以还需要手动在集合中删除一次
        notifyItemRemoved(position);//通知删除了数据,但是没有删除list集合中的数据
        return value;
    }
}
