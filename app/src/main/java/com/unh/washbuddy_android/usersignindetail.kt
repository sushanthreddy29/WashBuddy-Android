package com.unh.washbuddy_android

data class usersignindetail(
    var useruid:String,
    var documentid: String,
    var firstname: String,
    var lastname: String,
    var email: String,
    var username: String
    )

public var usersignin: usersignindetail = usersignindetail("", "", "", "", "", "")
