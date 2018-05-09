package com.jikexueyuan.tulingdemo;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class HttpData extends AsyncTask<String, Void, String> {

    private final HttpGetDataListener listener;

    private final String url;

    HttpData(String url, HttpGetDataListener listener) {
        this.url = url;
        this.listener = listener;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected String doInBackground(String... params) {
        try {
            HttpClient mHttpClient = new DefaultHttpClient();
            HttpGet mHttpGet = new HttpGet(url);
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            InputStream in = mHttpEntity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.getDataUrl(result);
        super.onPostExecute(result);
    }
}
