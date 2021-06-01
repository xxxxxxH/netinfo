package com.xxxxxxH.netinfo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xxxxxxH.netinfo.R;

public class CustomFiberItem extends LinearLayout {
    private TextView portName;
    private TextView fiberName;
    private EditText portContent;
    private EditText fiberRxContent;
    private EditText fiberTxContent;

    public CustomFiberItem(Context context) {
        super(context);
        initView(context);
    }

    public CustomFiberItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomFiberItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private View initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_fiber_item, this, true);
        portName = view.findViewById(R.id.net_port_name);
        portContent = view.findViewById(R.id.net_port_content);
        fiberName = view.findViewById(R.id.net_fiber_name);
        fiberRxContent = view.findViewById(R.id.net_fiber_rx);
        fiberTxContent = view.findViewById(R.id.net_fiber_tx);
        return view;
    }

    public String getPortName() {
        return portName.getText().toString();
    }

    public void setPortName(String s) {
        portName.setText(s);
    }

    public String getFiberName() {
        return fiberName.getText().toString();
    }

    public void setFiberName(String s) {
        fiberName.setText(s);
    }

    public String getPortContent() {
        return portContent.getText().toString();
    }

    public void setPortContent(String s) {
        portContent.setText(s);
    }

    public String getFiberRxContent() {
        return fiberRxContent.getText().toString();
    }

    public void setFiberRxContent(String s) {
        fiberRxContent.setText(s);
    }

    public String getFiberTxContent() {
        return fiberTxContent.getText().toString();
    }

    public void setFiberTxContent(String s) {
        fiberTxContent.setText(s);
    }
}
