package com.nguyen.instafire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// derive from PostActivity since ProfileActivity and PostActivity shares a lot of common code
// the interface of ProfileActivity and PostActivity are almost identical except for the menus
class ProfileActivity : PostActivity() {
    companion object {
        const val TAG = "ProfileActivity"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        // return true instead of super.onCreateOptionsMenu(menu) to avoid creating duplicate menus
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mi_logout) {
            Log.i(TAG, "User wants to log out")
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}