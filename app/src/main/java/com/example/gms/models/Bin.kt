package com.example.gms.models

import java.io.Serializable

data class Bin(
    var uuid : String = "",
    val name : String = "",
    val desc : String = "",
    val state : String = ""
) : Serializable
