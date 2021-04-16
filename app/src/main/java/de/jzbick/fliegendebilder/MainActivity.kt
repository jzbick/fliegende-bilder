package de.jzbick.fliegendebilder

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper
import java.util.concurrent.CompletableFuture

class MainActivity : AppCompatActivity() {
    private var oldSights: ArrayList<Sight> = ArrayList()
    private lateinit var arFragment: ArFragment
    private var locationScene: LocationScene? = null
    private lateinit var location: Location
    private var sights: ArrayList<Sight> = ArrayList()
    private lateinit var locationManager: LocationManager
    private lateinit var requests: Requests
    private val settings = Settings.getInstance()

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(loc: Location?) {
            location = if (settings.useFixedLocation) {
                settings.fixedLocation
            } else {
                loc!!
            }
            GlobalScope.launch {
                sights = requests.getSights(location.latitude, location.longitude, settings.radius)
            }
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
        fab.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        requests = Requests()

        arFragment = sceneformFragmentView as ArFragment

        if (!ARLocationPermissionHelper.hasPermission(this)) {
            ARLocationPermissionHelper.requestPermission(this)
        }
    }

    override fun onStart() {
        super.onStart()
        if (ARLocationPermissionHelper.hasPermission(this)) {

            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f, locationListener)

            this.init()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            ARLocationPermissionHelper.requestPermission(this)
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            ARLocationPermissionHelper.requestPermission(this)
        }
    }

    private fun init() {
        arFragment.arSceneView.scene.addOnUpdateListener {
            if (locationScene == null) {
                locationScene = LocationScene(this, arFragment.arSceneView)
            } else if (!(oldSights.toArray() contentDeepEquals sights.toArray())) {
                oldSights.clear()
                oldSights.addAll(sights)

                for (sight in sights) {
                    if (sight.images.isNotEmpty()) {
                        val sightImageBase64 = sight.images.first().base64
                        createViewRenderable(sightImageBase64)?.thenAccept { viewRenderable ->
                            val locationMarker = sight.toLocationMarker(viewRenderable)
                            val distance = sight.getDistanceTo(location.latitude, location.longitude)

                            locationMarker.scaleModifier =
                                minOf(((1 - distance / settings.radius) + 0.1), 1.0).toFloat()
                            locationScene?.mLocationMarkers?.add(locationMarker)
                            locationScene?.refreshAnchors()

                        }
                    }
                }
            }
            locationScene?.processFrame(arFragment.arSceneView.arFrame)
        }

    }

    private fun createViewRenderable(imageString: String): CompletableFuture<ViewRenderable>? {

        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val layout = ConstraintLayout(this)
        val imageView = ImageView(this)
        val constraintSet = ConstraintSet()

        constraintSet.centerHorizontally(imageView.id, layout.id)
        constraintSet.centerVertically(imageView.id, layout.id)
        layout.layoutParams = ConstraintLayout.LayoutParams(settings.imageSize, settings.imageSize)

        imageView.setImageBitmap(imageBitmap)

        layout.addView(imageView)

        return ViewRenderable.builder()
            .setView(this, layout)
            .build()
    }
}
