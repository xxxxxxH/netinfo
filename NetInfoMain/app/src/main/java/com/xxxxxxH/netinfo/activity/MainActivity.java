package com.xxxxxxH.netinfo.activity;

import static android.view.WindowManager.LayoutParams;
import static com.tencent.mmkv.MMKV.initialize;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.tencent.mmkv.MMKV;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.entity.RoomEntity;
import com.xxxxxxH.netinfo.fragment.RoomInfoFragment;
import com.xxxxxxH.netinfo.fragment.ScramblingFragment;
import com.xxxxxxH.netinfo.utils.ZipUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_room)
    public TextView room;
    @BindView(R.id.tv_scram)
    public TextView scram;
    @BindView(R.id.tv_submit)
    public TextView submit;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        initMMKV();
        ButterKnife.bind(this);
        room.setOnClickListener(this);
        scram.setOnClickListener(this);
        submit.setOnClickListener(this);
        initFg();
        requestPermissions();
    }

    private void initMMKV() {
        initialize(this);
        MMKV.defaultMMKV();
        RoomEntity entity = new RoomEntity("a");
        MMKV.defaultMMKV().encode("e", entity);
        RoomEntity e = MMKV.defaultMMKV().decodeParcelable("e", RoomEntity.class);
    }

    private void initFg() {
        roomFg = new RoomInfoFragment();
        scramFg = new ScramblingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, roomFg).commitAllowingStateLoss();
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
                    //如果没有获取权限，那么可以提示用户去设置界面--->应用权限开启权限
                } else {
                    //获取权限成功提示，可以不要
                    Toast toast = Toast.makeText(this, "获取权限成功", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
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
                String filePath = Environment.getExternalStorageDirectory() + File.separator + "test";
                List<String> listFileInfo = getFilesAllName(filePath);
                try {
                    ZipUtils.zipFiles(listFileInfo, filePath + File.separator + "test.zip");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 获取文件夹下的所有文件名字
     *
     * @param path 文件夹路径
     * @return
     */
    public static ArrayList<String> getFilesAllName(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            list.add(path + File.separator + files[i].getName());
        }
        return list;
    }
}