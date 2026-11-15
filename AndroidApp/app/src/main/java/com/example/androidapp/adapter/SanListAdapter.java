package com.example.androidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.androidapp.model.SanCauLong;

import java.util.ArrayList;

public class SanListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<SanCauLong> arrayList;

    public SanListAdapter(Context context, ArrayList<SanCauLong> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_san_card, parent, false);
        }

        SanCauLong san = arrayList.get(position);

        FrameLayout boxCourtIcon = convertView.findViewById(R.id.boxCourtIcon);
        TextView tvName = convertView.findViewById(R.id.tvItemCourtName);
        TextView tvDesc = convertView.findViewById(R.id.tvItemCourtDesc);
        TextView tvRating = convertView.findViewById(R.id.tvItemCourtRating);
        TextView tvPrice = convertView.findViewById(R.id.tvItemCourtPrice);
        TextView tvStatus = convertView.findViewById(R.id.tvItemCourtStatus);

        tvName.setText(san.getTenSan());
        tvDesc.setText(san.getMoTa());
        tvRating.setText(String.format("%.1f ★", san.getDiemSao()));
        tvPrice.setText(String.format("%,dđ/h", san.getGiaMoiGio()));

        String st = san.getTrangThai();
        if ("TRONG".equals(st)) {
            boxCourtIcon.setBackgroundResource(R.drawable.bg_court_free);
            tvStatus.setBackgroundResource(R.drawable.bg_pill_teal);
            tvStatus.setText("Trống");
        } else if ("DA_DAT".equals(st)) {
            boxCourtIcon.setBackgroundResource(R.drawable.bg_court_booked);
            tvStatus.setBackgroundResource(R.drawable.bg_pill_amber);
            tvStatus.setText("Đã cọc");
        } else if ("DANG_CHOI".equals(st)) {
            boxCourtIcon.setBackgroundResource(R.drawable.bg_court_playing);
            tvStatus.setBackgroundResource(R.drawable.bg_pill_coral);
            tvStatus.setText("Đang chơi");
        } else {
            boxCourtIcon.setBackgroundResource(R.drawable.bg_court_maint);
            tvStatus.setBackgroundResource(R.drawable.bg_pill_grey);
            tvStatus.setText("Bảo trì");
        }

        return convertView;
    }
}
