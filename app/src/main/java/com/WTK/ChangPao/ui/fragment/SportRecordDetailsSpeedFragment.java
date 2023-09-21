package com.WTK.ChangPao.ui.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.WTK.ChangPao.R;
import com.WTK.ChangPao.commmon.bean.PathRecord;
import com.WTK.ChangPao.ui.BaseFragment;
import com.WTK.ChangPao.ui.activity.SportRecordDetailsActivity;

import java.text.DecimalFormat;

import butterknife.BindView;

public class SportRecordDetailsSpeedFragment extends BaseFragment {

    @BindView(R.id.tvDistribution)
    TextView tvDistribution;

    private PathRecord pathRecord = null;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sportrecorddetailsspeed;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            pathRecord = bundle.getParcelable(SportRecordDetailsActivity.SPORT_DATA);
        }

        if (null != pathRecord)
            tvDistribution.setText(decimalFormat.format(pathRecord.getDistribution()));
    }

    @Override
    public void initListener() {

    }

}
