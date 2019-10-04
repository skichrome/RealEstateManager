package com.skichrome.realestatemanager.model.retrofit

data class Result(
    var addressComponents: List<AddressComponent>? = null,
    var formattedAddress: String? = null,
    var geometry: Geometry? = null,
    var placeId: String? = null,
    var plusCode: PlusCode? = null,
    var types: List<String>? = null
)