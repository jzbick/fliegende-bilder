package de.jzbick.fliegendebilder

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.beyondar.android.fragment.BeyondarFragmentSupport
import com.beyondar.android.world.GeoObject
import com.beyondar.android.world.World


class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var world: World

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PermissionHelper.hasPermissions(this)) {
            PermissionHelper.requestPermissions(this)
        }

        setContentView(R.layout.activity_main)

        getLocation()
        createWorld()
    }

    override fun onResume() {
        super.onResume()
        if (!PermissionHelper.hasPermissions(this)) {
            PermissionHelper.requestPermissions(this)
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, this)
    }

    override fun onLocationChanged(location: Location) {
        Log.i("DEBUG", "${location.latitude}, ${location.longitude}, ${location.altitude}")
        world.setGeoPosition(location.latitude, location.longitude)
    }

    private fun createWorld() {
        val myBeyondArFragment = fragmentManager.findFragmentById(R.id.beyondArFragment) as BeyondarFragmentSupport

        world = World(this)
        world.setDefaultBitmap(R.drawable.beyondar_default_unknow_icon, 0)

        world.setGeoPosition(51.62162162162162, 7.5455740234313025)

        // Create an object with an image in the app resources.
        val go1 = GeoObject(1L)
        go1.setGeoPosition(51.62162162162162, 7.5455740234313025)
        go1.setImageResource(R.drawable.creature_1)
        go1.name = "Creature 1"

        // Is it also possible to load the image asynchronously form internet
        val go2 = GeoObject(2L)
        go2.setGeoPosition(51.62162162162162, 7.5455740234313025)
        go2.imageUri = "http://beyondar.com/sites/default/files/logo_reduced.png"
        go2.name = "Online image"

        // We add this GeoObjects to the world
        world.addBeyondarObject(go1)
        world.addBeyondarObject(go2)

        // Finally we add the Wold data in to the fragment

        myBeyondArFragment.world = world
    }
}
