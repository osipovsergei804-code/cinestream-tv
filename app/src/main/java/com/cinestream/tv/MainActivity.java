package com.cinestream.tv;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
public class MainActivity extends Activity {
    private WebView webView;
    private FrameLayout root;
    private LinearLayout splashScreen;
    private SharedPreferences prefs;
    private static final String PREF_URL = "server_url";
    private static final String DEFAULT_URL = "http://192.168.1.147:8888/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        prefs = getSharedPreferences("cinestream", MODE_PRIVATE);
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#0a0a0f"));
        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.setVisibility(View.GONE);
        root.addView(webView);
        splashScreen = buildSplash();
        root.addView(splashScreen);
        setContentView(root);
    }
    private LinearLayout buildSplash() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        layout.setBackgroundColor(Color.parseColor("#0a0a0f"));
        layout.setPadding(100, 60, 100, 60);
        TextView logo = new TextView(this);
        logo.setText("О");
        logo.setTextColor(Color.parseColor("#0a0a0f"));
        logo.setBackgroundColor(Color.parseColor("#e8b84b"));
        logo.setTextSize(72);
        logo.setTypeface(null, Typeface.BOLD);
        logo.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams logoLP = new LinearLayout.LayoutParams(180, 180);
        logoLP.gravity = Gravity.CENTER_HORIZONTAL;
        logoLP.setMargins(0, 0, 0, 32);
        logo.setLayoutParams(logoLP);
        layout.addView(logo);
        TextView title = new TextView(this);
        title.setText("ОСИПОВ");
        title.setTextColor(Color.parseColor("#e8b84b"));
        title.setTextSize(48);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.3f);
        LinearLayout.LayoutParams titleLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleLP.setMargins(0, 0, 0, 8);
        title.setLayoutParams(titleLP);
        layout.addView(title);
        TextView sub = new TextView(this);
        sub.setText("МЕДИАСЕРВЕР");
        sub.setTextColor(Color.parseColor("#6b6880"));
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER);
        sub.setLetterSpacing(0.4f);
        LinearLayout.LayoutParams subLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLP.setMargins(0, 0, 0, 60);
        sub.setLayoutParams(subLP);
        layout.addView(sub);
        Button btnPlay = new Button(this);
        btnPlay.setText("СМОТРЕТЬ");
        btnPlay.setTextColor(Color.parseColor("#0a0a0f"));
        btnPlay.setBackgroundColor(Color.parseColor("#e8b84b"));
        btnPlay.setTextSize(20);
        btnPlay.setTypeface(null, Typeface.BOLD);
        btnPlay.setPadding(60, 30, 60, 30);
        LinearLayout.LayoutParams btnLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnLP.setMargins(0, 0, 0, 20);
        btnPlay.setLayoutParams(btnLP);
        btnPlay.setOnClickListener(v -> connect());
        layout.addView(btnPlay);
        Button btnAddr = new Button(this);
        btnAddr.setText("ИЗМЕНИТЬ АДРЕС СЕРВЕРА");
        btnAddr.setTextColor(Color.parseColor("#e8b84b"));
        btnAddr.setBackgroundColor(Color.parseColor("#1a1a24"));
        btnAddr.setTextSize(16);
        btnAddr.setPadding(60, 24, 60, 24);
        btnAddr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnAddr.setOnClickListener(v -> showAddressDialog());
        layout.addView(btnAddr);
        TextView currentAddr = new TextView(this);
        currentAddr.setText(getSavedUrl());
        currentAddr.setTextColor(Color.parseColor("#4a4860"));
        currentAddr.setTextSize(13);
        currentAddr.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams addrLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addrLP.setMargins(0, 16, 0, 0);
        currentAddr.setLayoutParams(addrLP);
        currentAddr.setTag("current_addr");
        layout.addView(currentAddr);
        return layout;
    }
    private String getSavedUrl() { return prefs.getString(PREF_URL, DEFAULT_URL); }
    private void connect() {
        splashScreen.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(getSavedUrl());
    }
    private void showAddressDialog() {
        LinearLayout dl = new LinearLayout(this);
        dl.setOrientation(LinearLayout.VERTICAL);
        dl.setPadding(60, 40, 60, 40);
        EditText input = new EditText(this);
        input.setText(getSavedUrl().replace("http://", ""));
        input.setTextSize(18);
        input.setSingleLine(true);
        dl.addView(input);
        new AlertDialog.Builder(this)
            .setTitle("Адрес сервера")
            .setView(dl)
            .setPositiveButton("Сохранить", (d, w) -> {
                String ip = input.getText().toString().trim();
                if (!ip.isEmpty()) {
                    if (!ip.startsWith("http")) ip = "http://" + ip;
                    if (!ip.endsWith("/")) ip = ip + "/";
                    prefs.edit().putString(PREF_URL, ip).apply();
                    View v = splashScreen.findViewWithTag("current_addr");
                    if (v instanceof TextView) ((TextView) v).setText(ip);
                }
            })
            .setNegativeButton("Отмена", null).show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) { webView.goBack(); return true; }
            else if (webView.getVisibility() == View.VISIBLE) { webView.setVisibility(View.GONE); splashScreen.setVisibility(View.VISIBLE); return true; }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override protected void onPause() { super.onPause(); webView.onPause(); }
    @Override protected void onResume() { super.onResume(); webView.onResume(); }
}
