package de.jzbick.fliegendebilder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment

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
