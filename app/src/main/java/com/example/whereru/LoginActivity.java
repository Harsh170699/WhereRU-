package com.example.whereru;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText edtemail, edtpassword;
    Button btnlogi;
    String email, password;


    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        edtemail = (EditText) findViewById(R.id.edtemail);
        edtpassword = findViewById(R.id.edtpassword);

        btnlogi = findViewById(R.id.btnlogi);

        auth = FirebaseAuth.getInstance();

        btnlogi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = edtemail.getText().toString();
                password = edtpassword.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                edtpassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, "You have entered incorrect email id or password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                            new PrefManager(LoginActivity.this).saveLoginDetails(email, password);
                            FirebaseUser user = auth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, NaviActivity.class);
                            startActivity(intent);
                        }

                    }
                });
                notificationcall();
            }

        });


    }


    public void data(View v) {
        email = edtemail.getText().toString();
        password = edtpassword.getText().toString();

        if (IsValidEmail(edtemail, "Enter a Email id") && (ValidateEdittext(edtpassword, 7, "Enter a Password atleast 8 character !!"))) {
            Toast.makeText(LoginActivity.this, "Login Successfull....", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean IsValidEmail(EditText editText, String errormsg) {
        if (Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches() && editText.getText().toString() != null) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(errormsg);
            return false;
        }
    }

    public boolean ValidateEdittext(EditText editText, int allowedlength, String errormsg) {
        if (allowedlength < 0) {
            int temp = Integer.parseInt(String.valueOf(allowedlength).split("-")[1]);
            if (editText.getText().toString().length() == temp && editText.getText().toString() != null) {
                editText.setError(null);
                return true;
            } else {
                editText.setError(errormsg);
                return false;
            }
        } else if (editText.getText().toString().length() > allowedlength && editText.getText().toString() != null) {
            editText.setError(null);
            return true;
        } else {
            editText.setError(errormsg);
            return false;
        }
    }

    // CODE OF NOTIFICATION:


    public void notificationcall() {
        NotificationCompat.Builder notifi = (NotificationCompat.Builder) new NotificationCompat.Builder(LoginActivity.this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setContentTitle("Where R U ?")
                .setContentText("You are Successfully Loged In ");
//                .setContentText("Your Password is : " +  password);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notifi.build());
    }


}

