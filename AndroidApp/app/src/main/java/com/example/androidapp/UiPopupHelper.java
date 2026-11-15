package com.example.androidapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

/**
 * Hệ thống Popup thông báo theo ngữ cảnh (Contextual Notification Modal)
 * thay thế cho dòng chữ Toast đơn điệu ở dưới màn hình.
 */
public class UiPopupHelper {

    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_ERROR = 3;
    public static final int TYPE_INFO = 4;

    public static void showSuccess(Context context, String title, String message) {
        showContextualPopup(context, TYPE_SUCCESS, "✓", "THÀNH CÔNG", title, message, "Đã hiểu", 0, null);
    }

    public static void showSuccessWithIcon(Context context, String icon, String badgeText, String title, String message) {
        showContextualPopup(context, TYPE_SUCCESS, icon, badgeText, title, message, "Hoàn tất", 0, null);
    }

    public static void showSuccessWithAction(Context context, String icon, String badgeText,
                                             String title, String message, String buttonText,
                                             int autoDismissMs, Runnable onDone) {
        showContextualPopup(context, TYPE_SUCCESS, icon, badgeText, title, message, buttonText, autoDismissMs, onDone);
    }

    public static void showWarning(Context context, String title, String message) {
        showContextualPopup(context, TYPE_WARNING, "⚠️", "LƯU Ý", title, message, "Đã hiểu", 0, null);
    }

    public static void showWarningWithIcon(Context context, String icon, String badgeText, String title, String message) {
        showContextualPopup(context, TYPE_WARNING, icon, badgeText, title, message, "Đã hiểu", 0, null);
    }

    public static void showError(Context context, String title, String message) {
        showContextualPopup(context, TYPE_ERROR, "✕", "KHÔNG THÀNH CÔNG", title, message, "Kiểm tra lại", 0, null);
    }

    public static void showInfo(Context context, String icon, String badgeText, String title, String message) {
        showContextualPopup(context, TYPE_INFO, icon, badgeText, title, message, "Đóng", 0, null);
    }

    public static void showContextualPopup(final Context context, int type, String iconText,
                                           String badgeLabel, String title, String message,
                                           String buttonText, int autoDismissMs, final Runnable onDone) {
        if (context instanceof Activity) {
            Activity act = (Activity) context;
            if (act.isFinishing() || act.isDestroyed()) {
                if (onDone != null) onDone.run();
                return;
            }
        }

        int primaryColor;
        int softCircleColor;
        int softBoxBgColor;
        int softBoxStrokeColor;
        int btnDrawableRes;

        if (type == TYPE_SUCCESS) {
            primaryColor = Color.parseColor("#059669");
            softCircleColor = Color.parseColor("#D1FAE5");
            softBoxBgColor = Color.parseColor("#F0FDF4");
            softBoxStrokeColor = Color.parseColor("#A7F3D0");
            btnDrawableRes = R.drawable.bg_pill_teal;
        } else if (type == TYPE_WARNING) {
            primaryColor = Color.parseColor("#D97706");
            softCircleColor = Color.parseColor("#FEF3C7");
            softBoxBgColor = Color.parseColor("#FFFBEB");
            softBoxStrokeColor = Color.parseColor("#FDE68A");
            btnDrawableRes = R.drawable.bg_btn_amber_pill;
        } else if (type == TYPE_ERROR) {
            primaryColor = Color.parseColor("#DC2626");
            softCircleColor = Color.parseColor("#FEE2E2");
            softBoxBgColor = Color.parseColor("#FEF2F2");
            softBoxStrokeColor = Color.parseColor("#FECACA");
            btnDrawableRes = R.drawable.bg_btn_red_pill;
        } else {
            primaryColor = Color.parseColor("#0D9488");
            softCircleColor = Color.parseColor("#CCFBF1");
            softBoxBgColor = Color.parseColor("#F0FDFA");
            softBoxStrokeColor = Color.parseColor("#99F6E4");
            btnDrawableRes = R.drawable.bg_pill_teal;
        }

        LinearLayout outerWrap = new LinearLayout(context);
        outerWrap.setOrientation(LinearLayout.VERTICAL);
        outerWrap.setGravity(Gravity.CENTER);
        outerWrap.setPadding(dp(context, 18), dp(context, 18), dp(context, 18), dp(context, 18));

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(context, 22), dp(context, 22), dp(context, 22), dp(context, 20));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(dp(context, 24));
        cardBg.setStroke(dp(context, 1), softBoxStrokeColor);
        card.setBackground(cardBg);

