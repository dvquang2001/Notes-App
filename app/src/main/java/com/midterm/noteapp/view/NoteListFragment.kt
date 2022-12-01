package com.midterm.noteapp.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.midterm.noteapp.NoteApplication
import com.midterm.noteapp.R
import com.midterm.noteapp.data.Note
import com.midterm.noteapp.databinding.FragmentNoteListBinding
import com.midterm.noteapp.viewmodel.NoteViewModel
import com.midterm.noteapp.viewmodel.NoteViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database.noteDao()
        )
    }

    private var isIconDown = true
    private var isTitleSort = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NoteAdapter({
            val action =
                NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(it.id)
            findNavController().navigate(action)
        }, {}, {})
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        GlobalScope.launch(Dispatchers.IO) {
            lifecycleScope.launch {
                viewModel.notesByTitleDesc.collect { notes ->
                    adapter.submitList(notes)
                    checkStatus(notes)
                }
            }
        }
        binding.floatActionButton.setOnClickListener {
            val action = NoteListFragmentDirections.actionNoteListFragmentToAddEditNoteFragment(
                getString(
                    R.string.add_note_title
                )
            )
            findNavController().navigate(action)
        }

        binding.tvOption.setOnClickListener {
            isTitleSort = !isTitleSort
            setTextOptions()
            retrieveListByOption(adapter)
        }

        binding.imgOption.setOnClickListener {
            isIconDown = !isIconDown
            setIconOptions()
            retrieveListByOption(adapter)
        }
    }

    private fun checkStatus(notes: List<Note>) {
        if (notes.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvStatus.visibility = View.GONE
            if (notes.size == 1) {
                binding.tvAmountNote.text = getString(R.string.amount, notes.size)
            } else {
                binding.tvAmountNote.text = getString(R.string.amount_plural, notes.size)
            }
        } else {
            binding.tvAmountNote.text = getString(R.string.amount, 0)
            binding.recyclerView.visibility = View.GONE
            binding.tvStatus.visibility = View.VISIBLE
        }
    }

    private fun setTextOptions() {
        if (isTitleSort) {
            binding.tvOption.text = getString(R.string.title)
        } else {
            binding.tvOption.text = getString(R.string.date_created)
        }
    }

    private fun setIconOptions() {
        if (isIconDown) {
            binding.imgOption.setImageResource(R.drawable.ic_arrow_down)
        } else {
            binding.imgOption.setImageResource(R.drawable.ic_arrow_up)
        }
    }

    private fun retrieveListByOption(adapter: NoteAdapter) {
        if (isTitleSort) {
            if (isIconDown) {
                GlobalScope.launch(Dispatchers.IO) {
                    lifecycleScope.launch {
                        viewModel.notesByTitleDesc.collect { notes ->
                            adapter.submitList(notes)
                            checkStatus(notes)
                        }
                    }
                }
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    lifecycleScope.launch {
                        viewModel.notesByTitleAsc.collect { notes ->
                            adapter.submitList(notes)
                            checkStatus(notes)
                        }
                    }
                }
            }
        } else {
            if (isIconDown) {
                GlobalScope.launch(Dispatchers.IO) {
                    lifecycleScope.launch {
                        viewModel.notesByIdDesc.collect { notes ->
                            adapter.submitList(notes)
                            checkStatus(notes)
                        }
                    }
                }
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    lifecycleScope.launch {
                        viewModel.notesByIdAsc.collect { notes ->
                            adapter.submitList(notes)
                            checkStatus(notes)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.layout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.recycle_bin_menu -> {
                val action = NoteListFragmentDirections.actionNoteListFragmentToRecycleBinFragment()
                findNavController().navigate(action)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}