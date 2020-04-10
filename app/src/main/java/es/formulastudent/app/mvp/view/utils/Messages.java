package es.formulastudent.app.mvp.view.utils;

import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

public class Messages {

    private FragmentActivity activity;
    private View rootView;

    public Messages(FragmentActivity activity) {
        this.activity = activity;
        rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public void showError(Integer message,  Object... args){
        if(message != null) {
            if (args != null) {
                Snackbar.make(rootView, activity.getString(message, args), Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void showInfo(Integer message,  Object... args){
        if(args != null){
            //Snackbar.make(rootView, activity.getString(message, args), Snackbar.LENGTH_LONG).show();
        }else{
            //Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }
    }
}