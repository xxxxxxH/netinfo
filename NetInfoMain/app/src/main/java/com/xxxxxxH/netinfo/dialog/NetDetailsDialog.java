package com.xxxxxxH.netinfo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.utils.NetDetailsBtnClickListener;

public class NetDetailsDialog extends Dialog {

    private LinearLayout root;
    private Button cancel;
    private Button confirm;
    private NetDetailsBtnClickListener listener;

    public NetDetailsDialog(Context context) {
        super(context, R.style.NetDialog);

    }

    public void setListener(NetDetailsBtnClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_net_details);
        setCanceledOnTouchOutside(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        initView();
    }

    private void initView() {
        root = findViewById(R.id.ll_net_details);
        cancel = findViewById(R.id.dialog_cancel_net_details);
        confirm = findViewById(R.id.dialog_confirm_net_details);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCancel();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onConfirm();
            }
        });
    }

    public void addView(View view) {
        root.addView(view);
        root.invalidate();
    }

    public void invalidate() {
        root.invalidate();
    }

    public LinearLayout getRoot() {
        return root;
    }
}
