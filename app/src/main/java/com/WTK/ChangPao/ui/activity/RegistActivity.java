package com.WTK.ChangPao.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.WTK.ChangPao.R;
import com.WTK.ChangPao.commmon.bean.UserAccount;
import com.WTK.ChangPao.commmon.utils.Conn;
import com.WTK.ChangPao.commmon.utils.LogUtils;
import com.WTK.ChangPao.commmon.utils.UIHelper;
import com.WTK.ChangPao.commmon.utils.Utils;
import com.WTK.ChangPao.db.DataManager;
import com.WTK.ChangPao.db.RealmHelper;
import com.WTK.ChangPao.ui.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class RegistActivity extends BaseActivity {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.et_psd)
    EditText etPsd;
    @BindView(R.id.et_checkPsd)
    EditText etCheckPsd;
    @BindView(R.id.bt_regist)
    Button btRegist;

    private String code = "-1";

    private DataManager dataManager = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_regist;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        dataManager = new DataManager(new RealmHelper());

        LogUtils.d("已注册的账号", new Gson().toJson(dataManager.queryAccountList()) + "");

        chronometer.setText("获取验证码");

    }

    public void yzmStart() {
        chronometer.setTag(SystemClock.elapsedRealtime() / 1000 + 60);
        chronometer.setText("(60)重新获取");
        chronometer.start();
        chronometer.setEnabled(false);

        this.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void initListener() {
        chronometer.setOnChronometerTickListener(chronometer -> {
            long time = (Long) chronometer.getTag() - SystemClock.elapsedRealtime() / 1000;
            if (time > 0) {
                chronometer.setText(UIHelper.getString(R.string.chronometer_time, time));
            } else {
                chronometer.setText("重新获取");
                chronometer.stop();
                chronometer.setEnabled(true);
            }
        });
    }

    @OnClick({R.id.container, R.id.rlBadk, R.id.chronometer, R.id.bt_regist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.container:
                // 设置点击影藏输入法
                hideSoftKeyBoard();
                break;
            case R.id.rlBadk:
                finish();
                break;
            case R.id.chronometer:
                String phone = etAccount.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showShort("手机号码必须为11位");
                    return;
                }
                if (!Utils.isMobile(phone)) {
                    ToastUtils.showShort("手机号码格式要正确");
                    return;
                }

                // 先影藏输入法
                hideSoftKeyBoard();

                yanZhengMa();
                break;
            case R.id.bt_regist:
                hideSoftKeyBoard();
                if (TextUtils.isEmpty(etAccount.getText())) {
                    ToastUtils.showShort("手机号码必须为11位");
                } else if (!Utils.isMobile(etAccount.getText().toString())) {
                    ToastUtils.showShort("手机号码格式要正确");
                } else if (TextUtils.isEmpty(etCode.getText().toString())) {
                    ToastUtils.showShort("验证码不能为空");
                } else if (!TextUtils.equals(etCode.getText(), code)) {
                    ToastUtils.showShort("验证码格式正确");
                } else if (TextUtils.isEmpty(etPsd.getText().toString())) {
                    ToastUtils.showShort("密码不能为空");
                } else if (etPsd.getText().length() < 6) {
                    ToastUtils.showShort("密码至少要6位");
                } else if (TextUtils.isEmpty(etCheckPsd.getText().toString())) {
                    ToastUtils.showShort("确认密码不能为空");
                } else if (!TextUtils.equals(etPsd.getText(), etCheckPsd.getText())) {
                    ToastUtils.showShort("两次密码必须一致");
                } else {
                    btRegist.setEnabled(false);
                    regist();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证码
     */
    public void yanZhengMa() {
        showLoadingView();
        new Handler().postDelayed(() -> {
            dismissLoadingView();
            int numcode = (int) ((Math.random() * 9 + 1) * 100000);
            code = numcode + "";
            yzmStart();
            ToastUtils.showShort("已获取验证");
            etCode.setText(code);
        }, Conn.Delayed);
    }

    /**
     * 注册
     */
    public void regist() {
        showLoadingView();
        new Handler().postDelayed(() -> {
            dismissLoadingView();
            btRegist.setEnabled(true);
            if (dataManager.checkAccount(etAccount.getText().toString())) {
                ToastUtils.showShort("账号已经被注册");
            } else {
                ToastUtils.showShort("你已注册成功");
                UserAccount userAccount = new UserAccount();
                userAccount.setAccount(etAccount.getText().toString());
                userAccount.setPsd(etPsd.getText().toString());
                dataManager.insertAccount(userAccount);
                finish();
            }
        }, Conn.Delayed);
    }

    @Override
    protected void onDestroy() {
        if (null != dataManager)
            dataManager.closeRealm();
        super.onDestroy();
    }
}
