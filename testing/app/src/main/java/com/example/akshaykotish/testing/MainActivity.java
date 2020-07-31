package com.example.akshaykotish.testing;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String TAG = "PhoneActivityTAG";
    final Activity activity = this;
    String wantPermission = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;
    String Longitude, Latitude, PhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        } else {
            //Toast.makeText(this, "Phone:-" + getPhone(), Toast.LENGTH_LONG).show();
            PhoneNumber = getPhone();
            Log.d(TAG, "Phone number: " + getPhone());
        }

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION );

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            onGPS();
        }
        else{
            getLocation();
        }

        WebView webView = (WebView)findViewById(R.id.webviewurl);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSavePassword(true);
        webSettings.getSaveFormData();
        webSettings.setEnableSmoothTransition(true);

        String url = "";
        if(PhoneNumber != null && Latitude != null && Longitude != null)
        {
            url = "http://covid.thesafezone.in/Home.aspx?Contact=" + PhoneNumber + "&Lat="+Latitude + "&Long="+Longitude;
            webView.loadUrl(url);
        }
        else if(PhoneNumber != null && Latitude == null && Longitude == null){
            url = "http://covid.thesafezone.in/Home.aspx?Contact=" + PhoneNumber;
            webView.loadUrl(url);
        }
        else{
            webView.loadUrl("http://covid.thesafezone.in/");
        }
        webView.setWebChromeClient(new WebChromeClient());
        //Toast.makeText(activity, "Phone:- " + PhoneNumber + " Lat:- " + Latitude + " LOng" + Longitude, Toast.LENGTH_LONG).show();
     //   Toast.makeText(activity, url, Toast.LENGTH_SHORT).show();
    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION );

        }
        else{
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location1Network = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1Passive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(locationGPS != null)
            {
                double lat = locationGPS.getAltitude();
                double longi = locationGPS.getLongitude();

                Latitude = String.valueOf(lat);
                Longitude = String.valueOf(longi);
                //Toast.makeText(activity, "Lat:- " + Latitude + " Long:- " + Longitude, Toast.LENGTH_SHORT).show();
            }
            else if(location1Network != null)
            {
                double lat = location1Network.getAltitude();
                double longi = location1Network.getLongitude();

                Latitude = String.valueOf(lat);
                Longitude = String.valueOf(longi);
                //Toast.makeText(activity, "Lat:- " + Latitude + " Long:- " + Longitude, Toast.LENGTH_SHORT).show();
            }
            else if(location1Passive != null)
            {
                double lat = location1Passive.getAltitude();
                double longi = location1Passive.getLongitude();

                Latitude = String.valueOf(lat);
                Longitude = String.valueOf(longi);
                //Toast.makeText(activity, "Lat:- " + Latitude + " Long:- " + Longitude, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(activity, "Can't Get Your Locaion", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, wantPermission) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return phoneMgr.getLine1Number();
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            Toast.makeText(activity, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Phone number: " + getPhone());
                } else {
                    Toast.makeText(activity, "Permission Denied. We can't get phone number.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}

