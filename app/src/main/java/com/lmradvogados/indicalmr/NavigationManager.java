package com.lmradvogados.indicalmr;
import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;
import android.widget.Toast;

public class NavigationManager {
    WebView webView;
    Context contexto;

    NavigationManager(WebView webView, Context contexto){
        this.webView = webView;
        this.contexto = contexto;
    }

    public void carregarUrl(String url){
        if(!url.startsWith("https://")){
            url = "https://" + url;
        }
        webView.loadUrl(url);
    }

    public void voltarUrl(int id, Activity activity){
        if(webView.canGoBack() && (id == 0 || id == 1)){
            webView.goBack();
        }else if(!webView.canGoBack() && id == 0){
            Toast.makeText(contexto, "Não é possível voltar mais", Toast.LENGTH_SHORT).show();
        }else if(!webView.canGoBack() && id == 1){
            activity.finish();
        }
    }

    public void recarregar(){
        webView.reload();
    }
}
