package com.nguyen.instafire

import com.google.firebase.firestore.PropertyName

// with Firebase model, each parameter member must be a var (not a val) and must have a default value
data class Post(var description: String="",
                // specify @get and @set for a field name that doesn't match Firebase's field name
                @get:PropertyName("image_url")
                @set:PropertyName("image_url")
                var imageUrl: String="",
                @get:PropertyName("creation_time_ms")
                @set:PropertyName("creation_time_ms")
                var creationTimeMs: Long=0,
                var user: User?=null)