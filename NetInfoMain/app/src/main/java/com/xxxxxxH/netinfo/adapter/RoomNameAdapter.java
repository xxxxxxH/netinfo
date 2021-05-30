package com.xxxxxxH.netinfo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.OnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RoomNameAdapter extends RecyclerView.Adapter<RoomNameAdapter.ViewHolder> {

    private  List<String> data;
    private OnItemClickListener onItemClickListener;

    public RoomNameAdapter(List<String> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void updateData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_room, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RoomNameAdapter.ViewHolder holder, int position) {
        holder.textView.setText(data.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, holder.getLayoutPosition(), Constant.FLAG_NAME);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<String> getData() {
        return data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_room_name);
        }
    }

}

