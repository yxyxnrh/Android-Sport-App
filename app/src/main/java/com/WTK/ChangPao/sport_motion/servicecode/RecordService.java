package com.WTK.ChangPao.sport_motion.servicecode;

import com.amap.api.maps.model.LatLng;


public interface RecordService {

    //记录运动坐标和大概描述信息
    void recordSport(LatLng latLng, String location);
}
