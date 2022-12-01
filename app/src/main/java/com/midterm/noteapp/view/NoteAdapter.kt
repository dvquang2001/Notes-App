package com.midterm.noteapp.view

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.midterm.noteapp.data.Note
import com.midterm.noteapp.databinding.NoteItemBinding


class NoteAdapter(
    private val onItemClicked: (Note) -> Unit,
    val onDeleteClicked: (Note) -> Unit,
    val onRestoreClicked: (Note) -> Unit
) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(Diffcallback) {


    class NoteViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, onRestore: (Note) -> Unit, onDelete: (Note) -> Unit) {
            binding.apply {
                tvDate.text = note.date
                tvDescription.text = note.title
                if (note.isDelete) {
                    imgDelete.visibility = View.VISIBLE
                    imgRestore.visibility = View.VISIBLE
                } else {
                    imgDelete.visibility = View.GONE
                    imgRestore.visibility = View.GONE
                }

                imgRestore.setOnClickListener {
                    onRestore(note)
                }
                imgDelete.setOnClickListener {
                    onDelete(note)
                }
            }
        }
    }

    companion object {
        private val Diffcallback = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onViewRecycled(holder: NoteViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentNote)
        }
        holder.bind(note = currentNote, onRestore = onRestoreClicked, onDelete = onDeleteClicked)
    }
}