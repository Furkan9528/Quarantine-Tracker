package com.example.ppd


import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Classe responsable de la  connexion de l'utilisateur , représentée graphiquement par activity_login.xml
 *
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    //Passe directement a MainActivity quand l'utilisateur est deja connecte
    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            var intent =Intent(this,MainActivity::class.java)
            intent.putExtra("email",user?.email)
            startActivity(intent)
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //just a test for now
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }


        auth= FirebaseAuth.getInstance()
        Register.setOnClickListener {
            var intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        mtpo.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Mot de pass oublié")
            val view = layoutInflater.inflate(R.layout.forgot_password,null)
            val username= view.findViewById<EditText>(R.id.mtpo)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, _ ->
                forgotPassword(username)
            })
            builder.setNegativeButton("Ferme", DialogInterface.OnClickListener { _, _ ->  })
            builder.show()
        }

        Login.setOnClickListener {
            if(checking()){
                val email=Email.text.toString()
                val password= Password.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            var intent =Intent(this,MainActivity::class.java)
                            intent.putExtra("email",email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                        }
                    }
            }
            else{
                Toast.makeText(this,"Enter the Details",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun forgotPassword(username: EditText) {
        if(username.text.toString().isEmpty()){
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()){
            return
        }
        auth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Email envoyé",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checking():Boolean
    {
        if(Email.text.toString().trim{it<=' '}.isNotEmpty()
            && Password.text.toString().trim{it<=' '}.isNotEmpty())
        {
            return true
        }
        return false
    }


}