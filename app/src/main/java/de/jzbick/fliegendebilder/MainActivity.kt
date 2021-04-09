package de.jzbick.fliegendebilder

import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private var locationScene: LocationScene? = null
    private lateinit var location: android.location.Location
    private var locations: ArrayList<Location> = ArrayList()
    private lateinit var locationManager: LocationManager

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(loc: android.location.Location?) {
            location = loc!!
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            Log.e("LOCATION_ERROR", "$p0, $p1, $p2")
        }

        override fun onProviderEnabled(p0: String?) {
            Log.e("LOCATION_ERROR", "$p0")
        }

        override fun onProviderDisabled(p0: String?) {
            Log.e("LOCATION_ERROR", "$p0")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = sceneformFragmentView as ArFragment

        if (!ARLocationPermissionHelper.hasPermission(this)) {
            ARLocationPermissionHelper.requestPermission(this)
        }

        if (ARLocationPermissionHelper.hasPermission(this)) {

            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f, locationListener)

            this.loadLocations()
            this.init()
        }
    }

    private fun loadLocations() {
        locations.add(Location(51.505454, 7.472779))
        locations.add(Location(51.5047335, 7.4704443))
        locations.add(Location(51.5047376, 7.4674283))
        locations.add(Location(51.5023117, 7.4639605))
    }

    override fun onResume() {
        super.onResume()
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            ARLocationPermissionHelper.requestPermission(this)
        }
    }

    private fun init() {
        arFragment.arSceneView.scene.addOnUpdateListener {
            if (locationScene == null) {
                locationScene = LocationScene(this, arFragment.arSceneView)

                createViewRenderable()?.thenAccept { viewRenderable ->
                    for (location in locations) {
                        val locationMarker = location.toLocationMarker(viewRenderable)

                        val distance = location.getDistanceTo(this.location.latitude, this.location.longitude)

                        if (distance <= 1) {
                            locationMarker.scaleModifier = (1 - distance + 0.1).toFloat();
                            locationScene?.mLocationMarkers?.add(locationMarker)
                            locationScene?.refreshAnchors()
                        }
                    }
                }
            } else {
                locationScene?.processFrame(arFragment.arSceneView.arFrame)
            }
        }
    }

    private fun createViewRenderable(): CompletableFuture<ViewRenderable>? {
        return ViewRenderable.builder()
            .setView(this, R.layout.test_image)
            .build()
    }

    private fun renderableToLocationMarker(
        renderable: ViewRenderable,
        longitude: Double,
        latitude: Double
    ): LocationMarker {
        val node = Node()

        node.renderable = renderable

        return LocationMarker(longitude, latitude, node)
    }

}
