package com.nguyen.instafire

// with Firebase model, each parameter member must be a var (not a val) and must have a default value
data class User(var username: String="", var age: Int=0)