package com.example.androidapp;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.androidapp.adapter.LeaderboardAdapter;
import com.example.androidapp.adapter.SanListAdapter;
import com.example.androidapp.adapter.ServiceShopAdapter;
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

    // Views - 4 Tabs & Bottom Navigation
    private LinearLayout tabHome, tabServices, tabTimeline, tabProfile;
    private LinearLayout navTabHome, navTabServices, navTabTimeline, navTabProfile;
    private TextView tvNavHome, tvNavServices, tvNavTimeline, tvNavProfile;

    // Views - Tab 1 (Home / Sân)
    private TextView tvWelcomeUser, tvCurrentRoleBadge, tvHeroTitle, tvBannerCta;
    private TextView btnAddCourtAdmin, btnSeeAllCourts, tvCourtListHeader, tvCatServiceLabel;
    private TextView btnClearSearch, btnBellNotif, btnGridMenu;
    private EditText edtSearchCourt;
    private LinearLayout catAllCourts, catFreeCourts, catInventory;
    private ListView listViewSan;

    // Views - Tab 2 (Services / Cửa hàng nước & Phụ kiện / Kho hàng)
    private TextView tvShopTitle, tvShopSubtitle, btnAddServiceAdmin;
    private ListView listViewServices;
    private LinearLayout layoutShopCheckoutBar;
    private TextView btnShopMethodCash, btnShopMethodQR, tvShopCount, tvShopTotal;
    private Button btnShopCheckout;
    private boolean isShopPayQR = false;

    // Views - Tab 3 (Timeline / Lịch sân)
    private TextView tvTimelineHeaderTitle, tvStatFree, tvStatBooked, tvStatPlaying, tvStatMaint;
    private Button btnQuickBookFromTimeline;
    private ListView listViewTimeline;

    // Views - Tab 4 (Profile / Ca trực / Doanh thu & BXH)
    private TextView tvProfileHeaderTitle, tvProfileVipBadge, tvProfileName, tvProfileRoleSub;
    private TextView tvStatRevTotal, tvStatRevCoc, tvStatRevSvc;
    private TextView tvStatLabel1, tvStatLabel2, tvStatLabel3;
    private TextView tvProfileNoteTitle, tvProfileNoteContent, btnProfileBack;
    private LinearLayout layoutPersonalSummary, layoutAdminLeaderboard;
    private LinearLayout cardPersonalAction1, cardPersonalAction2;
    private TextView tvPersonalSectionTitle, tvCard1Title, tvCard1Sub, tvCard2Title, tvCard2Sub;
    private ListView listViewLeaderboard;
    private Button btnLogoutBack;

    // Adapters & Lists
    private ArrayList<SanCauLong> filteredSanList = new ArrayList<>();
    private SanListAdapter sanAdapter;
    private ServiceShopAdapter serviceShopAdapter;
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
        setupServiceShopActions();
        setupSearchFilter();
        setupListClickEvents();

        applyRoleUI();
        refreshAllData();
    }

    private void initViews() {
        tabHome = findViewById(R.id.tabHome);
        tabServices = findViewById(R.id.tabServices);
        tabTimeline = findViewById(R.id.tabTimeline);
        tabProfile = findViewById(R.id.tabProfile);

        navTabHome = findViewById(R.id.navTabHome);
        navTabServices = findViewById(R.id.navTabServices);
        navTabTimeline = findViewById(R.id.navTabTimeline);
        navTabProfile = findViewById(R.id.navTabProfile);

        tvNavHome = findViewById(R.id.tvNavHome);
        tvNavServices = findViewById(R.id.tvNavServices);
        tvNavTimeline = findViewById(R.id.tvNavTimeline);
        tvNavProfile = findViewById(R.id.tvNavProfile);

        // Tab 1
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvCurrentRoleBadge = findViewById(R.id.tvCurrentRoleBadge);
        tvHeroTitle = findViewById(R.id.tvHeroTitle);
        tvBannerCta = findViewById(R.id.tvBannerCta);
        btnAddCourtAdmin = findViewById(R.id.btnAddCourtAdmin);
        btnSeeAllCourts = findViewById(R.id.btnSeeAllCourts);
        tvCourtListHeader = findViewById(R.id.tvCourtListHeader);
        tvCatServiceLabel = findViewById(R.id.tvCatServiceLabel);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        btnBellNotif = findViewById(R.id.btnBellNotif);
        btnGridMenu = findViewById(R.id.btnGridMenu);
        edtSearchCourt = findViewById(R.id.edtSearchCourt);

        catAllCourts = findViewById(R.id.catAllCourts);
        catFreeCourts = findViewById(R.id.catFreeCourts);
        catInventory = findViewById(R.id.catInventory);
        listViewSan = findViewById(R.id.listViewSan);

        // Tab 2 (Services)
        tvShopTitle = findViewById(R.id.tvShopTitle);
        tvShopSubtitle = findViewById(R.id.tvShopSubtitle);
        btnAddServiceAdmin = findViewById(R.id.btnAddServiceAdmin);
        listViewServices = findViewById(R.id.listViewServices);
        layoutShopCheckoutBar = findViewById(R.id.layoutShopCheckoutBar);
        btnShopMethodCash = findViewById(R.id.btnShopMethodCash);
        btnShopMethodQR = findViewById(R.id.btnShopMethodQR);
        tvShopCount = findViewById(R.id.tvShopCount);
        tvShopTotal = findViewById(R.id.tvShopTotal);
        btnShopCheckout = findViewById(R.id.btnShopCheckout);

        // Tab 3 (Timeline)
        tvTimelineHeaderTitle = findViewById(R.id.tvTimelineHeaderTitle);
        tvStatFree = findViewById(R.id.tvStatFree);
        tvStatBooked = findViewById(R.id.tvStatBooked);
        tvStatPlaying = findViewById(R.id.tvStatPlaying);
        tvStatMaint = findViewById(R.id.tvStatMaint);
        btnQuickBookFromTimeline = findViewById(R.id.btnQuickBookFromTimeline);
        listViewTimeline = findViewById(R.id.listViewTimeline);

        // Tab 4 (Profile)
        tvProfileHeaderTitle = findViewById(R.id.tvProfileHeaderTitle);
        tvProfileVipBadge = findViewById(R.id.tvProfileVipBadge);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileRoleSub = findViewById(R.id.tvProfileRoleSub);
        tvStatRevTotal = findViewById(R.id.tvStatRevTotal);
        tvStatRevCoc = findViewById(R.id.tvStatRevCoc);
        tvStatRevSvc = findViewById(R.id.tvStatRevSvc);
        tvStatLabel1 = findViewById(R.id.tvStatLabel1);
        tvStatLabel2 = findViewById(R.id.tvStatLabel2);
        tvStatLabel3 = findViewById(R.id.tvStatLabel3);
        tvProfileNoteTitle = findViewById(R.id.tvProfileNoteTitle);
        tvProfileNoteContent = findViewById(R.id.tvProfileNoteContent);
        btnProfileBack = findViewById(R.id.btnProfileBack);

        layoutPersonalSummary = findViewById(R.id.layoutPersonalSummary);
        layoutAdminLeaderboard = findViewById(R.id.layoutAdminLeaderboard);
        cardPersonalAction1 = findViewById(R.id.cardPersonalAction1);
        cardPersonalAction2 = findViewById(R.id.cardPersonalAction2);
        tvPersonalSectionTitle = findViewById(R.id.tvPersonalSectionTitle);
        tvCard1Title = findViewById(R.id.tvCard1Title);
        tvCard1Sub = findViewById(R.id.tvCard1Sub);
        tvCard2Title = findViewById(R.id.tvCard2Title);
        tvCard2Sub = findViewById(R.id.tvCard2Sub);

        listViewLeaderboard = findViewById(R.id.listViewLeaderboard);
        btnLogoutBack = findViewById(R.id.btnLogoutBack);
    }

    private void setupAdapters() {
        filteredSanList.clear();
        filteredSanList.addAll(DataStore.listSan);

        sanAdapter = new SanListAdapter(this, filteredSanList);
        listViewSan.setAdapter(sanAdapter);

        boolean isAdmin = "ADMIN".equals(currentRole);
        serviceShopAdapter = new ServiceShopAdapter(this, DataStore.listDichVu, isAdmin, new ServiceShopAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged() {
                updateShopSummaryBar();
            }

            @Override
            public void onItemClickAdmin(DichVuKho item, int position) {
                showAdminEditServicePriceDialog(item);
            }
        });
        listViewServices.setAdapter(serviceShopAdapter);

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

        navTabServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(2);
            }
        });

        navTabTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(3);
            }
        });

        navTabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(4);
            }
        });
    }

    private void switchTab(int tabIndex) {
        tabHome.setVisibility(View.GONE);
        tabServices.setVisibility(View.GONE);
        tabTimeline.setVisibility(View.GONE);
        tabProfile.setVisibility(View.GONE);

        int muted = Color.parseColor("#64748B");
        int active = Color.parseColor("#10B981");
        tvNavHome.setTextColor(muted);
        tvNavServices.setTextColor(muted);
        tvNavTimeline.setTextColor(muted);
        tvNavProfile.setTextColor(muted);

        if (tabIndex == 1) {
            tabHome.setVisibility(View.VISIBLE);
            tvNavHome.setTextColor(active);
        } else if (tabIndex == 2) {
            tabServices.setVisibility(View.VISIBLE);
            tvNavServices.setTextColor(active);
            updateShopSummaryBar();
        } else if (tabIndex == 3) {
            tabTimeline.setVisibility(View.VISIBLE);
            tvNavTimeline.setTextColor(active);
        } else {
            tabProfile.setVisibility(View.VISIBLE);
            tvNavProfile.setTextColor(active);
        }
    }

    // ==================== TÁCH BIỆT GIAO DIỆN 3 ROLE RÕ RÀNG ====================
    private void applyRoleUI() {
        tvWelcomeUser.setText("Chào, " + currentUserName);
        tvProfileName.setText(currentUserName);

        if ("CUSTOMER".equals(currentRole)) {
            // 1. ROLE KHÁCH HÀNG: Chỉ thấy Đặt sân, Mua nước, Lịch đặt & Hồ sơ cá nhân
            tvCurrentRoleBadge.setText("Khách hàng");
            tvHeroTitle.setText("Sân Cầu Lông\nChuẩn Quốc Tế!");
            tvBannerCta.setText("Đặt sân ngay ›");
            tvCatServiceLabel.setText("Mua nước");
            tvCourtListHeader.setText("Danh sách sân");
            btnAddCourtAdmin.setVisibility(View.GONE);

            tvNavHome.setText("🏠 Đặt sân");
            tvNavServices.setText("🥤 Dịch vụ");
            tvNavTimeline.setText("📅 Lịch sân");
            tvNavProfile.setText("👤 Hồ sơ");

            // Tab Dịch vụ: Cửa hàng mua nước & phụ kiện
            tvShopTitle.setText("Nước uống & Phụ kiện");
            tvShopSubtitle.setText("Chọn số lượng [ - ] [ + ] rồi bấm Thanh toán");
            btnAddServiceAdmin.setVisibility(View.GONE);
            layoutShopCheckoutBar.setVisibility(View.VISIBLE);

            // Tab Lịch sân
            tvTimelineHeaderTitle.setText("Lịch đặt sân");
            btnQuickBookFromTimeline.setVisibility(View.VISIBLE);

            // Tab Hồ sơ cá nhân (Ẩn hoàn toàn Doanh thu CLB & BXH Nhân viên nội bộ)
            tvProfileHeaderTitle.setText("Hồ sơ cá nhân");
            tvProfileVipBadge.setText("★ VIP");
            tvProfileRoleSub.setText("📍 Davies Arena • Khách hàng thành viên");
            tvStatLabel1.setText("Lượt đặt");
            tvStatLabel2.setText("Giờ chơi");
            tvStatLabel3.setText("Hạng thẻ");
            tvProfileNoteTitle.setText("Quyền lợi thành viên");
            tvProfileNoteContent.setText("Khi đặt sân cọc trước 30% được giữ sân ưu tiên. Hoàn thành trận đấu được chấm sao nhân viên.");

            layoutPersonalSummary.setVisibility(View.VISIBLE);
            layoutAdminLeaderboard.setVisibility(View.GONE);
            tvPersonalSectionTitle.setText("Tiện ích của bạn");
            tvCard1Title.setText("Lịch đặt sân của tôi");
            tvCard1Sub.setText("Xem các khung giờ đã đặt cọc & đánh giá sao");
            tvCard2Title.setText("Mua nước suối & Phụ kiện");
            tvCard2Sub.setText("Đặt nước khoáng, Revive, ống cầu, thuê vợt");

        } else if ("STAFF".equals(currentRole)) {
            // 2. ROLE NHÂN VIÊN: Sơ đồ vận hành sân, Quầy bán nước POS, Duyệt cọc, Hồ sơ ca trực
            tvCurrentRoleBadge.setText("Nhân viên");
            tvHeroTitle.setText("Trực Quầy Thu Ngân\n& Vận Hành Sân");
            tvBannerCta.setText("Duyệt đơn cọc 30% ›");
            tvCatServiceLabel.setText("Quầy nước");
            tvCourtListHeader.setText("Sơ đồ vận hành sân");
            btnAddCourtAdmin.setVisibility(View.GONE);

            tvNavHome.setText("🏟️ Sơ đồ sân");
            tvNavServices.setText("🥤 Quầy nước");
            tvNavTimeline.setText("📋 Duyệt cọc");
            tvNavProfile.setText("👨‍💼 Ca trực");

            // Tab Dịch vụ: Quầy bán nước POS
            tvShopTitle.setText("Quầy Bán Nước & Phụ Kiện");
            tvShopSubtitle.setText("Chọn món [ - ] [ + ] để bán lẻ hoặc cộng vào sân đang chơi");
            btnAddServiceAdmin.setVisibility(View.GONE);
            layoutShopCheckoutBar.setVisibility(View.VISIBLE);

            // Tab Lịch & Duyệt cọc
            tvTimelineHeaderTitle.setText("Duyệt cọc & Check-out");
            btnQuickBookFromTimeline.setVisibility(View.GONE);

            // Tab Ca trực cá nhân
            tvProfileHeaderTitle.setText("Hồ sơ ca trực");
            tvProfileVipBadge.setText("Ca Sáng");
            tvProfileRoleSub.setText("📍 Davies Arena • Thu ngân & Điều phối sân");
            tvStatLabel1.setText("Đơn xử lý");
            tvStatLabel2.setText("Điểm uy tín");
            tvStatLabel3.setText("Đánh giá");
            tvProfileNoteTitle.setText("Nhiệm vụ ca trực");
            tvProfileNoteContent.setText("Kiểm tra kỹ biên lai VietQR cọc 30% trước khi duyệt. Phục vụ chu đáo để giữ điểm uy tín trên 4.5 ★.");

            layoutPersonalSummary.setVisibility(View.VISIBLE);
            layoutAdminLeaderboard.setVisibility(View.GONE);
            tvPersonalSectionTitle.setText("Tác vụ thu ngân nhanh");
            tvCard1Title.setText("Duyệt đơn cọc & Trả sân");
            tvCard1Sub.setText("Mở danh sách đơn chờ duyệt cọc 30% và Check-out");
            tvCard2Title.setText("Bán nước & Phụ kiện tại quầy");
            tvCard2Sub.setText("Mở menu nước suối, Revive, cầu, vợt cho khách");

        } else {
            // 3. ROLE QUẢN LÝ (ADMIN): Quản lý giá/bảo trì sân, Kho hàng, Doanh thu & BXH Nhân viên
            tvCurrentRoleBadge.setText("Quản lý");
            tvHeroTitle.setText("Quản Trị Hệ Thống\nDavies Arena");
            tvBannerCta.setText("Xem doanh thu & KPI ›");
            tvCatServiceLabel.setText("Kho hàng");
            tvCourtListHeader.setText("Quản lý danh sách sân");
            btnAddCourtAdmin.setVisibility(View.VISIBLE);

            tvNavHome.setText("⚙️ Quản lý sân");
            tvNavServices.setText("📦 Kho hàng");
            tvNavTimeline.setText("📅 Lịch đặt");
            tvNavProfile.setText("📊 Doanh thu");

            // Tab Dịch vụ: Quản lý Kho hàng (chỉnh tồn kho [-] [+] trực tiếp, bấm thẻ để sửa giá)
            tvShopTitle.setText("Quản Lý Kho Dịch Vụ");
            tvShopSubtitle.setText("Bấm [ - ] [ + ] để chỉnh tồn kho • Chạm vào món để sửa giá");
            btnAddServiceAdmin.setVisibility(View.VISIBLE);
            layoutShopCheckoutBar.setVisibility(View.GONE);

            // Tab Lịch sân
            tvTimelineHeaderTitle.setText("Toàn bộ lịch đặt sân");
            btnQuickBookFromTimeline.setVisibility(View.GONE);

            // Tab Doanh thu & BXH Uy tín nhân viên
            tvProfileHeaderTitle.setText("Doanh thu & KPI");
            tvProfileVipBadge.setText("★ ADMIN");
            tvProfileRoleSub.setText("📍 Davies Arena • Quản lý hệ thống");
            tvStatLabel1.setText("Tổng thu");
            tvStatLabel2.setText("Tiền cọc");
            tvStatLabel3.setText("Dịch vụ");
            tvProfileNoteTitle.setText("Ghi chú quản trị");
            tvProfileNoteContent.setText("Theo dõi doanh thu thời gian thực và bảng xếp hạng sao nhân viên. Cảnh báo đỏ khi nhân viên bị đánh giá <= 2 sao.");

            layoutPersonalSummary.setVisibility(View.GONE);
            layoutAdminLeaderboard.setVisibility(View.VISIBLE);
        }
    }

    private void setupCategoryActions() {
        catAllCourts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearchCourt.setText("");
                filterCourts("");
            }
        });

        catFreeCourts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filteredSanList.clear();
                for (SanCauLong san : DataStore.listSan) {
                    if ("TRONG".equals(san.getTrangThai())) {
                        filteredSanList.add(san);
                    }
                }
                sanAdapter.notifyDataSetChanged();
                Toast.makeText(DaviesArenaActivity.this, "Đang lọc các sân trống", Toast.LENGTH_SHORT).show();
            }
        });

        catInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển thẳng sang Tab 2 (Cửa hàng Dịch vụ / Kho hàng có ảnh trực quan)
                switchTab(2);
            }
        });

        btnSeeAllCourts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearchCourt.setText("");
                filterCourts("");
            }
        });

        btnProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(1);
            }
        });

        tvBannerCta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("CUSTOMER".equals(currentRole)) {
                    for (int i = 0; i < filteredSanList.size(); i++) {
                        if ("TRONG".equals(filteredSanList.get(i).getTrangThai())) {
                            showCustomerBookDialog(i);
                            return;
                        }
                    }
                    Toast.makeText(DaviesArenaActivity.this, "Hiện không còn sân trống!", Toast.LENGTH_SHORT).show();
                } else if ("STAFF".equals(currentRole)) {
                    switchTab(3);
                } else {
                    switchTab(4);
                }
            }
        });

        btnQuickBookFromTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(1);
                Toast.makeText(DaviesArenaActivity.this, "Chọn một sân trống để đặt lịch", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DaviesArenaActivity.this, "Hệ thống có " + pending + " đơn chờ duyệt cọc 30%", Toast.LENGTH_SHORT).show();
            }
        });

        btnGridMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(4);
            }
        });

        cardPersonalAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(3);
            }
        });

        cardPersonalAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(2);
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

    // ==================== CỬA HÀNG DỊCH VỤ (NƯỚC SUỐI, REVIVE, CẦU, VỢT) ====================
    private void setupServiceShopActions() {
        btnShopMethodCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShopPayQR = false;
                btnShopMethodCash.setBackgroundResource(R.drawable.bg_pill_teal);
                btnShopMethodCash.setTextColor(Color.WHITE);
                btnShopMethodQR.setBackgroundResource(R.drawable.bg_badge_good);
                btnShopMethodQR.setTextColor(Color.parseColor("#047857"));
            }
        });

        btnShopMethodQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShopPayQR = true;
                btnShopMethodQR.setBackgroundResource(R.drawable.bg_pill_teal);
                btnShopMethodQR.setTextColor(Color.WHITE);
                btnShopMethodCash.setBackgroundResource(R.drawable.bg_badge_good);
                btnShopMethodCash.setTextColor(Color.parseColor("#047857"));
            }
        });

        btnShopCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleServiceShopCheckout();
            }
        });

        btnAddServiceAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminAddServiceDialog();
            }
        });
    }

    private void updateShopSummaryBar() {
        int totalQty = 0;
        int totalMoney = 0;
        for (DichVuKho dv : DataStore.listDichVu) {
            totalQty += dv.getSoLuongChon();
            totalMoney += dv.getSoLuongChon() * dv.getDonGia();
        }
        tvShopCount.setText("Đã chọn: " + totalQty + " món");
        tvShopTotal.setText(String.format("%,dđ", totalMoney));
    }

    private void handleServiceShopCheckout() {
        int totalQty = 0;
        int totalMoney = 0;
        StringBuilder details = new StringBuilder();
        for (DichVuKho dv : DataStore.listDichVu) {
            if (dv.getSoLuongChon() > 0) {
                totalQty += dv.getSoLuongChon();
                int lineTotal = dv.getSoLuongChon() * dv.getDonGia();
                totalMoney += lineTotal;
                details.append("• ").append(dv.getTenMon())
                        .append(" x").append(dv.getSoLuongChon())
                        .append(" = ").append(String.format("%,dđ", lineTotal)).append("\n");
            }
        }

        if (totalQty == 0) {
            Toast.makeText(this, "Vui lòng bấm dấu [ + ] để chọn ít nhất 1 món!", Toast.LENGTH_SHORT).show();
            return;
        }

        final int finalTotalMoney = totalMoney;
        final String orderSummary = details.toString();

        if (!isShopPayQR) {
            // Thanh toán Tiền mặt
            new AlertDialog.Builder(this)
                    .setTitle("💵 Thanh toán Tiền mặt")
                    .setMessage(orderSummary + "\nTổng cộng: " + String.format("%,d VNĐ", finalTotalMoney))
                    .setPositiveButton("Thanh toán", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            completeServiceOrder(finalTotalMoney, "Tiền mặt");
                        }
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        } else {
            // Thanh toán Mã QR (VietQR trực quan)
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(40, 24, 40, 10);

            ImageView ivQr = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(420, 420);
            params.gravity = android.view.Gravity.CENTER_HORIZONTAL;
            ivQr.setLayoutParams(params);

            String qrUrl = "https://img.vietqr.io/image/VCB-0901234567-compact2.png?amount=" + finalTotalMoney + "&addInfo=MUA%20DICH%20VU";
            Glide.with(this)
                    .load(qrUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivQr);
            layout.addView(ivQr);

            TextView tvInfo = new TextView(this);
            tvInfo.setText(orderSummary + "\nTổng thanh toán QR: " + String.format("%,d VNĐ", finalTotalMoney));
            tvInfo.setTextColor(Color.parseColor("#1A1F36"));
            tvInfo.setTextSize(13f);
            tvInfo.setPadding(0, 14, 0, 0);
            layout.addView(tvInfo);

            new AlertDialog.Builder(this)
                    .setTitle("📱 Quét mã QR Thanh toán")
                    .setView(layout)
                    .setPositiveButton("Thanh toán", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            completeServiceOrder(finalTotalMoney, "Mã QR");
                        }
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        }
    }

    private void completeServiceOrder(int totalMoney, String method) {
        for (DichVuKho dv : DataStore.listDichVu) {
            if (dv.getSoLuongChon() > 0) {
                dv.setSoLuongTon(Math.max(0, dv.getSoLuongTon() - dv.getSoLuongChon()));
                dv.setSoLuongChon(0);
            }
        }
        DataStore.doanhThuDichVu += totalMoney;

        // Nếu có sân đang chơi, cộng vào hóa đơn dịch vụ hoặc tính doanh thu lẻ
        serviceShopAdapter.notifyDataSetChanged();
        updateShopSummaryBar();
        refreshAllData();
        Toast.makeText(this, "Đã thanh toán " + String.format("%,dđ", totalMoney) + " (" + method + ") thành công!", Toast.LENGTH_LONG).show();
    }

    private void showAdminEditServicePriceDialog(final DichVuKho item) {
        final EditText edtPrice = new EditText(this);
        edtPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtPrice.setText(String.valueOf(item.getDonGia()));

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh giá: " + item.getTenMon())
                .setMessage("Nhập đơn giá bán mới (VNĐ/" + item.getDonVi() + "):")
                .setView(edtPrice)
                .setPositiveButton("Lưu giá mới", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String val = edtPrice.getText().toString().trim();
                        if (!val.isEmpty()) {
                            item.setDonGia(Integer.parseInt(val));
                            serviceShopAdapter.notifyDataSetChanged();
                            Toast.makeText(DaviesArenaActivity.this, "Đã cập nhật giá " + item.getTenMon(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAdminAddServiceDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(45, 25, 45, 10);

        final EditText edtName = new EditText(this);
        edtName.setHint("Tên mặt hàng (VD: Nước tăng lực RedBull)");
        layout.addView(edtName);

        final EditText edtPrice = new EditText(this);
        edtPrice.setHint("Đơn giá (VD: 15000)");
        edtPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(edtPrice);

        new AlertDialog.Builder(this)
                .setTitle("+ Thêm Mặt Hàng Mới")
                .setView(layout)
                .setPositiveButton("Thêm vào kho", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edtName.getText().toString().trim();
                        String priceStr = edtPrice.getText().toString().trim();
                        if (!name.isEmpty() && !priceStr.isEmpty()) {
                            int id = DataStore.listDichVu.size() + 1;
                            DataStore.listDichVu.add(new DichVuKho(id, name, Integer.parseInt(priceStr), "Lon", 50, "🥤"));
                            serviceShopAdapter.notifyDataSetChanged();
                            Toast.makeText(DaviesArenaActivity.this, "Đã thêm " + name, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
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
                        Toast.makeText(DaviesArenaActivity.this, san.getTenSan() + " hiện không trống", Toast.LENGTH_SHORT).show();
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

        // 3. Sự kiện bấm vào nhân viên trên Bảng Xếp Hạng (Admin)
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

    // ==================== POPUP ĐẶT SÂN ĐẦU TƯ TRỰC QUAN (CHỌN CA / ĐỒNG HỒ TIMEPICKER) ====================
    private void showCustomerBookDialog(int position) {
        final SanCauLong san = filteredSanList.get(position);

        View dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_book_court, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dlgView)
                .create();

        TextView tvDlgCourtName = dlgView.findViewById(R.id.tvDlgCourtName);
        TextView tvDlgCourtPrice = dlgView.findViewById(R.id.tvDlgCourtPrice);
        final TextView chipSlot1 = dlgView.findViewById(R.id.chipSlot1);
        final TextView chipSlot2 = dlgView.findViewById(R.id.chipSlot2);
        final TextView chipSlot3 = dlgView.findViewById(R.id.chipSlot3);
        final TextView btnPickCustomTime = dlgView.findViewById(R.id.btnPickCustomTime);
        final TextView tvSelectedSlotInfo = dlgView.findViewById(R.id.tvSelectedSlotInfo);
        final EditText edtBookingNote = dlgView.findViewById(R.id.edtBookingNote);
        final TextView btnPayMethodQR = dlgView.findViewById(R.id.btnPayMethodQR);
        final TextView btnPayMethodCash = dlgView.findViewById(R.id.btnPayMethodCash);
        final ImageView imgBookingQr = dlgView.findViewById(R.id.imgBookingQr);
        final TextView tvDlgTotalAmount = dlgView.findViewById(R.id.tvDlgTotalAmount);
        Button btnCancelBooking = dlgView.findViewById(R.id.btnCancelBooking);
        Button btnConfirmPayBooking = dlgView.findViewById(R.id.btnConfirmPayBooking);

        tvDlgCourtName.setText(san.getTenSan());
        tvDlgCourtPrice.setText(String.format("Đơn giá: %,dđ / giờ", san.getGiaMoiGio()));

        // Mặc định ca 17:00 - 19:00 (2 giờ)
        final int[] selectedStartHour = {17};
        final int[] selectedStartMin = {0};
        final int[] selectedDuration = {2};
        final boolean[] payByQR = {true};

        final Runnable updatePriceAndQr = new Runnable() {
            @Override
            public void run() {
                int tienSan = selectedDuration[0] * san.getGiaMoiGio();
                int tienCoc = (int) (tienSan * 0.3);
                String timeSlotStr = String.format("%02d:%02d - %02d:%02d (%d giờ)",
                        selectedStartHour[0], selectedStartMin[0],
                        (selectedStartHour[0] + selectedDuration[0]) % 24, selectedStartMin[0],
                        selectedDuration[0]);
                tvSelectedSlotInfo.setText("✓ Đã chọn: " + timeSlotStr);
                tvDlgTotalAmount.setText(String.format("Tổng sân: %,dđ  •  Cọc 30%%: %,dđ", tienSan, tienCoc));

                if (payByQR[0]) {
                    imgBookingQr.setVisibility(View.VISIBLE);
                    String qrUrl = "https://img.vietqr.io/image/VCB-0901234567-compact2.png?amount=" + tienCoc + "&addInfo=COC%20SAN%20" + san.getId();
                    Glide.with(DaviesArenaActivity.this)
                            .load(qrUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(imgBookingQr);
                } else {
                    imgBookingQr.setVisibility(View.GONE);
                }
            }
        };

        final Runnable resetChipStyles = new Runnable() {
            @Override
            public void run() {
                chipSlot1.setBackgroundResource(R.drawable.bg_card_soft_22);
                chipSlot1.setTextColor(Color.parseColor("#1A1F36"));
                chipSlot2.setBackgroundResource(R.drawable.bg_card_soft_22);
                chipSlot2.setTextColor(Color.parseColor("#1A1F36"));
                chipSlot3.setBackgroundResource(R.drawable.bg_card_soft_22);
                chipSlot3.setTextColor(Color.parseColor("#1A1F36"));
                btnPickCustomTime.setBackgroundResource(R.drawable.bg_badge_gold);
                btnPickCustomTime.setTextColor(Color.parseColor("#D97706"));
            }
        };

        chipSlot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChipStyles.run();
                chipSlot1.setBackgroundResource(R.drawable.bg_pill_teal);
                chipSlot1.setTextColor(Color.WHITE);
                selectedStartHour[0] = 7;
                selectedStartMin[0] = 0;
                selectedDuration[0] = 2;
                updatePriceAndQr.run();
            }
        });

        chipSlot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChipStyles.run();
                chipSlot2.setBackgroundResource(R.drawable.bg_pill_teal);
                chipSlot2.setTextColor(Color.WHITE);
                selectedStartHour[0] = 17;
                selectedStartMin[0] = 0;
                selectedDuration[0] = 2;
                updatePriceAndQr.run();
            }
        });

        chipSlot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChipStyles.run();
                chipSlot3.setBackgroundResource(R.drawable.bg_pill_teal);
                chipSlot3.setTextColor(Color.WHITE);
                selectedStartHour[0] = 19;
                selectedStartMin[0] = 0;
                selectedDuration[0] = 2;
                updatePriceAndQr.run();
            }
        });

        // Mở đồng hồ TimePickerDialog chuẩn Android để không ai nhập bậy được
        btnPickCustomTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog picker = new TimePickerDialog(DaviesArenaActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                resetChipStyles.run();
                                btnPickCustomTime.setBackgroundResource(R.drawable.bg_pill_teal);
                                btnPickCustomTime.setTextColor(Color.WHITE);
                                selectedStartHour[0] = hourOfDay;
                                selectedStartMin[0] = minute;
                                selectedDuration[0] = 2;
                                updatePriceAndQr.run();
                            }
                        }, selectedStartHour[0], selectedStartMin[0], true);
                picker.setTitle("Chọn giờ bắt đầu chơi");
                picker.show();
            }
        });

        btnPayMethodQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payByQR[0] = true;
                btnPayMethodQR.setBackgroundResource(R.drawable.bg_pill_teal);
                btnPayMethodQR.setTextColor(Color.WHITE);
                btnPayMethodCash.setBackgroundResource(R.drawable.bg_card_soft_22);
                btnPayMethodCash.setTextColor(Color.parseColor("#1A1F36"));
                updatePriceAndQr.run();
            }
        });

        btnPayMethodCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payByQR[0] = false;
                btnPayMethodCash.setBackgroundResource(R.drawable.bg_pill_teal);
                btnPayMethodCash.setTextColor(Color.WHITE);
                btnPayMethodQR.setBackgroundResource(R.drawable.bg_card_soft_22);
                btnPayMethodQR.setTextColor(Color.parseColor("#1A1F36"));
                updatePriceAndQr.run();
            }
        });

        btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirmPayBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tienSan = selectedDuration[0] * san.getGiaMoiGio();
                int tienCoc = (int) (tienSan * 0.3);
                String gioMoc = String.format("%02d:%02d", selectedStartHour[0], selectedStartMin[0]);
                String khung = String.format("%02d:%02d - %02d:%02d (%d giờ)",
                        selectedStartHour[0], selectedStartMin[0],
                        (selectedStartHour[0] + selectedDuration[0]) % 24, selectedStartMin[0],
                        selectedDuration[0]);
                String note = edtBookingNote.getText().toString().trim();
                if (!note.isEmpty()) {
                    khung += " • " + note;
                }

                LichDatSan newBooking = new LichDatSan(
                        DataStore.nextId(), san.getId(), san.getTenSan(),
                        currentUserName, "0901234567", gioMoc, khung,
                        tienSan, tienCoc, 0, "CHO_DUYET", "Nguyễn Văn Hùng", false
                );
                DataStore.listLichDat.add(0, newBooking);
                dialog.dismiss();
                refreshAllData();
                switchTab(3);
                Toast.makeText(DaviesArenaActivity.this,
                        "Đã thanh toán đặt sân thành công! Lịch đã chuyển sang chờ duyệt.",
                        Toast.LENGTH_LONG).show();
            }
        });

        updatePriceAndQr.run();
        dialog.show();
    }

    // ==================== NGHIỆP VỤ ROLE 2: NHÂN VIÊN TRỰC QUẦY ====================
    private void handleStaffCourtTap(final SanCauLong san) {
        String st = san.getTrangThai();
        if ("TRONG".equals(st)) {
            new AlertDialog.Builder(this)
                    .setTitle(san.getTenSan() + " (Đang trống)")
                    .setMessage("Mở sân ngay cho khách vãng lai đến chơi trực tiếp?")
                    .setPositiveButton("Check-in Mở Sân", new DialogInterface.OnClickListener() {
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
                            Toast.makeText(DaviesArenaActivity.this, "Đã mở " + san.getTenSan() + "!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(DaviesArenaActivity.this, "Đã Check-in nhận sân!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Không tìm thấy đơn chơi trên sân này.", Toast.LENGTH_SHORT).show();
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
                .setPositiveButton("Trả Sân & Thanh Toán", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCheckoutBillDialog(booking);
                    }
                })
                .setNeutralButton("+ Thêm Nước / Cầu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchTab(2);
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showCheckoutBillDialog(final LichDatSan booking) {
        int canThuThem = booking.getTienSan() + booking.getTienDichVu() - booking.getTienCoc30();
        String msg = "[+] Tiền sân thực tế: " + String.format("%,d VNĐ", booking.getTienSan()) + "\n"
                + "[+] Tiền nước & phụ kiện: " + String.format("%,d VNĐ", booking.getTienDichVu()) + "\n"
                + "[-] Khấu trừ tiền cọc 30%: -" + String.format("%,d VNĐ", booking.getTienCoc30()) + "\n"
                + "─────────────────────────\n"
                + "[=] TỔNG CẦN THU THÊM: " + String.format("%,d VNĐ", canThuThem) + "\n\n"
                + "👨‍💼 Thu ngân: " + (currentRole.equals("STAFF") ? currentUserName : "Nguyễn Văn Hùng");

        new AlertDialog.Builder(this)
                .setTitle("Thanh Toán Trả Sân (Check-out)")
                .setMessage(msg)
                .setPositiveButton("Thanh toán", new DialogInterface.OnClickListener() {
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
                        Toast.makeText(DaviesArenaActivity.this, "Đã thanh toán xong! Sân đã trống, khách có thể chấm sao.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(DaviesArenaActivity.this, "Đã duyệt cọc 30%!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Đơn đang chờ nhân viên quầy xác nhận cọc 30%.", Toast.LENGTH_SHORT).show();
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
                        .setMessage(booking.getTenSan() + " (" + booking.getKhungGio() + ")\nLưu ý: Hủy trước >= 2 tiếng được hoàn cọc, hủy muộn mất cọc 30%.")
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
                Toast.makeText(this, "Hóa đơn này đã được đánh giá sao!", Toast.LENGTH_SHORT).show();
            } else {
                showRatingStaffDialog(booking);
            }
        }
    }

    // ==================== CHẤM SAO NHÂN VIÊN ====================
    private void showRatingStaffDialog(final LichDatSan booking) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(45, 25, 45, 10);

        TextView tvStaff = new TextView(this);
        tvStaff.setText("Nhân viên phục vụ: " + booking.getNhanVienThu() + "\nHóa đơn: " + booking.getTenSan());
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
                .setTitle("⭐ Đánh Giá Nhân Viên")
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
                        Toast.makeText(DaviesArenaActivity.this, "Cảm ơn bạn đã gửi " + stars + " ⭐ cho " + booking.getNhanVienThu() + "!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ==================== ROLE 3 ADMIN: SỬA GIÁ SÂN & THÊM SÂN ====================
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
        if (serviceShopAdapter != null) {
            serviceShopAdapter.notifyDataSetChanged();
        }
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
        tvStatFree.setText("Trống: " + cFree);
        tvStatBooked.setText("Đã cọc: " + cBooked);
        tvStatPlaying.setText("Đang chơi: " + cPlaying);
        tvStatMaint.setText("Bảo trì: " + cMaint);

        // Cập nhật 3 ô thống kê trong Tab 4 theo đúng Role
        if ("CUSTOMER".equals(currentRole)) {
            int myBookings = DataStore.listLichDat.size();
            tvStatRevTotal.setText(String.format("%02d", myBookings));
            tvStatRevCoc.setText((myBookings * 2) + "h");
            tvStatRevSvc.setText("VIP");
        } else if ("STAFF".equals(currentRole)) {
            tvStatRevTotal.setText(DataStore.listLichDat.size() + " đơn");
            tvStatRevCoc.setText("4.9 ★");
            tvStatRevSvc.setText("20 lượt");
        } else {
            tvStatRevTotal.setText(String.format("%,dk", DataStore.getTongDoanhThu() / 1000));
            tvStatRevCoc.setText(String.format("%,dk", DataStore.doanhThuCocQR / 1000));
            tvStatRevSvc.setText(String.format("%,dk", DataStore.doanhThuDichVu / 1000));
        }
    }
}
