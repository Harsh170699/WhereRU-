package com.example.whereru;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstPage extends AppCompatActivity {

    Button btnlogin,btnsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        btnlogin= findViewById(R.id.btnlogin);
        btnsignup= findViewById(R.id.btnsignup);

    }

    public void login(View v)
    {
        Intent log=new Intent(FirstPage.this,LoginActivity.class);
        startActivity(log);

    }

    public void signup(View v)
    {
        Intent log=new Intent(FirstPage.this,SignActivity.class);
        startActivity(log);

    }
}
