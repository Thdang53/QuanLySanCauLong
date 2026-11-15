package com.example.androidapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.androidapp.model.DataStore;
import com.example.androidapp.model.DichVuKho;
import com.example.androidapp.model.LichDatSan;
import com.example.androidapp.model.NhanVienUyTin;
import com.example.androidapp.model.SanCauLong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class DaviesArenaActivity extends AppCompatActivity {

    private String currentRole = "CUSTOMER";
    private String currentUserName = "Nguyễn Minh Khang";
    private int currentTab = 0;

    // Booking form state (Role Customer)
    private int selectedCourtId = 1;
    private String selectedDateStr = "11/15/2026";
    private int selectedFromHour = 18;
    private int selectedToHour = 20;

    // Staff reputation filter state
    private int selectedStaffIndex = 0;

    // Header & Body Views
    private TextView tvHeaderTitle, tvHeaderSub, tvHeaderGlass, btnHeaderLogout, btnHeaderTheme;
    private LinearLayout containerDynamicBody;
    private ScrollView scrollMainContent;

    // Bottom Navigation Views (.u041)
    private FrameLayout navSlot0, navSlot1, navSlot2, navSlot3;
    private TextView navBubble0, navBubble1, navBubble2, navBubble3;
    private TextView navIcon0, navIcon1, navIcon2, navIcon3;
    private TextView navLabel0, navLabel1, navLabel2, navLabel3;
    private TextView navBadge1;

    // Live 1-second timer for playing courts
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final ArrayList<TextView> activeTimerViews = new ArrayList<>();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedSec = Math.max(0, (System.currentTimeMillis() - DataStore.startTimeSan3) / 1000L);
            String formatted = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    elapsedSec / 3600, (elapsedSec / 60) % 60, elapsedSec % 60);
            for (TextView tv : activeTimerViews) {
                if (tv != null) {
                    tv.setText(formatted);
                }
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_davies_arena);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#0D9488"));
            window.setNavigationBarColor(Color.parseColor("#EEF4F3"));
        }

        DataStore.initIfEmpty();

        Intent intent = getIntent();
        if (intent != null) {
            String roleExtra = intent.getStringExtra("USER_ROLE");
            String nameExtra = intent.getStringExtra("USER_NAME");
            if (roleExtra != null) currentRole = roleExtra;
            if (nameExtra != null) currentUserName = nameExtra;
        }

        // Tìm sân trống đầu tiên làm mặc định cho Khách hàng
        for (SanCauLong s : DataStore.listSan) {
            if ("TRONG".equals(s.getTrangThai())) {
                selectedCourtId = s.getId();
                break;
            }
        }

        initViews();
        setupHeaderAndNavActions();
        renderScreen();
        timerHandler.post(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void initViews() {
        scrollMainContent = findViewById(R.id.scrollMainContent);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSub = findViewById(R.id.tvHeaderSub);
        tvHeaderGlass = findViewById(R.id.tvHeaderGlass);
        btnHeaderLogout = findViewById(R.id.btnHeaderLogout);
        btnHeaderTheme = findViewById(R.id.btnHeaderTheme);
        containerDynamicBody = findViewById(R.id.containerDynamicBody);

        navSlot0 = findViewById(R.id.navSlot0);
        navSlot1 = findViewById(R.id.navSlot1);
        navSlot2 = findViewById(R.id.navSlot2);
        navSlot3 = findViewById(R.id.navSlot3);

        navBubble0 = findViewById(R.id.navBubble0);
        navBubble1 = findViewById(R.id.navBubble1);
        navBubble2 = findViewById(R.id.navBubble2);
        navBubble3 = findViewById(R.id.navBubble3);

        navIcon0 = findViewById(R.id.navIcon0);
        navIcon1 = findViewById(R.id.navIcon1);
        navIcon2 = findViewById(R.id.navIcon2);
        navIcon3 = findViewById(R.id.navIcon3);

        navLabel0 = findViewById(R.id.navLabel0);
        navLabel1 = findViewById(R.id.navLabel1);
        navLabel2 = findViewById(R.id.navLabel2);
        navLabel3 = findViewById(R.id.navLabel3);

        navBadge1 = findViewById(R.id.navBadge1);
    }

    private void setupHeaderAndNavActions() {
        btnHeaderLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(DaviesArenaActivity.this, LoginActivity.class);
                startActivity(back);
                finish();
            }
        });

        btnHeaderTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DaviesArenaActivity.this, "Giao diện Emerald Davies Arena", Toast.LENGTH_SHORT).show();
            }
        });

        navSlot0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTab = 0;
                renderScreen();
                scrollMainContent.smoothScrollTo(0, 0);
            }
        });

        navSlot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTab = 1;
                renderScreen();
                scrollMainContent.smoothScrollTo(0, 0);
            }
        });

        navSlot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTab = 2;
                renderScreen();
                scrollMainContent.smoothScrollTo(0, 0);
            }
        });

        navSlot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTab = 3;
                renderScreen();
                scrollMainContent.smoothScrollTo(0, 0);
            }
        });
    }

    // ==================== RENDER CHÍNH ĐỒNG BỘ 100% THEO ROLE & TAB ====================
    private void renderScreen() {
        activeTimerViews.clear();
        containerDynamicBody.removeAllViews();
        updateBottomNavForRole();

        if ("CUSTOMER".equals(currentRole)) {
            if (currentTab == 0) renderCustomerBookTab();
            else if (currentTab == 1) renderCustomerBookingsTab();
            else renderCustomerAccountTab();
        } else if ("STAFF".equals(currentRole)) {
            if (currentTab == 0) renderStaffCourtMapTab();
            else if (currentTab == 1) renderStaffApproveDepositTab();
            else renderStaffReputationTab();
        } else {
            if (currentTab == 0) renderAdminRevenueTab();
            else if (currentTab == 1) renderAdminLeaderboardTab();
            else if (currentTab == 2) renderAdminCourtsAndPriceTab();
            else renderAdminInventoryTab();
        }
    }

    private void updateBottomNavForRole() {
        String[] icons;
        String[] labels;
        if ("CUSTOMER".equals(currentRole)) {
            icons = new String[]{"⊞", "📅", "👤"};
            labels = new String[]{"Đặt sân", "Lịch đặt của tôi", "Tài khoản"};
            navSlot3.setVisibility(View.GONE);
        } else if ("STAFF".equals(currentRole)) {
            icons = new String[]{"⊞", "✓", "☆"};
            labels = new String[]{"Sơ đồ sân", "Duyệt cọc", "Uy tín"};
            navSlot3.setVisibility(View.GONE);
        } else {
            icons = new String[]{"📊", "🏆", "⚙️", "📦"};
            labels = new String[]{"Doanh thu", "Xếp hạng", "Sân & Giá", "Kho"};
            navSlot3.setVisibility(View.VISIBLE);
        }

        TextView[] bubbles = {navBubble0, navBubble1, navBubble2, navBubble3};
        TextView[] rawIcons = {navIcon0, navIcon1, navIcon2, navIcon3};
        TextView[] rawLabels = {navLabel0, navLabel1, navLabel2, navLabel3};

        for (int i = 0; i < icons.length; i++) {
            bubbles[i].setText(icons[i]);
            rawIcons[i].setText(icons[i]);
            rawLabels[i].setText(labels[i]);

            if (i == currentTab) {
                bubbles[i].setVisibility(View.VISIBLE);
                rawIcons[i].setVisibility(View.GONE);
                rawLabels[i].setVisibility(View.VISIBLE);
            } else {
                bubbles[i].setVisibility(View.GONE);
                rawIcons[i].setVisibility(View.VISIBLE);
                rawLabels[i].setVisibility(View.GONE);
            }
        }

        // Badge đỏ báo số đơn chờ duyệt cọc ở Tab Duyệt cọc của Nhân viên
        int pendingCount = countPendingDeposits();
        if ("STAFF".equals(currentRole) && pendingCount > 0 && currentTab != 1) {
            navBadge1.setText(String.valueOf(pendingCount));
            navBadge1.setVisibility(View.VISIBLE);
        } else {
            navBadge1.setVisibility(View.GONE);
        }
    }

    // ==================== ROLE 1: KHÁCH HÀNG ====================

    /**
     * Tab 0 Khách hàng: Chọn sân 2 cột + Khung chọn Ngày, Giờ Từ - Đến + Cọc 30% VietQR
     */
    private void renderCustomerBookTab() {
        tvHeaderTitle.setText("Bạn muốn chơi\nsân nào?");
        tvHeaderSub.setText("Davies Arena • Đặt sân nhanh, cọc 30% qua VietQR");
        tvHeaderGlass.setText("🔎 Chọn sân, ngày và khung giờ bên dưới");
        tvHeaderGlass.setVisibility(View.VISIBLE);

        // 1. Thẻ trắng chứa lưới 2 cột (6 sân cầu lông)
        LinearLayout gridCard = createWhiteCard();
        gridCard.addView(buildCourtGridView(true));
        containerDynamicBody.addView(gridCard);

        // 2. Thẻ trắng chọn Ngày chơi, Giờ Từ - Đến, Tính tiền cọc 30%
        LinearLayout formCard = createWhiteCard();

        formCard.addView(createFieldLabel("Ngày chơi"));
        final TextView tvDateInput = createInputBox(selectedDateStr + "   📅");
        tvDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(DaviesArenaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                selectedDateStr = String.format(Locale.getDefault(), "%02d/%02d/%04d", month + 1, dayOfMonth, year);
                                renderScreen();
                            }
                        }, 2026, 10, 15);
                dp.show();
            }
        });
        formCard.addView(tvDateInput);

        // Hàng 2 cột: Từ (Spinner) - Đến (Spinner)
        LinearLayout rowTime = new LinearLayout(this);
        rowTime.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowTimeLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowTimeLp.topMargin = dp(6);
        rowTime.setLayoutParams(rowTimeLp);

        LinearLayout colFrom = new LinearLayout(this);
        colFrom.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams colFromLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        colFromLp.rightMargin = dp(8);
        colFrom.setLayoutParams(colFromLp);
        colFrom.addView(createFieldLabel("Từ"));

        final ArrayList<Integer> fromHours = new ArrayList<>();
        ArrayList<String> fromLabels = new ArrayList<>();
        int fromSelIdx = 0;
        for (int h = 6; h <= 21; h++) {
            if (h == selectedFromHour) fromSelIdx = fromHours.size();
            fromHours.add(h);
            fromLabels.add(String.format(Locale.getDefault(), "%02d:00", h));
        }
        Spinner spFrom = createStyledSpinner(fromLabels, fromSelIdx, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int newFrom = fromHours.get(position);
                if (newFrom != selectedFromHour) {
                    selectedFromHour = newFrom;
                    if (selectedToHour <= selectedFromHour) {
                        selectedToHour = selectedFromHour + 2;
                        if (selectedToHour > 22) selectedToHour = 22;
                    }
                    renderScreen();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        colFrom.addView(spFrom);

        LinearLayout colTo = new LinearLayout(this);
        colTo.setOrientation(LinearLayout.VERTICAL);
        colTo.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        colTo.addView(createFieldLabel("Đến"));

        final ArrayList<Integer> toHours = new ArrayList<>();
        ArrayList<String> toLabels = new ArrayList<>();
        int toSelIdx = 0;
        for (int h = selectedFromHour + 1; h <= 22; h++) {
            if (h == selectedToHour) toSelIdx = toHours.size();
            toHours.add(h);
            toLabels.add(String.format(Locale.getDefault(), "%02d:00", h));
        }
        Spinner spTo = createStyledSpinner(toLabels, toSelIdx, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int newTo = toHours.get(position);
                if (newTo != selectedToHour) {
                    selectedToHour = newTo;
                    renderScreen();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        colTo.addView(spTo);

        rowTime.addView(colFrom);
        rowTime.addView(colTo);
        formCard.addView(rowTime);

        SanCauLong selCourt = getCourtById(selectedCourtId);
        int hours = Math.max(1, selectedToHour - selectedFromHour);
        int fee = hours * selCourt.getGiaMoiGio();
        int dep = (int) (fee * 0.3);

        TextView tvNoteTime = createMintNote(String.format(Locale.getDefault(),
                "⏱ %02d:00 - %02d:00 = %d giờ  •  Đặt sân cọc trước 30%% để giữ sân",
                selectedFromHour, selectedToHour, hours));
        formCard.addView(tvNoteTime);

        formCard.addView(createKeyValueRow("Tiền sân tạm tính", formatMoney(fee), false));
        formCard.addView(createKeyValueRow("Tiền cọc bắt buộc (30%)", formatMoney(dep), true));

        AppCompatButton btnBookPay = createPrimaryPillButton("Đặt sân & Thanh toán cọc 30%");
        btnBookPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomerVietQrModal();
            }
        });
        formCard.addView(btnBookPay);

        containerDynamicBody.addView(formCard);
    }

    /**
     * Popup Quét VietQR để cọc 30% (Giống hệt Ảnh 3)
     */
    private void showCustomerVietQrModal() {
        final SanCauLong c = getCourtById(selectedCourtId);
        final int hours = Math.max(1, selectedToHour - selectedFromHour);
        final int fee = hours * c.getGiaMoiGio();
        final int dep = (int) (fee * 0.3);

        LinearLayout root = createModalContainer();
        root.addView(createModalTitle("Quét VietQR để cọc"));

        // Khung ảnh QR căn giữa + pill VietQR • Vietcombank
        ImageView ivQr = new ImageView(this);
        LinearLayout.LayoutParams qrLp = new LinearLayout.LayoutParams(dp(186), dp(186));
        qrLp.gravity = Gravity.CENTER_HORIZONTAL;
        qrLp.topMargin = dp(8);
        ivQr.setLayoutParams(qrLp);
        ivQr.setBackgroundResource(R.drawable.bg_input_field_14);
        ivQr.setPadding(dp(8), dp(8), dp(8), dp(8));
        ivQr.setImageResource(R.drawable.ic_vietqr_sample);

        String qrUrl = "https://img.vietqr.io/image/VCB-0901234567-compact2.png?amount=" + dep + "&addInfo=COC%20SAN%20" + c.getId();
        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.ic_vietqr_sample)
                .error(R.drawable.ic_vietqr_sample)
                .into(ivQr);
        root.addView(ivQr);

        TextView tvBankBadge = createStatusPill("VietQR • Vietcombank", "DA_COC");
        LinearLayout.LayoutParams badgeLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeLp.gravity = Gravity.CENTER_HORIZONTAL;
        badgeLp.topMargin = dp(8);
        badgeLp.bottomMargin = dp(10);
        tvBankBadge.setLayoutParams(badgeLp);
        root.addView(tvBankBadge);

        // Thẻ nền xanh nhạt #F0FDF9 tóm tắt Số tiền cọc, Nội dung CK, Sân / Khung giờ
        LinearLayout infoBox = new LinearLayout(this);
        infoBox.setOrientation(LinearLayout.VERTICAL);
        infoBox.setBackgroundResource(R.drawable.bg_note_mint);
        infoBox.setPadding(dp(14), dp(12), dp(14), dp(12));
        infoBox.addView(createKeyValueRow("Số tiền cọc", formatMoney(dep), true));
        infoBox.addView(createKeyValueRow("Nội dung CK", "COC SAN " + c.getId() + " 0901234567", false));
        infoBox.addView(createKeyValueRow("Sân / Khung giờ",
                String.format(Locale.getDefault(), "%s • %02d-%02dh", c.getTenSan(), selectedFromHour, selectedToHour), false));
        root.addView(infoBox);

        final AlertDialog dlg = new AlertDialog.Builder(this).setView(root).create();

        AppCompatButton btnPaid = createPrimaryPillButton("Tôi đã chuyển khoản cọc");
        btnPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gioMoc = String.format(Locale.getDefault(), "%02d:00", selectedFromHour);
                String khung = String.format(Locale.getDefault(), "2026-11-15 • %02d:00 - %02d:00 (%d giờ)",
                        selectedFromHour, selectedToHour, hours);
                LichDatSan b = new LichDatSan(
                        DataStore.nextId(), c.getId(), c.getTenSan(),
                        currentUserName, "0901234567", gioMoc, khung,
                        fee, dep, 0, "CHO_DUYET", "Nguyễn Văn Hùng", false
                );
                DataStore.listLichDat.add(0, b);
                dlg.dismiss();
                currentTab = 1;
                renderScreen();
                Toast.makeText(DaviesArenaActivity.this, "Đã tạo đơn – chờ nhân viên xác nhận cọc", Toast.LENGTH_LONG).show();
            }
        });
        root.addView(btnPaid);

        AppCompatButton btnClose = createOutlinePillButton("Đóng");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        root.addView(btnClose);

        dlg.show();
    }

    /**
     * Tab 1 Khách hàng: Lịch đặt của tôi (Giống hệt Ảnh 4)
     */
    private void renderCustomerBookingsTab() {
        tvHeaderTitle.setText("Lịch đặt của tôi");
        tvHeaderSub.setText("Theo dõi cọc và số tiền còn lại");
        tvHeaderGlass.setVisibility(View.GONE);

        if (DataStore.listLichDat.isEmpty()) {
            LinearLayout empty = createWhiteCard();
            TextView tv = new TextView(this);
            tv.setText("Chưa có lịch đặt nào.");
            tv.setTextColor(Color.parseColor("#6B7280"));
            empty.addView(tv);
            containerDynamicBody.addView(empty);
            return;
        }

        for (final LichDatSan b : DataStore.listLichDat) {
            LinearLayout card = createWhiteCard();

            // Dòng 1: Tên sân + Pill trạng thái
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvSan = new TextView(this);
            tvSan.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tvSan.setText(b.getTenSan());
            tvSan.setTextColor(Color.parseColor("#1F2937"));
            tvSan.setTextSize(16.5f);
            tvSan.setTypeface(null, Typeface.BOLD);

            TextView tvPill = createStatusPill(getBookingStatusLabel(b.getTrangThai()), b.getTrangThai());
            topRow.addView(tvSan);
            topRow.addView(tvPill);
            card.addView(topRow);

            // Dòng 2: Ngày & Khung giờ
            TextView tvTime = new TextView(this);
            tvTime.setText(b.getKhungGio());
            tvTime.setTextColor(Color.parseColor("#6B7280"));
            tvTime.setTextSize(13f);
            tvTime.setPadding(0, dp(4), 0, dp(4));
            card.addView(tvTime);

            // Dòng 3 & 4: Tiền cọc 30% & Còn lại trả tại sân
            int remain = Math.max(0, b.getTienSan() - b.getTienCoc30());
            card.addView(createKeyValueRow("Tiền cọc 30%", formatMoney(b.getTienCoc30()), false));
            card.addView(createKeyValueRow("Còn lại trả tại sân", formatMoney(remain), true));

            String st = b.getTrangThai();
            if ("CHO_DUYET".equals(st) || "DA_COC".equals(st)) {
                card.addView(createMintNote("Hủy trước ≥ 2 tiếng hoàn cọc, hủy muộn mất cọc 30%."));
                AppCompatButton btnCancel = createOutlinePillButton("Hủy lịch");
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        b.setTrangThai("DA_HUY");
                        SanCauLong s = getCourtById(b.getMaSan());
                        if (s != null && "DA_DAT".equals(s.getTrangThai())) {
                            s.setTrangThai("TRONG");
                        }
                        renderScreen();
                        Toast.makeText(DaviesArenaActivity.this, "Đã hủy lịch – hoàn cọc", Toast.LENGTH_SHORT).show();
                    }
                });
                card.addView(btnCancel);
            } else if ("HOAN_THANH".equals(st) && !b.isDaDanhGia()) {
                AppCompatButton btnRate = createAmberPillButton("⭐ Đánh giá nhân viên phục vụ");
                btnRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCustomerRateStaffModal(b);
                    }
                });
                card.addView(btnRate);
            } else if ("HOAN_THANH".equals(st) && b.isDaDanhGia()) {
                card.addView(createMintNote("✅ Bạn đã đánh giá hóa đơn này"));
            }

            containerDynamicBody.addView(card);
        }
    }

    /**
     * Tab 2 Khách hàng: Tài khoản (Giống hệt Ảnh 5)
     */
    private void renderCustomerAccountTab() {
        tvHeaderTitle.setText("Tài khoản");
        tvHeaderSub.setText("Xin chào, Khách hàng");
        tvHeaderGlass.setVisibility(View.GONE);

        LinearLayout card = createWhiteCard();
        card.addView(createKeyValueRow("Họ tên", "Nguyễn Minh Khang", false));
        card.addView(createKeyValueRow("Số điện thoại", "0901234567", false));
        card.addView(createKeyValueRow("Tổng đơn đã đặt", String.valueOf(DataStore.listLichDat.size()), false));
        containerDynamicBody.addView(card);
    }

    // ==================== ROLE 2: NHÂN VIÊN TRỰC QUẦY ====================

    /**
     * Tab 0 Nhân viên: Sơ đồ sân + 4 ô thống kê màu + Lưới 6 sân có đồng hồ đếm giây thực tế (Giống hệt Ảnh 6)
     */
    private void renderStaffCourtMapTab() {
        tvHeaderTitle.setText("Sơ đồ sân");
        tvHeaderSub.setText("Cập nhật thời gian thực");
        tvHeaderGlass.setVisibility(View.GONE);

        // Thẻ 4 ô màu: Trống (Xanh), Đã cọc (Vàng), Đang chơi (Đỏ), Bảo trì (Xám)
        int cFree = 0, cBooked = 0, cPlaying = 0, cMaint = 0;
        for (SanCauLong s : DataStore.listSan) {
            if ("TRONG".equals(s.getTrangThai())) cFree++;
            else if ("DA_DAT".equals(s.getTrangThai())) cBooked++;
            else if ("DANG_CHOI".equals(s.getTrangThai())) cPlaying++;
            else cMaint++;
        }

        LinearLayout statCard = createWhiteCard();
        LinearLayout statRow = new LinearLayout(this);
        statRow.setOrientation(LinearLayout.HORIZONTAL);
        statRow.addView(createColoredStatBox(String.valueOf(cFree), "Trống", R.drawable.bg_court_free, true));
        statRow.addView(createColoredStatBox(String.valueOf(cBooked), "Đã cọc", R.drawable.bg_court_booked, true));
        statRow.addView(createColoredStatBox(String.valueOf(cPlaying), "Đang chơi", R.drawable.bg_court_playing, true));
        statRow.addView(createColoredStatBox(String.valueOf(cMaint), "Bảo trì", R.drawable.bg_court_maint, false));
        statCard.addView(statRow);
        containerDynamicBody.addView(statCard);

        // Lưới 2 cột các sân trực quan
        containerDynamicBody.addView(buildCourtGridView(false));
    }

    /**
     * Tab 1 Nhân viên: Duyệt cọc (Giống hệt Ảnh 7)
     */
    private void renderStaffApproveDepositTab() {
        tvHeaderTitle.setText("Duyệt cọc");
        tvHeaderSub.setText("Xác nhận khách đã chuyển khoản");
        tvHeaderGlass.setVisibility(View.GONE);

        ArrayList<LichDatSan> pendingList = new ArrayList<>();
        for (LichDatSan b : DataStore.listLichDat) {
            if ("CHO_DUYET".equals(b.getTrangThai())) {
                pendingList.add(b);
            }
        }

        if (pendingList.isEmpty()) {
            LinearLayout emptyCard = createWhiteCard();
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Không có đơn nào chờ duyệt 🎉");
            tvEmpty.setTextColor(Color.parseColor("#1F2937"));
            tvEmpty.setTextSize(14.5f);
            emptyCard.addView(tvEmpty);
            containerDynamicBody.addView(emptyCard);
            return;
        }

        for (final LichDatSan b : pendingList) {
            LinearLayout card = createWhiteCard();

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvTitle = new TextView(this);
            tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tvTitle.setText(b.getTenSan() + " • " + b.getGioMoc());
            tvTitle.setTextColor(Color.parseColor("#1F2937"));
            tvTitle.setTextSize(15.5f);
            tvTitle.setTypeface(null, Typeface.BOLD);

            topRow.addView(tvTitle);
            topRow.addView(createStatusPill("Chờ xác nhận cọc", "CHO_DUYET"));
            card.addView(topRow);

            card.addView(createKeyValueRow("Nội dung CK", "COC SAN " + b.getMaSan() + " 0901234567", false));
            card.addView(createKeyValueRow("Cọc cần nhận", formatMoney(b.getTienCoc30()), true));

            AppCompatButton btnApprove = createPrimaryPillButton("Xác nhận đã nhận cọc 30%");
            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b.setTrangThai("DA_COC");
                    SanCauLong court = getCourtById(b.getMaSan());
                    if (court != null) {
                        court.setTrangThai("DA_DAT");
                    }
                    DataStore.doanhThuCocQR += b.getTienCoc30();
                    renderScreen();
                    Toast.makeText(DaviesArenaActivity.this, "Sân chuyển sang màu VÀNG (Đã đặt cọc 30%)", Toast.LENGTH_SHORT).show();
                }
            });
            card.addView(btnApprove);

            containerDynamicBody.addView(card);
        }
    }

    /**
     * Tab 2 Nhân viên: Điểm uy tín của tôi (Giống hệt Ảnh 8)
     */
    private void renderStaffReputationTab() {
        tvHeaderTitle.setText("Điểm uy tín của tôi");
        tvHeaderSub.setText("Chỉ xem – không chỉnh sửa");
        tvHeaderGlass.setVisibility(View.GONE);

        LinearLayout scoreCard = createWhiteCard();
        scoreCard.setGravity(Gravity.CENTER_HORIZONTAL);

        ArrayList<String> staffNames = new ArrayList<>();
        for (NhanVienUyTin nv : DataStore.listNhanVien) {
            staffNames.add(nv.getHoTen());
        }
        Spinner spStaff = createStyledSpinner(staffNames, selectedStaffIndex, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != selectedStaffIndex) {
                    selectedStaffIndex = position;
                    renderScreen();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        scoreCard.addView(spStaff);

        NhanVienUyTin currentStaff = DataStore.listNhanVien.get(selectedStaffIndex);
        TextView tvBigScore = new TextView(this);
        tvBigScore.setGravity(Gravity.CENTER);
        tvBigScore.setText(String.format(Locale.getDefault(), "%.1f / 5.0 ⭐", currentStaff.getDiemTrungBinh()));
        tvBigScore.setTextColor(Color.parseColor("#065F46"));
        tvBigScore.setTextSize(34f);
        tvBigScore.setTypeface(null, Typeface.BOLD);
        tvBigScore.setPadding(0, dp(10), 0, dp(2));
        scoreCard.addView(tvBigScore);

        TextView tvCount = new TextView(this);
        tvCount.setGravity(Gravity.CENTER);
        tvCount.setText(currentStaff.getSoLuotDanhGia() + " lượt đánh giá");
        tvCount.setTextColor(Color.parseColor("#6B7280"));
        tvCount.setTextSize(13f);
        scoreCard.addView(tvCount);

        containerDynamicBody.addView(scoreCard);
        containerDynamicBody.addView(createSectionHeading("Lời khen & góp ý"));

        for (DataStore.ReviewItem r : DataStore.listReview) {
            if (r.staffIndex == selectedStaffIndex) {
                containerDynamicBody.addView(buildReviewCardView(r, false));
            }
        }
    }

    // ==================== ROLE 3: CHỦ SÂN / ADMIN ====================

    /**
     * Tab 0 Admin: Tổng quan doanh thu + Biểu đồ cột theo ngày (Giống hệt Ảnh 9)
     */
    private void renderAdminRevenueTab() {
        tvHeaderTitle.setText("Tổng quan doanh thu");
        tvHeaderSub.setText("Hôm nay • tự cập nhật từ quầy");
        tvHeaderGlass.setVisibility(View.GONE);

        int totalRev = DataStore.getTongDoanhThu();

        // Thẻ xanh đậm #064E3B: Tổng doanh thu hôm nay
        LinearLayout heroCard = new LinearLayout(this);
        heroCard.setOrientation(LinearLayout.VERTICAL);
        heroCard.setBackgroundResource(R.drawable.bg_card_dark_emerald);
        heroCard.setPadding(dp(18), dp(16), dp(18), dp(16));
        LinearLayout.LayoutParams heroLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        heroLp.bottomMargin = dp(12);
        heroCard.setLayoutParams(heroLp);

        TextView tvHeroSub = new TextView(this);
        tvHeroSub.setText("Tổng doanh thu hôm nay");
        tvHeroSub.setTextColor(Color.parseColor("#D1FAE5"));
        tvHeroSub.setTextSize(12.5f);
        heroCard.addView(tvHeroSub);

        TextView tvHeroTotal = new TextView(this);
        tvHeroTotal.setText(formatMoney(totalRev));
        tvHeroTotal.setTextColor(Color.WHITE);
        tvHeroTotal.setTextSize(28f);
        tvHeroTotal.setTypeface(null, Typeface.BOLD);
        tvHeroTotal.setPadding(0, dp(2), 0, 0);
        heroCard.addView(tvHeroTotal);
        containerDynamicBody.addView(heroCard);

        // Lưới 2x2: Tiền cọc 30% qua QR | Tiền thu tại quầy | Bán nước/phụ kiện | Số đơn hoàn thành
        int doneCount = 0;
        for (LichDatSan b : DataStore.listLichDat) {
            if ("HOAN_THANH".equals(b.getTrangThai())) doneCount++;
        }

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLp.bottomMargin = dp(10);
        row1.setLayoutParams(rowLp);
        row1.addView(createKpiMiniCard("Tiền cọc 30% qua QR", formatMoney(DataStore.doanhThuCocQR), true));
        row1.addView(createKpiMiniCard("Tiền thu tại quầy", formatMoney(DataStore.doanhThuTaiQuay), false));
        containerDynamicBody.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setLayoutParams(rowLp);
        row2.addView(createKpiMiniCard("Bán nước/phụ kiện", formatMoney(DataStore.doanhThuDichVu), true));
        row2.addView(createKpiMiniCard("Số đơn hoàn thành", String.valueOf(doneCount), false));
        containerDynamicBody.addView(row2);

        // Biểu đồ Doanh thu theo ngày (nghìn đồng)
        containerDynamicBody.addView(createSectionHeading("Doanh thu theo ngày (nghìn đồng)"));
        LinearLayout chartCard = createWhiteCard();
        LinearLayout barContainer = new LinearLayout(this);
        barContainer.setOrientation(LinearLayout.HORIZONTAL);
        barContainer.setGravity(Gravity.BOTTOM);
        barContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(150)));

        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        DataStore.doanhThuTuan[6] = totalRev / 1000;
        int maxVal = 1250;
        for (int v : DataStore.doanhThuTuan) {
            if (v > maxVal) maxVal = v;
        }

        for (int i = 0; i < 7; i++) {
            int val = DataStore.doanhThuTuan[i];
            LinearLayout col = new LinearLayout(this);
            col.setOrientation(LinearLayout.VERTICAL);
            col.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams colLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            if (i < 6) colLp.rightMargin = dp(6);
            col.setLayoutParams(colLp);

            int barHeightDp = Math.max(18, (int) ((val * 96f) / maxVal));
            View bar = new View(this);
            bar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(barHeightDp)));
            bar.setBackgroundResource(i == 6 ? R.drawable.bg_court_booked : R.drawable.bg_court_free);
            col.addView(bar);

            TextView tvDay = new TextView(this);
            tvDay.setGravity(Gravity.CENTER);
            tvDay.setText(days[i]);
            tvDay.setTextColor(Color.parseColor("#6B7280"));
            tvDay.setTextSize(10.5f);
            tvDay.setPadding(0, dp(4), 0, 0);
            col.addView(tvDay);

            TextView tvVal = new TextView(this);
            tvVal.setGravity(Gravity.CENTER);
            tvVal.setText(String.valueOf(val));
            tvVal.setTextColor(Color.parseColor("#1F2937"));
            tvVal.setTextSize(10.5f);
            tvVal.setTypeface(null, Typeface.BOLD);
            col.addView(tvVal);

            barContainer.addView(col);
        }
        chartCard.addView(barContainer);
        containerDynamicBody.addView(chartCard);
    }

    /**
     * Tab 1 Admin: Bảng xếp hạng nhân viên + Lịch sử đánh giá khách hàng (Giống hệt Ảnh 10)
     */
    private void renderAdminLeaderboardTab() {
        tvHeaderTitle.setText("Bảng xếp hạng");
        tvHeaderSub.setText("Nhân viên theo số sao trung bình");
        tvHeaderGlass.setVisibility(View.GONE);

        ArrayList<NhanVienUyTin> sorted = new ArrayList<>(DataStore.listNhanVien);
        Collections.sort(sorted, new Comparator<NhanVienUyTin>() {
            @Override
            public int compare(NhanVienUyTin a, NhanVienUyTin b) {
                return Double.compare(b.getDiemTrungBinh(), a.getDiemTrungBinh());
            }
        });

        String[] medals = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < sorted.size(); i++) {
            NhanVienUyTin nv = sorted.get(i);
            LinearLayout card = createWhiteCard();
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvMedal = new TextView(this);
            tvMedal.setText(i < 3 ? medals[i] : "🏅");
            tvMedal.setTextSize(24f);
            tvMedal.setPadding(0, 0, dp(10), 0);
            row.addView(tvMedal);

            LinearLayout infoCol = new LinearLayout(this);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvRankName = new TextView(this);
            tvRankName.setText("Hạng " + (i + 1) + ": " + nv.getHoTen());
            tvRankName.setTextColor(Color.parseColor("#1F2937"));
            tvRankName.setTextSize(15f);
            tvRankName.setTypeface(null, Typeface.BOLD);
            infoCol.addView(tvRankName);

            TextView tvVotes = new TextView(this);
            tvVotes.setText(nv.getSoLuotDanhGia() + " lượt đánh giá");
            tvVotes.setTextColor(Color.parseColor("#6B7280"));
            tvVotes.setTextSize(12f);
            infoCol.addView(tvVotes);
            row.addView(infoCol);

            TextView tvAvg = new TextView(this);
            tvAvg.setText(String.format(Locale.getDefault(), "%.1f ⭐", nv.getDiemTrungBinh()));
            tvAvg.setTextColor(Color.parseColor("#065F46"));
            tvAvg.setTextSize(18f);
            tvAvg.setTypeface(null, Typeface.BOLD);
            row.addView(tvAvg);

            card.addView(row);
            containerDynamicBody.addView(card);
        }

        containerDynamicBody.addView(createSectionHeading("Lịch sử đánh giá khách hàng"));
        for (DataStore.ReviewItem r : DataStore.listReview) {
            containerDynamicBody.addView(buildReviewCardView(r, true));
        }
    }

    /**
     * Tab 2 Admin: Quản lý sân & giá (Giống hệt Ảnh 11)
     */
    private void renderAdminCourtsAndPriceTab() {
        tvHeaderTitle.setText("Quản lý sân & giá");
        tvHeaderSub.setText("Thêm sân, đổi giá, bảo trì");
        tvHeaderGlass.setVisibility(View.GONE);

        for (final SanCauLong c : DataStore.listSan) {
            LinearLayout card = createWhiteCard();

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvName = new TextView(this);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tvName.setText(c.getTenSan());
            tvName.setTextColor(Color.parseColor("#1F2937"));
            tvName.setTextSize(16f);
            tvName.setTypeface(null, Typeface.BOLD);
            topRow.addView(tvName);

            topRow.addView(createStatusPill(getCourtStatusLabel(c.getTrangThai()), c.getTrangThai()));
            card.addView(topRow);

            card.addView(createFieldLabel("Giá thuê mỗi giờ (VNĐ)"));
            final EditText edtPrice = createEditableNumberBox(String.valueOf(c.getGiaMoiGio()));
            edtPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String val = s.toString().trim();
                    if (!val.isEmpty()) {
                        try {
                            c.setGiaMoiGio(Integer.parseInt(val));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            });
            card.addView(edtPrice);

            boolean isMaint = "BAO_TRI".equals(c.getTrangThai());
            AppCompatButton btnMaint = isMaint
                    ? createPrimaryPillButton("Kết thúc bảo trì")
                    : createOutlinePillButton("Bật chế độ Bảo trì");
            btnMaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("BAO_TRI".equals(c.getTrangThai())) {
                        c.setTrangThai("TRONG");
                        renderScreen();
                    } else if ("TRONG".equals(c.getTrangThai())) {
                        c.setTrangThai("BAO_TRI");
                        renderScreen();
                    } else {
                        Toast.makeText(DaviesArenaActivity.this, "Sân đang có khách, chưa thể bảo trì", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            card.addView(btnMaint);

            containerDynamicBody.addView(card);
        }

        AppCompatButton btnAddCourt = createPrimaryPillButton("+ Thêm sân mới");
        btnAddCourt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newId = DataStore.listSan.size() + 1;
                DataStore.listSan.add(new SanCauLong(newId, "Sân " + newId, "Sân tiêu chuẩn mới", 80000, 5.0, "TRONG"));
                renderScreen();
                Toast.makeText(DaviesArenaActivity.this, "Đã thêm Sân " + newId, Toast.LENGTH_SHORT).show();
            }
        });
        containerDynamicBody.addView(btnAddCourt);
    }

    /**
     * Tab 3 Admin: Kho dịch vụ (Giống hệt Ảnh 12 + kèm hình ảnh sản phẩm trực quan)
     */
    private void renderAdminInventoryTab() {
        tvHeaderTitle.setText("Kho dịch vụ");
        tvHeaderSub.setText("Tồn kho trừ tự động khi nhân viên thêm bill");
        tvHeaderGlass.setVisibility(View.GONE);

        for (final DichVuKho item : DataStore.listDichVu) {
            LinearLayout card = createWhiteCard();

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            // Ảnh sản phẩm bo tròn nhỏ gọn + Tên mặt hàng
            CardView thumbWrap = new CardView(this);
            LinearLayout.LayoutParams thumbLp = new LinearLayout.LayoutParams(dp(40), dp(40));
            thumbLp.rightMargin = dp(10);
            thumbWrap.setLayoutParams(thumbLp);
            thumbWrap.setRadius(dp(10));
            thumbWrap.setCardElevation(0f);

            ImageView ivThumb = new ImageView(this);
            ivThumb.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ivThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivThumb.setImageResource(getServiceDrawableRes(item.getTenMon()));
            thumbWrap.addView(ivThumb);
            topRow.addView(thumbWrap);

            TextView tvTitle = new TextView(this);
            tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tvTitle.setText(item.getBieuTuong() + " " + item.getTenMon());
            tvTitle.setTextColor(Color.parseColor("#1F2937"));
            tvTitle.setTextSize(15.5f);
            tvTitle.setTypeface(null, Typeface.BOLD);
            topRow.addView(tvTitle);

            TextView tvStockPill = createStatusPill("Tồn " + item.getSoLuongTon(),
                    item.getSoLuongTon() < 20 ? "DANG_CHOI" : "DA_COC");
            topRow.addView(tvStockPill);
            card.addView(topRow);

            card.addView(createFieldLabel("Đơn giá (VNĐ)"));
            EditText edtPrice = createEditableNumberBox(String.valueOf(item.getDonGia()));
            edtPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String val = s.toString().trim();
                    if (!val.isEmpty()) {
                        try {
                            item.setDonGia(Integer.parseInt(val));
                        } catch (NumberFormatException ignored) {}
                    }
                }
            });
            card.addView(edtPrice);

            AppCompatButton btnRestock = createOutlinePillButton("+ Nhập thêm hàng");
            btnRestock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAdminRestockModal(item);
                }
            });
            card.addView(btnRestock);

            containerDynamicBody.addView(card);
        }
    }

    // ==================== MODALS VẬN HÀNH SÂN, GỌI NƯỚC CÓ ẢNH & CHẤM SAO ====================

    private void handleStaffCourtClick(final SanCauLong court) {
        String st = court.getTrangThai();
        if ("DA_DAT".equals(st)) {
            LichDatSan found = null;
            for (LichDatSan b : DataStore.listLichDat) {
                if (b.getMaSan() == court.getId() && "DA_COC".equals(b.getTrangThai())) {
                    found = b;
                    break;
                }
            }
            final LichDatSan booking = found;
            LinearLayout root = createModalContainer();
            root.addView(createModalTitle(court.getTenSan() + " – Đã đặt cọc"));
            root.addView(createMintNote(booking != null
                    ? (booking.getKhungGio() + " • Cọc " + formatMoney(booking.getTienCoc30()))
                    : "Khách đã đặt cọc 30% qua VietQR"));

            final AlertDialog dlg = new AlertDialog.Builder(this).setView(root).create();
            AppCompatButton btnCheckIn = createPrimaryPillButton("Check-in Mở sân");
            btnCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    court.setTrangThai("DANG_CHOI");
                    if (booking != null) {
                        booking.setTrangThai("DANG_CHOI");
                    }
                    dlg.dismiss();
                    renderScreen();
                    Toast.makeText(DaviesArenaActivity.this, "Đã mở sân – bắt đầu tính giờ", Toast.LENGTH_SHORT).show();
                }
            });
            root.addView(btnCheckIn);

            AppCompatButton btnClose = createOutlinePillButton("Đóng");
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
            root.addView(btnClose);
            dlg.show();

        } else if ("DANG_CHOI".equals(st)) {
            LichDatSan active = null;
            for (LichDatSan b : DataStore.listLichDat) {
                if (b.getMaSan() == court.getId() && "DANG_CHOI".equals(b.getTrangThai())) {
                    active = b;
                    break;
                }
            }
            if (active == null && !DataStore.listLichDat.isEmpty()) {
                active = DataStore.listLichDat.get(0);
            }
            if (active != null) {
                showStaffPlayingModal(court, active);
            }
        } else {
            Toast.makeText(this, court.getTenSan() + ("TRONG".equals(st) ? " đang trống" : " đang bảo trì"), Toast.LENGTH_SHORT).show();
        }
    }

    private void showStaffPlayingModal(final SanCauLong court, final LichDatSan booking) {
        LinearLayout root = createModalContainer();
        root.addView(createModalTitle(court.getTenSan() + " – Đang chơi"));

        LinearLayout redBox = new LinearLayout(this);
        redBox.setOrientation(LinearLayout.VERTICAL);
        redBox.setGravity(Gravity.CENTER);
        redBox.setBackgroundResource(R.drawable.bg_badge_bad);
        redBox.setPadding(dp(16), dp(14), dp(16), dp(14));

        TextView tvTimer = new TextView(this);
        tvTimer.setTextColor(Color.parseColor("#DC2626"));
        tvTimer.setTextSize(30f);
        tvTimer.setTypeface(null, Typeface.BOLD);
        tvTimer.setText("00:29:20");
        activeTimerViews.add(tvTimer);
        redBox.addView(tvTimer);

        TextView tvSub = new TextView(this);
        tvSub.setText(booking.getKhungGio());
        tvSub.setTextColor(Color.parseColor("#991B1B"));
        tvSub.setTextSize(12.5f);
        redBox.addView(tvSub);
        root.addView(redBox);

        root.addView(createSectionHeading("Dịch vụ đang gọi"));
        root.addView(createKeyValueRow("Tổng tiền nước & phụ kiện", formatMoney(booking.getTienDichVu()), false));

        final AlertDialog dlg = new AlertDialog.Builder(this).setView(root).create();

        AppCompatButton btnAddSvc = createOutlinePillButton("+ Thêm dịch vụ/nước uống");
        btnAddSvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                showStaffAddServiceVisualModal(court, booking);
            }
        });
        root.addView(btnAddSvc);

        AppCompatButton btnCheckout = createRedPillButton("Trả sân & Thanh toán (Check-out)");
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                showStaffCheckoutBillModal(court, booking);
            }
        });
        root.addView(btnCheckout);

        AppCompatButton btnClose = createOutlinePillButton("Đóng");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        root.addView(btnClose);

        dlg.show();
    }

    /**
     * Modal Thêm dịch vụ/nước uống có ảnh chai nước suối, Revive, cầu lông, vợt + nút [-] [+] trực quan
     */
    private void showStaffAddServiceVisualModal(final SanCauLong court, final LichDatSan booking) {
        LinearLayout root = createModalContainer();
        root.addView(createModalTitle("Thêm dịch vụ • " + court.getTenSan()));

        final AlertDialog dlg = new AlertDialog.Builder(this).setView(root).create();

        for (final DichVuKho item : DataStore.listDichVu) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, dp(8), 0, dp(8));

            CardView cvImg = new CardView(this);
            LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(dp(48), dp(48));
            imgLp.rightMargin = dp(10);
            cvImg.setLayoutParams(imgLp);
            cvImg.setRadius(dp(12));
            cvImg.setCardElevation(0f);

            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource(getServiceDrawableRes(item.getTenMon()));
            cvImg.addView(iv);
            row.addView(cvImg);

            LinearLayout info = new LinearLayout(this);
            info.setOrientation(LinearLayout.VERTICAL);
            info.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvName = new TextView(this);
            tvName.setText(item.getTenMon());
            tvName.setTextColor(Color.parseColor("#1F2937"));
            tvName.setTextSize(14f);
            tvName.setTypeface(null, Typeface.BOLD);
            info.addView(tvName);

            final TextView tvSub = new TextView(this);
            tvSub.setText(formatMoney(item.getDonGia()) + " • tồn " + item.getSoLuongTon());
            tvSub.setTextColor(Color.parseColor("#6B7280"));
            tvSub.setTextSize(12f);
            info.addView(tvSub);
            row.addView(info);

            final int[] qty = {1};
            TextView btnMinus = new TextView(this);
            btnMinus.setLayoutParams(new LinearLayout.LayoutParams(dp(30), dp(30)));
            btnMinus.setBackgroundResource(R.drawable.bg_card_soft_22);
            btnMinus.setGravity(Gravity.CENTER);
            btnMinus.setText("−");
            btnMinus.setTextSize(16f);
            btnMinus.setTypeface(null, Typeface.BOLD);

            final TextView tvQty = new TextView(this);
            tvQty.setLayoutParams(new LinearLayout.LayoutParams(dp(28), ViewGroup.LayoutParams.WRAP_CONTENT));
            tvQty.setGravity(Gravity.CENTER);
            tvQty.setText("1");
            tvQty.setTextColor(Color.parseColor("#1F2937"));
            tvQty.setTypeface(null, Typeface.BOLD);

            TextView btnPlus = new TextView(this);
            btnPlus.setLayoutParams(new LinearLayout.LayoutParams(dp(30), dp(30)));
            btnPlus.setBackgroundResource(R.drawable.bg_card_soft_22);
            btnPlus.setGravity(Gravity.CENTER);
            btnPlus.setText("+");
            btnPlus.setTextColor(Color.parseColor("#059669"));
            btnPlus.setTextSize(16f);
            btnPlus.setTypeface(null, Typeface.BOLD);

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qty[0] > 1) {
                        qty[0]--;
                        tvQty.setText(String.valueOf(qty[0]));
                    }
                }
            });

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (qty[0] < item.getSoLuongTon()) {
                        qty[0]++;
                        tvQty.setText(String.valueOf(qty[0]));
                    }
                }
            });

            TextView btnAdd = new TextView(this);
            LinearLayout.LayoutParams addLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(32));
            addLp.leftMargin = dp(6);
            btnAdd.setLayoutParams(addLp);
            btnAdd.setBackgroundResource(R.drawable.bg_pill_teal);
            btnAdd.setPadding(dp(12), 0, dp(12), 0);
            btnAdd.setGravity(Gravity.CENTER);
            btnAdd.setText("Thêm");
            btnAdd.setTextColor(Color.WHITE);
            btnAdd.setTextSize(12f);
            btnAdd.setTypeface(null, Typeface.BOLD);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getSoLuongTon() < qty[0]) {
                        Toast.makeText(DaviesArenaActivity.this, "Không đủ tồn kho!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    item.setSoLuongTon(item.getSoLuongTon() - qty[0]);
                    booking.setTienDichVu(booking.getTienDichVu() + qty[0] * item.getDonGia());
                    tvSub.setText(formatMoney(item.getDonGia()) + " • tồn " + item.getSoLuongTon());
                    Toast.makeText(DaviesArenaActivity.this, "Đã thêm " + qty[0] + " × " + item.getTenMon(), Toast.LENGTH_SHORT).show();
                }
            });

            row.addView(btnMinus);
            row.addView(tvQty);
            row.addView(btnPlus);
            row.addView(btnAdd);
            root.addView(row);
        }

        AppCompatButton btnBack = createOutlinePillButton("Quay lại");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                showStaffPlayingModal(court, booking);
            }
        });
        root.addView(btnBack);
        dlg.show();
    }

    private void showStaffCheckoutBillModal(final SanCauLong court, final LichDatSan booking) {
        LinearLayout root = createModalContainer();
        root.addView(createModalTitle("Hóa đơn chốt tiền – " + court.getTenSan()));

        final int due = booking.getTienSan() + booking.getTienDichVu() - booking.getTienCoc30();

        LinearLayout mintCard = new LinearLayout(this);
        mintCard.setOrientation(LinearLayout.VERTICAL);
        mintCard.setBackgroundResource(R.drawable.bg_note_mint);
        mintCard.setPadding(dp(14), dp(12), dp(14), dp(12));
        mintCard.addView(createKeyValueRow("[+] Tiền sân thực tế", formatMoney(booking.getTienSan()), false));
        mintCard.addView(createKeyValueRow("[+] Tiền dịch vụ đã dùng", formatMoney(booking.getTienDichVu()), false));
        mintCard.addView(createKeyValueRow("[-] Khấu trừ tiền cọc 30%", "-" + formatMoney(booking.getTienCoc30()), false));
        mintCard.addView(createKeyValueRow("[=] TỔNG CẦN THU THÊM", formatMoney(due), true));
        root.addView(mintCard);

        root.addView(createFieldLabel("Nhân viên thu ngân"));
        final ArrayList<String> staffNames = new ArrayList<>();
        for (NhanVienUyTin nv : DataStore.listNhanVien) {
            staffNames.add(nv.getHoTen());
        }
        final int[] chosenCashier = {0};
        Spinner spCashier = createStyledSpinner(staffNames, 0, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenCashier[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        root.addView(spCashier);

        final AlertDialog dlg = new AlertDialog.Builder(this).setView(root).create();

        AppCompatButton btnFinish = createPrimaryPillButton("Hoàn tất thu tiền");
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStore.doanhThuTaiQuay += (booking.getTienSan() - booking.getTienCoc30());
                DataStore.doanhThuDichVu += booking.getTienDichVu();
                booking.setTrangThai("HOAN_THANH");
                booking.setNhanVienThu(staffNames.get(chosenCashier[0]));
                court.setTrangThai("TRONG");
                dlg.dismiss();
                renderScreen();
                Toast.makeText(DaviesArenaActivity.this, "Đã thu tiền – khách có thể đánh giá hóa đơn", Toast.LENGTH_LONG).show();
            }
        });
        root.addView(btnFinish);

        AppCompatButton btnBack = createOutlinePillButton("Quay lại");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                showStaffPlayingModal(court, booking);
            }
        });
        root.addView(btnBack);
        dlg.show();
    }

    private void showCustomerRateStaffModal(final LichDatSan booking) {
        LinearLayout root = createModalContainer();
        root.addView(createModalTitle("Đánh giá nhân viên phục vụ"));

        TextView tvStaffInfo = new TextView(this);
        tvStaffInfo.setGravity(Gravity.CENTER);
        tvStaffInfo.setText("🧑‍💼 " + booking.getNhanVienThu() + "\nThu ngân ca của bạn");
        tvStaffInfo.setTextColor(Color.parseColor("#1F2937"));
        tvStaffInfo.setTextSize(14.5f);
        tvStaffInfo.setTypeface(null, Typeface.BOLD);
        root.addView(tvStaffInfo);

        final int[] selectedStars = {5};
        final LinearLayout starsRow = new LinearLayout(this);
        starsRow.setOrientation(LinearLayout.HORIZONTAL);
        starsRow.setGravity(Gravity.CENTER);
        starsRow.setPadding(0, dp(10), 0, dp(6));

        final TextView[] starViews = new TextView[5];
        for (int i = 0; i < 5; i++) {
            final int starVal = i + 1;
            TextView tvStar = new TextView(this);
            tvStar.setText("⭐");
            tvStar.setTextSize(30f);
            tvStar.setPadding(dp(4), 0, dp(4), 0);
            tvStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedStars[0] = starVal;
                    for (int k = 0; k < 5; k++) {
                        starViews[k].setAlpha(k < starVal ? 1.0f : 0.3f);
                    }
                }
            });
            starViews[i] = tvStar;
            starsRow.addView(tvStar);
        }
        root.addView(starsRow);

        root.addView(createFieldLabel("Nhận xét chi tiết"));
        final EditText edtComment = new EditText(this);
        edtComment.setBackgroundResource(R.drawable.bg_input_field_14);
        edtComment.setPadding(dp(12), dp(10), dp(12), dp(10));
        edtComment.setHint("Chia sẻ trải nghiệm của bạn...");
        edtComment.setText("Phục vụ nhiệt tình, sân sạch đẹp!");
        edtComment.setTextSize(13.5f);
        root.addView(edtComment);

        final AlertDialog dlg = new AlertDialog.Builder(this).setView(root).create();

        AppCompatButton btnSend = createPrimaryPillButton("Gửi đánh giá");
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int st = selectedStars[0];
                String cmt = edtComment.getText().toString().trim();
                booking.setDaDanhGia(true);
                int staffIdx = 0;
                for (int i = 0; i < DataStore.listNhanVien.size(); i++) {
                    NhanVienUyTin nv = DataStore.listNhanVien.get(i);
                    if (nv.getHoTen().equals(booking.getNhanVienThu())) {
                        nv.themDanhGia(st, cmt);
                        staffIdx = i;
                        break;
                    }
                }
                DataStore.listReview.add(0, new DataStore.ReviewItem(
                        staffIdx, booking.getNhanVienThu(), currentUserName, st,
                        cmt.isEmpty() ? "(Không có nhận xét)" : cmt,
                        new String[]{"Phục vụ nhiệt tình", "Sân sạch đẹp"}));
                dlg.dismiss();
                renderScreen();
                Toast.makeText(DaviesArenaActivity.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
            }
        });
        root.addView(btnSend);

        root.addView(createMintNote("Mỗi hóa đơn chỉ được đánh giá 1 lần duy nhất."));
        dlg.show();
    }

    private void showAdminRestockModal(final DichVuKho item) {
        LinearLayout root = createModalContainer();
        root.addView(createModalTitle("Nhập thêm: " + item.getTenMon()));
        root.addView(createFieldLabel("Số lượng nhập"));

        final EditText edtQty = createEditableNumberBox("10");
        root.addView(edtQty);

        final AlertDialog dlg = new AlertDialog.Builder(this).setView(root).create();

        AppCompatButton btnOk = createPrimaryPillButton("Xác nhận nhập kho");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = edtQty.getText().toString().trim();
                if (!s.isEmpty()) {
                    int add = Integer.parseInt(s);
                    if (add > 0) {
                        item.setSoLuongTon(item.getSoLuongTon() + add);
                        dlg.dismiss();
                        renderScreen();
                        Toast.makeText(DaviesArenaActivity.this, "Đã nhập thêm " + add + " " + item.getDonVi(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        root.addView(btnOk);

        AppCompatButton btnClose = createOutlinePillButton("Đóng");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        root.addView(btnClose);
        dlg.show();
    }

    // ==================== HELPER BUILDERS CHO GIAO DIỆN ĐỒNG BỘ 100% ====================

    private View buildCourtGridView(final boolean isCustomerMode) {
        LinearLayout gridWrapper = new LinearLayout(this);
        gridWrapper.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < DataStore.listSan.size(); i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i + 2 < DataStore.listSan.size()) {
                rowLp.bottomMargin = dp(12);
            }
            row.setLayoutParams(rowLp);

            row.addView(buildSingleCourtTile(DataStore.listSan.get(i), isCustomerMode, true));
            if (i + 1 < DataStore.listSan.size()) {
                row.addView(buildSingleCourtTile(DataStore.listSan.get(i + 1), isCustomerMode, false));
            } else {
                View spacer = new View(this);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));
                row.addView(spacer);
            }
            gridWrapper.addView(row);
        }
        return gridWrapper;
    }

    private View buildSingleCourtTile(final SanCauLong court, final boolean isCustomerMode, boolean hasRightMargin) {
        FrameLayout tile = new FrameLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        if (hasRightMargin) lp.rightMargin = dp(12);
        tile.setLayoutParams(lp);
        tile.setMinimumHeight(dp(110));
        tile.setPadding(dp(14), dp(12), dp(14), dp(12));

        String st = court.getTrangThai();
        if ("TRONG".equals(st)) {
            if (isCustomerMode && court.getId() == selectedCourtId) {
                tile.setBackgroundResource(R.drawable.bg_court_free_sel);
            } else {
                tile.setBackgroundResource(R.drawable.bg_court_free);
            }
        } else if ("DA_DAT".equals(st)) {
            tile.setBackgroundResource(R.drawable.bg_court_booked);
        } else if ("DANG_CHOI".equals(st)) {
            tile.setBackgroundResource(R.drawable.bg_court_playing);
        } else {
            tile.setBackgroundResource(R.drawable.bg_court_maint);
        }

        if (isCustomerMode && !"TRONG".equals(st)) {
            tile.setAlpha(0.62f);
        }

        // Hình mờ vợt cầu lông 🏸 góc dưới phải giống .ct::after
        TextView tvWatermark = new TextView(this);
        FrameLayout.LayoutParams wmLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.END);
        wmLp.rightMargin = dp(-2);
        wmLp.bottomMargin = dp(-4);
        tvWatermark.setLayoutParams(wmLp);
        tvWatermark.setText("🏸");
        tvWatermark.setTextSize(34f);
        tvWatermark.setAlpha(0.25f);
        tile.addView(tvWatermark);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView tvName = new TextView(this);
        tvName.setText(court.getTenSan());
        tvName.setTextColor(Color.WHITE);
        tvName.setTextSize(17f);
        tvName.setTypeface(null, Typeface.BOLD);
        content.addView(tvName);

        TextView tvPrice = new TextView(this);
        tvPrice.setText(formatMoney(court.getGiaMoiGio()) + "/giờ");
        tvPrice.setTextColor(Color.parseColor("#F0FDF4"));
        tvPrice.setTextSize(12.5f);
        content.addView(tvPrice);

        TextView tvTag = new TextView(this);
        LinearLayout.LayoutParams tagLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tagLp.topMargin = dp(6);
        tvTag.setLayoutParams(tagLp);
        tvTag.setBackgroundResource(R.drawable.bg_tag_translucent);
        tvTag.setPadding(dp(10), dp(3), dp(10), dp(3));
        tvTag.setText(getCourtStatusLabel(st));
        tvTag.setTextColor(Color.WHITE);
        tvTag.setTextSize(10.5f);
        tvTag.setTypeface(null, Typeface.BOLD);
        content.addView(tvTag);

        // Nếu ở màn Nhân viên và sân ĐANG CHƠI -> hiện đồng hồ đếm giây thực tế 00:29:20
        if (!isCustomerMode && "DANG_CHOI".equals(st)) {
            TextView tvTimer = new TextView(this);
            tvTimer.setPadding(0, dp(6), 0, 0);
            tvTimer.setTextColor(Color.WHITE);
            tvTimer.setTextSize(17f);
            tvTimer.setTypeface(null, Typeface.BOLD);
            tvTimer.setText("00:29:20");
            activeTimerViews.add(tvTimer);
            content.addView(tvTimer);
        }

        tile.addView(content);

        tile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCustomerMode) {
                    if ("TRONG".equals(court.getTrangThai())) {
                        selectedCourtId = court.getId();
                        renderScreen();
                    } else {
                        Toast.makeText(DaviesArenaActivity.this,
                                court.getTenSan() + " hiện " + getCourtStatusLabel(court.getTrangThai()).toLowerCase(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleStaffCourtClick(court);
                }
            }
        });

        return tile;
    }

    private View buildReviewCardView(DataStore.ReviewItem r, boolean showStaffHeader) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(r.stars <= 2 ? R.drawable.bg_review_bad : R.drawable.bg_review_good);
        card.setPadding(dp(16), dp(14), dp(14), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(12);
        card.setLayoutParams(lp);

        StringBuilder starsBuilder = new StringBuilder();
        for (int i = 0; i < r.stars; i++) starsBuilder.append("⭐");

        if (showStaffHeader) {
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvStaff = new TextView(this);
            tvStaff.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tvStaff.setText(r.staffName);
            tvStaff.setTextColor(Color.parseColor("#1F2937"));
            tvStaff.setTextSize(15f);
            tvStaff.setTypeface(null, Typeface.BOLD);
            topRow.addView(tvStaff);

            TextView tvStars = new TextView(this);
            tvStars.setText(starsBuilder.toString());
            tvStars.setTextSize(13f);
            topRow.addView(tvStars);
            card.addView(topRow);

            TextView tvReviewer = new TextView(this);
            tvReviewer.setText(r.customerName);
            tvReviewer.setTextColor(Color.parseColor("#6B7280"));
            tvReviewer.setTextSize(12f);
            tvReviewer.setPadding(0, dp(2), 0, dp(2));
            card.addView(tvReviewer);
        } else {
            TextView tvStarsAndUser = new TextView(this);
            tvStarsAndUser.setText(starsBuilder.toString() + "  • " + r.customerName);
            tvStarsAndUser.setTextColor(Color.parseColor("#4B5563"));
            tvStarsAndUser.setTextSize(12.5f);
            tvStarsAndUser.setTypeface(null, Typeface.BOLD);
            card.addView(tvStarsAndUser);
        }

        TextView tvComment = new TextView(this);
        tvComment.setText(r.comment);
        tvComment.setTextColor(Color.parseColor("#1F2937"));
        tvComment.setTextSize(14f);
        tvComment.setPadding(0, dp(4), 0, dp(4));
        card.addView(tvComment);

        if (r.chips != null && r.chips.length > 0 && !showStaffHeader) {
            LinearLayout chipRow = new LinearLayout(this);
            chipRow.setOrientation(LinearLayout.HORIZONTAL);
            chipRow.setPadding(0, dp(4), 0, 0);
            for (String chipText : r.chips) {
                TextView chip = new TextView(this);
                LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                cLp.rightMargin = dp(6);
                chip.setLayoutParams(cLp);
                chip.setBackgroundResource(R.drawable.bg_pill_teal);
                chip.setPadding(dp(12), dp(5), dp(12), dp(5));
                chip.setText(chipText);
                chip.setTextColor(Color.WHITE);
                chip.setTextSize(11.5f);
                chip.setTypeface(null, Typeface.BOLD);
                chipRow.addView(chip);
            }
            card.addView(chipRow);
        }

        if (r.stars <= 2 && showStaffHeader) {
            TextView tvWarn = new TextView(this);
            LinearLayout.LayoutParams wLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            wLp.topMargin = dp(6);
            tvWarn.setLayoutParams(wLp);
            tvWarn.setBackgroundResource(R.drawable.bg_badge_bad);
            tvWarn.setPadding(dp(10), dp(6), dp(10), dp(6));
            tvWarn.setText("⚠️ Cần xử lý khiếu nại");
            tvWarn.setTextColor(Color.parseColor("#991B1B"));
            tvWarn.setTextSize(12f);
            tvWarn.setTypeface(null, Typeface.BOLD);
            card.addView(tvWarn);
        }

        return card;
    }

    private LinearLayout createWhiteCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_soft_22);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(12);
        card.setLayoutParams(lp);
        return card;
    }

    private LinearLayout createKpiMiniCard(String label, String value, boolean rightMargin) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_soft_22);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        if (rightMargin) lp.rightMargin = dp(10);
        card.setLayoutParams(lp);

        TextView tvLbl = new TextView(this);
        tvLbl.setText(label);
        tvLbl.setTextColor(Color.parseColor("#6B7280"));
        tvLbl.setTextSize(12f);
        card.addView(tvLbl);

        TextView tvVal = new TextView(this);
        tvVal.setText(value);
        tvVal.setTextColor(Color.parseColor("#065F46"));
        tvVal.setTextSize(17.5f);
        tvVal.setTypeface(null, Typeface.BOLD);
        tvVal.setPadding(0, dp(4), 0, 0);
        card.addView(tvVal);
        return card;
    }

    private LinearLayout createColoredStatBox(String number, String label, int bgRes, boolean rightMargin) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setBackgroundResource(bgRes);
        box.setPadding(dp(4), dp(10), dp(4), dp(10));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        if (rightMargin) lp.rightMargin = dp(8);
        box.setLayoutParams(lp);

        TextView tvNum = new TextView(this);
        tvNum.setText(number);
        tvNum.setTextColor(Color.WHITE);
        tvNum.setTextSize(20f);
        tvNum.setTypeface(null, Typeface.BOLD);
        box.addView(tvNum);

        TextView tvLbl = new TextView(this);
        tvLbl.setText(label);
        tvLbl.setTextColor(Color.WHITE);
        tvLbl.setTextSize(11f);
        tvLbl.setTypeface(null, Typeface.BOLD);
        box.addView(tvLbl);
        return box;
    }

    private TextView createSectionHeading(String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextColor(Color.parseColor("#1F2937"));
        tv.setTextSize(16f);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(dp(4), dp(8), dp(4), dp(8));
        return tv;
    }

    private TextView createFieldLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#6B7280"));
        tv.setTextSize(12f);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(0, dp(8), 0, dp(4));
        return tv;
    }

    private TextView createInputBox(String text) {
        TextView tv = new TextView(this);
        tv.setBackgroundResource(R.drawable.bg_input_field_14);
        tv.setPadding(dp(12), dp(11), dp(12), dp(11));
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#1F2937"));
        tv.setTextSize(14f);
        return tv;
    }

    private EditText createEditableNumberBox(String text) {
        EditText edt = new EditText(this);
        edt.setBackgroundResource(R.drawable.bg_input_field_14);
        edt.setPadding(dp(12), dp(10), dp(12), dp(10));
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt.setText(text);
        edt.setTextColor(Color.parseColor("#1F2937"));
        edt.setTextSize(14f);
        return edt;
    }

    private Spinner createStyledSpinner(ArrayList<String> items, int selectedIdx, AdapterView.OnItemSelectedListener listener) {
        Spinner sp = new Spinner(this);
        sp.setBackgroundResource(R.drawable.bg_input_field_14);
        sp.setPadding(dp(8), dp(4), dp(8), dp(4));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        sp.setAdapter(adapter);
        if (selectedIdx >= 0 && selectedIdx < items.size()) {
            sp.setSelection(selectedIdx, false);
        }
        sp.setOnItemSelectedListener(listener);
        return sp;
    }

    private TextView createMintNote(String text) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(8);
        tv.setLayoutParams(lp);
        tv.setBackgroundResource(R.drawable.bg_note_mint);
        tv.setPadding(dp(12), dp(8), dp(12), dp(8));
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#4B5563"));
        tv.setTextSize(12f);
        return tv;
    }

    private LinearLayout createKeyValueRow(String key, String value, boolean highlightBig) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(5), 0, dp(5));

        TextView tvKey = new TextView(this);
        tvKey.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvKey.setText(key);
        tvKey.setTextColor(Color.parseColor("#1F2937"));
        tvKey.setTextSize(13.5f);

        TextView tvVal = new TextView(this);
        tvVal.setText(value);
        tvVal.setTypeface(null, Typeface.BOLD);
        if (highlightBig) {
            tvVal.setTextColor(Color.parseColor("#065F46"));
            tvVal.setTextSize(20f);
        } else {
            tvVal.setTextColor(Color.parseColor("#1F2937"));
            tvVal.setTextSize(14f);
        }

        row.addView(tvKey);
        row.addView(tvVal);
        return row;
    }

    private AppCompatButton createPrimaryPillButton(String text) {
        AppCompatButton btn = new AppCompatButton(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(46));
        lp.topMargin = dp(8);
        btn.setLayoutParams(lp);
        btn.setBackgroundResource(R.drawable.bg_pill_teal);
        btn.setStateListAnimator(null);
        btn.setAllCaps(false);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(14f);
        btn.setTypeface(null, Typeface.BOLD);
        return btn;
    }

    private AppCompatButton createOutlinePillButton(String text) {
        AppCompatButton btn = new AppCompatButton(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(44));
        lp.topMargin = dp(8);
        btn.setLayoutParams(lp);
        btn.setBackgroundResource(R.drawable.bg_btn_outline_emerald);
        btn.setStateListAnimator(null);
        btn.setAllCaps(false);
        btn.setText(text);
        btn.setTextColor(Color.parseColor("#065F46"));
        btn.setTextSize(13.5f);
        btn.setTypeface(null, Typeface.BOLD);
        return btn;
    }

    private AppCompatButton createRedPillButton(String text) {
        AppCompatButton btn = new AppCompatButton(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(46));
        lp.topMargin = dp(8);
        btn.setLayoutParams(lp);
        btn.setBackgroundResource(R.drawable.bg_btn_red_pill);
        btn.setStateListAnimator(null);
        btn.setAllCaps(false);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(14f);
        btn.setTypeface(null, Typeface.BOLD);
        return btn;
    }

    private AppCompatButton createAmberPillButton(String text) {
        AppCompatButton btn = new AppCompatButton(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(46));
        lp.topMargin = dp(8);
        btn.setLayoutParams(lp);
        btn.setBackgroundResource(R.drawable.bg_btn_amber_pill);
        btn.setStateListAnimator(null);
        btn.setAllCaps(false);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(14f);
        btn.setTypeface(null, Typeface.BOLD);
        return btn;
    }

    private TextView createStatusPill(String label, String status) {
        TextView tv = new TextView(this);
        tv.setPadding(dp(10), dp(4), dp(10), dp(4));
        tv.setText(label);
        tv.setTextSize(11f);
        tv.setTypeface(null, Typeface.BOLD);

        if ("TRONG".equals(status) || "DA_COC".equals(status)) {
            tv.setBackgroundResource(R.drawable.bg_badge_good);
            tv.setTextColor(Color.parseColor("#065F46"));
        } else if ("CHO_DUYET".equals(status) || "DA_DAT".equals(status)) {
            tv.setBackgroundResource(R.drawable.bg_badge_gold);
            tv.setTextColor(Color.parseColor("#92400E"));
        } else if ("DANG_CHOI".equals(status)) {
            tv.setBackgroundResource(R.drawable.bg_badge_bad);
            tv.setTextColor(Color.parseColor("#991B1B"));
        } else if ("HOAN_THANH".equals(status)) {
            tv.setBackgroundResource(R.drawable.bg_circle_pastel_blue);
            tv.setTextColor(Color.parseColor("#3730A3"));
        } else {
            tv.setBackgroundResource(R.drawable.bg_card_soft_22);
            tv.setTextColor(Color.parseColor("#4B5563"));
        }
        return tv;
    }

    private LinearLayout createModalContainer() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(dp(20), dp(20), dp(20), dp(20));
        return root;
    }

    private TextView createModalTitle(String title) {
        TextView tv = new TextView(this);
        tv.setText(title);
        tv.setTextColor(Color.parseColor("#1F2937"));
        tv.setTextSize(19f);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(0, 0, 0, dp(8));
        return tv;
    }

    private SanCauLong getCourtById(int id) {
        for (SanCauLong s : DataStore.listSan) {
            if (s.getId() == id) return s;
        }
        return DataStore.listSan.get(0);
    }

    private int countPendingDeposits() {
        int c = 0;
        for (LichDatSan b : DataStore.listLichDat) {
            if ("CHO_DUYET".equals(b.getTrangThai())) c++;
        }
        return c;
    }

    private String getCourtStatusLabel(String st) {
        if ("TRONG".equals(st)) return "TRỐNG";
        if ("DA_DAT".equals(st)) return "ĐÃ ĐẶT CỌC 30%";
        if ("DANG_CHOI".equals(st)) return "ĐANG CHƠI";
        return "BẢO TRÌ";
    }

    private String getBookingStatusLabel(String st) {
        if ("CHO_DUYET".equals(st)) return "Chờ xác nhận cọc";
        if ("DA_COC".equals(st)) return "Đã cọc 30%";
        if ("DANG_CHOI".equals(st)) return "Đang chơi";
        if ("HOAN_THANH".equals(st)) return "Hoàn thành (Đã Check-out)";
        return "Đã hủy (hoàn cọc)";
    }

    private int getServiceDrawableRes(String name) {
        String lower = name.toLowerCase();
        if (lower.contains("suối") || lower.contains("aquafina")) {
            return R.drawable.img_item_water;
        } else if (lower.contains("revive") || lower.contains("chanh")) {
            return R.drawable.img_item_revive;
        } else if (lower.contains("cầu")) {
            return R.drawable.img_item_shuttlecock;
        } else {
            return R.drawable.img_item_racket;
        }
    }

    private String formatMoney(int amount) {
        return String.format(Locale.US, "%,d", amount).replace(',', '.') + "đ";
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
