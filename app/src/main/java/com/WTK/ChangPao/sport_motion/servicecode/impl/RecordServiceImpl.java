package com.WTK.ChangPao.sport_motion.servicecode.impl;

import android.content.Context;

import com.amap.api.maps.model.LatLng;
import com.WTK.ChangPao.commmon.utils.LogUtils;
import com.WTK.ChangPao.sport_motion.servicecode.RecordService;


public class RecordServiceImpl implements RecordService {

    private Context mContext;

    public RecordServiceImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void recordSport(LatLng latLng, String location) {
        LogUtils.d("保存定位数据 = " + latLng.latitude + ":" + latLng.longitude + "   " + location);
    }

}
