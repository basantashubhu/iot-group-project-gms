package com.example.gms.models

import java.io.Serializable

data class Bin(
    var uuid : String = "",
    var name : String = "",
    var desc : String = "",
    var state : String = ""
) : Serializable
