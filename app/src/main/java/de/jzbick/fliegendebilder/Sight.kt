package de.jzbick.fliegendebilder

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import uk.co.appoly.arcorelocation.LocationMarker
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class Sight(
    var id: Int,
    var name: String,
    var description: String?,
    var lat: Double,
    var long: Double,
    var height: Int?,
    var images: ArrayList<SightImage>
) {

    fun addImage(image: SightImage) {
        images.add(image)
    }

    fun addAllImages(images: ArrayList<SightImage>) {
        images.addAll(images)
    }

    fun toLocationMarker(renderable: ViewRenderable): LocationMarker {
        val node = Node()
        node.renderable = renderable

        return LocationMarker(long, lat, node)
    }

    fun getDistanceTo(lat: Double, long: Double): Double {
        val theta: Double = this.long - long
        var dist: Double = (
                sin(Math.toRadians(this.lat))
                        * sin(Math.toRadians(lat))
                        + cos(Math.toRadians(this.lat))
                        * cos(Math.toRadians(lat))
                        * cos(Math.toRadians(theta))
                )

        dist = acos(dist)
        dist = Math.toDegrees(dist)
        dist *= 60 * 1.1515
        dist *= 1.609344

        return dist
    }

    override fun toString(): String {
        return "Sight(id=$id, name=$name, descripton=$description, lat=$lat, long=$long, height=$height, images=$images)"
    }
}
