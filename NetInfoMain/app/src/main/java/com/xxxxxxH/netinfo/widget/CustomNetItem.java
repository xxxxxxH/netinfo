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

public class CustomNetItem extends LinearLayout {
    private TextView boardName;
    private EditText boardContent;
    private LinearLayout root;

    public CustomNetItem(Context context) {
        super(context);
        initView(context);
    }

    public CustomNetItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomNetItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private View initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_custom_net_item, this,
                true);
        boardName = view.findViewById(R.id.net_board_name);
        boardContent = view.findViewById(R.id.net_board_content);
        root = view.findViewById(R.id.net_root);
        return view;
    }

    public String getBoardName() {
        return boardName.getText().toString();
    }

    public void setBoardName(String s) {
        boardName.setText(s);
    }

    public String getBoardContent() {
        return boardContent.getText().toString();
    }

    public void setBoardContent(String s) {
        boardContent.setText(s);
    }

    public LinearLayout getRoot() {
        return root;
    }

    public void setRoot(LinearLayout root) {
        this.root = root;
    }

    public void addFiberItem(View view) {
        root.addView(view);
        root.invalidate();
    }

}
