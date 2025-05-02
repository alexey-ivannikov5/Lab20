package ru.alexeyivannikov.lab20

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.alexeyivannikov.lab20.databinding.FragmentExplanationBinding


class ExplanationFragment : Fragment() {

    private var _binding: FragmentExplanationBinding? = null
    private val binding: FragmentExplanationBinding
        get() = _binding ?: throw RuntimeException()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplanationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnOpenSettings.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                "package:ru.alexeyivannikov.lab20".toUri()
            )
            startActivity(intent)
        }

        binding.btnReject.setOnClickListener {
            findNavController().navigate(R.id.colorFragment)
        }
    }


}