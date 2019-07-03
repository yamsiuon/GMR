package net.y.y.googlemaprestaurant.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;

import net.y.y.googlemaprestaurant.R;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class UiUtility {

    public static ACProgressFlower acProgressFlower_dialog = null;

    public static void showProgressDialog(Context context) {

        acProgressFlower_dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text(context.getResources().getString(R.string.loading))
                .fadeColor(Color.DKGRAY).build();
        acProgressFlower_dialog.setCancelable(false);
        acProgressFlower_dialog.show();
    }

    public static void dismissProgressDialog() {
        acProgressFlower_dialog.dismiss();
    }
}
