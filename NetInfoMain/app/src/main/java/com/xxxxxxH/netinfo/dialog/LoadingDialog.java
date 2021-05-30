package com.xxxxxxH.netinfo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.xxxxxxH.netinfo.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDlg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_loading);
        setCanceledOnTouchOutside(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initView();
    }

    private void initView() {
        findViewById(R.id.loading);
    }
}
