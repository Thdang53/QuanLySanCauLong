package com.example.androidapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.androidapp.adapter.LeaderboardAdapter;
import com.example.androidapp.adapter.SanListAdapter;
import com.example.androidapp.adapter.TimelineBookingAdapter;
import com.example.androidapp.model.DataStore;
import com.example.androidapp.model.DichVuKho;
import com.example.androidapp.model.LichDatSan;
import com.example.androidapp.model.NhanVienUyTin;
import com.example.androidapp.model.SanCauLong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DaviesArenaActivity extends AppCompatActivity {

    // Role & User info
    private String currentRole = "CUSTOMER";
    private String currentUserName = "Khách Hàng";

    // Views - Tabs & Bottom Navigation
    private LinearLayout tabHome, tabTimeline, tabProfile;
    private LinearLayout navTabHome, navTabTimeline, navTabProfile;
    private TextView tvNavHome, tvNavTimeline, tvNavProfile;

    // Views - Tab 1 (Home)
    private TextView tvWelcomeUser, tvCurrentRoleBadge, btnAddCourtAdmin, tvBannerCta, btnClearSearch, btnBellNotif, btnGridMenu;
    private EditText edtSearchCourt;
    private LinearLayout catCustomer, catStaff, catAdmin, catInventory;
    private ListView listViewSan;

    // Views - Tab 2 (Timeline)
    private TextView tvStatFree, tvStatBooked, tvStatPlaying, tvStatMaint;
    private Button btnQuickBookFromTimeline;
    private ListView listViewTimeline;

    // Views - Tab 3 (Profile & Leaderboard)
    private TextView tvProfileName, tvProfileRoleSub, tvStatRevTotal, tvStatRevCoc, tvStatRevSvc;
    private ListView listViewLeaderboard;
    private Button btnLogoutBack;

    // Adapters & Lists (Chuẩn ArrayList + BaseAdapter của cô)
    private ArrayList<SanCauLong> filteredSanList = new ArrayList<>();
    private SanListAdapter sanAdapter;
    private TimelineBookingAdapter timelineAdapter;
    private LeaderboardAdapter leaderboardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_davies_arena);

        // Khởi tạo dữ liệu mẫu chuẩn 7 bảng
        DataStore.initIfEmpty();

        // Nhận thông tin Role từ LoginActivity qua Intent
        Intent intent = getIntent();
        if (intent != null) {
            String roleExtra = intent.getStringExtra("USER_ROLE");
            String nameExtra = intent.getStringExtra("USER_NAME");
            if (roleExtra != null) currentRole = roleExtra;
            if (nameExtra != null) currentUserName = nameExtra;
        }

        initViews();
        setupAdapters();
        setupBottomNav();
        setupCategoryActions();
        setupSearchFilter();
        setupListClickEvents();

        applyRoleUI();
        refreshAllData();
    }

    private void initViews() {
        tabHome = findViewById(R.id.tabHome);
        tabTimeline = findViewById(R.id.tabTimeline);
        tabProfile = findViewById(R.id.tabProfile);

        navTabHome = findViewById(R.id.navTabHome);
        navTabTimeline = findViewById(R.id.navTabTimeline);
        navTabProfile = findViewById(R.id.navTabProfile);
        tvNavHome = findViewById(R.id.tvNavHome);
        tvNavTimeline = findViewById(R.id.tvNavTimeline);
        tvNavProfile = findViewById(R.id.tvNavProfile);

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvCurrentRoleBadge = findViewById(R.id.tvCurrentRoleBadge);
        btnAddCourtAdmin = findViewById(R.id.btnAddCourtAdmin);
        tvBannerCta = findViewById(R.id.tvBannerCta);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        btnBellNotif = findViewById(R.id.btnBellNotif);
        btnGridMenu = findViewById(R.id.btnGridMenu);
        edtSearchCourt = findViewById(R.id.edtSearchCourt);

        catCustomer = findViewById(R.id.catCustomer);
        catStaff = findViewById(R.id.catStaff);
        catAdmin = findViewById(R.id.catAdmin);
        catInventory = findViewById(R.id.catInventory);
        listViewSan = findViewById(R.id.listViewSan);

        tvStatFree = findViewById(R.id.tvStatFree);
        tvStatBooked = findViewById(R.id.tvStatBooked);
        tvStatPlaying = findViewById(R.id.tvStatPlaying);
        tvStatMaint = findViewById(R.id.tvStatMaint);
        btnQuickBookFromTimeline = findViewById(R.id.btnQuickBookFromTimeline);
        listViewTimeline = findViewById(R.id.listViewTimeline);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileRoleSub = findViewById(R.id.tvProfileRoleSub);
        tvStatRevTotal = findViewById(R.id.tvStatRevTotal);
        tvStatRevCoc = findViewById(R.id.tvStatRevCoc);
        tvStatRevSvc = findViewById(R.id.tvStatRevSvc);
        listViewLeaderboard = findViewById(R.id.listViewLeaderboard);
        btnLogoutBack = findViewById(R.id.btnLogoutBack);
    }

    private void setupAdapters() {
        filteredSanList.clear();
        filteredSanList.addAll(DataStore.listSan);

        sanAdapter = new SanListAdapter(this, filteredSanList);
        listViewSan.setAdapter(sanAdapter);

        timelineAdapter = new TimelineBookingAdapter(this, DataStore.listLichDat);
        listViewTimeline.setAdapter(timelineAdapter);

        sortLeaderboard();
        leaderboardAdapter = new LeaderboardAdapter(this, DataStore.listNhanVien);
        listViewLeaderboard.setAdapter(leaderboardAdapter);
    }

    private void setupBottomNav() {
        navTabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(1);
            }
        });

        navTabTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(2);
            }
        });

        navTabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(3);
            }
        });
    }

    private void switchTab(int tabIndex) {
        tabHome.setVisibility(View.GONE);
        tabTimeline.setVisibility(View.GONE);
        tabProfile.setVisibility(View.GONE);

        int muted = Color.parseColor("#64748B");
        int active = Color.parseColor("#10B981");
        tvNavHome.setTextColor(muted);
        tvNavTimeline.setTextColor(muted);
        tvNavProfile.setTextColor(muted);

        if (tabIndex == 1) {
            tabHome.setVisibility(View.VISIBLE);
            tvNavHome.setTextColor(active);
        } else if (tabIndex == 2) {
            tabTimeline.setVisibility(View.VISIBLE);
            tvNavTimeline.setTextColor(active);
        } else {
            tabProfile.setVisibility(View.VISIBLE);
            tvNavProfile.setTextColor(active);
        }
    }

    private void setupCategoryActions() {
        catCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRole = "CUSTOMER";
                currentUserName = "Khách Hàng";
                applyRoleUI();
                Toast.makeText(DaviesArenaActivity.this, "Đã chuyển sang vai trò: Khách hàng", Toast.LENGTH_SHORT).show();
            }
        });

        catStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRole = "STAFF";
                currentUserName = "Nhân Viên";
                applyRoleUI();
                Toast.makeText(DaviesArenaActivity.this, "Đã chuyển sang vai trò: Nhân viên trực quầy", Toast.LENGTH_SHORT).show();
            }
        });

        catAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentRole = "ADMIN";
                currentUserName = "Quản Lý Sân";
                applyRoleUI();
                Toast.makeText(DaviesArenaActivity.this, "Đã chuyển sang vai trò: Quản lý sân (Admin)", Toast.LENGTH_SHORT).show();
            }
        });

        catInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInventoryDialog();
            }
        });

        tvBannerCta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tìm sân trống đầu tiên để mở đặt nhanh
                for (int i = 0; i < filteredSanList.size(); i++) {
                    if ("TRONG".equals(filteredSanList.get(i).getTrangThai())) {
                        showCustomerBookDialog(i);
                        return;
                    }
                }
                Toast.makeText(DaviesArenaActivity.this, "Hiện không còn sân trống!", Toast.LENGTH_SHORT).show();
            }
        });

        btnQuickBookFromTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(1);
                Toast.makeText(DaviesArenaActivity.this, "Chọn một sân màu XANH để đặt lịch cọc 30%", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddCourtAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCourtDialog();
            }
        });

        btnBellNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pending = 0;
                for (LichDatSan b : DataStore.listLichDat) {
                    if ("CHO_DUYET".equals(b.getTrangThai())) pending++;
                }
                Toast.makeText(DaviesArenaActivity.this, "Bạn có " + pending + " đơn chờ duyệt cọc 30%", Toast.LENGTH_SHORT).show();
            }
        });

        btnGridMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(3);
            }
        });

        btnLogoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(DaviesArenaActivity.this, LoginActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
    }

    private void setupSearchFilter() {
        edtSearchCourt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCourts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearchCourt.setText("");
                filterCourts("");
            }
        });
    }

    private void filterCourts(String keyword) {
        filteredSanList.clear();
        String kw = keyword.toLowerCase().trim();
        for (SanCauLong san : DataStore.listSan) {
            if (kw.isEmpty() || san.getTenSan().toLowerCase().contains(kw) || san.getMoTa().toLowerCase().contains(kw)) {
                filteredSanList.add(san);
            }
        }
        sanAdapter.notifyDataSetChanged();
    }

    private void applyRoleUI() {
        tvWelcomeUser.setText("Xin chào, " + currentUserName);
        tvProfileName.setText(currentUserName);

        if ("CUSTOMER".equals(currentRole)) {
            tvCurrentRoleBadge.setText("Vai trò: 🏸 Khách hàng (Đặt sân & Chấm sao)");
            tvProfileRoleSub.setText("📍 Davies Arena • Khách hàng O2O");
            btnAddCourtAdmin.setVisibility(View.GONE);
        } else if ("STAFF".equals(currentRole)) {
            tvCurrentRoleBadge.setText("Vai trò: 👨‍💼 Nhân viên trực quầy (Duyệt cọc & Check-out)");
            tvProfileRoleSub.setText("📍 Davies Arena • Thu ngân trực ca");
            btnAddCourtAdmin.setVisibility(View.GONE);
        } else {
            tvCurrentRoleBadge.setText("Vai trò: 👑 Chủ sân Admin (Sửa giá & Quản trị KPI)");
            tvProfileRoleSub.setText("📍 Davies Arena • Quản trị viên hệ thống");
            btnAddCourtAdmin.setVisibility(View.VISIBLE);
        }
    }

    private void setupListClickEvents() {
        // 1. Sự kiện bấm vào từng dòng Sân trên ListView
        listViewSan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SanCauLong san = filteredSanList.get(position);
                if ("CUSTOMER".equals(currentRole)) {
                    if ("TRONG".equals(san.getTrangThai())) {
                        showCustomerBookDialog(position);
                    } else {
                        Toast.makeText(DaviesArenaActivity.this, san.getTenSan() + " hiện đang " + san.getTrangThai(), Toast.LENGTH_SHORT).show();
                    }
                } else if ("STAFF".equals(currentRole)) {
                    handleStaffCourtTap(san);
                } else {
                    editSanAdmin(san);
                }
            }
        });

        // 2. Sự kiện bấm vào từng dòng Lịch đặt trên ListView Timeline
        listViewTimeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LichDatSan booking = DataStore.listLichDat.get(position);
                handleTimelineItemClick(position, booking);
            }
        });

        // 3. Sự kiện bấm vào nhân viên trên Bảng Xếp Hạng
        listViewLeaderboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NhanVienUyTin nv = DataStore.listNhanVien.get(position);
                new AlertDialog.Builder(DaviesArenaActivity.this)
                        .setTitle("Hồ sơ Uy tín: " + nv.getHoTen())
                        .setMessage("• Chức vụ: " + nv.getChucVu() + "\n"
                                + "• Điểm trung bình: " + String.format("%.1f / 5.0 ⭐", nv.getDiemTrungBinh()) + "\n"
                                + "• Tổng lượt đánh giá: " + nv.getSoLuotDanhGia() + "\n"
                                + "• Phản hồi mới nhất: \"" + nv.getNhanXetGanNhat() + "\"")
                        .setPositiveButton("Đóng", null)
                        .show();
            }
        });
    }

    // ==================== NGHIỆP VỤ ROLE 1: KHÁCH ĐẶT SÂN & CỌC 30% VIETQR ====================
    private void showCustomerBookDialog(int position) {
        final SanCauLong san = filteredSanList.get(position);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(45, 25, 45, 10);

        final EditText edtGioBatDau = new EditText(this);
        edtGioBatDau.setHint("Giờ bắt đầu (VD: 18)");
        edtGioBatDau.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtGioBatDau.setText("18");
        layout.addView(edtGioBatDau);

        final EditText edtSoGio = new EditText(this);
        edtSoGio.setHint("Số giờ thuê (VD: 2)");
        edtSoGio.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtSoGio.setText("2");
        layout.addView(edtSoGio);

        new AlertDialog.Builder(this)
                .setTitle("Đặt " + san.getTenSan())
                .setMessage("Đơn giá: " + String.format("%,dđ/giờ", san.getGiaMoiGio()) + "\nQuy định bắt buộc đặt cọc 30% qua VietQR:")
                .setView(layout)
                .setPositiveButton("Tiếp tục Thanh Toán Cọc 30%", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sGio = edtGioBatDau.getText().toString().trim();
                        String sSoGio = edtSoGio.getText().toString().trim();
                        if (sGio.isEmpty() || sSoGio.isEmpty()) {
                            Toast.makeText(DaviesArenaActivity.this, "Không được để trống giờ!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int gio = Integer.parseInt(sGio);
                        int soGio = Integer.parseInt(sSoGio);
                        int tienSan = soGio * san.getGiaMoiGio();
                        int tienCoc = (int) (tienSan * 0.3);
                        showVietQrDialog(san, gio, soGio, tienSan, tienCoc);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showVietQrDialog(final SanCauLong san, final int gio, final int soGio, final int tienSan, final int tienCoc) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        ImageView ivQr = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(450, 450);
        params.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        ivQr.setLayoutParams(params);

        // Dùng Glide tải mã QR VietQR thật tự động điền tiền cọc 30%
        String qrUrl = "https://img.vietqr.io/image/VCB-0901234567-compact2.png?amount=" + tienCoc + "&addInfo=COC%20SAN%20" + san.getId();
        Glide.with(this)
                .load(qrUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivQr);
        layout.addView(ivQr);

        TextView tvInfo = new TextView(this);
        tvInfo.setText("• Ngân hàng: Vietcombank - 0901234567\n"
                + "• Tiền sân tạm tính (" + soGio + "h): " + String.format("%,d VNĐ", tienSan) + "\n"
                + "• Tiền cọc 30% phải chuyển: " + String.format("%,d VNĐ", tienCoc) + "\n"
                + "• Nội dung CK: COC SAN " + san.getId() + " 0901234567");
        tvInfo.setTextSize(13);
        tvInfo.setPadding(0, 16, 0, 0);
        layout.addView(tvInfo);

        new AlertDialog.Builder(this)
                .setTitle("Quét VietQR Đặt Cọc 30%")
                .setView(layout)
                .setPositiveButton("Tôi Đã Chuyển Khoản Cọc", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String gioMoc = String.format("%02d:00", gio);
                        String khung = String.format("%02d:00 - %02d:00 (%d giờ)", gio, gio + soGio, soGio);
                        LichDatSan newBooking = new LichDatSan(
                                DataStore.nextId(), san.getId(), san.getTenSan(),
                                currentUserName, "0901234567", gioMoc, khung,
                                tienSan, tienCoc, 0, "CHO_DUYET", "Nguyễn Văn Hùng", false
                        );
                        DataStore.listLichDat.add(0, newBooking);
                        refreshAllData();
                        switchTab(2);
                        Toast.makeText(DaviesArenaActivity.this, "Đã gửi đơn cọc 30%! Chờ nhân viên quầy duyệt.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    // ==================== NGHIỆP VỤ ROLE 2: NHÂN VIÊN TRỰC QUẦY ====================
    private void handleStaffCourtTap(final SanCauLong san) {
        String st = san.getTrangThai();
        if ("TRONG".equals(st)) {
            new AlertDialog.Builder(this)
                    .setTitle(san.getTenSan() + " (Đang trống)")
                    .setMessage("Mở sân ngay cho khách vãng lai đến chơi trực tiếp?")
                    .setPositiveButton("Check-in Mở Sân Ngay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            san.setTrangThai("DANG_CHOI");
                            LichDatSan walkIn = new LichDatSan(
                                    DataStore.nextId(), san.getId(), san.getTenSan(),
                                    "Khách vãng lai", "Tại quầy", "Hiện tại", "Chơi trực tiếp (2 giờ)",
                                    san.getGiaMoiGio() * 2, 0, 0, "DANG_CHOI", currentUserName, false
                            );
                            DataStore.listLichDat.add(0, walkIn);
                            refreshAllData();
                            Toast.makeText(DaviesArenaActivity.this, "Đã mở " + san.getTenSan() + " (Màu ĐỎ)!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        } else if ("DA_DAT".equals(st)) {
            new AlertDialog.Builder(this)
                    .setTitle(san.getTenSan() + " (Đã Cọc 30%)")
                    .setMessage("Khách đã đến nhận sân? Bấm Check-in để bắt đầu tính giờ chơi.")
                    .setPositiveButton("Check-in Nhận Sân", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            san.setTrangThai("DANG_CHOI");
                            for (LichDatSan b : DataStore.listLichDat) {
                                if (b.getMaSan() == san.getId() && "DA_COC".equals(b.getTrangThai())) {
                                    b.setTrangThai("DANG_CHOI");
                                    break;
                                }
                            }
                            refreshAllData();
                            Toast.makeText(DaviesArenaActivity.this, "Đã Check-in! Sân đổi sang màu ĐỎ.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        } else if ("DANG_CHOI".equals(st)) {
            LichDatSan activeBooking = null;
            for (LichDatSan b : DataStore.listLichDat) {
                if (b.getMaSan() == san.getId() && "DANG_CHOI".equals(b.getTrangThai())) {
                    activeBooking = b;
                    break;
                }
            }
            if (activeBooking != null) {
                showPlayingActionsDialog(activeBooking);
            } else {
                Toast.makeText(this, "Sân đang bảo trì hoặc không tìm thấy đơn chơi.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, san.getTenSan() + " đang bảo trì!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPlayingActionsDialog(final LichDatSan booking) {
        new AlertDialog.Builder(this)
                .setTitle("Vận hành " + booking.getTenSan())
                .setMessage("• Khách: " + booking.getTenKhach() + "\n"
                        + "• Tiền sân tạm tính: " + String.format("%,dđ", booking.getTienSan()) + "\n"
                        + "• Tiền nước/cầu đã gọi: " + String.format("%,dđ", booking.getTienDichVu()) + "\n"
                        + "• Tiền cọc 30% đã đóng: " + String.format("%,dđ", booking.getTienCoc30()))
                .setPositiveButton("Trả Sân & Check-out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCheckoutBillDialog(booking);
                    }
                })
                .setNeutralButton("+ Thêm Nước / Cầu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAddServiceDialog(booking);
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showAddServiceDialog(final LichDatSan booking) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final Spinner spinner = new Spinner(this);
        ArrayList<String> items = new ArrayList<>();
        for (DichVuKho dv : DataStore.listDichVu) {
            items.add(dv.getBieuTuong() + " " + dv.getTenMon() + " (" + String.format("%,dđ", dv.getDonGia()) + " - tồn " + dv.getSoLuongTon() + ")");
        }
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(spAdapter);
        layout.addView(spinner);

        final EditText edtQty = new EditText(this);
        edtQty.setHint("Nhập số lượng (VD: 2)");
        edtQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtQty.setText("1");
        layout.addView(edtQty);

        new AlertDialog.Builder(this)
                .setTitle("+ Thêm Dịch Vụ Vào Bill")
                .setView(layout)
                .setPositiveButton("Xác nhận thêm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sQty = edtQty.getText().toString().trim();
                        if (sQty.isEmpty()) {
                            Toast.makeText(DaviesArenaActivity.this, "Không được để trống số lượng!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int qty = Integer.parseInt(sQty);
                        DichVuKho dv = DataStore.listDichVu.get(spinner.getSelectedItemPosition());
                        if (qty <= 0 || qty > dv.getSoLuongTon()) {
                            Toast.makeText(DaviesArenaActivity.this, "Số lượng tồn kho không đủ!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dv.setSoLuongTon(dv.getSoLuongTon() - qty);
                        int addMoney = qty * dv.getDonGia();
                        booking.setTienDichVu(booking.getTienDichVu() + addMoney);
                        refreshAllData();
                        Toast.makeText(DaviesArenaActivity.this, "Đã thêm " + qty + " " + dv.getTenMon() + " (+ " + String.format("%,dđ", addMoney) + ")", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showCheckoutBillDialog(final LichDatSan booking) {
        int canThuThem = booking.getTienSan() + booking.getTienDichVu() - booking.getTienCoc30();
        String msg = "[+] Tiền sân thực tế: " + String.format("%,d VNĐ", booking.getTienSan()) + "\n"
                + "[+] Tiền nước & phụ kiện: " + String.format("%,d VNĐ", booking.getTienDichVu()) + "\n"
                + "[-] Khấu trừ tiền cọc 30%: -" + String.format("%,d VNĐ", booking.getTienCoc30()) + "\n"
                + "─────────────────────────\n"
                + "[=] TỔNG CẦN THU THÊM: " + String.format("%,d VNĐ", canThuThem) + "\n\n"
                + "👨‍💼 Thu ngân chốt bill: " + (currentRole.equals("STAFF") ? currentUserName : "Nguyễn Văn Hùng");

        new AlertDialog.Builder(this)
                .setTitle("Hóa Đơn Thanh Toán (Check-out)")
                .setMessage(msg)
                .setPositiveButton("Hoàn Tất Thu Tiền", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataStore.doanhThuTaiQuay += (booking.getTienSan() - booking.getTienCoc30());
                        DataStore.doanhThuDichVu += booking.getTienDichVu();

                        booking.setTrangThai("HOAN_THANH");
                        if ("STAFF".equals(currentRole)) {
                            booking.setNhanVienThu(currentUserName);
                        }
                        for (SanCauLong s : DataStore.listSan) {
                            if (s.getId() == booking.getMaSan()) {
                                s.setTrangThai("TRONG");
                                break;
                            }
                        }
                        refreshAllData();
                        Toast.makeText(DaviesArenaActivity.this, "Đã thu tiền xong! Sân trả về màu XANH, khách có thể chấm sao.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Quay lại", null)
                .show();
    }

    // ==================== SỰ KIỆN BẤM DÒNG TIMELINE (DUYỆT CỌC / HỦY / ĐÁNH GIÁ SAO) ====================
    private void handleTimelineItemClick(final int pos, final LichDatSan booking) {
        String st = booking.getTrangThai();

        if ("CHO_DUYET".equals(st)) {
            if ("STAFF".equals(currentRole) || "ADMIN".equals(currentRole)) {
                new AlertDialog.Builder(this)
                        .setTitle("Duyệt Tiền Cọc 30%")
                        .setMessage("Khách: " + booking.getTenKhach() + "\nSân: " + booking.getTenSan()
                                + "\nCọc cần xác nhận: " + String.format("%,d VNĐ", booking.getTienCoc30()))
                        .setPositiveButton("Xác Nhận Đã Nhận Cọc", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                booking.setTrangThai("DA_COC");
                                DataStore.doanhThuCocQR += booking.getTienCoc30();
                                for (SanCauLong s : DataStore.listSan) {
                                    if (s.getId() == booking.getMaSan()) {
                                        s.setTrangThai("DA_DAT");
                                        break;
                                    }
                                }
                                refreshAllData();
                                Toast.makeText(DaviesArenaActivity.this, "Đã duyệt cọc! Sân đổi sang màu VÀNG.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Từ chối/Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                booking.setTrangThai("DA_HUY");
                                refreshAllData();
                            }
                        })
                        .show();
            } else {
                Toast.makeText(this, "Đơn đang chờ nhân viên quầy xác nhận cọc 30%. Chuyển sang vai trò Quầy để duyệt thử!", Toast.LENGTH_LONG).show();
            }
        } else if ("DA_COC".equals(st)) {
            if ("STAFF".equals(currentRole) || "ADMIN".equals(currentRole)) {
                for (SanCauLong s : DataStore.listSan) {
                    if (s.getId() == booking.getMaSan()) {
                        handleStaffCourtTap(s);
                        break;
                    }
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Quản Lý Lịch Đặt")
                        .setMessage(booking.getTenSan() + " (" + booking.getKhungGio() + ")\nLưu ý: Hủy trước >= 2 tiếng được hoàn cọc, hủy muộn mất 100% cọc 30%.")
                        .setPositiveButton("Hủy Lịch Đặt", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                booking.setTrangThai("DA_HUY");
                                for (SanCauLong s : DataStore.listSan) {
                                    if (s.getId() == booking.getMaSan()) {
                                        s.setTrangThai("TRONG");
                                        break;
                                    }
                                }
                                refreshAllData();
                                Toast.makeText(DaviesArenaActivity.this, "Đã hủy lịch đặt và giải phóng sân.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Giữ lịch", null)
                        .show();
            }
        } else if ("DANG_CHOI".equals(st)) {
            if ("STAFF".equals(currentRole) || "ADMIN".equals(currentRole)) {
                showPlayingActionsDialog(booking);
            } else {
                Toast.makeText(this, "Trận đấu đang diễn ra trên " + booking.getTenSan(), Toast.LENGTH_SHORT).show();
            }
        } else if ("HOAN_THANH".equals(st)) {
            if (booking.isDaDanhGia()) {
                Toast.makeText(this, "Mỗi hóa đơn chỉ được đánh giá duy nhất 1 lần!", Toast.LENGTH_SHORT).show();
            } else {
                showRatingStaffDialog(booking);
            }
        }
    }

    // ==================== CHẤM SAO NHÂN VIÊN (ALERTDIALOG + RATINGBAR + EDITTEXT CỦA CÔ) ====================
    private void showRatingStaffDialog(final LichDatSan booking) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(45, 25, 45, 10);

        TextView tvStaff = new TextView(this);
        tvStaff.setText("Thu ngân ca: " + booking.getNhanVienThu() + "\nHóa đơn: " + booking.getTenSan());
        tvStaff.setTextColor(Color.parseColor("#065F46"));
        tvStaff.setTextSize(13.5f);
        layout.addView(tvStaff);

        final RatingBar ratingBar = new RatingBar(this);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(5.0f);
        LinearLayout.LayoutParams rbParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rbParams.topMargin = 16;
        ratingBar.setLayoutParams(rbParams);
        layout.addView(ratingBar);

        final EditText edtComment = new EditText(this);
        edtComment.setHint("Nhập nhận xét thái độ phục vụ...");
        edtComment.setText("Phục vụ rất nhiệt tình, sân sạch đẹp!");
        layout.addView(edtComment);

        new AlertDialog.Builder(this)
                .setTitle("⭐ Đánh Giá Nhân Viên Phục Vụ")
                .setView(layout)
                .setPositiveButton("Gửi Đánh Giá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int stars = (int) ratingBar.getRating();
                        String comment = edtComment.getText().toString().trim();
                        if (stars <= 0) {
                            Toast.makeText(DaviesArenaActivity.this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        booking.setDaDanhGia(true);
                        for (NhanVienUyTin nv : DataStore.listNhanVien) {
                            if (nv.getHoTen().equals(booking.getNhanVienThu())) {
                                nv.themDanhGia(stars, comment);
                                break;
                            }
                        }
                        refreshAllData();
                        switchTab(3);
                        Toast.makeText(DaviesArenaActivity.this, "Đã gửi " + stars + " ⭐ cho " + booking.getNhanVienThu() + "! Bảng xếp hạng đã cập nhật.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ==================== ROLE 3 ADMIN: HÀM SỬA GIÁ SÂN THEO ĐÚNG MẪU editData CỦA CÔ ====================
    private void editSanAdmin(final SanCauLong san) {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(String.valueOf(san.getGiaMoiGio()));

        new AlertDialog.Builder(this)
                .setTitle("Quản trị: " + san.getTenSan())
                .setMessage("Nhập đơn giá thuê 1 giờ mới (VNĐ):")
                .setView(editText)
                .setPositiveButton("Sửa Giá", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String newValue = editText.getText().toString().trim();
                        if (newValue.isEmpty()) {
                            Toast.makeText(DaviesArenaActivity.this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                        } else {
                            san.setGiaMoiGio(Integer.parseInt(newValue));
                            refreshAllData();
                            Toast.makeText(DaviesArenaActivity.this, "Cập nhật giá thành công!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNeutralButton("BAO_TRI".equals(san.getTrangThai()) ? "Mở lại sân" : "Bật Bảo Trì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("BAO_TRI".equals(san.getTrangThai())) {
                            san.setTrangThai("TRONG");
                        } else {
                            san.setTrangThai("BAO_TRI");
                        }
                        refreshAllData();
                        Toast.makeText(DaviesArenaActivity.this, "Đã đổi trạng thái " + san.getTenSan(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAddCourtDialog() {
        final EditText edtTenSan = new EditText(this);
        edtTenSan.setHint("Nhập tên sân mới (VD: Sân 7 • VIP)");

        new AlertDialog.Builder(this)
                .setTitle("+ Thêm Sân Cầu Lông Mới")
                .setView(edtTenSan)
                .setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edtTenSan.getText().toString().trim();
                        if (name.isEmpty()) {
                            Toast.makeText(DaviesArenaActivity.this, "Không được rỗng!", Toast.LENGTH_SHORT).show();
                        } else {
                            int newId = DataStore.listSan.size() + 1;
                            DataStore.listSan.add(new SanCauLong(newId, name, "Sân mới khai trương", 80000, 5.0, "TRONG"));
                            refreshAllData();
                            Toast.makeText(DaviesArenaActivity.this, "Đã thêm " + name, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ==================== KHO HÀNG & DỊCH VỤ (DÙNG LISTVIEW + ALERTDIALOG CỦA CÔ) ====================
    private void showInventoryDialog() {
        final ArrayList<String> khoDisplay = new ArrayList<>();
        for (DichVuKho dv : DataStore.listDichVu) {
            khoDisplay.add(dv.getBieuTuong() + " " + dv.getTenMon() + " — " + String.format("%,dđ", dv.getDonGia()) + " (Tồn: " + dv.getSoLuongTon() + " " + dv.getDonVi() + ")");
        }

        ListView lvKho = new ListView(this);
        final ArrayAdapter<String> khoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, khoDisplay);
        lvKho.setAdapter(khoAdapter);

        lvKho.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                if (!"ADMIN".equals(currentRole)) {
                    Toast.makeText(DaviesArenaActivity.this, "Chỉ Chủ sân (Admin) mới được nhập kho!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final DichVuKho dv = DataStore.listDichVu.get(pos);
                final EditText edtAdd = new EditText(DaviesArenaActivity.this);
                edtAdd.setInputType(InputType.TYPE_CLASS_NUMBER);
                edtAdd.setHint("Nhập số lượng nhập thêm (VD: 20)");
                edtAdd.setText("20");

                new AlertDialog.Builder(DaviesArenaActivity.this)
                        .setTitle("Nhập thêm kho: " + dv.getTenMon())
                        .setView(edtAdd)
                        .setPositiveButton("Nhập kho", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String val = edtAdd.getText().toString().trim();
                                if (!val.isEmpty()) {
                                    dv.setSoLuongTon(dv.getSoLuongTon() + Integer.parseInt(val));
                                    khoDisplay.set(pos, dv.getBieuTuong() + " " + dv.getTenMon() + " — " + String.format("%,dđ", dv.getDonGia()) + " (Tồn: " + dv.getSoLuongTon() + " " + dv.getDonVi() + ")");
                                    khoAdapter.notifyDataSetChanged();
                                    Toast.makeText(DaviesArenaActivity.this, "Đã nhập thêm hàng!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        new AlertDialog.Builder(this)
                .setTitle("📦 Menu Dịch Vụ & Tồn Kho (Admin bấm để nhập)")
                .setView(lvKho)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void sortLeaderboard() {
        Collections.sort(DataStore.listNhanVien, new Comparator<NhanVienUyTin>() {
            @Override
            public int compare(NhanVienUyTin a, NhanVienUyTin b) {
                return Double.compare(b.getDiemTrungBinh(), a.getDiemTrungBinh());
            }
        });
    }

    private void refreshAllData() {
        filterCourts(edtSearchCourt.getText().toString());
        timelineAdapter.notifyDataSetChanged();
        sortLeaderboard();
        leaderboardAdapter.notifyDataSetChanged();

        int cFree = 0, cBooked = 0, cPlaying = 0, cMaint = 0;
        for (SanCauLong s : DataStore.listSan) {
            if ("TRONG".equals(s.getTrangThai())) cFree++;
            else if ("DA_DAT".equals(s.getTrangThai())) cBooked++;
            else if ("DANG_CHOI".equals(s.getTrangThai())) cPlaying++;
            else cMaint++;
        }
        tvStatFree.setText("🟢 Trống: " + cFree);
        tvStatBooked.setText("🟡 Cọc: " + cBooked);
        tvStatPlaying.setText("🔴 Chơi: " + cPlaying);
        tvStatMaint.setText("⚪ Bảo trì: " + cMaint);

        tvStatRevTotal.setText(String.format("%,dk", DataStore.getTongDoanhThu() / 1000));
        tvStatRevCoc.setText(String.format("%,dk", DataStore.doanhThuCocQR / 1000));
        tvStatRevSvc.setText(String.format("%,dk", DataStore.doanhThuDichVu / 1000));
    }
}
