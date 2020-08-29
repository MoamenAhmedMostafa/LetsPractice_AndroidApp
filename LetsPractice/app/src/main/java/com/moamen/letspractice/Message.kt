package com.moamen.letspractice

import java.util.*
import com.google.firebase.Timestamp

class Message (var mesg:String, var userid:String,var time:Timestamp,var id:String) {
    constructor() : this("", "", Timestamp.now(),"") {
    }
}