package com.WTK.ChangPao.ui.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.WTK.ChangPao.R;
import com.WTK.ChangPao.commmon.bean.PathRecord;
import com.WTK.ChangPao.sport_motion.MotionUtils;

import java.text.DecimalFormat;
import java.util.List;


public class SportCalendarAdapter extends BaseQuickAdapter<PathRecord, BaseViewHolder> {

    private DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private DecimalFormat intFormat = new DecimalFormat("#");

    public SportCalendarAdapter(int layoutResId, @Nullable List<PathRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PathRecord item) {
        helper.setText(R.id.distance, decimalFormat.format(item.getDistance() ));
        helper.setText(R.id.duration, MotionUtils.formatseconds(item.getDuration()));
        helper.setText(R.id.calorie, intFormat.format(item.getCalorie()));
    }
}
