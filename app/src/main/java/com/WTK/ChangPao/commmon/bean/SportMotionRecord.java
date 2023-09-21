package com.WTK.ChangPao.commmon.bean;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class SportMotionRecord extends RealmObject implements Serializable {


    @PrimaryKey
    private Long id;

    //登录者ID
//    @Required
    private int master;

    //运动距离
    @Required
    private Double distance;
    //运动时长
    @Required
    private Long duration;
    //运动轨迹
    @Required
    private String pathLine;
    //运动开始点
    @Required
    private String stratPoint;
    //运动结束点
    @Required
    private String endPoint;
    //运动开始时间
    @Required
    private Long mStartTime;
    //运动结束时间
    @Required
    private Long mEndTime;
    //消耗卡路里
    @Required
    private Double calorie;
    //平均时速(公里/小时)
    @Required
    private Double speed;
    //平均配速(分钟/千米)
    @Required
    private Double distribution;
    //日期标记
    @Required
    private String dateTag;

    private String str1;//预留字段
    private String str2;//预留字段
    private String str3;//预留字段

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMaster() {
        return master;
    }

    public void setMaster(int master) {
        this.master = master;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getPathLine() {
        return pathLine;
    }

    public void setPathLine(String pathLine) {
        this.pathLine = pathLine;
    }

    public String getStratPoint() {
        return stratPoint;
    }

    public void setStratPoint(String stratPoint) {
        this.stratPoint = stratPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Long getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(Long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public Long getmEndTime() {
        return mEndTime;
    }

    public void setmEndTime(Long mEndTime) {
        this.mEndTime = mEndTime;
    }

    public Double getCalorie() {
        return calorie;
    }

    public void setCalorie(Double calorie) {
        this.calorie = calorie;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getDistribution() {
        return distribution;
    }

    public void setDistribution(Double distribution) {
        this.distribution = distribution;
    }

    public String getDateTag() {
        return dateTag;
    }

    public void setDateTag(String dateTag) {
        this.dateTag = dateTag;
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public String getStr3() {
        return str3;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }
}
