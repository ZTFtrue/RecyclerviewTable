package com.ztftrue.recyclertablet.adapter;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztftrue.recyclertablet.R;

import java.util.List;

/**
 * Created by ztftrue on 2017/9/15
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private Context context;
    private static final int TYPE_HEADER = 0;  //说明是带有Header的
    private static final int TYPE_NORMAL = 2;  //说明是不带有header和footer的
    private List<ResolveInfo> list;
    private View mHeaderView;


    public HomeAdapter(Context context, List<ResolveInfo> exhibitlist) {
        list = exhibitlist;
        this.context = context;

    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        removeHeaderView();
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public void removeHeaderView() {
        if (mHeaderView != null) {
            notifyItemRemoved(0);
            mHeaderView = null;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new MyViewHolder(mHeaderView);
        }
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.adapter_home, parent,
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
        holder.textName.setText(list.get(pos).loadLabel(context.getPackageManager()));
        holder.imageView.setImageDrawable(list.get(pos).activityInfo.loadIcon(context.getPackageManager()));
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? list.size() : list.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        ImageView imageView;
        RelativeLayout relativeLayout;

        MyViewHolder(View view) {
            super(view);
            if (itemView == mHeaderView) {
                return;
            }
            relativeLayout = view.findViewById(R.id.parentPanel);
            textName = view.findViewById(R.id.nameLauncher);
            imageView = view.findViewById(R.id.iconLauncher);
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 240);

            relativeLayout.setLayoutParams(layoutParams);
            params.height = 110;
            params.width = 110;
            imageView.setLayoutParams(params);
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }
}