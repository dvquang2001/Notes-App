package com.midterm.noteapp.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.midterm.noteapp.NoteApplication
import com.midterm.noteapp.R
import com.midterm.noteapp.data.Note
import com.midterm.noteapp.databinding.FragmentRecycleBinBinding
import com.midterm.noteapp.viewmodel.NoteViewModel
import com.midterm.noteapp.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RecycleBinFragment : Fragment() {

    private var _binding: FragmentRecycleBinBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database.noteDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecycleBinBinding.inflate(inflater, container, false)
        return binding.root
    }

    lateinit var note: Note

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = NoteAdapter(
            {},
            onDeleteClicked = {
                note = it
                showDeleteConfirmDialog()
            },
            onRestoreClicked = {
                note = it
                showRestoreConfirmDialog()
            }
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        GlobalScope.launch(Dispatchers.IO) {
            lifecycleScope.launch {
                viewModel.notesFromRecycleBin.collect { notes ->
                    adapter.submitList(notes)
                    if (notes.isNotEmpty()) {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.tvStatus.visibility = View.GONE
                        if (notes.size == 1) {
                            binding.tvAmountNote.text = getString(R.string.amount, notes.size)
                        } else {
                            binding.tvAmountNote.text =
                                getString(R.string.amount_plural, notes.size)
                        }
                    } else {
                        binding.tvAmountNote.text = getString(R.string.amount, 0)
                        binding.recyclerView.visibility = View.GONE
                        binding.tvStatus.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun deleteNote(note: Note) {
        viewModel.deleteNote(note)
    }

    private fun restoreNote(note: Note) {
        viewModel.restoreNote(note)
        findNavController().popBackStack()
    }

    private fun showDeleteConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                deleteNote(note)
            }
            .show()
    }

    private fun showRestoreConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_alert_title))
            .setMessage(getString(R.string.restore_question))
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                restoreNote(note)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}