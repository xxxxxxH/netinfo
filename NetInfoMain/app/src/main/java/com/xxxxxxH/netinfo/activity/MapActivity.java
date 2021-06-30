package com.xxxxxxH.netinfo.activity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.event.MessageEvent;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.FormatUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Copyright (C) 2021,2021/6/29, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class MapActivity extends AppCompatActivity implements AMap.OnMapLoadedListener, AMap.OnMarkerDragListener, LocationSource.OnLocationChangedListener {
    MapView mMapView = null;
    AMap aMap;
    MyLocationStyle myLocationStyle;
    MarkerOptions markerOptions;
    Marker marker;
    Button button;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map);
        EventBus.getDefault().register(this);
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        getParma();
        initMap();
        initView();
    }

    private void getParma() {
        Bundle bundle = MapActivity.this.getIntent().getExtras();
        type = bundle.getString(Constant.ROUTER_KEY);
    }

    private void initMap() {
        aMap = mMapView.getMap();
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(aMap.getCameraPosition().target.latitude, aMap.getCameraPosition().target.longitude), 16));
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMarkerDragListener(this);
    }

    private void initView() {
        button = findViewById(R.id.map_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = null;
                String lat = "";
                String longt = "";
                if (marker == null) {
                    latLng = new LatLng(aMap.getCameraPosition().target.latitude, aMap.getCameraPosition().target.longitude);
                    lat = latLng.latitude != 0.0 ? FormatUtils.formatDouble(latLng.latitude) : "";
                    longt = latLng.longitude != 0.0 ? FormatUtils.formatDouble(latLng.longitude) : "";
                } else {
                    latLng = marker.getPosition();
                    lat = latLng.latitude != 0.0 ? FormatUtils.formatDouble(latLng.latitude) : "";
                    longt = latLng.longitude != 0.0 ? FormatUtils.formatDouble(latLng.longitude) : "";
                }
                EventBus.getDefault().post(new MessageEvent(lat, longt, type));
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void addMarker(double lat, double longt) {
        if (marker != null) {
            marker.remove();
        }
        markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(lat, longt))
                .title("当前纬度：" + FormatUtils.formatDouble(lat))
                .snippet("当前经度：" + FormatUtils.formatDouble(longt))
                .draggable(true);
        marker = aMap.addMarker(markerOptions);
        marker.showInfoWindow();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {

    }

    @Override
    public void onMapLoaded() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                addMarker(aMap.getCameraPosition().target.latitude, aMap.getCameraPosition().target.longitude);
            }
        }).start();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        addMarker(marker.getPosition().latitude, marker.getPosition().longitude);
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
