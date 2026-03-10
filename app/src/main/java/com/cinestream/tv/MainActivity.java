package com.cinestream.tv;

import android.app.Activity;
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
    private String serverUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Полноэкранный режим
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Главный контейнер
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0a0a0f"));

        // Экран настройки (ввод IP)
        setupScreen = buildSetupScreen();
        root.addView(setupScreen);

        // WebView
        webView = new WebView(this);
        webView.setVisibility(View.GONE);
        webView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Скрыть экран настройки когда страница загружена
                setupScreen.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // Показать экран настройки при ошибке
                webView.setVisibility(View.GONE);
                setupScreen.setVisibility(View.VISIBLE);
            }
        });

        root.addView(webView);
        setContentView(root);
    }

    private LinearLayout buildSetupScreen() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#0a0a0f"));
        layout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ));
        layout.setPadding(80, 80, 80, 80);

        // Логотип
        TextView logo = new TextView(this);
        logo.setText("CINE STREAM");
        logo.setTextColor(Color.parseColor("#e8b84b"));
        logo.setTextSize(42);
        logo.setGravity(Gravity.CENTER);
        logo.setPadding(0, 0, 0, 16);
        layout.addView(logo);

        // Подзаголовок
        TextView sub = new TextView(this);
        sub.setText("Введи адрес сервера с MacBook");
        sub.setTextColor(Color.parseColor("#6b6880"));
        sub.setTextSize(16);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 0, 0, 48);
        layout.addView(sub);

        // Поле ввода
        final EditText input = new EditText(this);
        input.setHint("например: 192.168.1.15:8888");
        input.setHintTextColor(Color.parseColor("#4a4860"));
        input.setTextColor(Color.parseColor("#f0ede8"));
        input.setTextSize(20);
        input.setBackgroundColor(Color.parseColor("#1a1a24"));
        input.setPadding(32, 24, 32, 24);
        input.setGravity(Gravity.CENTER);
        input.setSingleLine(true);
        input.setImeOptions(EditorInfo.IME_ACTION_GO);
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        inputParams.setMargins(0, 0, 0, 24);
        input.setLayoutParams(inputParams);
        layout.addView(input);

        // Кнопка подключения
        Button btn = new Button(this);
        btn.setText("ПОДКЛЮЧИТЬСЯ");
        btn.setTextColor(Color.parseColor("#0a0a0f"));
        btn.setBackgroundColor(Color.parseColor("#e8b84b"));
        btn.setTextSize(18);
        btn.setPadding(48, 24, 48, 24);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btn.setLayoutParams(btnParams);

        btn.setOnClickListener(v -> {
            String ip = input.getText().toString().trim();
            if (!ip.isEmpty()) {
                connect(ip);
            }
        });

        input.setOnEditorActionListener((v, actionId, event) -> {
            String ip = input.getText().toString().trim();
            if (!ip.isEmpty()) {
                connect(ip);
            }
            return true;
        });

        layout.addView(btn);

        // Подсказка
        TextView hint = new TextView(this);
        hint.setText("Узнай адрес в Терминале на Mac: ipconfig getifaddr en0");
        hint.setTextColor(Color.parseColor("#4a4860"));
        hint.setTextSize(13);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, 32, 0, 0);
        layout.addView(hint);

        return layout;
    }

    private void connect(String ip) {
        if (!ip.startsWith("http")) {
            ip = "http://" + ip;
        }
        serverUrl = ip;
        webView.loadUrl(serverUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.getVisibility() == View.VISIBLE && webView.canGoBack()) {
                webView.goBack();
                return true;
            } else if (webView.getVisibility() == View.VISIBLE) {
                // Показать экран настройки
                webView.setVisibility(View.GONE);
                setupScreen.setVisibility(View.VISIBLE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }
}
