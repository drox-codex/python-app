package com.example.data.model

data class PackageInfo(
    val name: String,
    val description: String,
    val version: String,
    val isInstalled: Boolean,
    val isPopular: Boolean = true,
    val author: String = "",
    val summaryAr: String = ""
)
