package com.midterm.noteapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.midterm.noteapp.NoteApplication
import com.midterm.noteapp.R
import com.midterm.noteapp.data.Note
import com.midterm.noteapp.databinding.FragmentNoteDetailBinding
import com.midterm.noteapp.viewmodel.NoteViewModel
import com.midterm.noteapp.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NoteDetailFragment : Fragment() {

    private val navigationArgs: NoteDetailFragmentArgs by navArgs()

    private var _binding: FragmentNoteDetailBinding? = null
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
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.id
        GlobalScope.launch(Dispatchers.IO) {
            lifecycleScope.launch {
                viewModel.retrieveNote(id).collect() { selectedNote ->
                    note = selectedNote
                    bind(note)
                }
            }
        }
    }

    private fun bind(note: Note) {
        binding.apply {
            tvDate.text = note.date
            tvTitle.text = note.title
            tvContent.text = note.content
            btnRemoveNote.setOnClickListener { showConfirmDialog() }
            btnEditNote.setOnClickListener { editNote() }
        }
    }

    private fun editNote() {
        val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToAddEditNoteFragment(
            getString(R.string.edit_note_title),
            note.id
        )
        findNavController().navigate(action)
    }

    private fun removeNote() {
        viewModel.removeNote(note)
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_alert_title))
            .setMessage(getString(R.string.remove_question))
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                removeNote()
                findNavController().popBackStack()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}