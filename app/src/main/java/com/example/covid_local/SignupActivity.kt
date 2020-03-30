package com.example.covid_local

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SignupActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var location: AutoCompleteTextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var enter: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        email = findViewById(R.id.enter_email)
        password = findViewById(R.id.enter_password)
        enter = findViewById(R.id.go_button)
        firebaseAuth = FirebaseAuth.getInstance()

        val preferences: SharedPreferences = getSharedPreferences(
            "covid-local",
            Context.MODE_PRIVATE
        )

        val countries = arrayOf(
            "United States", "China", "United Kingdom", "Belgium", "France", "Italy", "Germany", "Spain"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, countries
        )
        location = findViewById<AutoCompleteTextView>(R.id.enter_location)
        location.setAdapter(adapter)

        enter.setOnClickListener {
            val inputtedEmail: String = email.text.toString().trim()
            val inputtedPassword:String = password.text.toString().trim()
            val inputtedLocation:String = location.text.toString().trim()
            firebaseAuth.createUserWithEmailAndPassword(
                inputtedEmail, inputtedPassword
            ).addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    preferences
                        .edit()
                        .putString("email", inputtedEmail)
                        .putString("password", inputtedPassword)
                        .putString("location", inputtedLocation)
                        .apply()
                    //add the user's info to the database
                    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
                    val fixedEmail = inputtedEmail.replace(".", "")
                    val reference = firebaseDatabase.getReference("users/$fixedEmail")
                    reference.setValue(inputtedLocation)
                    val user = firebaseAuth.currentUser
                    Toast.makeText(
                        this,
                        "Registered successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    //TODO: send user to next screen
                } else {
                    val exception: Exception = task.exception!!
                    Toast.makeText(
                        this,
                        "Registration failed: $exception",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}