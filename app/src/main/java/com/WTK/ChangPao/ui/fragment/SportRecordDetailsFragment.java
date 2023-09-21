package com.WTK.ChangPao.ui.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.WTK.ChangPao.R;
import com.WTK.ChangPao.commmon.bean.PathRecord;
import com.WTK.ChangPao.sport_motion.MotionUtils;
import com.WTK.ChangPao.ui.BaseFragment;
import com.WTK.ChangPao.ui.activity.SportRecordDetailsActivity;

import java.text.DecimalFormat;

import butterknife.BindView;

public class SportRecordDetailsFragment extends BaseFragment {

    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvDuration)
    TextView tvDuration;
    @BindView(R.id.tvSpeed)
    TextView tvSpeed;
    @BindView(R.id.tvDistribution)
    TextView tvDistribution;
    @BindView(R.id.tvCalorie)
    TextView tvCalorie;

    private PathRecord pathRecord = null;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("#");

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sportrecorddetails;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            pathRecord = bundle.getParcelable(SportRecordDetailsActivity.SPORT_DATA);
        }

        if (null != pathRecord) {
            tvDistance.setText(decimalFormat.format(pathRecord.getDistance() ));
            tvDuration.setText(MotionUtils.formatseconds(pathRecord.getDuration()));
            tvSpeed.setText(decimalFormat.format(pathRecord.getSpeed()));
            tvDistribution.setText(decimalFormat.format(pathRecord.getDistribution()));
            tvCalorie.setText(intFormat.format(pathRecord.getCalorie()));
        }

    }

    @Override
    public void initListener() {

    }
}
