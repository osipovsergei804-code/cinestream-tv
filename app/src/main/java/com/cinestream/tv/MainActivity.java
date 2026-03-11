package com.cinestream.tv;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.graphics.Color;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;

public class MainActivity extends Activity {

    private WebView webView;
    private LinearLayout setupScreen;
    private SharedPreferences prefs;
    private static final String PREF_URL = "last_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        prefs = getSharedPreferences("cinestream", MODE_PRIVATE);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0a0a0f"));

        setupScreen = buildSetupScreen();
        root.addView(setupScreen);

        webView = new WebView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(lp);
        webView.setVisibility(View.GONE);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());

        root.addView(webView);
        setContentView(root);

        String saved = prefs.getString(PREF_URL, "");
        if (!saved.isEmpty()) connect(saved);
    }

    private LinearLayout buildSetupScreen() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#0a0a0f"));
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        layout.setPadding(80, 80, 80, 80);

        TextView logo = new TextView(this);
        logo.setText("CINE STREAM");
        logo.setTextColor(Color.parseColor("#e8b84b"));
        logo.setTextSize(42);
        logo.setGravity(Gravity.CENTER);
        logo.setPadding(0, 0, 0, 16);
        layout.addView(logo);

        TextView sub = new TextView(this);
        sub.setText("Введи адрес сервера с MacBook");
        sub.setTextColor(Color.parseColor("#6b6880"));
        sub.setTextSize(16);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 0, 0, 48);
        layout.addView(sub);

        final EditText input = new EditText(this);
        String saved = prefs.getString(PREF_URL, "").replace("http://", "");
        if (!saved.isEmpty()) input.setText(saved);
        input.setHint("например: 192.168.1.15:8888");
        input.setHintTextColor(Color.parseColor("#4a4860"));
        input.setTextColor(Color.parseColor("#f0ede8"));
        input.setTextSize(20);
        input.setBackgroundColor(Color.parseColor("#1a1a24"));
        input.setPadding(32, 24, 32, 24);
        input.setGravity(Gravity.CENTER);
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_GO);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ip.setMargins(0, 0, 0, 24);
        input.setLayoutParams(ip);
        layout.addView(input);

        Button btn = new Button(this);
        btn.setText("ПОДКЛЮЧИТЬСЯ");
        btn.setTextColor(Color.parseColor("#0a0a0f"));
        btn.setBackgroundColor(Color.parseColor("#e8b84b"));
        btn.setTextSize(18);
        btn.setPadding(48, 24, 48, 24);
        btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btn.setOnClickListener(v -> { String t = input.getText().toString().trim(); if (!t.isEmpty()) connect(t); });
        input.setOnEditorActionListener((v, a, e) -> { String t = input.getText().toString().trim(); if (!t.isEmpty()) connect(t); return true; });
        layout.addView(btn);

        TextView hint = new TextView(this);
        hint.setText("Узнай адрес: ipconfig getifaddr en0");
        hint.setTextColor(Color.parseColor("#4a4860"));
        hint.setTextSize(13);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, 32, 0, 0);
        layout.addView(hint);

        return layout;
    }

    private void connect(String ip) {
        if (!ip.startsWith("http")) ip = "http://" + ip;
        prefs.edit().putString(PREF_URL, ip).apply();
        setupScreen.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(ip);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
                webView.goBack(); return true;
            } else if (webView.getVisibility() == View.VISIBLE) {
                webView.setVisibility(View.GONE);
                setupScreen.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override protected void onPause() { super.onPause(); webView.onPause(); }
    @Override protected void onResume() { super.onResume(); webView.onResume(); }
}
