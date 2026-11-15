package com.example.androidapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.androidapp.model.LichDatSan;

import java.util.ArrayList;

public class TimelineBookingAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<LichDatSan> arrayList;

    public TimelineBookingAdapter(Context context, ArrayList<LichDatSan> arrayList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_timeline_booking, parent, false);
        }

        LichDatSan item = arrayList.get(position);

        TextView tvHour = convertView.findViewById(R.id.tvTimelineHour);
        TextView tvDot = convertView.findViewById(R.id.tvTimelineDot);
        LinearLayout boxPill = convertView.findViewById(R.id.boxTimelinePill);
        TextView tvTitle = convertView.findViewById(R.id.tvTimelineTitle);
        TextView tvBadge = convertView.findViewById(R.id.tvTimelineBadge);
        TextView tvSub = convertView.findViewById(R.id.tvTimelineSub);

        tvHour.setText(item.getGioMoc());
        tvTitle.setText(item.getTenSan());

        // Tạo hiệu ứng lệch tầng (Staggered Timeline Pills) giống hệt ảnh mẫu giữa
        float density = context.getResources().getDisplayMetrics().density;
        int[] offsetsDp = {8, 34, 16, 26};
        int startMarginPx = (int) (offsetsDp[position % offsetsDp.length] * density);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) boxPill.getLayoutParams();
        params.setMarginStart(startMarginPx);
        boxPill.setLayoutParams(params);

        String st = item.getTrangThai();
        if ("CHO_DUYET".equals(st)) {
            boxPill.setBackgroundResource(R.drawable.bg_pill_purple);
            tvDot.setTextColor(Color.parseColor("#8B5CF6"));
            tvBadge.setText("Chờ duyệt");
            tvSub.setText("🕒 " + item.getKhungGio());
        } else if ("DA_COC".equals(st)) {
            boxPill.setBackgroundResource(R.drawable.bg_pill_blue);
            tvDot.setTextColor(Color.parseColor("#2563EB"));
            tvBadge.setText("Đã cọc");
            tvSub.setText("🕒 " + item.getKhungGio());
        } else if ("DANG_CHOI".equals(st)) {
            boxPill.setBackgroundResource(R.drawable.bg_pill_coral);
            tvDot.setTextColor(Color.parseColor("#FB7185"));
            tvBadge.setText("Đang chơi");
            tvSub.setText("🕒 " + item.getKhungGio());
        } else if ("HOAN_THANH".equals(st)) {
            boxPill.setBackgroundResource(R.drawable.bg_pill_teal);
            tvDot.setTextColor(Color.parseColor("#14B8A6"));
            tvBadge.setText(item.isDaDanhGia() ? "Đã chấm ★" : "Chấm sao");
            tvSub.setText("🕒 " + item.getKhungGio());
        } else {
            boxPill.setBackgroundResource(R.drawable.bg_pill_grey);
            tvDot.setTextColor(Color.parseColor("#94A3B8"));
            tvBadge.setText("Đã hủy");
            tvSub.setText("🕒 " + item.getKhungGio());
        }

        return convertView;
    }
}
