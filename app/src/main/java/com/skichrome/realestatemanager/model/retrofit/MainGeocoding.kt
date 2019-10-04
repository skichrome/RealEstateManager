package com.skichrome.realestatemanager.model.retrofit

data class MainGeocoding(var results: List<Result> = emptyList(), var status: String? = null)