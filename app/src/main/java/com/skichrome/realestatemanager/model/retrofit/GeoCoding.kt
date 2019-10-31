package com.skichrome.realestatemanager.model.retrofit

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoCoding(
    @Json(name = "results") val results: List<Results> = emptyList(),
    @Json(name = "status") val status: String
)

@JsonClass(generateAdapter = true)
data class Results(
    @Json(name = "address_components") val addressComponents: List<AddressComponent> = emptyList(),
    @Json(name = "formatted_address") val formattedAddress: String,
    @Json(name = "geometry") val geometry: Geometry,
    @Json(name = "place_id") val placeId: String,
    @Json(name = "types") val types: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AddressComponent(
    @Json(name = "long_name") val longName: String,
    @Json(name = "short_name") val shortName: String,
    @Json(name = "types") val types: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Geometry(
    @Json(name = "location") val location: Location,
    @Json(name = "location_type") val locationType: String,
    @Json(name = "viewport") val viewport: Viewport
)

@JsonClass(generateAdapter = true)
data class Viewport(
    @Json(name = "northeast") val northeast: Northeast,
    @Json(name = "southwest") val Southwest: Southwest
)

@JsonClass(generateAdapter = true)
data class Location(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)

@JsonClass(generateAdapter = true)
data class Northeast(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)

@JsonClass(generateAdapter = true)
data class Southwest(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)