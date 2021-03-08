package com.nguyen.instafire

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nguyen.instafire.databinding.ItemPostBinding
import java.math.BigInteger
import java.security.MessageDigest

class PostAdapter(val context: Context, val posts: List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            val username = post.user?.username as String
            Glide.with(context)
                .load(getProfileImageUrl(username))
                .into(binding.imgProfileImage)
            binding.tvUsername.text = username
            binding.tvRelativeTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeMs)
            Glide.with(context)
                .load(post.imageUrl)
                .into(binding.imgPost)
            binding.tvDescription.text = post.description
        }

        fun getProfileImageUrl(username: String): String {
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(username.toByteArray())
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount() = posts.size
}