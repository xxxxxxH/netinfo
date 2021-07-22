package com.xxxxxxH.netinfo.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.mmkv.MMKV;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.activity.MapActivity;
import com.xxxxxxH.netinfo.adapter.RoomInfoImgAdapter;
import com.xxxxxxH.netinfo.adapter.RoomNameAdapter;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.event.MessageEvent;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.FileUtils;
import com.xxxxxxH.netinfo.utils.FormatUtils;
import com.xxxxxxH.netinfo.utils.GlideEngine;
import com.xxxxxxH.netinfo.utils.OnItemClickListener;
import com.xxxxxxH.netinfo.utils.RouterUtils;
import com.xxxxxxH.netinfo.widget.CustomItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C) 2021,2021/6/29, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class NetTourFragment extends Fragment implements View.OnClickListener, OnItemClickListener {

    @BindView(R.id.tour_id_et)
    EditText tourId;
    @BindView(R.id.tour_id_select)
    ImageView select;
    @BindView(R.id.tour_code_et)
    EditText stake;
    @BindView(R.id.tour_point)
    EditText point;
    @BindView(R.id.tour_loc_tv)
    TextView tourLoc;
    @BindView(R.id.tour_img_loc)
    ImageView refresh;
    @BindView(R.id.tour_img_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.tour_img_add)
    ImageView imgAdd;
    @BindView(R.id.custom_img_add_tour)
    ImageView custom;
    @BindView(R.id.ll_tour)
    LinearLayout rootView;

    private double curLongitude = 0.0;
    private double curLatitude = 0.0;
    private LocationManager locationManager;
    private Dialog selectImgDlg = null;
    private Dialog nameDlg = null;
    private Dialog customItemDlg = null;
    public RoomInfoImgAdapter adapter;
    public RoomNameAdapter nameAdapter;
    private EditText fieldName;
    private EditText fieldContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_fragment_net_tour, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        getLocation();
        initView();
    }

    private void initView() {
        select.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        custom.setOnClickListener(this);
        refresh.setOnClickListener(this);
        tourLoc.setOnClickListener(this);
        adapter = new RoomInfoImgAdapter(getActivity(), null);
        adapter.setOnItemClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tour_id_select:
                Set<String> data = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_TOUR_ID);
                if (data == null) {
                    Toast.makeText(getActivity(), "暂无保存数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                nameDlg = roomDialog(data);
                nameDlg.show();
                break;
            case R.id.tour_img_loc:
                if (FileUtils.isFastClick()) {
                    curLatitude = 0.0;
                    curLongitude = 0.0;
                    Toast.makeText(Constant.Context, "刷新成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Constant.Context, "请勿重复点击", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tour_img_add:
                if (selectImgDlg == null) {
                    selectImgDlg = imgDialog();
                }
                if (!selectImgDlg.isShowing()) {
                    selectImgDlg.show();
                }
                break;
            case R.id.custom_img_add_tour:
                if (customItemDlg == null) {
                    customItemDlg = customDialog();
                }
                customItemDlg.show();
                break;
            case R.id.dialog_cancel:
                if (customItemDlg != null && customItemDlg.isShowing()) {
                    customItemDlg.dismiss();
                }
                break;
            case R.id.dialog_confirm:
                if (fieldName != null && fieldContent != null) {
                    if (TextUtils.isEmpty(fieldName.getText().toString()) || TextUtils.isEmpty(fieldContent.getText().toString())) {
                        Toast.makeText(getActivity(), "请完整信息", Toast.LENGTH_LONG).show();
                    } else {
                        if (isHasEqualField(fieldName.getText().toString())) {
                            Toast.makeText(Constant.Context, "已有相同字段存在", Toast.LENGTH_LONG).show();
                            return;
                        }
                        CustomItem item = new CustomItem(getActivity());
                        item.setName(fieldName.getText().toString());
                        item.setContent(fieldContent.getText().toString());
                        Constant.customItem3.put(fieldName.getText().toString(),
                                fieldContent.getText().toString());
                        rootView.addView(item);
                        rootView.invalidate();
                        Constant.itemList3.add(item);
                        if (customItemDlg != null && customItemDlg.isShowing()) {
                            customItemDlg.dismiss();
                        }
                    }
                }
                break;
            case R.id.camera:
                openCameraV2();
                if (selectImgDlg != null && selectImgDlg.isShowing()) {
                    selectImgDlg.dismiss();
                }
                break;
            case R.id.photo:
                if (selectImgDlg != null && selectImgDlg.isShowing()) {
                    selectImgDlg.dismiss();
                }
                setOpenAlbumV2();
                break;
            case R.id.tour_loc_tv:
                RouterUtils.getInstance().router(getActivity(), MapActivity.class, Constant.ROUTER_KEY, Constant.TYPE_TOUR);
                break;
        }
    }

    public boolean isHasEqualField(String field) {
        boolean result = false;
        if (rootView.getChildCount() > 5) {
            for (int i = 6; i <= rootView.getChildCount() - 1; i++) {
                CustomItem item = (CustomItem) rootView.getChildAt(i);
                if (TextUtils.equals(item.getName(), field)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private Dialog imgDialog() {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_img, null);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        view.findViewById(R.id.photo).setOnClickListener(this);
        view.findViewById(R.id.camera).setOnClickListener(this);
        return dialog;
    }

    private Dialog roomDialog(Set<String> data) {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_name, null);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        RecyclerView recyclerView = view.findViewById(R.id.dialog_recycler);
        nameAdapter = new RoomNameAdapter(new ArrayList<>(data));
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(nameAdapter);
        nameAdapter.setOnItemClickListener(this);
        return dialog;
    }

    private Dialog customDialog() {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_custom_item
                , null);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
        fieldName = view.findViewById(R.id.field_name);
        fieldContent = view.findViewById(R.id.field_content);
        return dialog;
    }

    private ArrayList<String> handle(List<LocalMedia> list) {
        ArrayList<String> result = new ArrayList<>();
        if (adapter != null && adapter.getData() != null && adapter.getData().size() > 0) {
            result = adapter.getData();
        }
        for (LocalMedia item : list) {
            result.add(item.getRealPath());
        }
        return result;
    }

    private void openCameraV2() {
        PictureSelector.create(this).openCamera(PictureMimeType.ofImage()).imageEngine(GlideEngine.createGlideEngine()).forResult(PictureConfig.REQUEST_CAMERA);
    }

    private void setOpenAlbumV2() {
        PictureSelector.create(this).openGallery(PictureMimeType.ofImage()).imageEngine(GlideEngine.createGlideEngine()).forResult(PictureConfig.CHOOSE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            adapter.updateData(handle(PictureSelector.obtainMultipleResult(data)));
        }
    }

    @Override
    public void onItemClick(View view, int position, String flag) {
        if (TextUtils.equals(flag, Constant.FLAG_IMG)) {
            adapter.deleteItem(position);
        }else {
            String key = nameAdapter.getData().get(position);
            DataEntity entity = MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
            if (entity != null) {
                setViewData(entity);
            }
            if (nameDlg != null && nameDlg.isShowing()){
                nameDlg.dismiss();
            }
        }
    }

    private void setCustomItem(HashMap<String, String> hashMap) {
        removeCustomItem();
        Constant.itemList3.clear();
        Constant.customItem3.clear();
        for (String key : hashMap.keySet()) {
            CustomItem item = new CustomItem(getActivity());
            item.setName(key);
            item.setContent(hashMap.get(key));
            Constant.customItem3.put(key, hashMap.get(key));
            rootView.addView(item);
            Constant.itemList3.add(item);
        }
        rootView.invalidate();
    }

    public void removeCustomItem() {
        if (Constant.customItem3 != null && Constant.customItem3.size() > 0) {
            int count = Constant.customItem3.size();
            for (int i = count; i > 0; i--) {
                rootView.removeViewAt(rootView.getChildCount() - 1);
            }
            rootView.invalidate();
            Constant.customItem3.clear();
        }
    }

    public DataEntity getNetTourInfo() {
        DataEntity entity = new DataEntity();
        entity.setTourId(TextUtils.isEmpty(tourId.getText().toString()) ? "" : tourId.getText().toString());
        entity.setTourStake(TextUtils.isEmpty(stake.getText().toString()) ? "" : stake.getText().toString());
        entity.setTourPoint(TextUtils.isEmpty(point.getText().toString()) ? "" : point.getText().toString());
        entity.setTourLoc(TextUtils.isEmpty(tourLoc.getText().toString()) ? "" : tourLoc.getText().toString());
        entity.setTourImgList(adapter.getData());
        entity.setTourCustom(Constant.customItem3);
        return entity;
    }

    public void setViewData(DataEntity entity){
        if (tourId != null){
            removeCustomItem();
            tourId.setText(entity.getTourId());
            stake.setText(entity.getTourStake());
            point.setText(entity.getTourPoint());
            tourLoc.setText(entity.getTourLoc());
            if (entity.getTourImgList() != null && entity.getTourImgList().size() > 0){
                adapter.updateData(entity.getTourImgList());
            }
            if (entity.getTourCustom() != null && entity.getTourCustom().size() > 0){
                setCustomItem(entity.getTourCustom());
            }
        }
    }

    public HashMap<String, String> getCustomItemData() {
        HashMap<String, String> data = new HashMap<>();
        if (rootView.getChildCount() > 5) {
            for (int i = 5; i <= rootView.getChildCount() - 1; i++) {
                CustomItem item = (CustomItem) rootView.getChildAt(i);
                data.put(item.getName(), item.getContent());
            }
        }
        return data;
    }

    public void clear(){
        if (tourId != null){
            tourId.setText("");
        }
        if (stake != null){
            stake.setText("");
        }
        if (point != null){
            point.setText("");
        }
        if (recyclerView != null && adapter != null){
            adapter.updateData(new ArrayList<>());
        }
    }

    public String getKey(){
        return tourId.getText().toString();
    }

    public void getLocation() {
        locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "请打开gps定位", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                new MyLocationListener());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                new MyLocationListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        String[] message = event.getMessage();
        String lat = message[0];
        String longt = message[1];
        String type = message[2];
        if (TextUtils.equals(type, Constant.TYPE_TOUR)) {
            if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(longt)) {
                tourLoc.setText(longt + " , " + lat);
            }
        }
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.i("TAG", "GPS Enabled");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Log.i("TAG", "GPS Disabled");
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (curLongitude == 0.0 && curLatitude == 0.0) {
                curLongitude = location.getLongitude();
                curLatitude = location.getLatitude();
                MMKV.defaultMMKV().encode(Constant.Longitude, curLongitude);
                MMKV.defaultMMKV().encode(Constant.Latitude, curLatitude);
                tourLoc.setText(FormatUtils.formatDouble(curLongitude) + " , " + FormatUtils.formatDouble(curLatitude));
            }
            Log.i("TAG", "当前经度 = " + location.getLongitude());
            Log.i("TAG", "当前纬度 = " + location.getLatitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}
