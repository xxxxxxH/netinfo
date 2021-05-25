package com.xxxxxxH.netinfo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.fragment.RoomInfoFragment;
import com.xxxxxxH.netinfo.fragment.ScramblingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.WindowManager.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_room)
    public TextView room;
    @BindView(R.id.tv_scram)
    public TextView scram;

    private RoomInfoFragment roomFg;
    private ScramblingFragment scramFg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        room.setOnClickListener(this);
        scram.setOnClickListener(this);
        initFg();
    }

    private void initFg() {
        roomFg = new RoomInfoFragment();
        scramFg = new ScramblingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content,roomFg).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.tv_room:
                getSupportFragmentManager().beginTransaction().replace(R.id.content,roomFg).commitAllowingStateLoss();
                room.setTextColor(Color.RED);
                scram.setTextColor(Color.BLACK);
                break;
            case R.id.tv_scram:
                getSupportFragmentManager().beginTransaction().replace(R.id.content,scramFg).commitAllowingStateLoss();
                room.setTextColor(Color.BLACK);
                scram.setTextColor(Color.RED);
                break;
        }
    }
}