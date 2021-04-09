package de.jzbick.fliegendebilder

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

            // placeImage(hitResult.createAnchor())

        }



        this.init()

    }

    private fun init() {
        Log.i("DEBUG", "init")
        arFragment.arSceneView.scene.addOnUpdateListener {
            Log.i("DEBUG", "onUpdate")
            if (locationScene == null) {
                Log.i("DEBUG", "locationScene is null")
                locationScene = LocationScene(this, arFragment.arSceneView)

                createViewRenderable()?.thenAccept { viewRenderable ->
                    Log.i("DEBUG", "viewRenderable: $viewRenderable")

                    val marker = renderableToLocationMarker(viewRenderable, 7.472779, 51.505454)

                    locationScene?.mLocationMarkers?.add(marker)
                    locationScene?.refreshAnchors()
                }
            } else {
                Log.i("DEBUG", "processFrame")
                locationScene?.processFrame(arFragment.arSceneView.arFrame)
            }
        }
    }

    private fun createViewRenderable(): CompletableFuture<ViewRenderable>? {
        Log.i("DEBUG", "createLocationMarker")
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
