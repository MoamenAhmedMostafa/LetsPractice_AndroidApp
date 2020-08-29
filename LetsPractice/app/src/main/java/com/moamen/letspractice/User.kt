package com.moamen.letspractice

class User(var name:String,var gmail:String,var phone:String,
           var img:String,var id:String,var score:Long,var n_of_quiz:Int,var aboutMe:String,var rnk:Int) {
    constructor():this("","","","","",0,0,"",16){
    }
}