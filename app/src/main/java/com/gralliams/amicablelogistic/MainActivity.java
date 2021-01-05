package com.gralliams.amicablelogistic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    private String webUrl = "https://amicablelogistic.com";
    ProgressBar progressBarWeb;
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;
    Button btnNoInternetConnection;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView loadingAnimation;
    TextView connectionMessage;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         webView =  findViewById(R.id.myWebView);
         progressBarWeb =  findViewById(R.id.progressBar);
         loadingAnimation = findViewById(R.id.animationView);
         connectionMessage = findViewById(R.id.txtNoConnection);
        //TODO: LOTTIE START

//         progressDialog = new ProgressDialog(this);
//         progressDialog.setMessage("Loading Please Wait");

         btnNoInternetConnection = findViewById(R.id.btnNoConnection);
         relativeLayout =  findViewById(R.id.relativeLayout);
         swipeRefreshLayout =  findViewById(R.id.swipeRefreshLayout);

         swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.YELLOW,Color.GREEN);

         swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
             @Override
             public void onRefresh() {
                 checkConnection();
             }
         });



             webView.getSettings().setJavaScriptEnabled(true);
             webView.getSettings().setAllowContentAccess(false);
             webView.getSettings().setSupportZoom(false);
             webView.getSettings().setAppCacheEnabled(true);
             webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK);
             webView.getSettings().setDomStorageEnabled(true);
             checkConnection();




         //Solved WebView SwipeUp Problem
         webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
             @Override
             public void onScrollChanged() {
                 if (webView.getScrollY() == 0) {
                     swipeRefreshLayout.setEnabled(true);
                 } else {
                     swipeRefreshLayout.setEnabled(false);
                 }
             }
         });


         webView.setWebViewClient(new WebViewClient(){
             @Override
             public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                 super.onReceivedError(view, request, error);
//                 Toast.makeText(MainActivity.this, "Failed to load data, swipe down to refresh", Toast.LENGTH_SHORT).show();
                 webView.loadUrl("about:blank");
             checkConnection();
             }

             @Override
             public void onPageStarted(WebView view, String url, Bitmap favicon) {
                 super.onPageStarted(view, url, favicon);
                 loadingAnimation.setVisibility(View.VISIBLE);
             }

             @Override
             public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                 super.onReceivedSslError(view, handler, error);
                 handler.cancel();
             }

             @Override
             public void onPageFinished(WebView view, String url) {
                 swipeRefreshLayout.setRefreshing(false);
                 loadingAnimation.cancelAnimation();
                 loadingAnimation.setVisibility(View.GONE);
                 super.onPageFinished(view, url);
             }

             @Override
             public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                 if (Uri.parse(url).getHost().equals(webUrl)) {
//                     return false;
//                 }
//                 else {
//                     webView.setVisibility(View.GONE);
//                     connectionMessage.setText(R.string.error_msg);
//                     relativeLayout.setVisibility(View.VISIBLE);
//                 return true;
//                 }
                 view.loadUrl(url);
                 return true;
             }
         });

         webView.setWebChromeClient(new WebChromeClient(){

             @Override
             public void onProgressChanged(WebView view, int newProgress) {

                 progressBarWeb.setVisibility(View.VISIBLE);
                 progressBarWeb.setProgress(newProgress);
                 loadingAnimation.setVisibility(View.VISIBLE);
                 loadingAnimation.playAnimation();
//                 setTitle("Loading...");
//                 progressDialog.show();
                 if(newProgress == 100){

                     progressBarWeb.setVisibility(View.GONE);
                     loadingAnimation.cancelAnimation();
                     loadingAnimation.setVisibility(View.GONE);
//                     setTitle(view.getTitle());
//                     progressDialog.dismiss();

                 }


                 super.onProgressChanged(view, newProgress);
             }
         });




         btnNoInternetConnection.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 checkConnection();
             }
         });


     }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Exit?")
                    .setNegativeButton("No",null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finishAffinity();
                        }
                    }).show();
        }
    }

    public void checkConnection(){

        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if(wifi.isConnected()){
            webView.loadUrl(webUrl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            loadingAnimation.cancelAnimation();
            loadingAnimation.setVisibility(View.GONE);

        }
        else if (mobileNetwork.isConnected()){
            webView.loadUrl(webUrl);
            webView.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);
            loadingAnimation.cancelAnimation();
            loadingAnimation.setVisibility(View.GONE);
        }
        else{
            loadingAnimation.cancelAnimation();
            loadingAnimation.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_previous:
                onBackPressed();
                break;

            case R.id.nav_next:

                if(webView.canGoForward()){
                    webView.goForward();
                }

                break;

            case R.id.nav_reload:
                checkConnection();
                break;



        }


        return super.onOptionsItemSelected(item);
    }


}
