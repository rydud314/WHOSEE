package com.example.obc.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

import com.example.obc.databinding.FragmentProfileBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

import java.util.*

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding?= null
    private val binding get() = _binding!!
    var firestore = FirebaseFirestore.getInstance()
    lateinit var userInfoQuery : DocumentSnapshot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstancestate: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstancestate: Bundle?) {
        super.onViewCreated(view, savedInstancestate)
        val sharedPreferences = requireActivity().getSharedPreferences("other", 0)
        val editor = sharedPreferences.edit()

        CoroutineScope(Dispatchers.IO).launch {
            runBlocking {
                getUserInfo(sharedPreferences.getString("userEmail", "").toString())
            }
            requireActivity().runOnUiThread{
                binding.userName.setText(userInfoQuery.get("userName").toString())
                binding.userEmail.text = userInfoQuery.get("userEmail").toString()
                FirebaseStorage.getInstance().reference.child("userImages").child( userInfoQuery.get("userIdSt").toString()+"/photo").downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(requireActivity()).load(uri).into(binding.userImage)
                }.addOnFailureListener {
                }
            }

        }


    }

    suspend fun getUserInfo(email:String): Boolean {
        return try {
            var state=false
            firestore.collection("UserInfo").document(email).get()
                .addOnSuccessListener { result -> // 성공할 경우
                    userInfoQuery =result
                }
                .addOnFailureListener{
                }.await()
            state
        }catch (e: FirebaseException){
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }

}