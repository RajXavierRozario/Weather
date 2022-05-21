package com.example.weather.fragments.add

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.data.User
import com.example.weather.data.UserViewModel
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*

class AddFragment : Fragment() {

    private  lateinit var mUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        view.add_btn.setOnClickListener{
            insertDataToDatabase()
        }


        return view
    }

    private fun insertDataToDatabase() {
        val cityName = addCityName.text.toString()

        if(inputCheck(cityName)){
            //Create User Object
            val user = User(0,cityName)
            // Add Data to Database
            mUserViewModel.addUser(user)
            Toast.makeText(requireContext(),"Successfully added!",Toast.LENGTH_LONG).show()
            // Navigate Back
            //findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(),"Please fill out all the fields",Toast.LENGTH_LONG).show()
        }


    }

    private  fun inputCheck(cityName: String): Boolean{
        return !(TextUtils.isEmpty(cityName))
    }


}