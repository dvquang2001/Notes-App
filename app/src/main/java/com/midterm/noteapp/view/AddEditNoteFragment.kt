package com.midterm.noteapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.midterm.noteapp.NoteApplication
import com.midterm.noteapp.data.Note
import com.midterm.noteapp.databinding.FragmentAddEditNoteBinding
import com.midterm.noteapp.viewmodel.NoteViewModel
import com.midterm.noteapp.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddEditNoteFragment : Fragment() {

    private val navigationArgs: AddEditNoteFragmentArgs by navArgs()
    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database.noteDao()
        )
    }
    lateinit var note: Note

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            GlobalScope.launch(Dispatchers.IO) {
                lifecycleScope.launch {
                    viewModel.retrieveNote(id).collect() { selectedNote ->
                        note = selectedNote
                        bind(note)
                    }
                }
            }
        } else {
            binding.saveAction.setOnClickListener { addNewNote() }
        }
    }

    private fun bind(note: Note) {
        binding.apply {
            edtTitle.setText(note.title, TextView.BufferType.SPANNABLE)
            edtContent.setText(note.content, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateNote() }
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.edtTitle.text.toString(),
            binding.edtContent.text.toString()
        )
    }

    private fun addNewNote() {
        if (isEntryValid()) {
            viewModel.addNewNote(
                binding.edtTitle.text.toString(),
                binding.edtContent.text.toString()
            )
            val action = AddEditNoteFragmentDirections.actionAddEditNoteFragmentToNoteListFragment()
            findNavController().navigate(action)
        }
    }

    private fun updateNote() {
        if (isEntryValid()) {
            viewModel.updateNote(
                navigationArgs.itemId,
                binding.edtTitle.text.toString(),
                binding.edtContent.text.toString()
            )
            val action = AddEditNoteFragmentDirections.actionAddEditNoteFragmentToNoteListFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}