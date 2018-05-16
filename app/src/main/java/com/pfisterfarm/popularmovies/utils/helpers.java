package com.pfisterfarm.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.View;

public class helpers {

    // isOnline() test taken from
    // https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public static boolean isOnline(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getApplicationContext()
                     .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    // showAlertDialog adapted from
    // https://stackoverflow.com/questions/43513919/android-alert-dialog-with-one-two-and-three-buttons
    public static void showOKAlertDialog(Context context, int titleId, int messageId) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(titleId));
        builder.setMessage(context.getString(messageId));

        // add a button
        builder.setPositiveButton("OK", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
