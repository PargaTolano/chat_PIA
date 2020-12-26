package com.fcfm.poi.pia.modelos

import com.fcfm.poi.pia.enums.UserConectionState

class Usuario (
    var uid   : String = "",
    var email : String = "",
    var userConectionState: UserConectionState = UserConectionState.Absent
){
}