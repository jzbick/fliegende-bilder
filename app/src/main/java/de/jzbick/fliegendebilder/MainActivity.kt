package de.jzbick.fliegendebilder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import java.util.concurrent.CompletableFuture


class MainActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private var locationScene: LocationScene? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = sceneformFragmentView as ArFragment

        arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
            if (plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                return@setOnTapArPlaneListener
            }

            placeImage(hitResult.createAnchor())
        }

        arFragment.arSceneView.scene.addOnUpdateListener { _ ->
            if (locationScene == null) {
                locationScene = LocationScene(this, arFragment.arSceneView)

                val markerA = createLocationMarker(7.4737611, 51.5061397)?.get()
                val markerB = createLocationMarker(7.47361, 51.5060459)?.get()

                Log.i("DEBUG", "markerA: $markerA")
                Log.i("DEBUG", "markerB: $markerB")

                locationScene?.mLocationMarkers?.add(markerA)
                locationScene?.mLocationMarkers?.add(markerB)
            }

            if (locationScene != null) {
                locationScene?.processFrame(arFragment.arSceneView.arFrame)
            }
        }
    }

    private fun createLocationMarker(longitude: Double, latitude: Double): CompletableFuture<LocationMarker>? {
        return ViewRenderable.builder()
                .setView(this, R.layout.test_image)
                .build()
                .thenApply { viewRenderable ->
                    val node = Node()

                    node.renderable = viewRenderable

                    return@thenApply LocationMarker(longitude, latitude, node)
                }
    }

    /**
    private fun addLocation(longitude: Double, latitude: Double) {
    ViewRenderable.builder()
    .setView(this, R.layout.test_image)
    .build()
    .thenAccept { viewRenderable ->
    val locationScene = LocationScene(this, arFragment.arSceneView)
    val node = Node()

    node.renderable = viewRenderable

    val marker = LocationMarker(longitude, latitude, node)

    locationScene.mLocationMarkers.add(marker)

    val frame = arFragment.arSceneView.arFrame

    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

    val location = if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    } else {
    null
    }

    Log.i("DEBUG", "frame: $frame")
    Log.i("DEBUG", "location: $location")

    locationScene.refreshAnchors()
    locationScene.processFrame(arFragment.arSceneView.arFrame)

    Log.i("DEBUG", "Location added")
    }
    } */

    private fun placeImage(anchor: Anchor) {
        ViewRenderable.builder()
                .setView(this, R.layout.test_image)
                .build()
                .thenAccept { viewRenderable ->
                    addNodeToScene(anchor, viewRenderable)
                }
    }

    private fun addNodeToScene(anchor: Anchor, viewRenderable: ViewRenderable?) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment.transformationSystem)
        transformableNode.renderable = viewRenderable
        transformableNode.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }

}
