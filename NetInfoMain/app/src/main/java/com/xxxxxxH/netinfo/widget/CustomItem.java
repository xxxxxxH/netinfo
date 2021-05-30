package com.xxxxxxH.netinfo.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xxxxxxH.netinfo.R;

public class CustomItem extends LinearLayout {
    private TextView name;
    private EditText content;

    public CustomItem(Context context) {
        super(context);
        initView(context);
    }

    public CustomItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private View initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custon_item, this, true);
        name = view.findViewById(R.id.custom_tv);
        content = view.findViewById(R.id.custom_et);
        return view;
    }

    public void setName(String str) {
        name.setText(TextUtils.isEmpty(str) ? "自定义字段" : str);
    }

    public void setContent(String str){
        content.setText(TextUtils.isEmpty(str) ? "" : str);
    }

    public String getName(){
        if (name == null){
            return "";
        }
        if (TextUtils.isEmpty(name.getText().toString())){
            return "";
        }
        return name.getText().toString();
    }

    public String getContent() {
        if (content == null){
            return "";
        }
        if (TextUtils.isEmpty(content.getText().toString())){
            return "";
        }
        return content.getText().toString();
    }
}
