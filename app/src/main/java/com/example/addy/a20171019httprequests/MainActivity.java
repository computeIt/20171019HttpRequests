package com.example.addy.a20171019httprequests;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.addy.a20171019httprequests.model.Auth;
import com.example.addy.a20171019httprequests.model.Token;
import com.example.addy.a20171019httprequests.providers.HttpProvider;
import com.google.gson.Gson;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button loginBtn, registrationBtn;
    private FrameLayout progressFrame;
    private EditText inputEmail, inputPassword;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressFrame = (FrameLayout) findViewById(R.id.progress_frame);
        loginBtn = (Button) findViewById(R.id.login_btn);
        registrationBtn = (Button) findViewById(R.id.registration_btn);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);

        //не даем возможности нажать на кнопку сквозь прогрессбар - ставим нулевой листенер
        progressFrame.setOnClickListener(null);
        loginBtn.setOnClickListener(this);
        registrationBtn.setOnClickListener(this);

    }
//в UI треде запрещено посылать http запросы
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login_btn){
            isLogin = true;
            new AuthTask().execute();
        } else if (v.getId() == R.id.registration_btn){
            isLogin = false;
            new AuthTask().execute();
        }
    }

    class AuthTask extends AsyncTask<Void, Void, Boolean>{
        private Gson gson;
        private Auth auth;
        private String errorMsg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String email = String.valueOf(inputEmail.getText());
            String password = String.valueOf(inputPassword.getText());
            auth = new Auth(email, password);
            progressFrame.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = true;
            gson = new Gson();
            String data = gson.toJson(auth);
            try {
                String response = HttpProvider.getInstance().registration(data);
                Token token = gson.fromJson(response, Token.class);
                Log.d("MAIN_ACTIVITY", "doInBackground " + token.getToken());
            } catch (IOException e){
                result = false;
                errorMsg = "connection error! check your internet signal";//скорее всего IOException ошибка изза отсутствия инета у пользователя
            } catch (Exception e) {
                result = false;
                errorMsg = e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
            progressFrame.setVisibility(View.GONE);

        }
    }
}
