package com.xxxxxxH.netinfo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.dialog.LoadingDialog;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.fragment.NetElementFragment;
import com.xxxxxxH.netinfo.fragment.ScramblingNewFragment;
import com.xxxxxxH.netinfo.sendmain.EmailUtil;
import com.xxxxxxH.netinfo.sendmain.UsefulSTMP;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.ZipUtils;

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

    //    private RoomInfoFragment roomFg;
    //    private ScramblingFragment scramFg;
    private ScramblingNewFragment scramNewFg;
    private NetElementFragment netFg;
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
    private Dialog notice = null;

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
            switch (msg.what) {
                case 1:
                    showPosition(0);
                    room.setTextColor(Color.RED);
                    scram.setTextColor(Color.BLACK);
                    delCache(netFg.getKey());
                    netFg.removeCustomItem();
                    netFg.clearInfo();
                    Constant.customItem.clear();
                    if (scramNewFg != null) {
                        delCache2(scramNewFg.getKey());
                        scramNewFg.removeCustomItem();
                        scramNewFg.clearScramblingInfo();
                        Constant.customItem2.clear();
                    }
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
        showPosition(0);
        Constant.imgList = new ArrayList<>();
        Constant.itemList = new ArrayList<>();
        Constant.itemList2 = new ArrayList<>();
        Constant.customItem = new HashMap<>();
        Constant.customItem2 = new HashMap<>();
        Constant.Context = this;
    }

    private void showPosition(int position) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        hidAll(ft);
        if (position == 0) {
            netFg = (NetElementFragment) fm.findFragmentByTag("netFg");
            if (netFg == null) {
                netFg = new NetElementFragment();
                ft.add(R.id.content, netFg, "netFg");
            } else {
                ft.show(netFg);
            }
        } else if (position == 1) {
            scramNewFg = (ScramblingNewFragment) fm.findFragmentByTag("scramNewFg");
            if (scramNewFg == null) {
                scramNewFg = new ScramblingNewFragment();
                ft.add(R.id.content, scramNewFg, "scramNewFg");
            } else {
                ft.show(scramNewFg);
            }
        }
        ft.commit();
    }

    private void hidAll(FragmentTransaction ft) {
        if (netFg != null) {
            ft.hide(netFg);
        }
        if (scramNewFg != null) {
            ft.hide(scramNewFg);
        }
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
        switch (id) {
            case R.id.tv_room:
                room.setTextColor(Color.RED);
                scram.setTextColor(Color.BLACK);
                showPosition(0);
                break;
            case R.id.tv_scram:
                room.setTextColor(Color.BLACK);
                scram.setTextColor(Color.RED);
                showPosition(1);
                break;
            case R.id.tv_submit:
                if (TextUtils.isEmpty(netFg.getKey())) {
                    Toast.makeText(MainActivity.this, "请输入网元名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingDialog = new LoadingDialog(MainActivity.this);
                loadingDialog.show();
                ArrayList<String> files = new ArrayList<>();
                if (netFg != null && netFg.adapter != null && netFg.adapter.getData() != null && netFg.adapter.getData().size() > 0) {
                    Log.i("TAG", "开始压缩");
                    boolean result = false;
                    String zipPath =
                            Environment.getExternalStorageDirectory() + File.separator + "test.zip";
                    try {
                        result = ZipUtils.zipFiles(netFg.adapter.getData(), zipPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("TAG", "压缩出错");
                    }
                    Log.i("TAG", "压缩结束 result = " + result);
                    if (result) {
                        files.add(zipPath);
                    }
                }

                DataEntity entity = submit();
//
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean result = EmailUtil.autoSendMail(netFg.getKey(),
                                new Gson().toJson(entity), Constant.TO, UsefulSTMP.QQ,
                                Constant.FROM, Constant.pwd, files.size() > 0 ?
                                        files.toArray(new String[]{}) : null);
                        Message msg = new Message();
                        msg.what = result ? 1 : -1;
                        mHandler.sendMessage(msg);
                    }
                }).start();

                break;
            case R.id.tv_add:
                isSaved();
                if (notice==null||!notice.isShowing()){
                    add();
                }
                break;
            case R.id.tv_del:
                delete();
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_save:
                save();
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void saveKey(String newValue) {
        if (TextUtils.isEmpty(newValue)) {
            Toast.makeText(this, "请输入网元名称", Toast.LENGTH_LONG).show();
            return;
        }
        Set<String> key = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
        if (key == null) {
            key = new HashSet<>();
        }
        key.add(newValue);
        MMKV.defaultMMKV().encode(Constant.KEY_ROOM_NAME, key);
        if (netFg.nameAdapter != null) {
            netFg.nameAdapter.updateData(new ArrayList<>(key));
        }
    }

    private void saveKey2(String value) {
        if (scramNewFg == null) {
            return;
        }
        Set<String> key = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_SCRAM_ID);
        if (key == null) {
            key = new HashSet<>();
        }
        key.add(value);
        MMKV.defaultMMKV().encode(Constant.KEY_SCRAM_ID, key);
        if (scramNewFg.adapter != null) {
            scramNewFg.adapter.updateData(new ArrayList<>(key));
        }
    }

    private void delCache(String key) {
        if (!TextUtils.isEmpty(netFg.getKey())) {
            MMKV.defaultMMKV().remove(key);
            Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
            if (keySet != null) {
                keySet.remove(key);
            }
            MMKV.defaultMMKV().encode(Constant.KEY_ROOM_NAME, keySet);
            if (netFg.nameAdapter != null) {
                netFg.nameAdapter.updateData(new ArrayList<>(keySet));
            }
        }
    }

    private void delCache2(String key) {
        if (!TextUtils.isEmpty(scramNewFg.getKey())) {
            MMKV.defaultMMKV().remove(key);
            Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_SCRAM_ID);
            if (keySet != null) {
                keySet.remove(key);
            }
            MMKV.defaultMMKV().encode(Constant.KEY_SCRAM_ID, keySet);
            if (scramNewFg.adapter != null) {
                scramNewFg.adapter.updateData(new ArrayList<>(keySet));
            }
        }
    }

    public DataEntity getRoomInfo(String key) {
        return MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
    }

    public DataEntity getCurRoomInfo() {
        return getRoomInfo(netFg.getKey());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MMKV.defaultMMKV().remove(Constant.Longitude);
        MMKV.defaultMMKV().remove(Constant.Latitude);
    }

    private Dialog createNotice() {
        Dialog dialog = null;
        dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("是否保存当前数据")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        save();
                        dialog.dismiss();
                        add();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return dialog;
    }

    private void isSaved() {
        if (netFg != null && netFg.isVisible()) {
            if (TextUtils.isEmpty(netFg.getKey())) {
                return;
            }
            if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(netFg.getKey()))) {
                notice = createNotice();
                notice.show();
            }
        }
        if (scramNewFg != null && scramNewFg.isVisible()) {
            if (TextUtils.isEmpty(scramNewFg.getKey())) {
                return;
            }
            if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(scramNewFg.getKey()))) {
                notice = createNotice();
                notice.show();
            }
        }
    }


    //新增
    private void add() {
        if (netFg != null && netFg.isVisible()) {
            netFg.removeCustomItem();
            netFg.clearInfo();
            Constant.customItem.clear();
        }
        if (scramNewFg != null && scramNewFg.isVisible()) {
            scramNewFg.removeCustomItem();
            scramNewFg.clearScramblingInfo();
            Constant.customItem2.clear();
        }
    }

    //删除
    private void delete() {
        if (netFg != null && netFg.isVisible()) {
            delCache(netFg.getKey());
            netFg.removeCustomItem();
            netFg.clearInfo();
            Constant.customItem.clear();
        } else if (scramNewFg != null && scramNewFg.isVisible()) {
            delCache2(scramNewFg.getKey());
            scramNewFg.removeCustomItem();
            scramNewFg.clearScramblingInfo();
            Constant.customItem2.clear();
        }
    }

    //保存
    private void save() {
        if (netFg != null && netFg.isVisible()) {
            DataEntity netInfo = netFg.getNetInfo();
            DataEntity entity = new DataEntity(netInfo.getRoomName(), netInfo.getRoomLoc(),
                    netInfo.getNetName(), netFg.map, "", "", "", "", "", "", "",
                    netFg.getCustomItemData(), new HashMap<>(), netInfo.getImgList());
            MMKV.defaultMMKV().encode(netFg.getKey(), entity);
            saveKey(netFg.getKey());
        } else if (scramNewFg != null && scramNewFg.isVisible()) {
            DataEntity info = scramNewFg.getScramblingInfo();
            DataEntity entity = new DataEntity("", "", "", new HashMap<>(),
                    info.getScramblingId(), info.getChildName(), info.getStartTime(),
                    info.getEndTime(), info.getScramblingRate(), info.getScramblingCode(),
                    info.getScramblingLoc(), new HashMap<>(), scramNewFg.getCustomItemData(),
                    new ArrayList<>());
            MMKV.defaultMMKV().encode(scramNewFg.getKey(), entity);
            saveKey2(scramNewFg.getKey());
        }
    }

    //提交
    private DataEntity submit() {
        DataEntity netInfo = null;
        DataEntity info = null;
        if (netFg != null) {
            netInfo = netFg.getNetInfo();
        }
        if (scramNewFg != null) {
            info = scramNewFg.getScramblingInfo();
        }

        DataEntity entity = new DataEntity(netInfo != null ? netInfo.getRoomName() : "",
                netInfo != null ? netInfo.getRoomLoc() : "", netInfo != null ? netInfo.getNetName() : "",
                netInfo != null && netInfo.getNetDetails() != null && netInfo.getNetDetails().size() > 0 ? netInfo.getNetDetails() : new HashMap<>(),
                info != null ? info.getScramblingId() : "",
                info != null ? info.getChildName() : "",
                info != null ? info.getStartTime() : "",
                info != null ? info.getEndTime() : "",
                info != null ? info.getScramblingRate() : "",
                info != null ? info.getScramblingCode() : "",
                info != null ? info.getScramblingLoc() : "",
                netFg != null ? netFg.getCustomItemData() : new HashMap<>(),
                scramNewFg != null ? scramNewFg.getCustomItemData() : new HashMap<>(),
                netFg != null ? netInfo.getImgList() : new ArrayList<>());

        return entity;
    }
}