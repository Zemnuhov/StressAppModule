package com.example.feature_screen_relax_impl

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.feature_item_graph_api.ItemGraphApi
import com.example.feature_screen_relax_impl.databinding.FragmentRelaxBinding
import com.example.feature_screen_relax_impl.di.RelaxScreenComponent
import javax.inject.Inject
import dagger.Lazy

class RelaxFragment: Fragment(R.layout.fragment_relax) {

    @Inject
    lateinit var itemGraphApi: ItemGraphApi

    @Inject
    lateinit var factory: Lazy<RelaxViewModel.Factory>

    private val viewModel: RelaxViewModel by viewModels { factory.get() }

    private var _binding: FragmentRelaxBinding? = null
    private val binding: FragmentRelaxBinding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer



    private val relaxViews by lazy {
        listOf(binding.relaxScaleView, binding.relaxScaleView2)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        RelaxScreenComponent.get().inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelaxBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var previousTonic = 0
        mediaPlayer = MediaPlayer.create(context, R.raw.relax_music)
        mediaPlayer.setOnPreparedListener {
            it.isLooping = true
            it.start()
        }
        binding.apply {
            var lastTimeMillis = 0L
            childFragmentManager.beginTransaction().add(linearLayout5.id, itemGraphApi.getFragment()).commit()
            viewModel.tonic.observe(viewLifecycleOwner){
                tonicValue.text = it.value.toString()
                relaxViews.map { view -> view.value = it.value }
                if (System.currentTimeMillis() - lastTimeMillis > 5000){
                    lastTimeMillis = System.currentTimeMillis()
                    if(it.value <= previousTonic && viewModel.sessionState.value == SessionState.RECORDING){
                        mediaPlayer.setVolume(1F,1F)
                    }
                    if(it.value > previousTonic){
                        mediaPlayer.setVolume(0F,0F)
                    }
                    previousTonic = it.value
                }
            }

            viewModel.beginTonic.observe(viewLifecycleOwner){
                relaxViews.map { view -> view.beginValue = it}
            }
            viewModel.phase.observe(viewLifecycleOwner){
                peaks.text = it.toString()
            }
            viewModel.tonicDifference.observe(viewLifecycleOwner){
                tonicDifference.text = it.toString()
            }
            viewModel.timer.observe(viewLifecycleOwner){
                time.text = it
            }
            beginSession.setOnClickListener {
                //TODO(Надо начать сессию)
            }
        }

        viewModel.sessionState.observe(viewLifecycleOwner){state ->
            when(state){
                SessionState.RECORDING -> binding.beginSession.text = "Остановить сессию"
                else -> {
                    binding.beginSession.text = "Начать сессию"
                    mediaPlayer.setVolume(0F,0F)
                }
            }
        }

        binding.beginSession.setOnClickListener {
            viewModel.processSession()
        }
    }


    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }

}