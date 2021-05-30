package com.xxxxxxH.netinfo.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import com.xxxxxxH.netinfo.activity.MainActivity;
import com.xxxxxxH.netinfo.adapter.RoomInfoImgAdapter;
import com.xxxxxxH.netinfo.adapter.RoomNameAdapter;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.FormatUtils;
import com.xxxxxxH.netinfo.utils.GlideEngine;
import com.xxxxxxH.netinfo.utils.OnItemClickListener;
import com.xxxxxxH.netinfo.widget.CustomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomInfoFragment extends Fragment implements View.OnClickListener, OnItemClickListener {

    @BindView(R.id.room_loc_tv)
    TextView roomLoc;
    @BindView(R.id.room_name_et)
    EditText roomName;
    @BindView(R.id.room_img_loc)
    ImageView roomImgLoc;
    @BindView(R.id.net_img_add)
    ImageView netAdd;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.img_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.room_name_select)
    ImageView select;
    @BindView(R.id.net_name)
    EditText netName;
    @BindView(R.id.board_name)
    EditText boardName;
    @BindView(R.id.port_name)
    EditText portName;
    @BindView(R.id.fiber_name)
    EditText fiberName;
    @BindView(R.id.custom_img_add_r)
    ImageView customField;
    @BindView(R.id.ll_f_r)
    LinearLayout rootView;


    private LocationManager locationManager;
    private Dialog roomDialog = null;
    private Dialog customItemDlg = null;
    private Dialog selectImgDlg = null;
    private Dialog roomNameDlg = null;

    private double curLongitude = 0;
    private double curLatitude = 0;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;//声明一个请求码，用于识别返回的结果
    public static final int OPEN_ALBUM = 2;//声明一个请求码，用于识别返回的结果
    public RoomInfoImgAdapter adapter;
    public RoomNameAdapter nameAdapter;

    private EditText fieldName;
    private EditText fieldContent;


    public RoomInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_fragment_room, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getLocation();
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        adapter = new RoomInfoImgAdapter(getActivity(), null);
        adapter.setOnItemClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        roomImgLoc.setOnClickListener(this);
        netAdd.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        customField.setOnClickListener(this);
        select.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_new:
                if (roomDialog.isShowing()) {
                    roomDialog.dismiss();
                }
                break;
            case R.id.room_img_loc:
                curLongitude = 0.0;
                curLatitude = 0.0;
                break;
            case R.id.net_img_add:
                if (roomDialog == null) {
                    roomDialog = roomNameDialog();
                }
                if (!roomDialog.isShowing()) {
                    roomDialog.show();
                }
                break;
            case R.id.img_add:
                Constant.ADD = false;
                if (selectImgDlg == null) {
                    selectImgDlg = imgDialog();
                }
                if (!selectImgDlg.isShowing()) {
                    selectImgDlg.show();
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
            case R.id.custom_img_add_r:
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
                        CustomItem item = new CustomItem(getActivity());
                        item.setName(fieldName.getText().toString());
                        item.setContent(fieldContent.getText().toString());
                        Constant.customItem.put(fieldName.getText().toString(), fieldContent.getText().toString());
                        rootView.addView(item);
                        rootView.invalidate();
                        Constant.itemList.add(item);
                        if (customItemDlg != null && customItemDlg.isShowing()) {
                            customItemDlg.dismiss();
                        }
                    }
                }
                break;
            case R.id.room_name_select:
                Set<String> data = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
                if (data == null) {
                    Toast.makeText(getActivity(), "暂无保存数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                roomNameDlg = roomDialog(data);
                roomNameDlg.show();
                break;

        }
    }

    private Dialog roomNameDialog() {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_room, null);
        dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        view.findViewById(R.id.btn_new).setOnClickListener(this);
        view.findViewById(R.id.btn_edit).setOnClickListener(this);
        return dialog;
    }

    private Dialog imgDialog() {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_img, null);
        dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        view.findViewById(R.id.photo).setOnClickListener(this);
        view.findViewById(R.id.camera).setOnClickListener(this);
        return dialog;
    }

    private Dialog customDialog() {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_custom_item, null);
        dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        view.findViewById(R.id.dialog_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm).setOnClickListener(this);
        fieldName = view.findViewById(R.id.field_name);
        fieldContent = view.findViewById(R.id.field_content);
        return dialog;
    }

    private Dialog roomDialog(Set<String> data) {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_name, null);
        dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        RecyclerView recyclerView = view.findViewById(R.id.dialog_recycler);
        nameAdapter = new RoomNameAdapter(new ArrayList<>(data));
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(nameAdapter);
        nameAdapter.setOnItemClickListener(this);
        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 结果回调
                    List<LocalMedia> albumList = PictureSelector.obtainMultipleResult(data);
                    getImgPath(albumList);
                    adapter.updateData(Constant.imgList);
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    // 结果回调
                    List<LocalMedia> cameraList = PictureSelector.obtainMultipleResult(data);
                    getImgPath(cameraList);
                    adapter.updateData(Constant.imgList);
                    break;
                default:
                    break;
            }
        }
    }

    public void getLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "请打开gps定位", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyLocationListener());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new MyLocationListener());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        roomLoc.setText(FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Longitude)) +
                " , " + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Latitude)));
        adapter.updateData(Constant.imgList);
        if (Constant.customItem != null && Constant.customItem.size() > 0) {
            for (String key : Constant.customItem.keySet()) {
                CustomItem item = new CustomItem(getActivity());
                item.setName(key);
                item.setContent(Constant.customItem.get(key));
                rootView.addView(item);
                Constant.itemList.add(item);
            }
            rootView.invalidate();
        }
        if (Constant.ADD){
            removeCustomItem();
            clearRoomInfo();
        }

    }


    @Override
    public void onItemClick(View view, int position, String flag) {
        if (TextUtils.equals(flag, Constant.FLAG_IMG)) {
            adapter.deleteItem(position);
        } else {
            String key = nameAdapter.getData().get(position);
            DataEntity entity = MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
            if (entity == null || entity.getImgList().size() == 0) {
                Constant.imgList.clear();
            }
            if (entity != null) {
                setViewData(entity);
                ((MainActivity) getActivity()).setScramViewData(entity);
            }
            if (roomNameDlg != null && roomNameDlg.isShowing()) {
                roomNameDlg.dismiss();
            }
        }
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            Log.i("TAG", "GPS Enabled");
            Toast.makeText(getActivity(), "GPS 不可用 请打开GPS", Toast.LENGTH_LONG).show();
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
                roomLoc.setText(FormatUtils.formatDouble(curLongitude) + " , " + FormatUtils.formatDouble(curLatitude));
                MMKV.defaultMMKV().encode(Constant.Longitude, curLongitude);
                MMKV.defaultMMKV().encode(Constant.Latitude, curLatitude);
            }
            Log.i("TAG", "当前经度 = " + location.getLongitude());
            Log.i("TAG", "当前纬度 = " + location.getLatitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }


    private void openCameraV2() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .forResult(PictureConfig.REQUEST_CAMERA);
    }

    private void setOpenAlbumV2() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine())
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private void getImgPath(List<LocalMedia> list) {
        for (LocalMedia item : list) {
            Constant.imgList.add(item.getRealPath());
        }
    }

    public ArrayList<String> getImgList() {
        return adapter.getData();
    }

    public DataEntity getRoomInfo() {
        DataEntity entity = new DataEntity();
        entity.setRoomName(TextUtils.isEmpty(roomName.getText().toString()) ? "" : roomName.getText().toString());
        entity.setRoomLoc(TextUtils.isEmpty(roomLoc.getText().toString()) ? "" : roomLoc.getText().toString());
        entity.setNetName(TextUtils.isEmpty(netName.getText().toString()) ? "" : netName.getText().toString());
        entity.setBoardName(TextUtils.isEmpty(boardName.getText().toString()) ? "" : boardName.getText().toString());
        entity.setPortName(TextUtils.isEmpty(portName.getText().toString()) ? "" : portName.getText().toString());
        entity.setFiberName(TextUtils.isEmpty(fiberName.getText().toString()) ? "" : fiberName.getText().toString());
        entity.setImgList(adapter.getData());
        return entity;
    }

    public void clearRoomInfo() {
        if (roomName != null) {
            roomName.setText("");
        }
        if (netName != null) {
            netName.setText("");
        }
        if (boardName != null) {
            boardName.setText("");
        }
        if (portName != null) {
            portName.setText("");
        }
        if (fiberName != null) {
            fiberName.setText("");
        }
        if (adapter != null) {
            Constant.imgList = new ArrayList<>();
            adapter.updateData(Constant.imgList);
        }
    }

    public void setViewData(DataEntity entity) {
        if (roomName != null) {
            roomName.setText(entity.getRoomName());
            roomLoc.setText(entity.getRoomLoc());
            netName.setText(entity.getNetName());
            boardName.setText(entity.getBoardName());
            portName.setText(entity.getPortName());
            fiberName.setText(entity.getFiberName());
            adapter.updateData(entity.getImgList());
            if (entity.getCustomRoom() != null && entity.getCustomRoom().size() > 0) {
                removeCustomItem();
                HashMap<String, String> customField = entity.getCustomRoom();
                Constant.itemList.clear();
                Constant.customItem.clear();
                for (String key : customField.keySet()) {
                    CustomItem item = new CustomItem(getActivity());
                    item.setName(key);
                    item.setContent(customField.get(key));
                    Constant.customItem.put(key, customField.get(key));
                    rootView.addView(item);
                    Constant.itemList.add(item);
                }
                rootView.invalidate();
            }
        }

    }

    public void removeCustomItem() {
        if (Constant.customItem != null && Constant.customItem.size() > 0) {
            int count = Constant.customItem.size();
            for (int i = count; i > 0; i--) {
                rootView.removeViewAt(rootView.getChildCount() - 1);
            }
            rootView.invalidate();
            Constant.customItem.clear();
        }
    }

    public String getKey() {
        if (roomName == null) {
            return "";
        }
        return roomName.getText().toString();
    }
}
