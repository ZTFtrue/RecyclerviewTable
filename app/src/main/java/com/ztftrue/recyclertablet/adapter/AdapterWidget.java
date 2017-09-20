package com.ztftrue.recyclertablet.adapter;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ztftrue.recyclertablet.R;

import java.util.List;

/**
 * Created by ztftrue on 2017/9/15
 */

public class AdapterWidget extends RecyclerView.Adapter<AdapterWidget.MyViewHolder> {

    private Context context;
    private static final int TYPE_HEADER = 0;  //说明是带有Header的
    private static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的
    private List<AppWidgetHostView> list;
    private View mHeaderView;

    public AdapterWidget(Context context, List<AppWidgetHostView> exhibitlist) {
        list = exhibitlist;
        this.context = context;


    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new MyViewHolder(mHeaderView);
        }
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.adapter_widget, parent,
                false));
        return holder;
    }

    private int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        final int pos = getRealPosition(holder);
//        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        holder.relativeLayout.addView(list.get(pos));
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? list.size() : list.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;

        MyViewHolder(View view) {
            super(view);
            if (itemView == mHeaderView) {
                return;
            }
            relativeLayout = view.findViewById(R.id.parentPanel);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            //第一个item应该加载Header
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }


}