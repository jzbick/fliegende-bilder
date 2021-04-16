package de.jzbick.fliegendebilder

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

class Requests {
    private val url = "https://fliegende-bilder.amicaldo.net"

    suspend fun getSights(lat: Double, long: Double, radius: Double): ArrayList<Sight> {

        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
        val response = client.get<ArrayList<Sight>>("$url/sights?lat=$lat&long=$long&radius=$radius")
        response.forEach {
            it.images = getImages(it.id)
        }
        client.close()

        return response
    }

    suspend fun getImages(sightId: Int): ArrayList<SightImage> {
        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }

        val response = client.get<ArrayList<SightImage>>("$url/sights/$sightId/images")

        client.close()

        return response
    }
}
