package de.jzbick.fliegendebilder.interfaces

import de.jzbick.fliegendebilder.Sight

fun interface SightCallback {
    fun onSuccess(sightArray: ArrayList<Sight>)
}
