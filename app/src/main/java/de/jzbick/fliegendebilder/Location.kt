package de.jzbick.fliegendebilder

import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import uk.co.appoly.arcorelocation.LocationMarker


class Location(val lat: Double, val long: Double) {
    fun toLocationMarker(renderable: ViewRenderable): LocationMarker {
        val node = Node();

        node.renderable = renderable

        return LocationMarker(long, lat, node)
    }

    fun getDistanceTo(lat: Double, long: Double): Double {
        val theta: Double = this.long - long;
        var dist: Double = (
            Math.sin(Math.toRadians(this.lat))
            * Math.sin(Math.toRadians(lat))
            + Math.cos(Math.toRadians(this.lat))
            * Math.cos(Math.toRadians(lat))
            * Math.cos(Math.toRadians(theta))
        )

        dist = Math.acos(dist)
        dist = Math.toDegrees(dist)
        dist = dist * 60 * 1.1515
        dist = dist * 1.609344

        return dist
    }
}