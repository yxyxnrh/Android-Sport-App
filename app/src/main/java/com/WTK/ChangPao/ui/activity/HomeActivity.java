package com.WTK.ChangPao.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.WTK.ChangPao.MyApplication;
import com.WTK.ChangPao.R;
import com.WTK.ChangPao.commmon.bean.PathRecord;
import com.WTK.ChangPao.commmon.bean.SportMotionRecord;
import com.WTK.ChangPao.commmon.utils.DateUtils;
import com.WTK.ChangPao.commmon.utils.LogUtils;
import com.WTK.ChangPao.commmon.utils.MySp;
import com.WTK.ChangPao.commmon.utils.UIHelper;
import com.WTK.ChangPao.db.DataManager;
import com.WTK.ChangPao.db.RealmHelper;
import com.WTK.ChangPao.sport_motion.MotionUtils;
import com.WTK.ChangPao.ui.BaseActivity;
import com.WTK.ChangPao.ui.adapter.SportCalendarAdapter;
import com.WTK.ChangPao.ui.weight.calendarview.custom.CustomWeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    /**
     * 上次点击返回键的时间
     */
    private long lastBackPressed;

    //上次点击返回键的时间
    public static final int QUIT_INTERVAL = 2500;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_month_day)
    TextView mTextMonthDay;
    @BindView(R.id.tv_year)
    TextView mTextYear;
    @BindView(R.id.tv_lunar)
    TextView mTextLunar;
    @BindView(R.id.tv_current_day)
    TextView mTextCurrentDay;
    @BindView(R.id.calendarView)
    CalendarView mCalendarView;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.calendarLayout)
    CalendarLayout mCalendarLayout;
    @BindView(R.id.sport_achievement)
    LinearLayout sport_achievement;

    private Dialog tipDialog = null;

    private int mYear;

    private SportCalendarAdapter adapter;
    private List<PathRecord> sportList = new ArrayList<>(0);

    private DataManager dataManager = null;

    private final int SPORT = 0x0012;

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        tvTitle.setText(" ");

        dataManager = new DataManager(new RealmHelper());

        mYear = mCalendarView.getCurYear();
        mTextYear.setText(String.valueOf(mYear));
        mTextMonthDay.setText(UIHelper.getString(R.string.date_month_day, mCalendarView.getCurMonth(), mCalendarView.getCurDay()));
        mTextLunar.setText("今天");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.line)));
        adapter = new SportCalendarAdapter(R.layout.adapter_sportcalendar, sportList);
        mRecyclerView.setAdapter(adapter);

        mCalendarView.setWeekStarWithSun();

        mCalendarView.setWeekBar(CustomWeekBar.class);

        upDateUI();

    }

    private void upDateUI() {
        loadSportData();

        getSports(DateUtils.formatStringDateShort(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay()));
    }

    private void loadSportData() {
        try {
            List<SportMotionRecord> records = dataManager.queryRecordList(Integer.parseInt(SPUtils.getInstance().getString(MySp.USERID, "0")));
            if (null != records) {
                Map<String, Calendar> map = new HashMap<>();
                for (SportMotionRecord record : records) {
                    String dateTag = record.getDateTag();
                    String[] strings = dateTag.split("-");
                    int year = Integer.parseInt(strings[0]);
                    int month = Integer.parseInt(strings[1]);
                    int day = Integer.parseInt(strings[2]);
                    map.put(getSchemeCalendar(year, month, day, 0xFFCC0000, "跑过").toString(),
                            getSchemeCalendar(year, month, day, 0xFFCC0000, "跑过"));
                }
                //此方法在巨大的数据量上不影响遍历性能，推荐使用
                mCalendarView.setSchemeDate(map);
            }
        } catch (Exception e) {
            LogUtils.e("无法获取长跑数据", e);
        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
//        calendar.addScheme(0xFF008800, text);
        return calendar;
    }

    private void getSports(String dateTag) {
        try {
            List<SportMotionRecord> records = dataManager.queryRecordList(Integer.parseInt(SPUtils.getInstance().getString(MySp.USERID, "0")), dateTag);
            if (null != records) {

                sportList.clear();
                adapter.notifyDataSetChanged();

                for (SportMotionRecord record : records) {
                    PathRecord pathRecord = new PathRecord();
                    pathRecord.setId(record.getId());
                    pathRecord.setDistance(record.getDistance());
                    pathRecord.setDuration(record.getDuration());
                    pathRecord.setPathline(MotionUtils.parseLatLngLocations(record.getPathLine()));
                    pathRecord.setStartpoint(MotionUtils.parseLatLngLocation(record.getStratPoint()));
                    pathRecord.setEndpoint(MotionUtils.parseLatLngLocation(record.getEndPoint()));
                    pathRecord.setStartTime(record.getmStartTime());
                    pathRecord.setEndTime(record.getmEndTime());
                    pathRecord.setCalorie(record.getCalorie());
                    pathRecord.setSpeed(record.getSpeed());
                    pathRecord.setDistribution(record.getDistribution());
                    pathRecord.setDateTag(record.getDateTag());
                    sportList.add(pathRecord);
                }
                if (sportList.isEmpty())
                    sport_achievement.setVisibility(View.GONE);
                else
                    sport_achievement.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                sport_achievement.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            LogUtils.e("无法获取长跑数据", e);
            sport_achievement.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {
        mCalendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                mTextLunar.setVisibility(View.VISIBLE);
                mTextYear.setVisibility(View.VISIBLE);
                mTextMonthDay.setText(UIHelper.getString(R.string.date_month_day, calendar.getMonth(), calendar.getDay()));
                mTextYear.setText(String.valueOf(calendar.getYear()));
                mTextLunar.setText(calendar.getLunar());
                mYear = calendar.getYear();

                getSports(DateUtils.formatStringDateShort(calendar.getYear(), calendar.getMonth(), calendar.getDay()));

                LogUtils.d("onDateSelected", "  -- " + calendar.getYear() +
                        "  --  " + calendar.getMonth() +
                        "  -- " + calendar.getDay() +
                        "  --  " + isClick + "  --   " + calendar.getScheme());
            }
        });
        mCalendarView.setOnYearChangeListener(year -> {
            mTextMonthDay.setText(String.valueOf(year));
        });
        adapter.setOnItemClickListener((adapter, view, position) -> {
            SportRecordDetailsActivity.StartActivity(this, sportList.get(position));
        });
    }

    @OnClick({R.id.fl_current, R.id.tv_month_day, R.id.reRight, R.id.reBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_current:
                mCalendarView.scrollToCurrent();
                break;
            case R.id.tv_month_day:
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
                break;
            case R.id.reRight:
                startActivityForResult(new Intent(HomeActivity.this, SportsActivity.class), SPORT);
                break;
            case R.id.reBack:
                showTipDialog("用户注销",
                        "注销后本地数据会被删除，确定继续？",
                        () -> logOut());
                break;
            default:
                break;
        }
    }

    private void logOut() {
        SPUtils.getInstance().put(MySp.ISLOGIN, false);

        dataManager.deleteSportRecord();

        MyApplication.exitActivity();
        ToastUtils.showShort("你已安全退出");

        Intent it = new Intent(context, LoginActivity.class);
        context.startActivity(it);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) { // 表示按返回键 时的操作
                long backPressed = System.currentTimeMillis();
                if (backPressed - lastBackPressed > QUIT_INTERVAL) {
                    lastBackPressed = backPressed;
                    ToastUtils.showShort("再按一次退出");
                    return false;
                } else {
                    moveTaskToBack(false);
                    MyApplication.closeApp(this);
                    finish();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (null != dataManager)
            dataManager.closeRealm();
        super.onDestroy();
    }

    //recyclerView设置间距
    protected class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.right = mSpace;
            outRect.left = mSpace;
            outRect.bottom = mSpace;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSpace;
            } else {
                outRect.top = 0;
            }

        }

        SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case SPORT:
                upDateUI();
                break;
            default:
                break;
        }
    }

    private void showTipDialog(String title, String tips, TipCallBack tipCallBack) {
        tipDialog = new Dialog(context, R.style.matchDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.tip_dialog_layout, null);
        ((TextView) (view.findViewById(R.id.title))).setText(title);
        ((TextView) (view.findViewById(R.id.tips))).setText(tips);
        view.findViewById(R.id.cancelTV).setOnClickListener(
                v -> tipDialog.dismiss());
        view.findViewById(R.id.confirmTV).setOnClickListener(v -> {
            tipCallBack.confirm();
            tipDialog.dismiss();
        });
        tipDialog.setContentView(view);
        tipDialog.show();
    }

    private interface TipCallBack {
        void confirm();
    }
}
