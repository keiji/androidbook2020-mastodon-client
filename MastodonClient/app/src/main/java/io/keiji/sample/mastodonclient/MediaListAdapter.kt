package io.keiji.sample.mastodonclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.keiji.sample.mastodonclient.databinding.ListItemMediaBinding

class MediaListAdapter(
        private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<MediaListAdapter.ViewHolder>() {

    var mediaList: List<Media> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = mediaList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<ListItemMediaBinding>(
                layoutInflater,
                R.layout.list_item_media,
                parent,
                false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(mediaList[position])
    }

    class ViewHolder(
            private val binding: ListItemMediaBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(media: Media) {
            binding.media = media
        }
    }
}