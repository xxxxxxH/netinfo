package com.xxxxxxH.netinfo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.OnItemChildClickListener;
import com.xxxxxxH.netinfo.utils.OnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Copyright (C) 2021,2021/6/2, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class NetInfoAdapter extends RecyclerView.Adapter<NetInfoAdapter.ViewHolder> {

    private OnItemClickListener itemClickListener;
    private OnItemChildClickListener itemChildClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemChildClickListener(OnItemChildClickListener itemChildClickListener) {
        this.itemChildClickListener = itemChildClickListener;
    }

    List<String> data;

    public NetInfoAdapter(List<String> data) {
        this.data = data;
    }

    public void updateData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /**
     * 删除Item
     */
    public void deleteItem(int position) {
        if (data == null || data.isEmpty()) {
            return;
        }
        data.remove(position);
        notifyItemRemoved(position);
    }


    public List<String> getData() {
        return data;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_netinfo, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NetInfoAdapter.ViewHolder holder, int position) {
        holder.content.setText(data.get(position));
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    itemClickListener.onItemClick(holder.itemView, pos, Constant.FLAG_NET_INFO);
                }
            }
        });
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemChildClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    itemChildClickListener.onItemChildClick(holder.del, pos, "");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView content;
        private final ImageView del;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.item_netinfo_tv);
            del = itemView.findViewById(R.id.item_netinfo_del);
        }
    }
}
