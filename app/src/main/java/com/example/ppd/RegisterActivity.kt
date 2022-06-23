package com.example.ppd
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //supportActionBar!!.setDisplayShowHomeEnabled(true)
        Places.initialize(applicationContext, "AIzaSyAkjSBvNg2mqI_GgCW1-sMKqPog1IUQ8To")

        Address.setOnClickListener(View.OnClickListener {
            val fieldList = Arrays.asList(
                Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME
            )
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this@RegisterActivity)
            startActivityForResult(intent, 100)
        })

        //just a test for now
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        Retour.setOnClickListener {
            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        auth= FirebaseAuth.getInstance()
        db= FirebaseFirestore.getInstance()
        Continue.setOnClickListener {
            if(checking())
            {
                var email=EmailRegister.text.toString()
                var password= PasswordRegister.text.toString()
                passwordCharacter(password)
                var name=Name.text.toString()
                var address=Address.text.toString()

                var date=Dates.text.toString()
                val user= hashMapOf(
                    "Name" to name,
                    "Address" to address,
                    "Date" to date,
                    "email" to email
                )
                val Users=db.collection("USERS")
                val query =Users.whereEqualTo("email",email).get()
                    .addOnSuccessListener {
                            tasks->
                        if(tasks.isEmpty)
                        {
                            auth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(this){
                                        task->
                                    if(task.isSuccessful)
                                    {
                                        Users.document(email).set(user)
                                        val intent=Intent(this,MapsActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else
                                    {
                                        Toast.makeText(this,"Authentication Failed : ", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                        else
                        {
                            Toast.makeText(this,"User Already Registered", Toast.LENGTH_LONG).show()
                            val intent= Intent(this,LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
            }
            else{
                Toast.makeText(this,"Enter the Details", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(data)
            Address.setText(place.address)
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(data)
            Toast.makeText(applicationContext, status.statusMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun passwordCharacter(password: String) {
        if(password.length < 8 && !password.matches(".*[A-Z].*".toRegex()) && !password.matches(".*[a-z].*".toRegex()) && !password.matches(".*[@#\$%^&+=].*".toRegex()) ){
            Toast.makeText(this,"Doit contenir 8 caractères,1 minuscule, 1 majuscule, 1 spécial", Toast.LENGTH_LONG).show()
        }
    }

    private fun checking():Boolean{
        if(Name.text.toString().trim{it<=' '}.isNotEmpty()
            && Address.text.toString().trim{it<=' '}.isNotEmpty()
            && Dates.text.toString().trim{it<=' '}.isNotEmpty()
            && EmailRegister.text.toString().trim{it<=' '}.isNotEmpty()
            && PasswordRegister.text.toString().trim{it<=' '}.isNotEmpty()
        )
        {
            return true
        }
        return false
    }
}