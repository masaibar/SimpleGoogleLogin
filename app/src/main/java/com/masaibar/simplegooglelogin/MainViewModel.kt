package com.masaibar.simplegooglelogin

import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onClickSignIn(activity: FragmentActivity) {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(
            activity,
            googleSignInOptions
        )

        activity.activityResultRegistry.register(
            "key",
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.data == null) {
                return@register
            }

            GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.getResult(ApiException::class.java)?.let {
                            firebaseAuthWithGoogle(it)
                        }
                    }
                }
        }.launch(googleSignInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        firebaseAuth.signInWithCredential(
            GoogleAuthProvider.getCredential(
                account.idToken,
                null
            )
        ).addOnSuccessListener {
            Log.d("!!!!", "${it.user?.uid}")
        }
    }
}
