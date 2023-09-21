package com.WTK.ChangPao.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.WTK.ChangPao.R;
import com.WTK.ChangPao.commmon.bean.PathRecord;
import com.WTK.ChangPao.commmon.bean.SportMotionRecord;
import com.WTK.ChangPao.commmon.utils.LogUtils;
import com.WTK.ChangPao.commmon.utils.MySp;
import com.WTK.ChangPao.db.DataManager;
import com.WTK.ChangPao.db.RealmHelper;
import com.WTK.ChangPao.sport_motion.MotionUtils;
import com.WTK.ChangPao.sport_motion.PathSmoothTool;
import com.WTK.ChangPao.ui.BaseActivity;
import com.WTK.ChangPao.ui.weight.CustomPopWindow;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;


public class SportResultActivity extends BaseActivity {

    @BindView(R.id.ivStar1)
    ImageView ivStar1;
    @BindView(R.id.ivStar2)
    TextView ivStar2;
    @BindView(R.id.ivStar3)
    TextView ivStar3;
    @BindView(R.id.tvResult)
    TextView tvResult;
    @BindView(R.id.tvDistancet)
    TextView tvDistancet;
    @BindView(R.id.tvDuration)
    TextView tvDuration;
    @BindView(R.id.tvCalorie)
    TextView tvCalorie;
    @BindView(R.id.mapView)
    MapView mapView;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("#");

    private final int AMAP_LOADED = 0x0088;

