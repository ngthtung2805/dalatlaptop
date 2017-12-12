package com.tungnui.abccomputer.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Nasir on 9/10/17.
 */

public class MyWebViewClient extends WebViewClient {
    private android.app.ProgressDialog ProgressDialog;
    AlertDialog alertDialog ;
    private Context context;


    public MyWebViewClient(Context context) {
        this.context=context;
        ProgressDialog = new ProgressDialog(context);
        ProgressDialog.setMessage("Please wait...");
        ProgressDialog.setCancelable(true);
        //ProgressDialog.show();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon){
        // TODO Auto-generated method stub
        super.onPageStarted(view, url, favicon);
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub

        view.loadUrl(url);
        return true;
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

/*        if (ProgressDialog.isShowing() ){
            ProgressDialog.dismiss();
        }*/
    }
    @Override
    public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {

/*        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(description);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.show();*/
    }
}
