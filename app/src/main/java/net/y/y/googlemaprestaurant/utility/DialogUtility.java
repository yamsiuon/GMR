package net.y.y.googlemaprestaurant.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class DialogUtility {

    public static AlertDialog customDialog(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

}
