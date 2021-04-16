package de.jzbick.fliegendebilder.interfaces

import de.jzbick.fliegendebilder.SightImage

fun interface SightImageCallback {
    fun onSuccess(imageArray: ArrayList<SightImage>)
}
