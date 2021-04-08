package de.jzbick.fliegendebilder

import android.Manifest.permission.*
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHelper {

    companion object {
        fun hasPermissions(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(activity, CAMERA) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, INTERNET) == PERMISSION_GRANTED
        }

        fun requestPermissions(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(CAMERA, ACCESS_COARSE_LOCATION, ACCESS_COARSE_LOCATION, INTERNET),
                2
            )
        }
    }
}
