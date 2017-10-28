package com.tungnui.dalatlaptop;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;

import java.util.Locale;

import com.tungnui.dalatlaptop.api.OkHttpStack;
import timber.log.Timber;

/**
 * Created by petr.melicherik.
 */
public class MyApplication extends Application {
    public static final String PACKAGE_NAME = MyApplication.class.getPackage().getName();

    private static final String TAG = MyApplication.class.getSimpleName();


    public static String APP_VERSION = "0.0.0";
    public static String ANDROID_ID = "0000000000000000";

    private static MyApplication mInstance;

    private RequestQueue mRequestQueue;


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


    /**
     * Method sets app specific language localization by selected shop.
     * Have to be called from every activity.
     *
     * @param lang language code.
     */
    public static void setAppLocale(String lang) {
        Resources res = mInstance.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang);
        Timber.d("Setting language: %s", lang);
        res.updateConfiguration(conf, dm);
    }

    /**
     * Method provides defaultRetryPolice.
     * First Attempt = 14+(14*1)= 28s.
     * Second attempt = 28+(28*1)= 56s.
     * then invoke Response.ErrorListener callback.
     *
     * @return DefaultRetryPolicy object
     */
    public static DefaultRetryPolicy getDefaultRetryPolice() {
        return new DefaultRetryPolicy(14000, 2, 1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FacebookSdk.sdkInitialize(this);
    }

    /**
     * Method check, if internet is available.
     *
     * @return true if internet is available. Else otherwise.
     */
    public boolean isDataConnected() {
        ConnectivityManager connectMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectMan.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isWiFiConnection() {
        ConnectivityManager connectMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectMan.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

  public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this, new OkHttpStack());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
