package com.example.whereru;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.lang.Object;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.internal.FirebaseAppHelper;

public class SignActivity extends AppCompatActivity {

    EditText edtfname;
    EditText edtlname;
    EditText edtpno;
    EditText edtmail;
    EditText edtpword;
    EditText edtcpword;
    Button btnsign;
    String fname,lname,pno,mail,pword,cpword;
    FirebaseAuth signAuth;
    FirebaseAppHelper firebaseHelper;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        edtfname=(EditText)findViewById(R.id.edtfname);
        edtlname=(EditText)findViewById(R.id.edtlname);
        edtpno=(EditText)findViewById(R.id.edtpno);
        edtmail=(EditText)findViewById(R.id.edtmail);
        edtpword=(EditText)findViewById(R.id.edtpword);
        edtcpword=(EditText)findViewById(R.id.edtcpword);

        btnsign=(Button)findViewById(R.id.btnsign);

        db = FirebaseDatabase.getInstance().getReference();
        /*btnsign.setOnClickListener(this, new View.OnClickListener<>(){
            @Override
            public void onClick(View view) {

            }
        });*/
        signAuth = FirebaseAuth.getInstance();

        btnsign.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                final String fname = edtfname.getText().toString();
                final String lname = edtlname.getText().toString();
                final String pno = edtpno.getText().toString();
                final String email = edtmail.getText().toString();
                final String pword = edtpword.getText().toString();
                final String cpword = edtcpword.getText().toString();

                if (TextUtils.isEmpty(fname)) {
                    Toast.makeText(getApplicationContext(), "Enter Firstname!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lname)) {
                    Toast.makeText(getApplicationContext(), "Enter Lastname!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pno)) {
                    Toast.makeText(getApplicationContext(), "Enter phone no!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pword)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(cpword)) {
                    Toast.makeText(getApplicationContext(), "Enter password again!", Toast.LENGTH_SHORT).show();
                    return;
                }








                    signAuth.createUserWithEmailAndPassword(edtmail.getText().toString(), edtcpword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Register Example", "createUserWithEmail:success");
                                        FirebaseUser user = signAuth.getCurrentUser();
                                        Toast.makeText(SignActivity.this, "Registration Successful",
                                                Toast.LENGTH_LONG).show();
                                        User u = new User();
                                        u.setEmail(edtmail.getText().toString());
                                        u.setFirstName(edtfname.getText().toString());
                                        u.setLastName(edtlname.getText().toString());
                                        u.setPhone(edtpno.getText().toString());
                                        u.setPassword(edtpword.getText().toString());
                                        db
                                                .child("users")
                                                .child(encodeUserEmail(
                                                        edtmail.getText().toString()
                                                ))
                                                .setValue(u);

                                        //DatabaseReference userRef = db.child("whereru-firebase").child(edtmail.getText().toString());
                                        //userRef.setValue(u);
                                        Intent i = new Intent(SignActivity.this,FirstPage.class);
                                        startActivity(i);
                                        //new PrefManager(SignActivity.this).saveSignDetails(fname);
                                        //updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Resgister user 2", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignActivity.this, "."+task.getException(),
                                                Toast.LENGTH_LONG).show();
                                        //updateUI(null);
                                    }

                                }
                            });
            }
        });

    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    static String decodeUserEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public void data(View v)
    {
        fname=edtfname.getText().toString();
        lname=edtlname.getText().toString();
        pno=edtpno.getText().toString();
        mail=edtmail.getText().toString();
        pword=edtpword.getText().toString();
        cpword=edtcpword.getText().toString();

        if (IsValidEmail(edtmail,"Enter a Email id") && (ValidateEdittext(edtfname,20,"Enter a First Name")) && (ValidateEdittext(edtlname,20,"Enter a Last Name")) && (ValidateEdittext(edtpno, 9,"Enter a valid phone number")) &&(ValidateEdittext(edtpword,7,"Enter a Password atleast 8 character !!")) && (ValidateEdittext(edtcpword,7,"Passwords you enter do not match !!")))
        {
            Toast.makeText(SignActivity.this, "Sign up Successfull....", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean IsValidEmail(EditText editText, String errormsg)
    {
        if(Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches() && editText.getText().toString()!=null)
        {
            editText.setError(null);
            return true;
        }
        else
        {
            editText.setError(errormsg);
            return false;
        }
    }

    public boolean ValidateEdittext(EditText editText, int allowedlength, String errormsg)
    {
        if(allowedlength<0)
        {
            int temp=Integer.parseInt(String.valueOf(allowedlength).split("-")[1]);
            if (editText.getText().toString().length()==temp && editText.getText().toString()!=null)
            {
                editText.setError(null);
                return true;
            }
            else
            {
                editText.setError(errormsg);
                return false;
            }
        }
        else if (editText.getText().toString().length()>allowedlength && editText.getText().toString()!=null )
        {
            editText.setError(null);
            return true;
        }
        else
        {
            editText.setError(errormsg);
            return false;
        }
    }


}
