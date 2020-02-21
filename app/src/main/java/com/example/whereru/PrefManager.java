package com.example.whereru;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Harsh on 16-03-2018.
 */

public class PrefManager {

    Context context;

    PrefManager(Context context) {
        this.context = context;
    }

    public void saveLoginDetails(String email, String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.putBoolean("islogin", true);
        editor.commit();
    }

   /* public void saveSignDetails(String Name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", Name);
        //editor.putString("lname", lname);
        editor.putBoolean("isSign", true);
        editor.commit();
    }*/

    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Email", "");
    }
   /* public String getfname() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Name", "");
    }*//*public String getlname() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SignDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("lname", "");
    }*/
    public boolean getisLogin() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("islogin", false);
    }
   /* public boolean getisSign() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isSign", false);
    }*/

    public boolean isUserLogedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isPasswordEmpty = sharedPreferences.getString("Password", "").isEmpty();
        return isEmailEmpty || isPasswordEmpty;
    }

    public void clearAll() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
