package de.jzbick.fliegendebilder

import android.location.Location
import android.location.LocationManager

class Settings {

    companion object {
        private var instance: Settings = Settings()

        fun getInstance(): Settings {
            return instance
        }
    }

    var radius: Double = 1.0
    var imageSize: Int = 200
    var fixedLocation: Location = Location(LocationManager.GPS_PROVIDER)
    var useFixedLocation: Boolean = false
}
