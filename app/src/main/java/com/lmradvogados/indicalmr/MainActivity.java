package com.lmradvogados.indicalmr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private WebView webview;
    private NavigationManager navigationManager;
    private TextView txtSaudacao, txtMensagem;
    private ImageView imgErro, imgCarregamento;
    private Button btTentar;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webview = findViewById(R.id.webview);
        txtSaudacao = findViewById(R.id.txtSaudacao);
        txtMensagem = findViewById(R.id.txtMensagem);
        imgErro = findViewById(R.id.imgErro);
        imgCarregamento = findViewById(R.id.imgCarregamento);
        btTentar = findViewById(R.id.btTentar);
        rootView = findViewById(R.id.main);

        navigationManager = new NavigationManager(webview, getApplicationContext());
        WebSettings config = webview.getSettings();
        config.setJavaScriptEnabled(true);
        config.setDomStorageEnabled(true);
        config.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        config.setMediaPlaybackRequiresUserGesture(false);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(verificarConexao()){
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.setAcceptCookie(true);
                    cookieManager.acceptCookie();
                    cookieManager.flush();
                    exibirWebView();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (error.getErrorCode() == ERROR_CONNECT || error.getErrorCode() == ERROR_HOST_LOOKUP) {
                    exibirPaginaErro();
                }
            }
        });

        webview.setWebChromeClient(new WebChromeClient());
        navigationManager.carregarUrl("https://indica.lmradvogados.com.br/");

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight = r.bottom;

                if(keypadHeight > screenHeight * 0.15){
                    webview.setLayoutParams(new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            r.height()
                    ));
                }else{
                    webview.setLayoutParams(new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.MATCH_PARENT
                    ));
                }
            }
        });

        btTentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificarConexao()){
                    navigationManager.recarregar();
                    exibirCarregamento();
                }else{
                    exibirPaginaErro();
                }
            }
        });

        if(verificarConexao()){
            exibirWebView();
        }else{
            exibirPaginaErro();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if(navigationManager != null){
            navigationManager.voltarUrl(1, this);
        }
    }

    private boolean verificarConexao(){
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void exibirCarregamento(){
        iniciarAnim();
        imgCarregamento.setVisibility(View.VISIBLE);
        txtMensagem.setVisibility(View.GONE);
        btTentar.setVisibility(View.GONE);
    }

    private void exibirPaginaErro(){
        pararAnim();
        webview.setVisibility(View.GONE);
        imgCarregamento.setVisibility(View.GONE);
        txtSaudacao.setVisibility(View.VISIBLE);
        txtMensagem.setVisibility(View.VISIBLE);
        imgErro.setVisibility(View.VISIBLE);
        btTentar.setVisibility(View.VISIBLE);
    }

    private void exibirWebView(){
        pararAnim();
        webview.setVisibility(View.VISIBLE);
        txtSaudacao.setVisibility(View.GONE);
        txtMensagem.setVisibility(View.GONE);
        imgErro.setVisibility(View.GONE);
        imgCarregamento.setVisibility(View.GONE);
        btTentar.setVisibility(View.GONE);
    }

    private void iniciarAnim(){
        txtSaudacao.setText(R.string.saudacao_aguarde);
        RotateAnimation rotateAnimation = new RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
        );
        rotateAnimation.setDuration(2000); // Tempo de duração da animação em milissegundos
        rotateAnimation.setRepeatCount(Animation.INFINITE); // Repetir a animação indefinidamente

        imgCarregamento.startAnimation(rotateAnimation);
    }

    private void pararAnim(){
        txtSaudacao.setText(R.string.saudacao_erro);
        imgCarregamento.clearAnimation();
    }
}