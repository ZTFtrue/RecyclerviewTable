package com.ztftrue.recyclertablet.fragment;

import android.app.Service;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ztftrue.recyclertablet.adapter.AdapterWidget;
import com.ztftrue.recyclertablet.ItemDecoration;
import com.ztftrue.recyclertablet.OnRecyclerItemClickListener;
import com.ztftrue.recyclertablet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * Created by ztftrue on 2017/2/27.
 * 选择博物馆下载页面
 */

public class NoFragment extends Fragment {


    private RecyclerView mAllIconRecyclerView;
    private AdapterWidget adapterWidget;
    private static final int MY_REQUEST_APPWIDGET = 1;
    private static final int MY_CREATE_APPWIDGET = 2;
    ItemTouchHelper mItemTouchHelper;
    private static final int HOST_ID = 1024;

    private AppWidgetHost mAppWidgetHost = null;
    AppWidgetManager appWidgetManager = null;
    List<AppWidgetHostView> list = new ArrayList<>();
    private EditText mSearchEdittextSearchView;

    private ImageButton mQrCodeImageButton;
    private LinearLayout mHeaderLinearLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapterWidget = new AdapterWidget(getActivity(), list);
        mAllIconRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerWidget);
        mSearchEdittextSearchView = (EditText) view.findViewById(R.id.searchEdittext);
        mQrCodeImageButton = (ImageButton) view.findViewById(R.id.qrCode);
        initWidget();
        initHeader();
    }

    private void initWidget() {
        mAllIconRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAllIconRecyclerView.addItemDecoration(new ItemDecoration(getActivity(),
                ItemDecoration.VERTICAL_LIST));
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
                        Collections.swap(list, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(list, i, i - 1);
                    }
                }
                adapterWidget.notifyItemMoved(fromPosition, toPosition);
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
        mItemTouchHelper.attachToRecyclerView(mAllIconRecyclerView);
        mAllIconRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mAllIconRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
//                ResolveInfo info = apps.get(vh.getLayoutPosition());
////该应用的包名
//                String pkg = info.activityInfo.packageName;
////应用的主activity类
//                String cls = info.activityInfo.name;
//                ComponentName componet = new ComponentName(pkg, cls);
//                Intent intent = new Intent();
//                intent.setComponent(componet);
//                startActivity(intent);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                mItemTouchHelper.startDrag(vh);
                //获取系统震动服务
                Vibrator vib = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
                vib.vibrate(70);
            }
        });

        mAllIconRecyclerView.setAdapter(adapterWidget);
        //其参数hostid大意是指定该AppWidgetHost 即本Activity的标记Id， 直接设置为一个整数值吧 。
        mAppWidgetHost = new AppWidgetHost(getActivity(), HOST_ID);
        //为了保证AppWidget的及时更新 ， 必须在Activity的onCreate/onStar方法调用该方法
        // 当然可以在onStop方法中，调用mAppWidgetHost.stopListenering() 停止AppWidget更新
        mAppWidgetHost.startListening();
        //获得AppWidgetManager对象
        appWidgetManager = AppWidgetManager.getInstance(getActivity());
    }

    private void initHeader() {
        mQrCodeImageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //显示所有能创建AppWidget的列表 发送此 ACTION_APPWIDGET_PICK 的Action
                Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                //向系统申请一个新的appWidgetId ，该appWidgetId与我们发送Action为ACTION_APPWIDGET_PICK
                //后所选择的AppWidget绑定 。 因此，我们可以通过这个appWidgetId获取该AppWidget的信息了
                //为当前所在进程申请一个新的appWidgetId
                int newAppWidgetId = mAppWidgetHost.allocateAppWidgetId();
                //作为Intent附加值 ， 该appWidgetId将会与选定的AppWidget绑定
                pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, newAppWidgetId);
                //选择某项AppWidget后，立即返回，即回调onActivityResult()方法
                startActivityForResult(pickIntent, MY_REQUEST_APPWIDGET);
                return true;
            }
        });
        mQrCodeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mSearchEdittextSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(getActivity(), "1111111", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
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
        int widget_minHeight = appWidgetProviderInfo.minHeight;
        //设置长宽  appWidgetProviderInfo 对象的 minWidth 和  minHeight 属性
        RelativeLayout.LayoutParams linearLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        hostView.setLayoutParams(linearLayoutParams);
        list.add(hostView);
        adapterWidget.notifyItemChanged(list.size() - 1);
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
}
