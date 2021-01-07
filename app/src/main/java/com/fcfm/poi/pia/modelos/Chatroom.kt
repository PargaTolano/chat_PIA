package com.fcfm.poi.pia.modelos

import com.fcfm.poi.pia.enums.ChatroomType

class Chatroom (
    var id: String = "",
    var participantes: MutableMap<String,String> =  mutableMapOf(),
    var nombre: String = "",
    var type : ChatroomType = ChatroomType.DirectMessage
)