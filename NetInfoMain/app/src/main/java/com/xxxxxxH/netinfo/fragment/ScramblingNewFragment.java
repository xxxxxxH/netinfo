package com.xxxxxxH.netinfo.fragment;

import android.Manifest;
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

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.tencent.mmkv.MMKV;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.adapter.RoomNameAdapter;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.FileUtils;
import com.xxxxxxH.netinfo.utils.FormatUtils;
import com.xxxxxxH.netinfo.utils.OnItemClickListener;
import com.xxxxxxH.netinfo.widget.CustomItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScramblingNewFragment extends Fragment implements View.OnClickListener,
        OnItemClickListener {

    @BindView(R.id.scrambling_id)
    EditText id;
    @BindView(R.id.scram_id_select)
    ImageView idSelect;
    @BindView(R.id.child_name)
    EditText childName;
    @BindView(R.id.start_time)
    EditText start;
    @BindView(R.id.end_time)
    EditText end;
    @BindView(R.id.scrambling_rate)
    EditText rate;
    @BindView(R.id.scrambling_code)
    EditText code;
    @BindView(R.id.scrambling_loc)
    TextView loc;
    @BindView(R.id.start_time_refresh)
    ImageView startRefresh;
    @BindView(R.id.end_time_refresh)
    ImageView endRefresh;
    @BindView(R.id.custom_img_add_s)
    ImageView customField;
    @BindView(R.id.ll_f_)
    LinearLayout rootView;
    @BindView(R.id.scrambling_img_add_loc)
    ImageView refreshLoc;

    private double curLongitude = 0.0;
    private double curLatitude = 0.0;
    private LocationManager locationManager;
    private Dialog customItemDlg = null;
    private Dialog idDlg = null;
    private EditText fieldName;
    private EditText fieldContent;
    public RoomNameAdapter adapter;
    private boolean isStart = true;

    public ScramblingNewFragment() {

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
        initView();
    }

    private void initView() {
        startRefresh.setOnClickListener(this);
        endRefresh.setOnClickListener(this);
        customField.setOnClickListener(this);
        refreshLoc.setOnClickListener(this);
        idSelect.setOnClickListener(this);
        id.setText(System.currentTimeMillis() + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Longitude)).replace(".", "") + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Latitude)).replace(".", ""));
        start.setText(FormatUtils.formatDate(new Date()));
        end.setText(FormatUtils.formatDate(new Date()));

        start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TimePickerView timePickerView = createTimePicker("start");
                    timePickerView.show();
                }
                return false;
            }
        });

        end.setOnTouchListener(new View.OnTouchListener() {
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
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.scram_id_select:
                Set<String> data = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_SCRAM_ID);
                if (data == null || data.size() == 0) {
                    Toast.makeText(getActivity(), "暂无保存数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                idDlg = roomDialog(data);
                idDlg.show();
                break;
            case R.id.scrambling_img_add_loc:
                if (FileUtils.isFastClick()) {
                    curLatitude = 0.0;
                    curLongitude = 0.0;
                    Toast.makeText(Constant.Context, "刷新成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Constant.Context, "请勿重复点击", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.start_time_refresh:
                if (isStart) {
                    start.setText(FormatUtils.formatDate(new Date()));
                    Toast.makeText(Constant.Context, "刷新成功", Toast.LENGTH_SHORT).show();
                    isStart = false;
                } else {
                    end.setText(FormatUtils.formatDate(new Date()));
                    Toast.makeText(Constant.Context, "刷新成功", Toast.LENGTH_SHORT).show();
                    isStart = true;
                }
                break;
            case R.id.end_time_refresh:
                end.setText(FormatUtils.formatDate(new Date()));
                Toast.makeText(Constant.Context, "刷新成功", Toast.LENGTH_SHORT).show();
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
                        if (isHasEqualField(fieldName.getText().toString())) {
                            Toast.makeText(Constant.Context, "已有相同字段存在", Toast.LENGTH_LONG).show();
                            return;
                        }
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

    @Override
    public void onItemClick(View view, int position, String flag) {
        String key = adapter.getData().get(position);
        DataEntity entity = MMKV.defaultMMKV().decodeParcelable(key, DataEntity.class);
        if (entity != null) {
            setViewData(entity);
        }
        if (idDlg != null && idDlg.isShowing()) {
            idDlg.dismiss();
        }
    }

    private Dialog roomDialog(Set<String> data) {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_name, null);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        RecyclerView recyclerView = view.findViewById(R.id.dialog_recycler);
        adapter = new RoomNameAdapter(new ArrayList<>(data));
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
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
            id.setText(System.currentTimeMillis() + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Longitude)).replace(".", "") + FormatUtils.formatDouble(MMKV.defaultMMKV().decodeDouble(Constant.Latitude)).replace(".", ""));
        }
        if (childName != null) {
            childName.setText("");
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

    public HashMap<String, String> getCustomItemData() {
        HashMap<String, String> data = new HashMap<>();
        if (rootView.getChildCount() > 6) {
            for (int i = 7; i <= rootView.getChildCount() - 1; i++) {
                CustomItem item = (CustomItem) rootView.getChildAt(i);
                data.put(item.getName(), item.getContent());
            }
        }
        return data;
    }

    public boolean isHasEqualField(String field) {
        boolean result = false;
        if (rootView.getChildCount() > 6) {
            for (int i = 7; i <= rootView.getChildCount() - 1; i++) {
                CustomItem item = (CustomItem) rootView.getChildAt(i);
                if (TextUtils.equals(item.getName(), field)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
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
                new ScramblingNewFragment.MyLocationListener());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                new ScramblingNewFragment.MyLocationListener());
    }

    public String getKey() {
        if (id == null) {
            return "";
        }
        return id.getText().toString();
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
                loc.setText(FormatUtils.formatDouble(curLongitude) + " , " + FormatUtils.formatDouble(curLatitude));
            }
            Log.i("TAG", "当前经度 = " + location.getLongitude());
            Log.i("TAG", "当前纬度 = " + location.getLatitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}
