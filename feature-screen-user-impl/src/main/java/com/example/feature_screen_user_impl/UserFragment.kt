package com.example.feature_screen_user_impl

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.cesarferreira.tempo.Tempo
import com.cesarferreira.tempo.toString
import com.example.feature_screen_user_impl.databinding.FragmentUserBinding
import com.example.feature_screen_user_impl.di.UserScreenComponent
import com.google.firebase.auth.FirebaseUser
import com.neurotech.core_database_api.model.UserParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@RequiresApi(Build.VERSION_CODES.N)
class UserFragment: Fragment(R.layout.fragment_user) {

    @Inject
    lateinit var factory: Provider<UserViewModel.Factory>

    private val viewModel: UserViewModel by viewModels {
        factory.get()
    }

    private var _binding: FragmentUserBinding? = null
    private val binding: FragmentUserBinding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        UserScreenComponent.get().inject(this)
        viewModel.register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.switchButton.setText("День", "Месяц", "Год")
        fillUserData()
        setAuthListeners()
        editListeners()
        fillUserData()
        fillUserNormalParams()
        CoroutineScope(Dispatchers.IO).launch {
            binding.switchButton.state.collect {
                when (it) {
                    1 -> viewModel.setOneDayInterval()
                    2 -> viewModel.setOneMonthInterval()
                    3 -> viewModel.setOneYearInterval()
                }
            }
        }
        viewModel.firebaseUser.observe(viewLifecycleOwner){
            fillProfileData(it)
        }
        viewModel.userParameter.observe(viewLifecycleOwner){
            fillUserParams(it)
        }
    }

    fun fillUserParams(params: UserParameters){
        binding.avgTonic.text = params.tonic.toString()
        binding.avgPeakCountInDayTextView.text = params.dayPhase.toString()
        binding.avgPeakCountInTenMinuteTextView.text = params.tenMinutePhase.toString()

    }

    private fun fillUserNormalParams(){
        binding.normalTonic.text = viewModel.user.tonicAvg.toString()
        binding.normalPeakCountInTenMinuteTextView.text = viewModel.user.phaseNormal.toString()
        binding.normalPeakCountInDayTextView.text = viewModel.user.phaseInDayNormal.toString()
    }

    private fun editListeners(){
        binding.dateOfBirthEdit.setOnClickListener {
            val datePicker = DatePickerDialog(requireActivity())
            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                viewModel.setDateOfBirth(
                    Tempo.with(
                        year = year,
                        month = month + 1,
                        day = dayOfMonth
                    )
                )
            }
            datePicker.show()
        }
        binding.genderEdit.setOnClickListener {
            binding.maleCheckBox.visibility = View.VISIBLE
            binding.femaleCheckBox.visibility = View.VISIBLE
            binding.maleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setGender(true)
                    binding.maleCheckBox.visibility = View.GONE
                    binding.femaleCheckBox.visibility = View.GONE
                }
            }
            binding.femaleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.setGender(false)
                    binding.maleCheckBox.visibility = View.GONE
                    binding.femaleCheckBox.visibility = View.GONE

                }
            }
        }
    }

    private fun setAuthListeners(){
        binding.signInButton.setOnClickListener{
            viewModel.singInWithGoogle()
        }
        binding.logOutTextView.setOnClickListener {
            viewModel.singOutWithGoogle()
        }
    }

    private fun fillUserData(){
        if (viewModel.user.dateOfBirth != null){
            binding.ageTextView.visibility = View.VISIBLE
            binding.dateOfBirthEdit.visibility = View.GONE
            val d1: Int = viewModel.user.dateOfBirth!!.toString("yyyyMMdd").toInt()
            val d2: Int = Tempo.now.toString("yyyyMMdd").toInt()
            val age = (d2 - d1) / 10000
            binding.ageTextView.text = "$age"
        }else{
            binding.ageTextView.visibility = View.GONE
            binding.dateOfBirthEdit.visibility = View.VISIBLE
        }

        if (viewModel.user.gender != null){
            binding.genderTextView.visibility = View.VISIBLE
            binding.genderEdit.visibility = View.GONE
            binding.genderTextView.text = viewModel.user.gender
        }else{
            binding.genderTextView.visibility = View.GONE
            binding.genderEdit.visibility = View.VISIBLE
        }

    }

    private fun fillProfileData(user: FirebaseUser?){
        if(user != null){
            binding.userNameTextView.text = user.displayName
            binding.nameTextView.text = user.displayName
            Glide.with(this).load(user.photoUrl).into(binding.imageView)
            binding.signInButton.visibility = View.GONE
            binding.logOutTextView.visibility = View.VISIBLE
        }else{
            binding.nameTextView.text = viewModel.user.name.ifEmpty { "Мы с вами пока не знакомы" }
            binding.signInButton.visibility = View.VISIBLE
            binding.logOutTextView.visibility = View.GONE
            Glide.with(this).clear(binding.imageView)
        }
    }
}