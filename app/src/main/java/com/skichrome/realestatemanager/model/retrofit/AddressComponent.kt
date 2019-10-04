package com.skichrome.realestatemanager.model.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddressComponent()
{
    @SerializedName("long_name")
    @Expose
    var longName: String? = null
    @SerializedName("short_name")
    @Expose
    var shortName: String? = null
    @SerializedName("types")
    @Expose
    var types: List<String>? = null

    constructor(longName: String? = null, shortName: String? = null, types: List<String>? = null) : this()
    {
        this.longName = longName
        this.shortName = shortName
        this.types = types
    }
}