        // 1. Vòng tròn biểu tượng nổi bật ở giữa trên cùng
        TextView tvIconBadge = new TextView(context);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(context, 64), dp(context, 64));
        iconLp.bottomMargin = dp(context, 10);
        tvIconBadge.setLayoutParams(iconLp);
        tvIconBadge.setGravity(Gravity.CENTER);
        tvIconBadge.setText(iconText);
        tvIconBadge.setTextColor(primaryColor);
        tvIconBadge.setTextSize(28f);
        tvIconBadge.setTypeface(null, Typeface.BOLD);

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(softCircleColor);
        circleBg.setStroke(dp(context, 2), softBoxStrokeColor);
        tvIconBadge.setBackground(circleBg);
        card.addView(tvIconBadge);

        // 2. Nhãn ngữ cảnh (Pill Tag)
        if (badgeLabel != null && !badgeLabel.isEmpty()) {
            TextView tvTag = new TextView(context);
            LinearLayout.LayoutParams tagLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tagLp.bottomMargin = dp(context, 8);
            tvTag.setLayoutParams(tagLp);
            tvTag.setPadding(dp(context, 12), dp(context, 4), dp(context, 12), dp(context, 4));
            tvTag.setText(badgeLabel);
            tvTag.setTextColor(primaryColor);
            tvTag.setTextSize(11f);
            tvTag.setTypeface(null, Typeface.BOLD);

            GradientDrawable tagBg = new GradientDrawable();
            tagBg.setColor(softCircleColor);
            tagBg.setCornerRadius(dp(context, 50));
            tvTag.setBackground(tagBg);
            card.addView(tvTag);
        }

        // 3. Tiêu đề chính
        TextView tvTitle = new TextView(context);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.parseColor("#111827"));
        tvTitle.setTextSize(18.5f);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, dp(context, 10));
        card.addView(tvTitle);

        // 4. Khung nội dung chi tiết theo ngữ cảnh
        LinearLayout msgBox = new LinearLayout(context);
        msgBox.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams boxLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        boxLp.bottomMargin = dp(context, 16);
        msgBox.setLayoutParams(boxLp);
        msgBox.setPadding(dp(context, 14), dp(context, 12), dp(context, 14), dp(context, 12));

        GradientDrawable boxBg = new GradientDrawable();
        boxBg.setColor(softBoxBgColor);
        boxBg.setCornerRadius(dp(context, 14));
        boxBg.setStroke(dp(context, 1), softBoxStrokeColor);
        msgBox.setBackground(boxBg);

        TextView tvMessage = new TextView(context);
        tvMessage.setGravity(Gravity.CENTER);
        tvMessage.setText(message);
        tvMessage.setTextColor(Color.parseColor("#374151"));
        tvMessage.setTextSize(13.8f);
        tvMessage.setLineSpacing(dp(context, 3), 1.0f);
        msgBox.addView(tvMessage);
        card.addView(msgBox);

        outerWrap.addView(card, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final AlertDialog dlg = new AlertDialog.Builder(context)
                .setView(outerWrap)
                .setCancelable(onDone == null)
                .create();

        if (dlg.getWindow() != null) {
            dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        final boolean[] completed = {false};
        final Runnable finishAction = new Runnable() {
            @Override
            public void run() {
                if (completed[0]) return;
                completed[0] = true;
                try {
                    if (dlg.isShowing()) {
                        dlg.dismiss();
                    }
                } catch (Exception ignored) {}
                if (onDone != null) {
                    onDone.run();
                }
            }
        };

        // 5. Nút bấm hành động
        AppCompatButton btnAction = new AppCompatButton(context);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(context, 46));
        btnAction.setLayoutParams(btnLp);
        btnAction.setBackgroundResource(btnDrawableRes);
        btnAction.setStateListAnimator(null);
        btnAction.setAllCaps(false);
        btnAction.setText(buttonText != null ? buttonText : "Đã hiểu");
        btnAction.setTextColor(Color.WHITE);
        btnAction.setTextSize(14.5f);
        btnAction.setTypeface(null, Typeface.BOLD);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAction.run();
            }
        });
        card.addView(btnAction);

        dlg.show();

        if (autoDismissMs > 0) {
            new Handler(Looper.getMainLooper()).postDelayed(finishAction, autoDismissMs);
        }
    }

    private static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}
