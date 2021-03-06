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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.xxxxxxH.netinfo.entity.NetInfoEntity;
import com.xxxxxxH.netinfo.entity.NetTourEntity;
import com.xxxxxxH.netinfo.entity.ScramEntity;
import com.xxxxxxH.netinfo.fragment.NetElementFragment;
import com.xxxxxxH.netinfo.fragment.NetTourFragment;
import com.xxxxxxH.netinfo.fragment.ScramblingNewFragment;
import com.xxxxxxH.netinfo.sendmain.EmailUtil;
import com.xxxxxxH.netinfo.sendmain.UsefulSTMP;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.FileUtils;
import com.xxxxxxH.netinfo.utils.FormatUtils;
import com.xxxxxxH.netinfo.utils.ZipUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.WindowManager.LayoutParams;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_room)
    public TextView room;
    @BindView(R.id.tv_scram)
    public TextView scram;
    @BindView(R.id.tv_tour)
    public TextView tour;
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
    private NetTourFragment tourFg;
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
    private Dialog submitDlg = null;
    private Dialog notice = null;
    private EditText address;
    List<String> data = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
            switch (msg.what) {
                case 1:
                    if (netFg != null && netFg.isVisible()) {
                        room.setTextColor(Color.RED);
                        scram.setTextColor(Color.BLACK);
                        delData();
                        netFg.removeCustomItem();
                        netFg.clearInfo();
                        Constant.customItem.clear();
                    }

                    if (scramNewFg != null && scramNewFg.isVisible()) {
                        delData();
                        scramNewFg.removeCustomItem();
                        scramNewFg.clearScramblingInfo();
                        Constant.customItem2.clear();
                    }

                    if (tourFg != null && tourFg.isVisible()) {
                        delData();
                        tourFg.removeCustomItem();
                        tourFg.clear();
                        Constant.customItem3.clear();
                    }
                    Toast.makeText(MainActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(MainActivity.this, "??????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
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
        tour.setOnClickListener(this);
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
        Constant.imgRoomList = new ArrayList<>();
        Constant.imgScramblingList = new ArrayList<>();
        Constant.itemList = new ArrayList<>();
        Constant.itemList2 = new ArrayList<>();
        Constant.itemList3 = new ArrayList<>();
        Constant.customItem = new HashMap<>();
        Constant.customItem2 = new HashMap<>();
        Constant.customItem3 = new HashMap<>();
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
        } else if (position == 2) {
            tourFg = (NetTourFragment) fm.findFragmentByTag("tourFg");
            if (tourFg == null) {
                tourFg = new NetTourFragment();
                ft.add(R.id.content, tourFg, "tourFg");
            } else {
                ft.show(tourFg);
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
        if (tourFg != null) {
            ft.hide(tourFg);
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
                    Log.i("xxxxxxH", "??????????????????");
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
                tour.setTextColor(Color.BLACK);
                showPosition(0);
                break;
            case R.id.tv_scram:
                room.setTextColor(Color.BLACK);
                scram.setTextColor(Color.RED);
                tour.setTextColor(Color.BLACK);
                showPosition(1);
                break;
            case R.id.tv_tour:
                room.setTextColor(Color.BLACK);
                scram.setTextColor(Color.BLACK);
                tour.setTextColor(Color.RED);
                showPosition(2);
                break;
            case R.id.tv_submit:
                if (submitDlg == null) {
                    submitDlg = submitDlg();
                }
                submitDlg.show();
                break;
            case R.id.tv_add:
                save();
                isSaved();
                if (notice == null || !notice.isShowing()) {
                    add();
                }
                break;
            case R.id.tv_del:
                delete();
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_save:
                save();

                break;
            case R.id.btn_submit_cancel:
                if (submitDlg != null && submitDlg.isShowing()) {
                    submitDlg.dismiss();
                }
                break;
            case R.id.btn_submit_confirm:
                if (address != null) {
                    if (isEmail(address.getText().toString())) {

                        MMKV.defaultMMKV().encode(Constant.KEY_EMAIL,
                                TextUtils.isEmpty(address.getText().toString()) ? "" :
                                        address.getText().toString());

                        if (netFg != null && netFg.isVisible()) {
                            if (TextUtils.isEmpty(netFg.getKey())) {
                                Toast.makeText(MainActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (scramNewFg != null && scramNewFg.isVisible()) {
                            if (TextUtils.isEmpty(scramNewFg.getKey())) {
                                Toast.makeText(MainActivity.this, "?????????id", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (tourFg != null && tourFg.isVisible()) {
                            if (TextUtils.isEmpty(tourFg.getKey())) {
                                Toast.makeText(MainActivity.this, "?????????id", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }


                        if (submitDlg != null && submitDlg.isShowing()) {
                            submitDlg.dismiss();
                        }
                        loadingDialog = new LoadingDialog(MainActivity.this);
                        loadingDialog.show();
                        ArrayList<String> files = new ArrayList<>();

                        //?????????????????????????????????
                        List<DataEntity> list = getCurAllData();

//                        Log.i("TAG", "????????????");
//                        boolean result = false;
//                        String zipPath =
//                                Environment.getExternalStorageDirectory() + File.separator +
//                                        "imgs.zip";
//                        try {
//                            List<String> data = new ArrayList<>();
//                            data = getAllImgs(list);
//                            if (data.size() != 0) {
//                                result = ZipUtils.zipFiles(data, zipPath);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.i("TAG", "????????????");
//                        }
//                        Log.i("TAG", "???????????? result = " + result);
//                        if (result) {
//                            files.add(zipPath);
//                        }


                        DataEntity entity = submit();

                        data.clear();

                        String filename = "";
                        Date dt=new Date();
                        SimpleDateFormat matter1=new SimpleDateFormat("yyyyMMdd");
                        String filedate =  matter1.format(dt);
                        filedate.replace("","/");

                        if (netFg != null && netFg.isVisible()) {
                            data = submitNetInfo(list);
                            filename = "inventory-"+list.get(0).getPoint()+"-"+filedate+".json";
                        }

                        if (scramNewFg != null && scramNewFg.isVisible()) {
                            data = submitScramInfo(list);
                            filename = "scramble-"+list.get(0).getChildName()+"-"+filedate+".json";
                        }

                        if (tourFg != null && tourFg.isVisible()) {
                            data = submitTourInfo(list);
                            filename = "nettour-"+list.get(0).getTourPoint()+"-"+filedate+".json";
                        }

                        if (data.size() == 0) {
                            return;
                        }

                        String filePath =
                                Environment.getExternalStorageDirectory() + File.separator +
                                        filename;
                        try {
                            FileUtils.writeTxt2File(data.toString(),
                                    Environment.getExternalStorageDirectory() + "", filename);
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        File file = new File(filePath);

                        if (file.exists()) {
                            files.add(filePath);
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean result = EmailUtil.autoSendMail(getThemeText(),
                                        data.toString(), address.getText().toString(),
                                        UsefulSTMP.QQ, Constant.FROM, Constant.pwd,
                                        files.size() > 0 ? files.toArray(new String[]{}) : null);

                                Message msg = new Message();
                                msg.what = result ? 1 : -1;
                                mHandler.sendMessage(msg);
                            }
                        }).start();
                    } else {
                        Toast.makeText(MainActivity.this, "???????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                } break;
        }
    }

    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//????????????
        Matcher m = p.matcher(email);
        return m.matches();
    }


    private void saveKey(String newValue) {
        if (TextUtils.isEmpty(newValue)) {
            Toast.makeText(this, "?????????????????????", Toast.LENGTH_LONG).show();
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
        if (scramNewFg.roomadapter != null) {
            scramNewFg.roomadapter.updateData(new ArrayList<>(key));
        }
    }

    private void saveKey3(String value) {
        if (tourFg == null) {
            return;
        }
        if (TextUtils.isEmpty(value)) {
            return;
        }
        Set<String> key = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_TOUR_ID);
        if (key == null) {
            key = new HashSet<>();
        }
        key.add(value);
        MMKV.defaultMMKV().encode(Constant.KEY_TOUR_ID, key);
        if (tourFg.nameAdapter != null) {
            tourFg.nameAdapter.updateData(new ArrayList<>(key));
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

    private void delData() {
        Set<String> keySet = new HashSet<>();
        if (netFg != null && netFg.isVisible()) {
            keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
            if (keySet != null) {
                for (String key : keySet) {
                    MMKV.defaultMMKV().removeValueForKey(key);
                }
                keySet.clear();
                MMKV.defaultMMKV().encode(Constant.KEY_ROOM_NAME, keySet);
            }

        }

        if (scramNewFg != null && scramNewFg.isVisible()) {
            keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_SCRAM_ID);
            if (keySet != null) {
                for (String key : keySet) {
                    MMKV.defaultMMKV().removeValueForKey(key);
                }
                keySet.clear();
                MMKV.defaultMMKV().encode(Constant.KEY_SCRAM_ID, keySet);
            }

        }

        if (tourFg != null && tourFg.isVisible()) {
            keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_TOUR_ID);
            if (keySet != null) {
                for (String key : keySet) {
                    MMKV.defaultMMKV().removeValueForKey(key);
                }
                keySet.clear();
                MMKV.defaultMMKV().encode(Constant.KEY_TOUR_ID, keySet);
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
            if (scramNewFg.roomadapter != null) {
                scramNewFg.roomadapter.updateData(new ArrayList<>(keySet));
            }
        }
    }

    private void delCache3(String key) {
        if (!TextUtils.isEmpty(tourFg.getKey())) {
            MMKV.defaultMMKV().remove(key);
            Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_TOUR_ID);
            if (keySet != null) {
                keySet.remove(key);
            }
            MMKV.defaultMMKV().encode(Constant.KEY_TOUR_ID, keySet);
            if (tourFg.nameAdapter != null) {
                tourFg.nameAdapter.updateData(new ArrayList<>(keySet));
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
        dialog =
                new AlertDialog.Builder(MainActivity.this).setTitle("??????").setMessage("????????????????????????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
                dialog.dismiss();
                add();
            }
        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        return dialog;
    }

    private void isSaved() {
        if (netFg != null && netFg.isVisible()) {
            if (TextUtils.isEmpty(netFg.getKey())) {
                return;
            }
            if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(Constant.KEY_ROOM_NAME)) && !MMKV.defaultMMKV().decodeString(Constant.KEY_ROOM_NAME).contains(netFg.getKey())) {
                notice = createNotice();
                notice.show();
            }
        }
        if (scramNewFg != null && scramNewFg.isVisible()) {
            if (TextUtils.isEmpty(scramNewFg.getKey())) {
                return;
            }
            if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(Constant.KEY_SCRAM_ID)) && !MMKV.defaultMMKV().decodeString(Constant.KEY_SCRAM_ID).contains(scramNewFg.getKey())) {
                notice = createNotice();
                notice.show();
            }
        }
    }


    //??????
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
        if (tourFg != null && tourFg.isVisible()) {
            tourFg.removeCustomItem();
            tourFg.clear();
            Constant.customItem3.clear();
        }
    }

    //??????
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
        } else if (tourFg != null && tourFg.isVisible()) {
            delCache3(tourFg.getKey());
            tourFg.removeCustomItem();
            tourFg.clear();
        }
    }

    //??????
    private void save() {
        if (netFg != null && netFg.isVisible()) {
            DataEntity netInfo = netFg.getNetInfo();
            if(TextUtils.isEmpty(netInfo.getRoomName()))
            {
                Toast.makeText(this, "?????????????????????", Toast.LENGTH_LONG).show();
                return;
            }
            DataEntity entity = new DataEntity(netInfo.getRoomName(), netInfo.getRoomLoc(),
                    netInfo.getNetName(), netInfo.getPoint(), netFg.map, "", "", "", "", "", "",
                    "", netFg.getCustomItemData(), new HashMap<>(), netInfo.getRoomImgList(),
                    new ArrayList<>(), "", "", "", "", new ArrayList<>(), new HashMap<>());
            MMKV.defaultMMKV().encode(netFg.getKey(), entity);
            saveKey(netFg.getKey());
        } else if (scramNewFg != null && scramNewFg.isVisible()) {
            DataEntity info = scramNewFg.getScramblingInfo();
            DataEntity entity = new DataEntity("", "", "", "", new HashMap<>(),
                    info.getScramblingId(), info.getChildName(), info.getStartTime(),
                    info.getEndTime(), info.getScramblingRate(), info.getScramblingCode(),
                    info.getScramblingLoc(), new HashMap<>(), scramNewFg.getCustomItemData(),
                    new ArrayList<>(), info.getScramblingImgList(), "", "", "", "",
                    new ArrayList<>(), new HashMap<>());
            MMKV.defaultMMKV().encode(scramNewFg.getKey(), entity);
            saveKey2(scramNewFg.getKey());
        } else if (tourFg != null && tourFg.isVisible()) {
            DataEntity tourInfo = tourFg.getNetTourInfo();
            DataEntity entity = new DataEntity("", "", "", "", new HashMap<>(), "", "", "", "",
                    "", "", "", tourFg.getCustomItemData(), new HashMap<>(), new ArrayList<>(),
                    new ArrayList<>(), tourInfo.getTourId(), tourInfo.getTourStake(),
                    tourInfo.getTourPoint(), tourInfo.getTourLoc(), tourInfo.getTourImgList(),
                    tourInfo.getTourCustom());
            MMKV.defaultMMKV().encode(tourFg.getKey(), entity);
            saveKey3(tourFg.getKey());
        }
        Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
    }

    //??????
    private DataEntity submit() {
        DataEntity netInfo = null;
        DataEntity info = null;
        DataEntity tourInfo = null;
        if (netFg != null && netFg.isVisible()) {
            netInfo = netFg.getNetInfo();
        }
        if (scramNewFg != null && scramNewFg.isVisible()) {
            info = scramNewFg.getScramblingInfo();
        }
        if (tourFg != null && tourFg.isVisible()) {
            tourInfo = tourFg.getNetTourInfo();
        }

        DataEntity entity = new DataEntity(netInfo != null ? netInfo.getRoomName() : "",
                netInfo != null ? netInfo.getRoomLoc() : "", netInfo != null ?
                netInfo.getNetName() : "", netInfo != null ? netInfo.getPoint() : "",
                netInfo != null && netInfo.getNetDetails() != null && netInfo.getNetDetails().size() > 0 ? netInfo.getNetDetails() : new HashMap<>(), info != null ? info.getScramblingId() : "", info != null ? info.getChildName() : "", info != null ? info.getStartTime() : "", info != null ? info.getEndTime() : "", info != null ? info.getScramblingRate() : "", info != null ? info.getScramblingCode() : "", info != null ? info.getScramblingLoc() : "", netFg != null && netFg.getCustomItemData() != null ? netFg.getCustomItemData() : new HashMap<>(), scramNewFg != null && scramNewFg.getCustomItemData() != null ? scramNewFg.getCustomItemData() : new HashMap<>(), netFg != null && netInfo != null ? netInfo.getRoomImgList() : new ArrayList<>(), scramNewFg != null && info != null ? info.getScramblingImgList() : new ArrayList<>(), tourFg != null && tourInfo != null ? tourInfo.getTourId() : "", tourFg != null && tourInfo != null ? tourInfo.getTourStake() : "", tourFg != null && tourInfo != null ? tourInfo.getTourPoint() : "", tourFg != null && tourInfo != null ? tourInfo.getTourLoc() : "", tourFg != null && tourInfo != null ? tourInfo.getTourImgList() : new ArrayList<>(), tourFg != null && tourInfo != null ? tourInfo.getTourCustom() : new HashMap<>());

        return entity;
    }

    private Dialog submitDlg() {
        Dialog dialog = null;
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_dialog_submit,
                null);
        dialog = new AlertDialog.Builder(MainActivity.this).setView(view).create();
        address = view.findViewById(R.id.mail_address);
        address.setText(TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(Constant.KEY_EMAIL)) ?
                "" : MMKV.defaultMMKV().decodeString(Constant.KEY_EMAIL));
        view.findViewById(R.id.btn_submit_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_submit_confirm).setOnClickListener(this);
        return dialog;
    }

    private DataEntity getCurData(String key) {
        DataEntity entity = null;
        entity = MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
        return entity;
    }

    private List<DataEntity> getCurAllData() {
        List<DataEntity> list = new ArrayList<>();
        if (netFg != null && netFg.isVisible()) {
            if (getCurData(netFg.getKey()) == null) {
                list.add(netFg.getNetInfo());
            }
            Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
            if (keySet != null) {
                for (String key : keySet) {
                    DataEntity entity = MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
                    list.add(entity);
                }
            }
        }
        if (scramNewFg != null && scramNewFg.isVisible()) {
            if (getCurData(scramNewFg.getKey()) == null) {
                list.add(scramNewFg.getScramblingInfo());
            }
            Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_SCRAM_ID);
            if (keySet != null) {
                for (String key : keySet) {
                    DataEntity entity = MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
                    list.add(entity);
                }
            }
        }

        if (tourFg != null && tourFg.isVisible()) {
            if (getCurData(tourFg.getKey()) == null) {
                list.add(tourFg.getNetTourInfo());
            }
            Set<String> keySet = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_TOUR_ID);
            if (keySet != null) {
                for (String key : keySet) {
                    DataEntity entity = MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
                    list.add(entity);
                }
            }
        }
        return list;
    }

    private List<String> submitNetInfo(List<DataEntity> list) {
        List<String> result = new ArrayList<>();
        List<NetInfoEntity> entityList = new ArrayList<>();
        for (DataEntity item : list) {
            if (item == null) {
                continue;
            }
            NetInfoEntity entity = new NetInfoEntity();
            entity.setRoomName(item.getRoomName());
            entity.setRoomLoc(item.getRoomLoc());
            entity.setNetName(item.getNetName());
            entity.setPoint(item.getPoint());
            entity.setNetDetails(item.getNetDetails());
            entity.setImgRoomList(null==item.getRoomImgList()||item.getRoomImgList().size()==0?new ArrayList<>():item.getRoomImgList());
            entity.setCustomRoom(item.getCustomRoom());
            entityList.add(entity);
        }

        if (entityList.size() > 0) {
            for (NetInfoEntity item : entityList) {
                result.add(new Gson().toJson(item));
            }
        }

        return result;
    }

    private List<String> submitScramInfo(List<DataEntity> list) {
        List<String> result = new ArrayList<>();
        List<ScramEntity> entityList = new ArrayList<>();

        for (DataEntity item : list) {
            if (item == null) {
                continue;
            }
            ScramEntity entity = new ScramEntity();
            entity.setScramblingId(item.getScramblingId());
            entity.setChildName(item.getChildName());
            entity.setStartTime(item.getStartTime());
            entity.setEndTime(item.getEndTime());
            entity.setScramblingRate(item.getScramblingRate());
            entity.setScramblingCode(item.getScramblingCode());
            entity.setScramblingLoc(item.getScramblingLoc());
            entity.setImgScramblingList(null == item.getScramblingImgList()||item.getScramblingImgList().size()==0?new ArrayList<>():item.getScramblingImgList());
            entity.setCustomScrambling(item.getCustomScrambling());
            entityList.add(entity);
        }

        if (entityList.size() > 0) {
            for (ScramEntity item : entityList) {
                result.add(new Gson().toJson(item));
            }
        }

        return result;
    }

    private List<String> submitTourInfo(List<DataEntity> list) {
        List<String> result = new ArrayList<>();
        List<NetTourEntity> entityList = new ArrayList<>();
        for (DataEntity item : list) {
            if (item == null) {
                continue;
            }
            NetTourEntity entity = new NetTourEntity();
            entity.setId(item.getTourId());
            entity.setStake(item.getTourStake());
            entity.setPoint(item.getTourPoint());
            entity.setLoc(item.getTourLoc());
            entity.setImgList(null==item.getTourImgList()||item.getTourImgList().size()==0?new ArrayList<>():item.getTourImgList());
            entity.setCustom(item.getTourCustom());
            entityList.add(entity);
        }
        if (entityList.size() > 0) {
            for (NetTourEntity item : entityList) {
                result.add(new Gson().toJson(item));
            }
        }
        return result;
    }

    private List<String> getAllImgs(List<DataEntity> list) {
        List<String> result = new ArrayList<>();
        if (netFg != null && netFg.isVisible()) {
            for (DataEntity item : list) {
                result.addAll(item.getRoomImgList());
            }
        }
        if (scramNewFg != null && scramNewFg.isVisible()) {
            for (DataEntity item : list) {
                result.addAll(item.getScramblingImgList());
            }
        }
        if (tourFg != null && tourFg.isVisible()) {
            for (DataEntity item : list) {
                result.addAll(item.getTourImgList());
            }
        }

        return result;
    }

    public String getThemeText() {
        String theme = "";

        if (netFg != null && netFg.isVisible()) {
            theme = "????????????" + FormatUtils.formatDate(new Date());
        }

        if (scramNewFg != null && scramNewFg.isVisible()) {
            theme = "????????????" + FormatUtils.formatDate(new Date());
        }

        if (tourFg != null && tourFg.isVisible()) {
            theme = "????????????" + FormatUtils.formatDate(new Date());
        }
        return theme;
    }
}