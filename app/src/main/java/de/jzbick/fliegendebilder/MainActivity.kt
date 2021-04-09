package de.jzbick.fliegendebilder

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = sceneformFragmentView as ArFragment

        if (!ARLocationPermissionHelper.hasPermission(this)) {
            ARLocationPermissionHelper.requestPermission(this)
        }

        this.init()

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

                    val marker = renderableToLocationMarker(viewRenderable, 7.472779, 51.505454)

                    locationScene?.mLocationMarkers?.add(marker)
                    locationScene?.refreshAnchors()
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
