package io.keiji.sample.mastodonclient.ui.toot_edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.keiji.sample.mastodonclient.R
import io.keiji.sample.mastodonclient.databinding.ListItemMediaPreviewBinding
import io.keiji.sample.mastodonclient.entity.LocalMedia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaPreviewAdapter(
    private val inflater: LayoutInflater,
    private val coroutineScope: CoroutineScope
) : RecyclerView.Adapter<MediaPreviewAdapter.ViewHolder>() {

    private class RecyclerDiffCallback(
        private val oldList: List<LocalMedia>,
        private val newList: List<LocalMedia>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ) = oldList[oldItemPosition] == newList[newItemPosition]

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {
            val oFilePath = oldList[oldItemPosition].file.absolutePath
            val nFilePath = newList[newItemPosition].file.absolutePath
            return oFilePath == nFilePath
        }
    }

    var mediaAttachments = ArrayList<LocalMedia>()
        set(value) {
            coroutineScope.launch(Dispatchers.Main) {
                val diffResult = withContext(Dispatchers.Default) {
                    DiffUtil.calculateDiff(
                        RecyclerDiffCallback(field, value)
                    )
                }

                field = value

                diffResult.dispatchUpdatesTo(this@MediaPreviewAdapter)
            }
        }

    override fun getItemCount() = mediaAttachments.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<ListItemMediaPreviewBinding>(
            inflater,
            R.layout.list_item_media_preview,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(mediaAttachments[position])
    }

    class ViewHolder(
        private val binding: ListItemMediaPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(localMedia: LocalMedia) {
            binding.localMedia = localMedia
        }
    }
}