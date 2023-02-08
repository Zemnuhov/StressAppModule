package com.example.core_firebase_auth_impl

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.core_firebase_auth.FirebaseAuthApi
import com.example.core_firebase_auth_impl.di.FirebaseAuthComponent
import com.example.core_firebase_auth_impl.model.UserFirebase
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.neurotech.core_database_api.UserApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppFirebaseAuth : FirebaseAuthApi {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val firebaseDatabase: FirebaseDatabase = Firebase.database

    @Inject
    lateinit var activity: AppCompatActivity

    @Inject
    lateinit var userApi: UserApi

    private val databaseReference: DatabaseReference by lazy { firebaseDatabase.reference }

    override val user = MutableStateFlow<FirebaseUser?>(null)
    var launcher: ActivityResultLauncher<Intent>? = null

    lateinit var currentFragment: Fragment

    init {
        FirebaseAuthComponent.get().inject(this)
        user.value = firebaseAuth.currentUser
    }

    suspend fun registerUserInLocalDB() {
        databaseReference.child("users").child(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val userData = it.getValue<UserFirebase>()
                CoroutineScope(Dispatchers.IO).launch {
                    userApi.registerUser(userData!!.toUserEntity())
                }
            }.addOnFailureListener {
            Log.e("Firebase read error: ", "$it")
        }
    }


    override fun registerResultActivity(fragment: Fragment) {
        currentFragment = fragment
        launcher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
                user.value = firebaseAuth.currentUser
            } catch (e: ApiException) {
                Log.w("TAG", "Google sign in failed", e)
            }

        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(currentFragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    CoroutineScope(Dispatchers.IO).launch {
                        registerUserInLocalDB()
                    }
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                }
            }
    }


    override suspend fun singInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(currentFragment.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(currentFragment.requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent
        launcher?.launch(signInIntent)
    }


    override suspend fun singOutWithGoogle() {
        firebaseAuth.signOut()
        user.value = firebaseAuth.currentUser
    }


}