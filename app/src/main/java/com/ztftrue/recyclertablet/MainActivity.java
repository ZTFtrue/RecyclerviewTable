package com.ztftrue.recyclertablet;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.ztftrue.recyclertablet.adapter.TuoPanAdapter;
import com.ztftrue.recyclertablet.fragment.AllIconFragment;
import com.ztftrue.recyclertablet.fragment.MainFragment;
import com.ztftrue.recyclertablet.fragment.NoFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ztftrue.recyclertablet.SettingsData.touchStart;

public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private List<ResolveInfo> apps = new ArrayList<>();
    private ViewPager mHomeViewPagerViewPager;
    private RecyclerView mRecyclerBottomRecyclerView;
    TuoPanAdapter homeAdapter;
    ItemTouchHelper mItemTouchHelper;
    private float touchPY;
    public ResolveInfo resolveInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        SettingsData.screenWidth = displayMetrics.widthPixels;
        SettingsData.screenHeight = displayMetrics.heightPixels;

        mHomeViewPagerViewPager = (ViewPager) findViewById(R.id.homeViewPager);
        mRecyclerBottomRecyclerView = (RecyclerView) findViewById(R.id.recyclerBottom);
        mHomeViewPagerViewPager = (ViewPager) findViewById(R.id.homeViewPager);
        mRecyclerBottomRecyclerView = (RecyclerView) findViewById(R.id.recyclerBottom);

        final List<Fragment> fragList = new ArrayList<>();
        fragList.add(new NoFragment());
        fragList.add(new MainFragment());
        fragList.add(new AllIconFragment());
        mHomeViewPagerViewPager.setOffscreenPageLimit(2);
        mHomeViewPagerViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragList.get(position);
            }

            @Override
            public int getCount() {
                return fragList.size();
            }
        });
        mHomeViewPagerViewPager.addOnPageChangeListener(this);
        mHomeViewPagerViewPager.setCurrentItem(1);


        mRecyclerBottomRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.HORIZONTAL));
        mRecyclerBottomRecyclerView.addItemDecoration(new ItemDecoration(this,
                ItemDecoration.HORIZONTAL_LIST));
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> allApps = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < allApps.size(); i++) {
            CharSequence name = allApps.get(i).loadLabel(getPackageManager());
            if (TextUtils.equals(name, "电话") || TextUtils.equals(name, "短信") || TextUtils.equals(name, "相机") || TextUtils.equals(name, "浏览器")) {
                apps.add(allApps.get(i));
            }
        }

        homeAdapter = new TuoPanAdapter(this, apps);
        mRecyclerBottomRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerBottomRecyclerView) {
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
                Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
                vib.vibrate(70);
            }
        });
        mRecyclerBottomRecyclerView.setAdapter(homeAdapter);
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
        mItemTouchHelper.attachToRecyclerView(mRecyclerBottomRecyclerView);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    float xStart;
    float yStart;

    float x;
    float y;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (touchStart)
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xStart = ev.getX();
                    yStart = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
//                    x = ev.getX();
//                    y = ev.getY();

                    break;
                case MotionEvent.ACTION_UP:
                    x = ev.getX();
                    y = ev.getY();
                    if (y > touchPY) {
                        if (touchOutSlide != null) {
                            touchOutSlide.touchBottom();
                            apps.add(resolveInfo);
                            homeAdapter.notifyItemChanged(apps.size() - 1);
                            mRecyclerBottomRecyclerView.scrollToPosition(apps.size() - 1);
                        }
                    }
                    break;
            }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerBottomRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                SettingsData.tuopanHeight = mRecyclerBottomRecyclerView.getHeight();
                touchPY = (SettingsData.screenHeight - SettingsData.tuopanHeight / 2);
                mRecyclerBottomRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setTouchOutSlide(TouchOutSlide touchOutSlide) {
        this.touchOutSlide = touchOutSlide;
    }

    TouchOutSlide touchOutSlide;

    public interface TouchOutSlide {
        void touchBottom();
    }
}
