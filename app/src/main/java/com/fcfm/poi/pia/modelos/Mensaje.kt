package com.fcfm.poi.pia.modelos

import com.google.firebase.database.Exclude

class Mensaje(
    var id: String = "",
    var contenido: String = "",
    var de: String = "",
    val timeStamp: Any? = null
) {
    @Exclude
    var esMio: Boolean = false
}