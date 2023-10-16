package com.example.blueleaf.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.blueleaf.R
import com.example.blueleaf.contentsList.UserModel
import com.example.blueleaf.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding

    //Database Reference
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        //* Display & Editing UserName (Jinhyun)
        // #1. Variables
        database = Firebase.database.reference
        val userUID = Firebase.auth.currentUser?.uid
        var userName: String = "잎파랑이 사용자"
        var userEmail: String = "temporary@email.com"

        // #2. Function
        fun setUserName(newName: String){
            binding.homeUsernameTextView.text = newName
            binding.homeUsernameEditText.hint = newName

        }
        fun setEditMode(isEditing: Boolean){
            if(isEditing){
                binding.homeUsernameTextView.visibility = TextView.INVISIBLE
                binding.homeEditImageView.visibility = ImageView.GONE
                binding.homeUsernameEditText.visibility = TextView.VISIBLE
                binding.homeSaveImageView.visibility = ImageView.VISIBLE
            }
            else{
                binding.homeUsernameTextView.visibility = TextView.VISIBLE
                binding.homeEditImageView.visibility = ImageView.VISIBLE
                binding.homeUsernameEditText.visibility = TextView.GONE
                binding.homeSaveImageView.visibility = ImageView.GONE
            }
        }

       try {
           // #3. Details - Display UserName & Email
           if (userUID == null) {
               throw Exception("userUID is null")
           }
           //Gets the user's name
           database.child("users").child(userUID!!).child("userName").get()
               .addOnSuccessListener {
                   userName = it.value.toString()
                   setUserName(userName)
                   Log.d("Success_userName", "userName = $userName")

               }.addOnFailureListener {
                   setUserName(userName)
                   Log.e("Fail_userName", "Failed to get user name.")
                   throw Exception("userName not found.")
               }
           //Gets the user's email
           database.child("users").child(userUID!!).child("userEmail").get()
               .addOnSuccessListener {
                   userEmail = it.value.toString()
                   binding.homeEmailTextView.text = userEmail
                   Log.d("Success_userEmail", "userEmail = $userEmail")

               }.addOnFailureListener {
                   binding.homeEmailTextView.text = userEmail
                   Log.e("Fail_userEmail", "Failed to get user email.")
                   throw Exception("userEmail not found.")
               }

           // #4. Details - Editing UserName
           binding.homeEditImageView.setOnClickListener {
               setEditMode(true)
               binding.homeSaveImageView.setOnClickListener {
                   //Edit Local
                   val newUserName: String = binding.homeUsernameEditText.text.toString()
                   setUserName(newUserName)

                   //Edit Database
                   database.child("users").child(userUID!!).child("userName").setValue(newUserName)

                   setEditMode(false)
               }
           }
       }catch(e: Exception){
           Toast.makeText(context, "사용자 정보를 불러오는데 실패했습니다.", Toast.LENGTH_LONG).show()
           Log.e("Error", e.message.toString())
       }

        //Navigate
        binding.homeTab.setOnClickListener(){

        }

        binding.informationTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_homeFragment_to_informationFragment)
        }

        binding.boardTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_homeFragment_to_boardFragment)
        }

        binding.bookmarkTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_homeFragment_to_bookmarkFragment)
        }

        binding.plantTab.setOnClickListener(){
            it.findNavController().navigate(R.id.action_homeFragment_to_plantFragment)
        }

        return binding.root
    }

}