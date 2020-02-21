package com.example.whereru;


import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Contactus extends Fragment {

        Button btn;
        String s="7778995329";
        EditText et;
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v=null;
            v = inflater.inflate(R.layout.activity_contactus,container,false);
            btn = (Button)v.findViewById(R.id.btncall);
            et = (EditText)v.findViewById(R.id.edtphone);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+et.getText().toString()));
                        startActivity(callIntent);
                    }catch (Exception e){
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+et.getText().toString()));
                        startActivity(callIntent);
                    }

                }
            });
            return v;
        }
}


