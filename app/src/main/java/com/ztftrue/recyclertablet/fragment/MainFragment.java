package com.ztftrue.recyclertablet.fragment;

import android.app.Service;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ztftrue.recyclertablet.DividerGridItemDecoration;
import com.ztftrue.recyclertablet.MainActivity;
import com.ztftrue.recyclertablet.OnRecyclerItemClickListener;
import com.ztftrue.recyclertablet.R;
import com.ztftrue.recyclertablet.SettingsData;
import com.ztftrue.recyclertablet.adapter.HomeAdapter;

import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.support.v4.widget.ExploreByTouchHelper.HOST_ID;

/**
 * Created by ztftrue on 2017/2/27.
 * 菜单页面
 */

public class MainFragment extends Fragment implements MainActivity.TouchOutSlide {
    Context context;
    private RecyclerView allIcon;
    private List<ResolveInfo> apps;
    HomeAdapter homeAdapter;
    ItemTouchHelper mItemTouchHelper;
    private SwipeRefreshLayout mSwipeRefreshLayoutAddSwipeRefreshLayout;
    private AppWidgetHost mAppWidgetHost = null;
    private AppWidgetManager appWidgetManager = null;
    private static final int MY_REQUEST_APPWIDGET = 1;
    private static final int MY_CREATE_APPWIDGET = 2;
    MainActivity mainActivity;
    int startPosition;

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mainActivity = (MainActivity) getActivity();
        mainActivity.setTouchOutSlide(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_icon, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        allIcon = view.findViewById(R.id.allIcon);
        mSwipeRefreshLayoutAddSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutAdd);
        init();
        mSwipeRefreshLayoutAddSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //显示所有能创建AppWidget的列表 发送此 ACTION_APPWIDGET_PICK 的Action
                Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);

                //向系统申请一个新的appWidgetId ，该appWidgetId与我们发送Action为ACTION_APPWIDGET_PICK
                //  后所选择的AppWidget绑定 。 因此，我们可以通过这个appWidgetId获取该AppWidget的信息了

                //为当前所在进程申请一个新的appWidgetId
                int newAppWidgetId = mAppWidgetHost.allocateAppWidgetId();

                //作为Intent附加值 ， 该appWidgetId将会与选定的AppWidget绑定
                pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newAppWidgetId);

                //选择某项AppWidget后，立即返回，即回调onActivityResult()方法
                startActivityForResult(pickIntent, MY_REQUEST_APPWIDGET);
                mSwipeRefreshLayoutAddSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayoutAddSwipeRefreshLayout.setEnabled(false);
            }
        });
        mAppWidgetHost = new AppWidgetHost(getActivity(), HOST_ID);
        //为了保证AppWidget的及时更新 ， 必须在Activity的onCreate/onStar方法调用该方法
        // 当然可以在onStop方法中，调用mAppWidgetHost.stopListenering() 停止AppWidget更新
        mAppWidgetHost.startListening();

        //获得AppWidgetManager对象
        appWidgetManager = AppWidgetManager.getInstance(getActivity());
    }

    void init() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        allIcon.setLayoutManager(new GridLayoutManager(context, 4));
        allIcon.addItemDecoration(new DividerGridItemDecoration(context));
        if (apps != null) {
            homeAdapter = new HomeAdapter(context, apps);
            allIcon.addOnItemTouchListener(new OnRecyclerItemClickListener(allIcon) {
                @Override
                public void onItemClick(RecyclerView.ViewHolder vh) {
                    int pos = vh.getLayoutPosition();
                    if (homeAdapter.getHeaderView() != null) {
                        pos = pos - 1;
                    }
                    if (pos < 0) {
                        return;
                    }
                    ResolveInfo info = apps.get(pos);
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
                    if (homeAdapter.getHeaderView() != null && vh.getLayoutPosition() == 0) {
                        //显示所有能创建AppWidget的列表 发送此 ACTION_APPWIDGET_PICK 的Action
                        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                        //向系统申请一个新的appWidgetId ，该appWidgetId与我们发送Action为ACTION_APPWIDGET_PICK
                        //  后所选择的AppWidget绑定 。 因此，我们可以通过这个appWidgetId获取该AppWidget的信息了
                        //为当前所在进程申请一个新的appWidgetId
                        int newAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                        //作为Intent附加值 ， 该appWidgetId将会与选定的AppWidget绑定
                        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newAppWidgetId);
                        //选择某项AppWidget后，立即返回，即回调onActivityResult()方法
                        startActivityForResult(pickIntent, MY_REQUEST_APPWIDGET);
                    } else {
                        mItemTouchHelper.startDrag(vh);
                        SettingsData.touchStart = true;

                        //获取系统震动服务
                        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
                        vib.vibrate(70);
                    }
                }
            });
            allIcon.setAdapter(homeAdapter);
        }
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            /**
             * 是否处理滑动事件 以及拖拽和滑动的方向 如果是列表类型的RecyclerView的只存在UP和DOWN，如果是网格类RecyclerView则还应该多有LEFT和RIGHT
             *
             * @param recyclerView recyclerView
             * @param viewHolder RecyclerView.ViewHolder
             * @return int
             */
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    //得到当拖拽的viewHolder的Position
                    startPosition = viewHolder.getAdapterPosition();
                    //拿到当前拖拽到的item的viewHolder
                    if (homeAdapter.getHeaderView() != null) {
                        mainActivity.resolveInfo = apps.get(startPosition - 1);
                    } else {
                        mainActivity.resolveInfo = apps.get(startPosition);

                    }

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
                if (homeAdapter.getHeaderView() != null && toPosition == 0) {
                    return true;
                }
                if (homeAdapter.getHeaderView() != null) {
                    toPosition = toPosition - 1;
                    fromPosition = fromPosition - 1;
                }

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
                //拿到当前拖拽到的item的viewHolder
                viewHolder.itemView.setBackgroundColor(0);
                SettingsData.touchStart = false;

            }

        });
        mItemTouchHelper.attachToRecyclerView(allIcon);
    }

    //向当前视图添加一个用户选择的
    private void completeAddAppWidget(Intent data) {
        Bundle extra = data.getExtras();
        int appWidgetId = extra.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        if (appWidgetId == -1) {
            Toast.makeText(getActivity(), "添加窗口小部件有误", Toast.LENGTH_SHORT).show();
            return;
        }
        AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView = mAppWidgetHost.createView(getActivity(), appWidgetId, appWidgetProviderInfo);
        int widget_minWidht = appWidgetProviderInfo.minWidth;
        int widget_minHeight = appWidgetProviderInfo.minHeight;
        //设置长宽  appWidgetProviderInfo 对象的 minWidth 和  minHeight 属性
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hostView.setLayoutParams(linearLayoutParams);
        homeAdapter.setHeaderView(hostView);
    }

    // 如果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //直接返回，没有选择任何一项 ，例如按Back键
        if (resultCode == RESULT_CANCELED)
            return;
        switch (requestCode) {
            case MY_REQUEST_APPWIDGET:
                int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                //得到的为有效的id
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    //查询指定appWidgetId的 AppWidgetProviderInfo对象 ， 即在xml文件配置的<appwidget-provider />节点信息
                    AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);

                    //如果配置了configure属性 ， 即android:configure = "" ，需要再次启动该configure指定的类文件,通常为一个Activity
                    if (appWidgetProviderInfo.configure != null) {
                        //配置此Action
                        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                        intent.setComponent(appWidgetProviderInfo.configure);
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                        startActivityForResult(intent, MY_CREATE_APPWIDGET);
                    } else  //直接创建一个AppWidget
                        onActivityResult(MY_CREATE_APPWIDGET, RESULT_OK, data);  //参数不同，简单回调而已
                }
                break;
            case MY_CREATE_APPWIDGET:
                completeAddAppWidget(data);
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAppWidgetHost != null)
            mAppWidgetHost.startListening();
    }


    @Override
    public void touchBottom() {
        if (homeAdapter.getHeaderView() != null) {
            apps.remove(startPosition - 1);
        } else {
            apps.remove(startPosition);

        }
//      homeAdapter.notifyItemRemoved(startPosition);
        homeAdapter.notifyDataSetChanged();
    }
}
