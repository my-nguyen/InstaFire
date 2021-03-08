package com.nguyen.instafire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nguyen.instafire.databinding.ActivityPostBinding

open class PostActivity : AppCompatActivity() {

    companion object {
        const val TAG = "PostsActivity"
        const val EXTRA_USERNAME = "EXTRA_USERNAME"
    }

    lateinit var binding: ActivityPostBinding
    lateinit var posts: MutableList<Post>
    lateinit var adapter: PostAdapter
    lateinit var firestore: FirebaseFirestore
    var signedInUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_posts)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        posts = mutableListOf()
        adapter = PostAdapter(this, posts)
        binding.rvPosts.adapter = adapter
        binding.rvPosts.layoutManager = LinearLayoutManager(this)

        // get the database root
        firestore = FirebaseFirestore.getInstance()

        // extract the signed-in user from the Users table
        val userId = FirebaseAuth.getInstance().currentUser?.uid as String
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                signedInUser = snapshot.toObject(User::class.java)
                Log.i(TAG, "signed-in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure getting signed-in user", exception)
            }

        // get the Posts table, limit to 20 records, order by reversed creation time
        var postTable = firestore.collection("posts")
            .limit(20)
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)

        val username = intent.getStringExtra(EXTRA_USERNAME)
        // if username is null, we're in PostActivity: show all posts.
        if (username != null) {
            // if username is not null, we're in ProfileActivity: filter to show posts belonging to
            // the signed-in user and show username in title bar
            supportActionBar?.title = username
            postTable = postTable.whereEqualTo("user.username", username)
        }
        // retrieve and display the rows in Posts table
        // also, any update to the Posts table will trigger this
        postTable.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying posts", exception)
            } else {
                // convert from Firebase table to Kotlin data class
                val postRows = snapshot.toObjects(Post::class.java)
                // iterate thru each row in the posts table
                for (post in postRows) {
                    Log.i(TAG, "Post $post")
                }

                posts.clear()
                posts.addAll(postRows)
                adapter.notifyDataSetChanged()
            }
        }

        binding.fabCreate.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mi_profile) {
            // clicking on menu item 'Profile' will navigate to ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}