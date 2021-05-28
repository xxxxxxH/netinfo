package com.xxxxxxH.netinfo.activity;

import static android.view.WindowManager.LayoutParams;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.tencent.mmkv.MMKV;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.fragment.RoomInfoFragment;
import com.xxxxxxH.netinfo.fragment.ScramblingFragment;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.ZipUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_room)
    public TextView room;
    @BindView(R.id.tv_scram)
    public TextView scram;
    @BindView(R.id.tv_submit)
    public TextView submit;
    @BindView(R.id.tv_add)
    public TextView add;
    @BindView(R.id.tv_del)
    public TextView del;
    @BindView(R.id.tv_save)
    public TextView save;

    private RoomInfoFragment roomFg;
    private ScramblingFragment scramFg;
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private Location location;
    private LocationManager locationManager;
    public static final int LOCATION_CODE = 301;
    private final String locationProvider = null;
    private final double curLongitude = 0;
    private final double curLatitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        initMMKV();
        ButterKnife.bind(this);
        room.setOnClickListener(this);
        scram.setOnClickListener(this);
        add.setOnClickListener(this);
        del.setOnClickListener(this);
        save.setOnClickListener(this);
        submit.setOnClickListener(this);
        initFg();
        requestPermissions();
    }

    private void initMMKV() {
        MMKV.initialize(this);
    }

    private void initFg() {
        roomFg = new RoomInfoFragment();
        scramFg = new ScramblingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, roomFg).commitAllowingStateLoss();
        Constant.imgList = new ArrayList<>();
    }

    public void requestPermissions() {
        if (checkPermission(permissions[0]) && checkPermission(permissions[1]) && checkPermission(permissions[2])
                && checkPermission(permissions[3]) && checkPermission(permissions[4])) {

        } else {
            ActivityCompat.requestPermissions(this, permissions, 321);
        }
    }


    private boolean checkPermission(String per) {
        return ContextCompat.checkSelfPermission(this, per) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull @org.jetbrains.annotations.NotNull String[] permissions,
            @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                } else {
                    Log.i("xxxxxxH", "获取权限成功");
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_room:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, roomFg).commitAllowingStateLoss();
                room.setTextColor(Color.RED);
                scram.setTextColor(Color.BLACK);
                break;
            case R.id.tv_scram:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, scramFg).commitAllowingStateLoss();
                room.setTextColor(Color.BLACK);
                scram.setTextColor(Color.RED);
                break;
            case R.id.tv_submit:
                try {
                    ZipUtils.zipFiles(Constant.imgList,
                            Environment.getExternalStorageDirectory() + File.separator + "test.zip");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_add:

                break;
            case R.id.tv_del:

                break;
            case R.id.tv_save:
                saveDataInfo();
                break;
        }
    }

    private void addDataInfo() {
        roomFg.clearRoomInfo();
    }

    private void delDataInfo() {
        roomFg.clearRoomInfo();
    }

    private void saveDataInfo() {
        DataEntity roomInfo = roomFg.getRoomInfo();
        DataEntity scramblingInfo = scramFg.getScramblingInfo();
        DataEntity entity = new DataEntity(roomInfo.getRoomName(), roomInfo.getRoomLoc(), roomInfo.getNetName(),
                roomInfo.getBoardName(), roomInfo.getPortName(), roomInfo.getFiberName(),
                scramblingInfo.getScramblingId(), scramblingInfo.getChildName(), scramblingInfo.getStartTime(),
                scramblingInfo.getEndTime(), scramblingInfo.getScramblingRate(), scramblingInfo.getScramblingCode(),
                scramblingInfo.getScramblingLoc(), new HashMap<>(), new HashMap<>(), roomInfo.getImgList());
        MMKV.defaultMMKV().encode(roomInfo.getRoomName(), entity);
        saveKey(roomInfo.getRoomName());
    }

    private void saveKey(String newValue) {
        Set<String> key = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
        if (key == null) {
            key = new HashSet<>();
        }
        key.add(newValue);
        MMKV.defaultMMKV().encode(Constant.KEY_ROOM_NAME, key);
    }

    private DataEntity getRoomInfo(String key) {
        return MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MMKV.defaultMMKV().remove(Constant.Longitude);
        MMKV.defaultMMKV().remove(Constant.Latitude);
    }
}