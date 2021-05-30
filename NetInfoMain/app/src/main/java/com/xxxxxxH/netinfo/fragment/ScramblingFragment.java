package com.xxxxxxH.netinfo.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.tencent.mmkv.MMKV;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.activity.MainActivity;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.FormatUtils;
import com.xxxxxxH.netinfo.widget.CustomItem;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScramblingFragment extends Fragment implements OnClickListener {

    @BindView(R.id.start_time)
    public EditText start;
    @BindView(R.id.end_time)
    public EditText end;
    @BindView(R.id.scrambling_loc)
    public TextView loc;
    @BindView(R.id.start_time_refresh)
    public ImageView startRefresh;
    @BindView(R.id.end_time_refresh)
    public ImageView endRefresh;
    @BindView(R.id.scrambling_id)
    public EditText id;
    @BindView(R.id.child_name)
    public EditText childName;
    @BindView(R.id.scrambling_rate)
    public EditText rate;
    @BindView(R.id.scrambling_code)
    public EditText code;
    @BindView(R.id.custom_img_add_s)
    public ImageView customField;
    @BindView(R.id.ll_f_)
    public LinearLayout rootView;

    private double curLongitude = 0;
    private double curLatitude = 0;
    private LocationManager locationManager;
    private Dialog customItemDlg = null;
    private EditText fieldName;
    private EditText fieldContent;

    public ScramblingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_fragment_scrambling, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getLocation();
        start.setOnClickListener(this);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        startRefresh.setOnClickListener(this);
        endRefresh.setOnClickListener(this);
        customField.setOnClickListener(this);
        id.setText(FormatUtils.formatDate(new Date()) + " , " + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Longitude)) + "," + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Latitude)));
        start.setText(FormatUtils.formatDate(new Date()));
        end.setText(FormatUtils.formatDate(new Date()));

        start.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TimePickerView timePickerView = createTimePicker("start");
                    timePickerView.show();
                }
                return false;
            }
        });

        end.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    TimePickerView timePickerView = createTimePicker("end");
                    timePickerView.show();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.start_time:

                break;
            case R.id.start_time_refresh:
                start.setText(FormatUtils.formatDate(new Date()));
                break;
            case R.id.end_time_refresh:
                end.setText(FormatUtils.formatDate(new Date()));
                break;
            case R.id.custom_img_add_s:
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
                        Constant.customItem2.put(fieldName.getText().toString(),
                                fieldContent.getText().toString());
                        rootView.addView(item);
                        rootView.invalidate();
                        Constant.itemList2.add(item);
                        if (customItemDlg != null && customItemDlg.isShowing()) {
                            customItemDlg.dismiss();
                        }
                    }
                }
                break;
        }
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

    private TimePickerView createTimePicker(String flag) {
        TimePickerView pvTime = null;
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(2021, 0, 1);
        endDate.set(2099, 11, 31);
        pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (TextUtils.equals(flag, "start")) {
                    start.setText(FormatUtils.formatDate(date));
                } else {
                    end.setText(FormatUtils.formatDate(date));
                }
            }
        }).setType(new boolean[]{true, true, true, true, true, false}).setLabel("年", "月", "日",
                "时", "分", "秒").setDate(Calendar.getInstance()).isDialog(true).setRangDate(startDate, endDate).build();
        return pvTime;
    }

    @Override
    public void onStart() {
        super.onStart();
        loc.setText(FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Longitude)) + " , " + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Latitude)));
        DataEntity entity = ((MainActivity) getActivity()).getCurRoomInfo();
        if (entity != null) {
            setViewData(entity);
        }else {
            removeCustomItem();
            clearScramblingInfo();
        }

        setDefaultText(id,
                FormatUtils.formatDate(new Date()) + " , " + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Longitude)) + "," + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Latitude)));

        setDefaultText(start, FormatUtils.formatDate(new Date()));
        setDefaultText(end, FormatUtils.formatDate(new Date()));
    }

    private void setDefaultText(EditText view, String defaultStr) {
        if (view != null && TextUtils.equals(view.getText().toString(),"")){
            view.setText(defaultStr);
        }
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
                loc.setText(FormatUtils.formatDouble(curLongitude) + " , " + FormatUtils.formatDouble(curLatitude));
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

    public DataEntity getScramblingInfo() {
        DataEntity entity = new DataEntity();
        entity.setScramblingId(id == null || TextUtils.isEmpty(id.getText().toString()) ? "" :
                id.getText().toString());
        entity.setChildName(childName == null || TextUtils.isEmpty(childName.getText().toString()) ? "" : childName.getText().toString());
        entity.setStartTime(start == null || TextUtils.isEmpty(start.getText().toString()) ? "" :
                start.getText().toString());
        entity.setEndTime(end == null || TextUtils.isEmpty(end.getText().toString()) ? "" :
                end.getText().toString());
        entity.setScramblingRate(rate == null || TextUtils.isEmpty(rate.getText().toString()) ?
                "" : rate.getText().toString());
        entity.setScramblingCode(code == null || TextUtils.isEmpty(code.getText().toString()) ?
                "" : code.getText().toString());
        entity.setScramblingLoc(loc == null || TextUtils.isEmpty(loc.getText().toString()) ? "" :
                loc.getText().toString());
        return entity;
    }

    public void setViewData(DataEntity entity) {
        if (id != null) {
            id.setText(entity.getScramblingId());
            childName.setText(entity.getChildName());
            start.setText(entity.getStartTime());
            end.setText(entity.getEndTime());
            rate.setText(entity.getScramblingRate());
            code.setText(entity.getScramblingCode());
            loc.setText(entity.getScramblingLoc());
            if (entity.getCustomScrambling() != null && entity.getCustomScrambling().size() > 0) {
                removeCustomItem();
                HashMap<String, String> customField = entity.getCustomScrambling();
                Constant.itemList2.clear();
                Constant.customItem2.clear();
                for (String key : customField.keySet()) {
                    CustomItem item = new CustomItem(Constant.Context);
                    item.setName(key);
                    item.setContent(customField.get(key));
                    Constant.customItem2.put(key, customField.get(key));
                    rootView.addView(item);
                    Constant.itemList2.add(item);
                }
                rootView.invalidate();
            }
        }

    }

    public void clearScramblingInfo() {
        if (id != null) {
            id.setText("");
        }
        if (childName != null) {
            childName.setText("");
        }
        if (start != null) {
            start.setText("");
        }
        if (end != null) {
            end.setText("");
        }
        if (rate != null) {
            rate.setText("");
        }
        if (code != null) {
            code.setText("");
        }

    }

    public void removeCustomItem() {
        if (Constant.customItem2 != null && Constant.customItem2.size() > 0) {
            int count = Constant.customItem2.size();
            for (int i = count; i > 0; i--) {
                rootView.removeViewAt(rootView.getChildCount() - 1);
            }
            rootView.invalidate();
            Constant.customItem2.clear();
        }
    }
}
