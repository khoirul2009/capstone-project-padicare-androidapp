package com.padicare.ui.forum

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.padicare.R
import com.padicare.databinding.FragmentForumBinding
import com.padicare.ui.addPost.AddPostActivity

class ForumFragment : Fragment() {

    companion object {
        fun newInstance() = ForumFragment()
    }

    private lateinit var viewModel: ForumViewModel

    private var _binding : FragmentForumBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForumBinding.inflate(inflater, container, false)

        val root = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddPost.setOnClickListener {
            startActivity(Intent(activity, AddPostActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}