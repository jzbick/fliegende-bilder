package de.jzbick.fliegendebilder

import java.net.URL
import java.util.*

class SightImage(
    var id: Int,
    var url: URL,
    var description: String?,
    var date: Date?,
    var sight_id: Int,
    var base64: String
) {
    override fun toString(): String {
        return "SightImage(id=$id, url=$url, description=$description, date=$date, sight_id=$sight_id, base64='$base64')"
    }
}
