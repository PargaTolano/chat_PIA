package com.fcfm.poi.pia.modelos

import android.graphics.Bitmap
import com.google.firebase.database.Exclude

class Imagen(
    var id: String = "",
    var foto: Bitmap? = null
) {
    @Exclude
    var esMio: Boolean = false
}