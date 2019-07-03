package net.y.y.googlemaprestaurant.utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import net.y.y.googlemaprestaurant.global.Constant;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtility {

    public interface PermissionCallBack {
        void onPermissionAllow();
        void onPermissionReject();
    }

    private Activity activity;
    private PermissionCallBack permissionCallBack;

    public PermissionUtility(Activity activity, PermissionCallBack permissionCallBack) {
        this.activity = activity;
        this.permissionCallBack = permissionCallBack;
    }

    public boolean allowLocationPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                permissionCallBack.onPermissionAllow();
                return true;
            } else {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constant.REQUEST_PERMISSION_LOCATION);
                return false;
            }
        }
        return false;
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.REQUEST_PERMISSION_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionCallBack.onPermissionAllow();
                } else {
                    permissionCallBack.onPermissionReject();
                }
                break;
            default:
                break;
        }
    }
}
