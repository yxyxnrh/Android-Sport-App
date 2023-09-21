package com.WTK.ChangPao.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.WTK.ChangPao.R;
import com.WTK.ChangPao.commmon.bean.SportMotionRecord;
import com.WTK.ChangPao.commmon.utils.LogUtils;
import com.WTK.ChangPao.commmon.utils.MySp;
import com.WTK.ChangPao.db.DataManager;
import com.WTK.ChangPao.db.RealmHelper;
import com.WTK.ChangPao.ui.BaseActivity;
import com.WTK.ChangPao.ui.permission.PermissionHelper;
import com.WTK.ChangPao.ui.permission.PermissionListener;
import com.WTK.ChangPao.ui.permission.Permissions;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class SportsActivity extends BaseActivity {

    @BindView(R.id.tv_sport_mile)
    TextView tvSportMile;
    @BindView(R.id.tv_sport_count)
    TextView tvSportCount;
    @BindView(R.id.tv_sport_time)
    TextView tvSportTime;
    @BindView(R.id.btStart)
    Button btStart;

    private DecimalFormat decimalFormat = new DecimalFormat("0.0");

    private final int SPORT = 0x0012;

    private DataManager dataManager = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sports;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        dataManager = new DataManager(new RealmHelper());

        upDateUI();
    }

    @Override
    public void initListener() {

    }

    @OnClick(R.id.btStart)
    public void onViewClicked() {
        PermissionHelper.requestPermissions(this, Permissions.PERMISSIONS_LOCATION,
                 "长跑系统"+ "要获取位置权限", new PermissionListener() {
                    @Override
                    public void onPassed() {
                        startActivityForResult(new Intent(SportsActivity.this, SportMapActivity.class), SPORT);
                    }
                });
    }

    private void upDateUI() {
        try {
            List<SportMotionRecord> records = dataManager.queryRecordList(Integer.parseInt(SPUtils.getInstance().getString(MySp.USERID, "0")));
            if (null != records) {

                double sportMile = 0;
                long sportTime = 0;
                for (SportMotionRecord record : records) {
                    sportMile += record.getDistance();
                    sportTime += record.getDuration();
                }
                tvSportMile.setText(decimalFormat.format(sportMile / 1000d));
                tvSportCount.setText(String.valueOf(records.size()));
                tvSportTime.setText(decimalFormat.format((double) sportTime / 60d));
            }
        } catch (Exception e) {
            LogUtils.e("无法获取长跑数据", e);
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
                setResult(RESULT_OK);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (null != dataManager)
            dataManager.closeRealm();
        super.onDestroy();
    }

}
