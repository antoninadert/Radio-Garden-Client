package org.dynamicsoft.radiogarden;

import android.Manifest;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    public WebView mywebView;
    public boolean internetIsUp=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, BackgroundService.class));

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            // if connected with internet
            internetIsUp=true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            internetIsUp=false;
        }

        mywebView = (WebView) findViewById(R.id.webview);   //WebView
        WebSettings webSettings= mywebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if(internetIsUp==true){
            mywebView.loadUrl("http://radio.garden/live/");
        }
        else{
            mywebView.loadUrl("file:///android_asset/disconnected.html");
        }

        //Location
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 0);

        mywebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        mywebView.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3278.0 Safari/537.36 OPR/51.0.2809.0 (Edition developer)");
        mywebView.setBackgroundColor(Color.TRANSPARENT);
        mywebView.setWebViewClient(new WebViewClient());
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Radio Garden");
        wakeLock.acquire();
    }
}