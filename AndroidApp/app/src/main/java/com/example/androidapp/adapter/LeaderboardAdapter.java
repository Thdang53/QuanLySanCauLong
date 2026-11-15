package com.example.androidapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidapp.R;
import com.example.androidapp.model.NhanVienUyTin;

import java.util.ArrayList;

public class LeaderboardAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<NhanVienUyTin> arrayList;

    public LeaderboardAdapter(Context context, ArrayList<NhanVienUyTin> arrayList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_staff_leaderboard, parent, false);
        }

        NhanVienUyTin nv = arrayList.get(position);

        LinearLayout card = convertView.findViewById(R.id.cardLeaderboardItem);
        TextView tvMedal = convertView.findViewById(R.id.tvRankMedal);
        TextView tvName = convertView.findViewById(R.id.tvStaffName);
        TextView tvScore = convertView.findViewById(R.id.tvStaffScore);
        TextView tvComment = convertView.findViewById(R.id.tvStaffComment);
        ProgressBar pb = convertView.findViewById(R.id.pbStaffRating);

        String[] medals = {"🥇", "🥈", "🥉"};
        tvMedal.setText(position < medals.length ? medals[position] : "★");

        tvName.setText(nv.getHoTen() + " • " + nv.getChucVu());
        double avg = nv.getDiemTrungBinh();
        tvScore.setText(String.format("%.1f ★", avg));

        if (nv.getSaoGanNhat() <= 2) {
            card.setBackgroundResource(R.drawable.bg_badge_bad);
            tvScore.setTextColor(Color.parseColor("#E11D48"));
            tvComment.setText("Khiếu nại: " + nv.getNhanXetGanNhat());
        } else {
            card.setBackgroundResource(R.drawable.bg_card_soft_22);
            tvScore.setTextColor(Color.parseColor("#059669"));
            tvComment.setText(nv.getNhanXetGanNhat());
        }

        int pct = (int) Math.round((avg / 5.0) * 100);
        pb.setProgress(Math.min(100, Math.max(0, pct)));

        return convertView;
    }
}