    private Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AMAP_LOADED:
                    setupRecord();
                    break;
                default:
                    break;
            }
        }
    };

    private AMap aMap;

    private PathRecord pathRecord = null;

    private DataManager dataManager = null;

    private ExecutorService mThreadPool;
    private List<LatLng> mOriginLatLngList;
    private Marker mOriginStartMarker, mOriginEndMarker;
    private Polyline mOriginPolyline;
    private PathSmoothTool mpathSmoothTool = null;
    private PolylineOptions polylineOptions;

    public static String SPORT_START = "SPORT_START";
    public static String SPORT_END = "SPORT_END";

    public static void StartActivity(Activity activity, long mStartTime, long mEndTime) {
        Intent intent = new Intent();
        intent.putExtra(SPORT_START, mStartTime);
        intent.putExtra(SPORT_END, mEndTime);
        intent.setClass(activity, SportResultActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sportresult;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        mapView.onCreate(savedInstanceState);// 此方法必须重写

        dataManager = new DataManager(new RealmHelper());

        if (!getIntent().hasExtra(SPORT_START) || !getIntent().hasExtra(SPORT_END)) {
            ToastUtils.showShort("参数错误!");
            finish();
        }

        int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 3;
        mThreadPool = Executors.newFixedThreadPool(threadPoolSize);

        initPolyline();

        if (aMap == null)
            aMap = mapView.getMap();

        setUpMap();

    }

    private void initPolyline() {
        polylineOptions = new PolylineOptions();
        polylineOptions.color(getResources().getColor(R.color.colorAccent));
        polylineOptions.width(20f);
        polylineOptions.useGradient(true);

        mpathSmoothTool = new PathSmoothTool();
        mpathSmoothTool.setIntensity(4);
    }

    private void setupRecord() {
        try {
            SportMotionRecord records = dataManager.queryRecord(
                    Integer.parseInt(SPUtils.getInstance().getString(MySp.USERID, "0")),
                    getIntent().getLongExtra(SPORT_START, 0),
                    getIntent().getLongExtra(SPORT_END, 0));
            if (null != records) {
                pathRecord = new PathRecord();
                pathRecord.setId(records.getId());
                pathRecord.setDistance(records.getDistance());
                pathRecord.setDuration(records.getDuration());
                pathRecord.setPathline(MotionUtils.parseLatLngLocations(records.getPathLine()));
                pathRecord.setStartpoint(MotionUtils.parseLatLngLocation(records.getStratPoint()));
                pathRecord.setEndpoint(MotionUtils.parseLatLngLocation(records.getEndPoint()));
                pathRecord.setStartTime(records.getmStartTime());
                pathRecord.setEndTime(records.getmEndTime());
                pathRecord.setCalorie(records.getCalorie());
                pathRecord.setSpeed(records.getSpeed());
                pathRecord.setDistribution(records.getDistribution());
                pathRecord.setDateTag(records.getDateTag());

                upDataUI();
            } else {
                pathRecord = null;
                ToastUtils.showShort("无法获取长跑数据");
            }
        } catch (Exception e) {
            pathRecord = null;
            ToastUtils.showShort("无法获取长跑数据");
            LogUtils.e("无法获取长跑数据", e);
        }
    }

    private void upDataUI() {
        tvDistancet.setText(decimalFormat.format(pathRecord.getDistance() ));
        tvDuration.setText(MotionUtils.formatseconds(pathRecord.getDuration()));
        tvCalorie.setText(intFormat.format(pathRecord.getCalorie()));

        //评分规则：依次判断 距离大于0 ；运动时间大于30分钟 ；速度在2-5/h之间
        if (pathRecord.getDuration() > (60 * 60) && pathRecord.getSpeed() > 5) {
            ivStar1.setImageResource(R.mipmap.tou_ming);
            ivStar2.setText("9");
            ivStar3.setText("9");
            tvResult.setText("    过程分析：用户速度较慢，坚持的时间较短.\n" +
                    "    指导建议：步频和步距要增大,速度要超过1km/h,长跑持续时间要超过15分.\n");
        }
        //
        else if (pathRecord.getDuration() > (30 * 60) && pathRecord.getSpeed() > 3) {
            ivStar1.setImageResource(R.mipmap.tou_ming);
            ivStar2.setText("9");
            ivStar3.setText("0");
            tvResult.setText("    过程分析：用户速度较慢，坚持的时间较短.\n" +
                    "    指导建议：步频和步距要增大,速度争取超过5km/h,长跑持续时间要超过60分.\n");
        }

        else if (pathRecord.getDuration() > (15 * 60) && pathRecord.getSpeed() > 1) {
            ivStar1.setImageResource(R.mipmap.tou_ming);
            ivStar2.setText("8");
            ivStar3.setText("0");
            tvResult.setText("    过程分析：用户速度较慢，坚持的时间较短.\n" +
                    "    指导建议：步频和步距要增大,速度要超过3km/h,长跑持续时间要超过30分.\n");
        }
        else if (pathRecord.getDuration() > (0.1 * 60) && pathRecord.getSpeed() > 0.5) {
            ivStar1.setImageResource(R.mipmap.tou_ming);
            ivStar2.setText("7");
            ivStar3.setText("0");
            tvResult.setText("    过程分析：用户速度较慢，坚持的时间较短.\n" +
                    "    指导建议：步频和步距要增大,速度要超过1km/h,长跑持续时间要超过15分.\n"
                   );
        }
        //时间超过5分钟，则及格
        else if (pathRecord.getDuration() > (0.1 * 60)) {
            ivStar1.setImageResource(R.mipmap.tou_ming);
            ivStar2.setText("6");
            ivStar3.setText("0");
            tvResult.setText("    过程分析：用户速度较慢，坚持的时间较短.\n" +
                    "    指导建议：步频和步距要增大,速度要超过0.5km/h.\n");
        }
        //不及格
        else {
            ivStar1.setImageResource(R.mipmap.tou_ming);
            ivStar2.setText("5");
            ivStar3.setText("0");
            tvResult.setText("分析：不及格");
        }



        {
            List<LatLng> recordList = pathRecord.getPathline();
            LatLng startLatLng = pathRecord.getStartpoint();
            LatLng endLatLng = pathRecord.getEndpoint();
            if (recordList == null || startLatLng == null || endLatLng == null) {
                return;
            }
            mOriginLatLngList = mpathSmoothTool.pathOptimize(recordList);
            addOriginTrace(startLatLng, endLatLng, mOriginLatLngList);
        }
    }

    @Override
    public void initListener() {
        aMap.setOnMapLoadedListener(() -> {
            Message msg = handler.obtainMessage();
            msg.what = AMAP_LOADED;
            handler.sendMessage(msg);
        });
    }

    @OnClick({R.id.tvResult, R.id.ll_share, R.id.ll_details})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvResult:
                new CustomPopWindow.PopupWindowBuilder(this)
                        .setView(R.layout.layout_sport_result_tip)
                        .setFocusable(true)
                        .setOutsideTouchable(true)
                        .create()
                        .showAsDropDown(tvResult, -200, 10);
                break;
            case R.id.ll_share:
                if (null != pathRecord) {
                    systemShareTxt();
                } else {
                    ToastUtils.showShort("获取长跑数据失败!");
                }
                break;
            case R.id.ll_details:
                if (null != pathRecord) {
                    SportRecordDetailsActivity.StartActivity(this, pathRecord);
                } else {
                    ToastUtils.showShort("获取长跑数据失败!");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.drawable.mylocation_point));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setScaleControlsEnabled(true);// 设置比例尺显示
        aMap.getUiSettings().setZoomControlsEnabled(true);// 设置默认缩放按钮是否显示
        aMap.getUiSettings().setCompassEnabled(true);// 设置默认指南针是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    /**
     * 地图上添加原始轨迹线路及起终点、轨迹动画小人
     *
     * @param startPoint
     * @param endPoint
     * @param originList
     */
    private void addOriginTrace(LatLng startPoint, LatLng endPoint,
                                List<LatLng> originList) {
        polylineOptions.addAll(originList);
        mOriginPolyline = aMap.addPolyline(polylineOptions);
        mOriginStartMarker = aMap.addMarker(new MarkerOptions().position(
                startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.sport_start)));
        mOriginEndMarker = aMap.addMarker(new MarkerOptions().position(
                endPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.sport_end)));

        try {
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(getBounds(), 16));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LatLngBounds getBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (mOriginLatLngList == null) {
            return b.build();
        }
        for (LatLng latLng : mOriginLatLngList) {
            b.include(latLng);
        }
        return b.build();
    }

    /**
     * 调用系统分享文本
     */
    private void systemShareTxt() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "长跑" + "系统");
        intent.putExtra(Intent.EXTRA_TEXT, "我在" + "长跑系统" + "跑了" + decimalFormat.format(pathRecord.getDistance())
                + "米,跑了" + decimalFormat.format(pathRecord.getDuration() ) + "秒!快来加入一起长跑吧!");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享到"));
    }

    /**
     * 调用系统分享图片
     */
    private void systemSharePic(String imagePath) {

        Uri imageUri = Uri.fromFile(new File(imagePath));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();

        if (mThreadPool != null)
            mThreadPool.shutdownNow();

        if (null != dataManager)
            dataManager.closeRealm();

        super.onDestroy();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
