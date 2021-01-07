package com.fcfm.poi.pia.modelos

import com.google.firebase.database.Exclude

class Mensaje(
    var id: String = "",
    var contenido: String = "",
    @get:Exclude var archivo: ByteArray? = null,
    var de: String = "",
    var timeStamp: Any? = null
) {
    @Exclude
    var esMio: Boolean = false
}