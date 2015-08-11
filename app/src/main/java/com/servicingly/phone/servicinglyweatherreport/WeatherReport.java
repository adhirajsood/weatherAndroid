package com.servicingly.phone.servicinglyweatherreport;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by adhiraj on 9/8/15.
 */

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RestAdapter;

public class WeatherReport extends Activity {

    @Bind(R.id.TempLocation)
    TextView TempLocation;
    @Bind(R.id.location)TextView location;
    String currentTemp,LocationQuery;
    Long currentTs;
    String locationReceived;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_report);
        ButterKnife.bind(this);
        Intent intent= getIntent();
        locationReceived = intent.getStringExtra("location");
        try {
            LocationQuery = URLEncoder.encode(locationReceived, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new GetTemp().execute();
    }



    class GetTemp extends AsyncTask<Void, Void, String> {
        String URL = "http://api.worldweatheronline.com/free/v2/weather.ashx?q="+LocationQuery+"&format=json&key=a1715a9cc7aec6edb9f9a68799173";
        ProgressDialog progressDialog = new ProgressDialog(WeatherReport.this);
        String reply;
        RestAdapter restAdapter;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {


            HttpURLConnection connection = null;
            try {
                URL obj = new URL(URL);
                connection = (HttpURLConnection) obj.openConnection();
                connection.setUseCaches(false);
                connection.setAllowUserInteraction(false);
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader bufferedReader = null;

                InputStream errorStream = connection.getErrorStream();

                if(errorStream==null){
                    InputStream inputStream = connection.getInputStream();
                    bufferedReader = new BufferedReader(
                            new InputStreamReader(inputStream));
                }else{
                    bufferedReader = new BufferedReader(
                            new InputStreamReader(errorStream));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
                bufferedReader.close();

                if(response!=null){
                    reply = response.toString();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return reply;

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s!=null){
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(s);

                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    if (dataObject.has("error")){
                        Toast.makeText(getBaseContext(),"Unable to find location!",Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    JSONArray current_condition = dataObject.getJSONArray("current_condition");
                    String temperature = ((JSONObject)current_condition.get(0)).get("temp_C").toString();
                    JSONArray request = dataObject.getJSONArray("request");
                    String locReq = ((JSONObject)request.get(0)).get("query").toString();
                    currentTemp = temperature + "°C";
                    currentTs = System.currentTimeMillis();
                    TempLocation.setText(currentTemp);
                    location.setText(locReq);
                    Recordedtemp t = new Recordedtemp(locReq, currentTemp ,currentTs);
                    t.save();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(getBaseContext(), "Location not found!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


}