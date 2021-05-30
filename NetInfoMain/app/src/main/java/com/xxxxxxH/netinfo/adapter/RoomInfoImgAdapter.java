package com.xxxxxxH.netinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.xxxxxxH.netinfo.R;
import com.xxxxxxH.netinfo.utils.Constant;
import com.xxxxxxH.netinfo.utils.GlideEngine;
import com.xxxxxxH.netinfo.utils.OnItemClickListener;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * Copyright (C) 2021,2021/5/28, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
public class RoomInfoImgAdapter extends RecyclerView.Adapter<RoomInfoImgAdapter.ViewHolder> {

    ArrayList<String> data;
    Context context;
    private OnItemClickListener onItemClickListener;

    public RoomInfoImgAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
    }

    public void updateData(ArrayList<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    /**
     * 删除Item
     */
    public void deleteItem(int position) {
        if(data == null || data.isEmpty()) {
            return;
        }
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_img, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RoomInfoImgAdapter.ViewHolder holder, int position) {
        GlideEngine.createGlideEngine().loadImage(context, data.get(position), holder.imageView);
        holder.del.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, pos, Constant.FLAG_IMG);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public ArrayList<String> getData() {
        return data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView del;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_img);
            del = itemView.findViewById(R.id.item_del);
        }
    }
}
