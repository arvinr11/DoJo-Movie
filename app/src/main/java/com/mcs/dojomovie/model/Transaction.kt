package com.mcs.dojomovie.model

class Transaction (
    var id: Int = 0,
    var user_id: Int = 0,
    var film_id: String = "",
    var quantity: Int = 0,

    var film_image: String = "",
    var film_price: Int = 0,
    var film_title: String = ""
)