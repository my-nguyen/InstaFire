package com.nguyen.instafire

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nguyen.instafire.PostActivity.Companion.EXTRA_USERNAME
import com.nguyen.instafire.databinding.ActivityCreateBinding

class CreateActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CreateActivity"
        const val RC_PICK_IMAGE = 1997
    }

    lateinit var binding: ActivityCreateBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var storage: StorageReference
    var imageUri: Uri? = null
    var signedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_create)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = FirebaseStorage.getInstance().reference

        // extract the signed-in user from the Users table
        firestore = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid as String
        firestore.collection("users1")
            .document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                signedInUser = snapshot.toObject(User::class.java)
                Log.i(PostActivity.TAG, "signed-in user: $signedInUser")
            }.addOnFailureListener { exception ->
                Log.i(PostActivity.TAG, "Failure getting signed-in user", exception)
            }

        binding.btnPickImage.setOnClickListener {
            Log.i(TAG, "Open up image picker on device")
            Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                resolveActivity(packageManager)?.let {
                    startActivityForResult(this, RC_PICK_IMAGE)
                }
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (imageUri == null) {
                Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
            } else if (binding.etDescription.text.isBlank()) {
                Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            } else if (signedInUser == null) {
                Toast.makeText(this, "No signed in user, please wait", Toast.LENGTH_SHORT).show()
            } else {
                // temporarily disable the Submit button
                binding.btnSubmit.isEnabled = false

                // generate unique image filename
                val image = storage.child("images/${System.currentTimeMillis()}-photo.jpg")
                // upload image to Firebase Storage
                // note the use of continueWithTask to chain multiple callbacks
                image.putFile(imageUri!!)
                    .continueWithTask { task ->
                        // retrieve image URL of the uploaded image
                        Log.i(TAG, "uploaded bytes: ${task.result?.bytesTransferred}")
                        image.downloadUrl
                    }.continueWithTask { task ->
                        // create a Post with the image URL and add that to the Posts collection
                        val post = Post(binding.etDescription.text.toString(),
                            task.result.toString(),
                            System.currentTimeMillis(),
                            signedInUser)
                        firestore.collection("posts").add(post)
                    }.addOnCompleteListener { task ->
                        // re-enable the Submit button
                        binding.btnSubmit.isEnabled = true

                        if (!task.isSuccessful) {
                            Log.e(TAG, "Exception during Firebase operations", task.exception)
                            Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show()
                        } else {
                            binding.etDescription.text.clear()
                            binding.imageView.setImageResource(0)
                            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                            Intent(this, ProfileActivity::class.java).apply {
                                putExtra(EXTRA_USERNAME, signedInUser?.username)
                                startActivity(this)
                            }

                            // end the CreateActivity flow
                            finish()
                        }
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data?.data
                Log.i(TAG, "imageUrl: $imageUri")
                binding.imageView.setImageURI(imageUri)
            } else {
                Toast.makeText(this, "Image picker action canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}