package com.xxxxxxH.netinfo.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.xxxxxxH.netinfo.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScramblingFragment extends Fragment implements OnClickListener {

    @BindView(R.id.start_time)
    public EditText start;

    public ScramblingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.layout_fragment_scrambling, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        start.setOnClickListener(this);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        start.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar startDate = Calendar.getInstance();
                    Calendar endDate = Calendar.getInstance();
                    startDate.set(2021,0,1);
                    endDate.set(2099,11,31);
                    TimePickerView pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Toast.makeText(getActivity(), dateFormat.format(date) + "", Toast.LENGTH_SHORT).show();
                        }
                    }).setType(new boolean[]{true, true, true, true, true, false})
                            .setLabel("年", "月", "日", "时", "分", "秒")
                            .setDate(Calendar.getInstance())
                            .isDialog(true)
                            .setRangDate(startDate,endDate)
                            .build();
                    pvTime.show();
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
        }
    }
}
