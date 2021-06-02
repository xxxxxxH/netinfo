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
import com.xxxxxxH.netinfo.adapter.NetInfoAdapter;
import com.xxxxxxH.netinfo.adapter.RoomInfoImgAdapter;
import com.xxxxxxH.netinfo.adapter.RoomNameAdapter;
import com.xxxxxxH.netinfo.dialog.NetDetailsDialog;
import com.xxxxxxH.netinfo.entity.BoardDetailsEntity;
import com.xxxxxxH.netinfo.entity.DataEntity;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.FormatUtils;
import com.xxxxxxH.netinfo.utils.GlideEngine;
import com.xxxxxxH.netinfo.utils.NetDetailsBtnClickListener;
import com.xxxxxxH.netinfo.utils.OnItemChildClickListener;
import com.xxxxxxH.netinfo.utils.OnItemClickListener;
import com.xxxxxxH.netinfo.widget.CustomFiberItem;
import com.xxxxxxH.netinfo.widget.CustomItem;
import com.xxxxxxH.netinfo.widget.CustomNetItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NetElementFragment extends Fragment implements View.OnClickListener,
        OnItemClickListener, NetDetailsBtnClickListener, OnItemChildClickListener {

    @BindView(R.id.net_name_et)
    EditText netName;
    @BindView(R.id.net_loc_tv)
    TextView netLoc;
    @BindView(R.id.room_name_et)
    EditText roomName;
    @BindView(R.id.net_img_add)
    ImageView netAdd;
    @BindView(R.id.net_recycler)
    RecyclerView netRecyclerView;
    @BindView(R.id.img_recycler)
    RecyclerView imgRecyclerView;
    @BindView(R.id.img_add)
    ImageView imgAdd;
    @BindView(R.id.custom_img_add_r)
    ImageView customAdd;
    @BindView(R.id.room_name_select)
    ImageView nameSelect;
    @BindView(R.id.ll_f_n)
    LinearLayout rootView;


    private LocationManager locationManager;
    private final Dialog roomDialog = null;
    private Dialog customItemDlg = null;
    private Dialog selectImgDlg = null;
    private Dialog nameDlg = null;
    private Dialog addNetDialog = null;
    private final Dialog netDetailsDialog = null;
    private NetDetailsDialog detailsDialog = null;

    private double curLongitude = 0;
    private double curLatitude = 0;
    public static final int TAKE_PHOTO = 1;//声明一个请求码，用于识别返回的结果
    public static final int OPEN_ALBUM = 2;//声明一个请求码，用于识别返回的结果
    public RoomInfoImgAdapter adapter;
    public RoomNameAdapter nameAdapter;
    public NetInfoAdapter netInfoAdapter;

    private EditText fieldName;
    private EditText fieldContent;
    private EditText boardNum;
    private EditText portNum;

    private HashMap<String, List<BoardDetailsEntity>> map;

    public NetElementFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_fragment_net, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getLocation();
        initView();
        iniAdapter();
    }

    private void initView() {
        map = new HashMap<>();
        adapter = new RoomInfoImgAdapter(getActivity(), null);
        adapter.setOnItemClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        imgRecyclerView.setLayoutManager(manager);
        imgRecyclerView.setAdapter(adapter);
        netAdd.setOnClickListener(this);
        nameSelect.setOnClickListener(this);
        imgAdd.setOnClickListener(this);
        customAdd.setOnClickListener(this);
    }

    private void iniAdapter() {
        netInfoAdapter = new NetInfoAdapter(new ArrayList<>());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        netRecyclerView.setLayoutManager(manager);
        netRecyclerView.setAdapter(netInfoAdapter);
        netInfoAdapter.setItemClickListener(this);
        netInfoAdapter.setItemChildClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            adapter.updateData(handle(PictureSelector.obtainMultipleResult(data)));
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.room_name_select:
                Set<String> data = MMKV.defaultMMKV().decodeStringSet(Constant.KEY_ROOM_NAME);
                if (data == null) {
                    Toast.makeText(getActivity(), "暂无保存数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                nameDlg = roomDialog(data);
                nameDlg.show();
                break;
            case R.id.img_add:
                if (selectImgDlg == null) {
                    selectImgDlg = imgDialog();
                }
                if (!selectImgDlg.isShowing()) {
                    selectImgDlg.show();
                }
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
                        if (isHasEqualField(fieldName.getText().toString())) {
                            Toast.makeText(Constant.Context, "已有相同字段存在", Toast.LENGTH_LONG).show();
                            return;
                        }
                        CustomItem item = new CustomItem(getActivity());
                        item.setName(fieldName.getText().toString());
                        item.setContent(fieldContent.getText().toString());
                        Constant.customItem.put(fieldName.getText().toString(),
                                fieldContent.getText().toString());
                        rootView.addView(item);
                        rootView.invalidate();
                        Constant.itemList.add(item);
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
            case R.id.net_img_add:
                addNetDialog = addNetDialog();
                addNetDialog.show();
                break;
            case R.id.dialog_cancel_add_net:
                if (addNetDialog != null && addNetDialog.isShowing()) {
                    addNetDialog.dismiss();
                }
                break;
            case R.id.dialog_confirm_add_net:
                addNetDialog.dismiss();
                detailsDialog = new NetDetailsDialog(getActivity());
                detailsDialog.setListener(this);
                detailsDialog.show();
                if (netInfoAdapter.getData() != null && netInfoAdapter.getData().size() > 0){

                }
                addNetDetails(true, Integer.parseInt(boardNum.getText().toString()),
                        Integer.parseInt(portNum.getText().toString()), map);
                break;
        }
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

    private Dialog imgDialog() {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_img, null);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        view.findViewById(R.id.photo).setOnClickListener(this);
        view.findViewById(R.id.camera).setOnClickListener(this);
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

    private Dialog addNetDialog() {
        Dialog dialog = null;
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_dialog_net, null);
        dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        view.findViewById(R.id.dialog_cancel_add_net).setOnClickListener(this);
        view.findViewById(R.id.dialog_confirm_add_net).setOnClickListener(this);
        boardNum = view.findViewById(R.id.board_num);
        portNum = view.findViewById(R.id.port_num);
        return dialog;
    }

    private void addNetDetails(boolean isNew, int boardNum, int portNum, HashMap<String, List<BoardDetailsEntity>> data) {
        if (isNew) {
            for (int i = 1; i < boardNum + 1; i++) {
                CustomNetItem netItem = new CustomNetItem(getActivity());
                netItem.setBoardName("单板名称" + (netInfoAdapter.getData().size() + i));
                for (int j = 1; j < portNum + 1; j++) {
                    CustomFiberItem fiberItem = new CustomFiberItem(getActivity());
                    fiberItem.setPortName("端口名称" + j);
                    fiberItem.setFiberName("光纤名称" + j);
                    netItem.addFiberItem(fiberItem);
                }
                detailsDialog.addView(netItem);
            }
            detailsDialog.invalidate();
        } else {
            if (data == null || data.size() == 0) {
                Toast.makeText(getActivity(), "map数据丢失", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 1; i < data.size() + 1; i++) {
                    CustomNetItem netItem = new CustomNetItem(getActivity());
                    String key = data.keySet().iterator().next();
                    netItem.setBoardName(key);
                    List<BoardDetailsEntity> list = data.get(key);
                    netItem.setBoardContent(list.get(0).getBoardContent());
                    for (int j = 0; j < list.size(); j++) {
                        CustomFiberItem fiberItem = new CustomFiberItem(getActivity());
                        fiberItem.setPortName("端口名称" + (j + 1));
                        fiberItem.setPortContent(list.get(j).getPortContent());
                        fiberItem.setFiberName("光纤名称" + (j + 1));
                        fiberItem.setFiberRxContent(list.get(j).getFiberRx());
                        fiberItem.setFiberTxContent(list.get(j).getFiberTx());
                        netItem.addFiberItem(fiberItem);
                    }
                    detailsDialog.addView(netItem);
                }
                detailsDialog.invalidate();
            }
        }

    }

    public HashMap<String, List<BoardDetailsEntity>> getNetDetails() {
        HashMap<String, List<BoardDetailsEntity>> netDetails = new HashMap<>();
        if (detailsDialog != null && detailsDialog.getRoot() != null) {
            LinearLayout detailsRoot = detailsDialog.getRoot();
            for (int i = 0; i < detailsRoot.getChildCount(); i++) {
                List<BoardDetailsEntity> list = new ArrayList<>();
                CustomNetItem netItem = (CustomNetItem) detailsRoot.getChildAt(i);
                LinearLayout fiberRoot = netItem.getRoot();
                for (int j = 0; fiberRoot != null && j < fiberRoot.getChildCount(); j++) {
                    CustomFiberItem fiberItem = (CustomFiberItem) fiberRoot.getChildAt(j);
                    BoardDetailsEntity entity = new BoardDetailsEntity(netItem.getBoardContent(), fiberItem.getPortName(), fiberItem.getPortContent(),
                            fiberItem.getFiberName(), fiberItem.getFiberRxContent(), fiberItem.getFiberTxContent());
                    list.add(entity);
                }
                netDetails.put(netItem.getBoardName(), list);
            }
        }
        return netDetails;
    }

    public List<CustomNetItem> getBoardContent() {
        List<CustomNetItem> list = new ArrayList<>();
        if (detailsDialog != null && detailsDialog.getRoot() != null) {
            LinearLayout detailsRoot = detailsDialog.getRoot();
            for (int i = 0; i < detailsRoot.getChildCount(); i++) {
                CustomNetItem netItem = (CustomNetItem) detailsRoot.getChildAt(i);
                list.add(netItem);
            }
        }
        return list;
    }


    @Override
    public void onItemClick(View view, int position, String flag) {
        if (TextUtils.equals(flag, Constant.FLAG_IMG)) {
            adapter.deleteItem(position);
        }
        if (TextUtils.equals(flag, Constant.FLAG_NET_INFO)) {
            detailsDialog = new NetDetailsDialog(getActivity());
            detailsDialog.show();
            detailsDialog.setListener(this);
            String key = netInfoAdapter.getData().get(position);
            HashMap<String, List<BoardDetailsEntity>> itemMap = new HashMap<>();
            for (String s : map.keySet()){
                List<BoardDetailsEntity> list = map.get(s);
                for (BoardDetailsEntity entity : list){
                    if (TextUtils.equals(key,entity.getBoardContent())){
                        itemMap.put(s, list);
                        break;
                    }
                }
            }

            addNetDetails(false, 0, 0, itemMap);
        }
    }


    @Override
    public void onItemChildClick(View view, int position, String flag) {
        String key = netInfoAdapter.getData().get(position);
        map.remove(key);
        netInfoAdapter.deleteItem(position);
    }

    @Override
    public void onCancel() {
        if (detailsDialog != null && detailsDialog.isShowing()) {
            detailsDialog.dismiss();
        }
    }

    @Override
    public void onConfirm() {

        List<CustomNetItem> customNetItems = getBoardContent();
        for (CustomNetItem item : customNetItems) {
            if (TextUtils.equals(item.getBoardContent(), "")) {
                Toast.makeText(getActivity(), "请填写单板名字", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (detailsDialog != null && detailsDialog.isShowing()) {
            detailsDialog.dismiss();
        }

        HashMap<String, List<BoardDetailsEntity>> hashMap = getNetDetails();

        for (String key : hashMap.keySet()) {
            map.put(key, hashMap.get(key));
        }

        List<String> data = new ArrayList<>();
//        if (netInfoAdapter != null && netInfoAdapter.getData() != null && netInfoAdapter.getData().size() > 0) {
//            data = netInfoAdapter.getData();
//        }
        if (map != null) {
            List<BoardDetailsEntity> list = new ArrayList<>();
            Set<String> set = new HashSet<>();
            for (String key : map.keySet()) {
                list = map.get(key);
                for (BoardDetailsEntity item : list) {
                    set.add(item.getBoardContent());
                }
            }
            netInfoAdapter.updateData(new ArrayList<>(set));
        }
    }

    private void openCameraV2() {
        PictureSelector.create(this).openCamera(PictureMimeType.ofImage()).imageEngine(GlideEngine.createGlideEngine()).forResult(PictureConfig.REQUEST_CAMERA);
    }

    private void setOpenAlbumV2() {
        PictureSelector.create(this).openGallery(PictureMimeType.ofImage()).imageEngine(GlideEngine.createGlideEngine()).forResult(PictureConfig.CHOOSE_REQUEST);
    }

    public DataEntity getNetInfo() {
        DataEntity entity = new DataEntity();

        return entity;
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
                new NetElementFragment.MyLocationListener());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                new NetElementFragment.MyLocationListener());
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
                netLoc.setText(FormatUtils.formatDouble(curLongitude) + " , " + FormatUtils.formatDouble(curLatitude));
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
}
