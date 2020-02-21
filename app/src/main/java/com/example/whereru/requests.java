package com.example.whereru;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Harsh on 24-03-2018.
 */

public class requests extends Fragment {

    private FirebaseListAdapter<Request> adapter;
    private View v;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_requests,container,false);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(getActivity(),
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getEmail(),
                    Toast.LENGTH_LONG)
                    .show();

            displayRequests();
        }
        return v;
    }

    private void displayRequests() {
        ListView listOfRequests = (ListView) v.findViewById(R.id.list_of_requests);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(SignActivity.encodeUserEmail(
                        currentUser.getEmail()
                ))
                .child("requests");

        adapter = new FirebaseListAdapter<Request>(getActivity(), Request.class,
                R.layout.request, requestsRef) {
            @Override
            protected void populateView(View v, final Request model, int position) {
                TextView messageUser = (TextView)v.findViewById(R.id.request_user);

                if (model.getRequestUser() != null) {
                    // Set their text
                    messageUser.setText(
                            SignActivity.decodeUserEmail(
                                    model.getRequestUser()
                            )
                    );
                }

                messageUser.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Are you sure?");

                        // Set up the buttons
                        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String emailAddress = model.getRequestUser();
                                FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(
                                                SignActivity.encodeUserEmail(currentUser.getEmail())
                                        )
                                        .child("friends")
                                        .child(
                                                SignActivity.encodeUserEmail(emailAddress)
                                        )
                                        .setValue(new Request(emailAddress, true));

                                FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(
                                                SignActivity.encodeUserEmail(emailAddress)
                                        )
                                        .child("friends")
                                        .child(
                                                SignActivity.encodeUserEmail(currentUser.getEmail())
                                        )
                                        .setValue(new Request(currentUser.getEmail(), true));

                                FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(
                                                SignActivity.encodeUserEmail(currentUser.getEmail())
                                        )
                                        .child("requests")
                                        .child(
                                                SignActivity.encodeUserEmail(emailAddress)
                                        )
                                        .removeValue();
                            }
                        });
                        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String emailAddress = model.getRequestUser();
                                FirebaseDatabase.getInstance()
                                        .getReference("users")
                                        .child(
                                                SignActivity.encodeUserEmail(currentUser.getEmail())
                                        )
                                        .child("requests")
                                        .child(
                                                SignActivity.encodeUserEmail(emailAddress)
                                        )
                                        .removeValue();
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });
            }
        };

        listOfRequests.setAdapter(adapter);
    }
}

