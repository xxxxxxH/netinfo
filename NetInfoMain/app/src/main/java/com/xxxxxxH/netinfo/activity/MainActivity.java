package com.xxxxxxH.netinfo.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.dialog.LoadingDialog;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.fragment.RoomInfoFragment;
import com.xxxxxxH.netinfo.fragment.ScramblingFragment;
import com.xxxxxxH.netinfo.sendmain.EmailUtil;
import com.xxxxxxH.netinfo.sendmain.UsefulSTMP;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.ZipUtils;
import com.xxxxxxH.netinfo.widget.CustomItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.WindowManager.LayoutParams;

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
    private final static String[] MULTI_PERMISSIONS =
            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
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
    private LoadingDialog loadingDialog;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
            switch (msg.what) {
                case 1:
                    Toast.makeText(MainActivity.this, "发送邮件成功", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(MainActivity.this, "发送邮件失败，请检查邮箱配置是否正确", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
        Constant.itemList = new ArrayList<>();
        Constant.itemList2 = new ArrayList<>();
        Constant.customItem = new HashMap<>();
        Constant.customItem2 = new HashMap<>();
        Constant.Context = this;
    }

    public void requestPermissions() {
        if (checkPermission(permissions[0]) && checkPermission(permissions[1]) && checkPermission(permissions[2]) && checkPermission(permissions[3]) && checkPermission(permissions[4])) {

        } else {
            ActivityCompat.requestPermissions(this, permissions, 321);
        }
    }


    private boolean checkPermission(String per) {
        return ContextCompat.checkSelfPermission(this, per) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
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
        if (Constant.imgList.size() == 0) {
            Constant.imgList.addAll(roomFg.getImgList());
        }
        switch (id) {
            case R.id.tv_room:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, roomFg).commitAllowingStateLoss();
                room.setTextColor(Color.RED);
                scram.setTextColor(Color.BLACK);
                Constant.ADD = false;
                break;
            case R.id.tv_scram:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, scramFg).commitAllowingStateLoss();
                room.setTextColor(Color.BLACK);
                scram.setTextColor(Color.RED);
                Constant.ADD = false;
                break;
            case R.id.tv_submit:
                if (TextUtils.isEmpty(roomFg.getKey())) {
                    Toast.makeText(MainActivity.this, "信息不完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingDialog = new LoadingDialog(MainActivity.this);
                loadingDialog.show();
                Log.i("TAG", "开始压缩");
                boolean result = false;
                String zipPath = Environment.getExternalStorageDirectory() + File.separator +
                        "test.zip";
                try {
                    result = ZipUtils.zipFiles(Constant.imgList, zipPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("TAG", "压缩出错");
                }
                Log.i("TAG", "压缩结束 result = " + result);
                DataEntity entity = mainContent();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean result = EmailUtil.autoSendMail(roomFg.getKey(),
                                new Gson().toJson(entity), Constant.TO, UsefulSTMP.QQ,
                                Constant.FROM, Constant.pwd, new String[]{zipPath});
                        Message msg = new Message();
                        msg.what = result ? 1 : -1;
                        mHandler.sendMessage(msg);
                    }
                }).start();

                break;
            case R.id.tv_add:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, roomFg).commitAllowingStateLoss();
                room.setTextColor(Color.RED);
                scram.setTextColor(Color.BLACK);
                roomFg.removeCustomItem();
                roomFg.clearRoomInfo();
                scramFg.clearScramblingInfo();
                scramFg.removeCustomItem();
                saveDataInfo();
                Constant.ADD = true;
                break;
            case R.id.tv_del:
                Constant.ADD = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.content, roomFg).commitAllowingStateLoss();
                room.setTextColor(Color.RED);
                scram.setTextColor(Color.BLACK);
                Constant.customItem.clear();
                delCache(roomFg.getKey());
                roomFg.removeCustomItem();
                roomFg.clearRoomInfo();
                scramFg.removeCustomItem();
                scramFg.clearScramblingInfo();
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_save:
                if (TextUtils.isEmpty(roomFg.getKey())) {
                    Toast.makeText(this, "保存了一堆空气", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveDataInfo();
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
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
        DataEntity entity = mainContent();
        MMKV.defaultMMKV().encode(roomFg.getKey(), entity);
        saveKey(roomFg.getKey());
    }

    private DataEntity mainContent() {
        DataEntity entity = null;
        entity = MMKV.defaultMMKV().decodeParcelable(roomFg.getKey(), DataEntity.class);
        if (entity == null) {
            DataEntity roomInfo = roomFg.getRoomInfo();
            DataEntity scramblingInfo = scramFg.getScramblingInfo();
            entity = new DataEntity(roomInfo.getRoomName(), roomInfo.getRoomLoc(),
                    roomInfo.getNetName(), roomInfo.getBoardName(), roomInfo.getPortName(),
                    roomInfo.getFiberName(), scramblingInfo.getScramblingId(),
                    scramblingInfo.getChildName(), scramblingInfo.getStartTime(),
                    scramblingInfo.getEndTime(), scramblingInfo.getScramblingRate(),
                    scramblingInfo.getScramblingCode(), scramblingInfo.getScramblingLoc(),
                    Constant.customItem, Constant.customItem2, roomInfo.getImgList());
        }
        if (Constant.customItem != null && Constant.customItem.size() > 0) {
            entity.setCustomRoom(Constant.customItem);
        }
        if (Constant.customItem2 != null && Constant.customItem2.size() > 0) {
            entity.setCustomScrambling(Constant.customItem2);
        }
        return entity;
    }

    private HashMap<String, String> addCustomItemInfo() {
        HashMap<String, String> customField = new HashMap<>();
        if (Constant.itemList != null && Constant.itemList.size() > 0) {
            for (CustomItem item : Constant.itemList) {
                customField.put(item.getName(), item.getContent());
            }
        }
        return customField;
    }

    private void saveKey(String newValue) {
        if (TextUtils.isEmpty(newValue)) {
            Toast.makeText(this, "请输入机房名称", Toast.LENGTH_LONG).show();
            return;
        }
        Set<String> key = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
        if (key == null) {
            key = new HashSet<>();
        }
        key.add(newValue);
        MMKV.defaultMMKV().encode(Constant.KEY_ROOM_NAME, key);
        if (roomFg.nameAdapter != null) {
            roomFg.nameAdapter.updateData(new ArrayList<>(key));
        }

    }

    private void delCache(String key) {
        if (!TextUtils.isEmpty(roomFg.getKey())) {
            MMKV.defaultMMKV().remove(key);
            Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
            if (keySet != null) {
                keySet.remove(key);
            }
            MMKV.defaultMMKV().encode(Constant.KEY_ROOM_NAME, keySet);
            if (roomFg.nameAdapter != null) {
                roomFg.nameAdapter.updateData(new ArrayList<>(keySet));
            }
        }
    }

    public DataEntity getRoomInfo(String key) {
        return MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
    }

    public DataEntity getCurRoomInfo() {
        return getRoomInfo(roomFg.getKey());
    }

    public void setScramViewData(DataEntity entity) {
        scramFg.setViewData(entity);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MMKV.defaultMMKV().remove(Constant.Longitude);
        MMKV.defaultMMKV().remove(Constant.Latitude);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        MMKV.defaultMMKV().clearAll();
    }

    private void loadingView() {

    }
}