package com.tungnui.abccomputer.network.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.tungnui.abccomputer.network.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ashiq on 5/28/16.
 */
public abstract class BaseHttp extends AsyncTask<Void, Void, String> {

    private Context mContext;
    private String requestUrl, requestMethod;
    private HashMap<String, String> requestParams;
    private String requestJsonParams;

    private static final String REQUEST_METHOD_GET = "GET";
    private static final String REQUEST_METHOD_POST = "POST";
    private static final String REQUEST_METHOD_DELETE = "DELETE";
    private static final String REQUEST_METHOD_PUT = "PUT";

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    public BaseHttp(Context context, String requestUrl) {
        this.mContext = context;
        this.requestUrl = requestUrl;
        this.requestMethod = REQUEST_METHOD_GET;
        Log.e("HTTP Request", "Base Url: "+requestUrl);
    }

    public void post(HashMap<String, String> requestParams) {
        this.requestMethod = REQUEST_METHOD_POST;
        this.requestParams = requestParams;
    }
    public void delete() {
        this.requestMethod = REQUEST_METHOD_DELETE;
    }

    public void postJsonParams(String jsonData) {
        this.requestMethod = REQUEST_METHOD_POST;
        this.requestJsonParams = jsonData;
    }
    public void putJsonParams(String jsonData) {
        this.requestMethod = REQUEST_METHOD_PUT;
        this.requestJsonParams = jsonData;
    }

    @Override
    protected String doInBackground(Void... max) {

        if (isNetworkAvailable(mContext)) {
            String response = sendRequest(requestUrl, requestMethod, requestParams);
            onBackgroundTask(response);
            return response;
        } else {
            // return null means no response
            onBackgroundTask(null);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        onTaskComplete();
    }


    public abstract void onBackgroundTask(String response);

    public abstract void onTaskComplete();
    
    private String sendRequest(String requestUrl, String requestMethod, HashMap<String, String> requestParams) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(requestMethod);
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
            // Generate encoded authorization as a header
            String userCredentials = HttpParams.CONSUMER_KEY+":"+ HttpParams.CONSUMER_SECRET;
            String basicAuth = "Basic " + new String(android.util.Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
            httpURLConnection.setRequestProperty ("Authorization", basicAuth.replace("\n","")); // remove unexpected new line from string

            if (requestMethod.equals(REQUEST_METHOD_GET)) {
                httpURLConnection.setDoOutput(false);
            }
            else if (requestMethod.equals(REQUEST_METHOD_DELETE)){
                httpURLConnection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
            }
            else {
                httpURLConnection.setDoOutput(true);
            }



            if (requestParams != null) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(getPostDataString(requestParams));

                writer.flush();
                writer.close();
                outputStream.close();
            }
            if(requestJsonParams != null){
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(requestJsonParams);

                writer.flush();
                writer.close();
                outputStream.close();
            }

            return getResponseInString(httpURLConnection);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getResponseInString(HttpURLConnection httpURLConnection) throws IOException {
        StringBuilder result = new StringBuilder();
        try {
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK ||
                    httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            else if ((httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST) ||
                    (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)){
                InputStream in = new BufferedInputStream(httpURLConnection.getErrorStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            else if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){

            }
            else {
            }

            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


}
