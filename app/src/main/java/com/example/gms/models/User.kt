package com.example.gms.models

data class User(
    var uuid : String = "",
    var first_name : String = "",
    var last_name : String = "",
    var email : String = "",
    var gender : String = "",
    var image : String = "",
    var profileCompleted : Int = 0
)
