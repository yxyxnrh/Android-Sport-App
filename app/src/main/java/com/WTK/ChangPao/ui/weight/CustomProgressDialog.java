package com.WTK.ChangPao.ui.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.WTK.ChangPao.R;


public class CustomProgressDialog extends Dialog {

    private Context mContext;

    public CustomProgressDialog(final Context context) {
        // TODO Auto-generated constructor stub
        super(context, R.style.customProgressDialog);
        this.mContext = context;

        // 设置点击进度条外部取消
        this.setCanceledOnTouchOutside(false);
        // 设置点击进度可以取消
        this.setCancelable(false);

        setTouchAble(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.custom_progress_dialog_layout, null);

        setContentView(rootView);
    }


    public void setTouchAble(boolean isTouchAble) {

        if (isTouchAble) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            //显示阴影
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.dimAmount = 0.3f;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);



        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.dimAmount = 0.3f;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

}
