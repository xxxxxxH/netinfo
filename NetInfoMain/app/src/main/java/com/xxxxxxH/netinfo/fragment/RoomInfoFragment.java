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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.mmkv.MMKV;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.adapter.RoomInfoImgAdapter;
import com.xxxxxxH.netinfo.adapter.RoomInfoImgAdapter.OnItemClickListener;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.FormatUtils;
import com.xxxxxxH.netinfo.utils.GlideEngine;
import java.util.ArrayList;
import java.util.List;

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


    private LocationManager locationManager;
    private Dialog roomDialog = null;

    private double curLongitude = 0;
    private double curLatitude = 0;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;//声明一个请求码，用于识别返回的结果
    public static final int OPEN_ALBUM = 2;//声明一个请求码，用于识别返回的结果
    public RoomInfoImgAdapter adapter;

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
                curLongitude = 0;
                curLongitude = 0;
                break;
            case R.id.net_img_add:
                if (roomDialog == null) {
                    roomDialog = roomNameDialog();
                }
                roomDialog.show();
                break;
            case R.id.img_add:
                Dialog dialog = imgDialog();
                dialog.show();
                break;
            case R.id.camera:
                openCameraV2();
                break;
            case R.id.photo:
                setOpenAlbumV2();
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
//        switch (requestCode) {
//            case TAKE_PHOTO:
//                if (resultCode == Activity.RESULT_OK) {
//                    try {
//                        Bitmap bitmap = BitmapFactory
//                                .decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
//                        img.setImageBitmap(bitmap);
//                        //将图片解析成Bitmap对象，并把它显现出来
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            case OPEN_ALBUM:
//                if (resultCode == Activity.RESULT_OK) {
//                    handImage(data);
//                }
//                break;
//            default:
//                break;
//        }
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
    }

    @Override
    public void onItemClick(View view, int position) {
        adapter.deleteItem(position);
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
        return entity;
    }

    public void clearRoomInfo() {
        roomName.setText("");
        netName.setText("");
        boardName.setText("");
        portName.setText("");
        fiberName.setText("");
        Constant.imgList = new ArrayList<>();
        adapter.updateData(Constant.imgList);
    }

    public void setViewData(DataEntity entity) {
        roomName.setText(entity.getRoomName());
        roomLoc.setText(entity.getRoomLoc());
        netName.setText(entity.getRoomLoc());
        boardName.setText(entity.getRoomLoc());
        portName.setText(entity.getRoomLoc());
        fiberName.setText(entity.getRoomLoc());
        adapter.updateData(entity.getImgList());
    }
}
