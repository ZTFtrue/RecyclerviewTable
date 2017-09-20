package com.ztftrue.recyclertablet.fragment;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ztftrue.recyclertablet.DividerGridItemDecoration;
import com.ztftrue.recyclertablet.adapter.HomeAdapter;
import com.ztftrue.recyclertablet.OnRecyclerItemClickListener;
import com.ztftrue.recyclertablet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by ztftrue on 2017/2/27.
 * 菜单页面
 */

public class AllIconFragment extends Fragment {
    Context context;
    private RecyclerView allIcon;
    private List<ResolveInfo> apps = new ArrayList<>();
    HomeAdapter homeAdapter;
    ItemTouchHelper mItemTouchHelper;

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_all_icon, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeAdapter = new HomeAdapter(context, apps);
        allIcon = (RecyclerView) view.findViewById(R.id.allIcon);
        final SwipeRefreshLayout mSwipeRefreshLayoutAddSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutAdd);
        init();
        mSwipeRefreshLayoutAddSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //显示所有能创建AppWidget的列表 发送此 ACTION_APPWIDGET_PICK 的Action
                init();
                mSwipeRefreshLayoutAddSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    void init() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps.addAll(context.getPackageManager().queryIntentActivities(mainIntent, 0));
        allIcon.setLayoutManager(new GridLayoutManager(context, 4));
        allIcon.addItemDecoration(new DividerGridItemDecoration(context));
        if (apps != null) {
            allIcon.addOnItemTouchListener(new OnRecyclerItemClickListener(allIcon) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder vh) {
                    ResolveInfo info = apps.get(vh.getLayoutPosition());
//该应用的包名
                    String pkg = info.activityInfo.packageName;
//应用的主activity类
                    String cls = info.activityInfo.name;
                    ComponentName componet = new ComponentName(pkg, cls);
                    Intent intent = new Intent();
                    intent.setComponent(componet);
                    startActivity(intent);
                }

                @Override
                public void onItemLongClick(RecyclerView.ViewHolder vh) {
                    mItemTouchHelper.startDrag(vh);
                    //获取系统震动服务
                    Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
                    vib.vibrate(70);
                }
            });
            allIcon.setAdapter(homeAdapter);
        } else {

        }
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            /**
             * 是否处理滑动事件 以及拖拽和滑动的方向 如果是列表类型的RecyclerView的只存在UP和DOWN，如果是网格类RecyclerView则还应该多有LEFT和RIGHT
             * @param recyclerView
             * @param viewHolder
             * @return
             */
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    final int swipeFlags = 0;
                    return makeMovementFlags(dragFlags, swipeFlags);
                } else {
                    final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    final int swipeFlags = 0;
//                    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(apps, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(apps, i, i - 1);
                    }
                }
                homeAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                myAdapter.notifyItemRemoved(position);
//                datas.remove(position);
            }

            /**
             * 重写拖拽可用
             * @return
             */
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            /**
             * 长按选中Item的时候开始调用
             *
             * @param viewHolder
             * @param actionState
             */
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 手指松开的时候还原
             * @param recyclerView
             * @param viewHolder
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(0);
            }
        });
        mItemTouchHelper.attachToRecyclerView(allIcon);
    }

}
