package com.example.androidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.androidapp.model.DichVuKho;

import java.util.ArrayList;

public class ServiceShopAdapter extends BaseAdapter {

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
        void onItemClickAdmin(DichVuKho item, int position);
    }

    private final Context context;
    private final ArrayList<DichVuKho> arrayList;
    private final boolean isAdminMode;
    private final OnQuantityChangeListener listener;

    public ServiceShopAdapter(Context context, ArrayList<DichVuKho> arrayList,
                              boolean isAdminMode, OnQuantityChangeListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.isAdminMode = isAdminMode;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_service_shop, parent, false);
        }

        final DichVuKho item = arrayList.get(position);

        ImageView imgThumb = convertView.findViewById(R.id.imgServiceThumb);
        TextView tvName = convertView.findViewById(R.id.tvServiceName);
        TextView tvPrice = convertView.findViewById(R.id.tvServicePrice);
        TextView tvStock = convertView.findViewById(R.id.tvServiceStock);
        TextView btnMinus = convertView.findViewById(R.id.btnMinusQty);
        TextView tvQty = convertView.findViewById(R.id.tvSelectedQty);
        TextView btnPlus = convertView.findViewById(R.id.btnPlusQty);

        tvName.setText(item.getTenMon());
        tvPrice.setText(String.format("%,dđ / %s", item.getDonGia(), item.getDonVi()));

        // Chọn ảnh sản phẩm tương ứng
        String lower = item.getTenMon().toLowerCase();
        if (lower.contains("suối") || lower.contains("aquafina")) {
            imgThumb.setImageResource(R.drawable.img_item_water);
        } else if (lower.contains("revive") || lower.contains("trà") || lower.contains("chanh")) {
            imgThumb.setImageResource(R.drawable.img_item_revive);
        } else if (lower.contains("cầu")) {
            imgThumb.setImageResource(R.drawable.img_item_shuttlecock);
        } else {
            imgThumb.setImageResource(R.drawable.img_item_racket);
        }

        if (isAdminMode) {
            // Chế độ Quản lý (ADMIN): Nút [-] [+] tăng giảm trực tiếp số lượng Tồn kho, bấm vào dòng để sửa giá
            tvStock.setText("Bấm vào thẻ để sửa giá bán");
            tvQty.setText(String.valueOf(item.getSoLuongTon()));

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getSoLuongTon() > 0) {
                        item.setSoLuongTon(item.getSoLuongTon() - 1);
                        notifyDataSetChanged();
                        if (listener != null) listener.onQuantityChanged();
                    }
                }
            });

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setSoLuongTon(item.getSoLuongTon() + 5);
                    notifyDataSetChanged();
                    if (listener != null) listener.onQuantityChanged();
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onItemClickAdmin(item, position);
                }
            });
        } else {
            // Chế độ Khách hàng & Nhân viên: Nút [-] [+] chọn số lượng mua vào giỏ
            tvStock.setText("Còn: " + item.getSoLuongTon() + " " + item.getDonVi());
            tvQty.setText(String.valueOf(item.getSoLuongChon()));

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getSoLuongChon() > 0) {
                        item.setSoLuongChon(item.getSoLuongChon() - 1);
                        notifyDataSetChanged();
                        if (listener != null) listener.onQuantityChanged();
                    }
                }
            });

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getSoLuongChon() < item.getSoLuongTon()) {
                        item.setSoLuongChon(item.getSoLuongChon() + 1);
                        notifyDataSetChanged();
                        if (listener != null) listener.onQuantityChanged();
                    }
                }
            });

            convertView.setOnClickListener(null);
        }

        return convertView;
    }
}
