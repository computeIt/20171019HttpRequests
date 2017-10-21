package com.example.addy.a20171019httprequests.providers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpProvider {
    public static final String BASE_URL = "https://telranstudentsproject.appspot.com/_ah/api/contactsApi/v1";
    private static final HttpProvider ourInstance = new HttpProvider();

    public static HttpProvider getInstance() {
        return ourInstance;
    }

    private HttpProvider() {    //singleton же ж
    }

    public String registration(String data) throws Exception {
        String result = "";

        URL url = new URL(BASE_URL + "/registration");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(15000);//ожидание установления соединения
        connection.setReadTimeout(15000);//ожидание ответа сервера
        connection.setDoOutput(true);
        connection.setDoInput(true);//говорим серверу что будем и получать и отправлять данные в рамках соединения

//        connection.connect(); //используется для GET запросов
        OutputStream outputStream = connection.getOutputStream();//в этот момент на сервер уходит header и управление передается нам
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        bufferedWriter.write(data);
        bufferedWriter.flush();
        bufferedWriter.close();

        BufferedReader bufferedReader;
        String str = "";

        if(connection.getResponseCode() < 400){
            InputStream inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((str = bufferedReader.readLine()) != null){
                result = result + str;
            }
            bufferedReader.close();
        } else if(connection.getResponseCode() == 409){
            String error = "";
            InputStream is = connection.getErrorStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            while((str = bufferedReader.readLine()) != null){
                error = error + str;
            }
            Log.d("MY_TAG", "registration() error " + error);
            throw new Exception("user already exist");
        } else {
            String error = "";
            InputStream is = connection.getErrorStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            while((str = bufferedReader.readLine()) != null){
                error = error + str;
            }
            Log.d("MY_TAG", "registration() error " + error);
            throw new Exception("server error");
        }
        return result;
    }
}
