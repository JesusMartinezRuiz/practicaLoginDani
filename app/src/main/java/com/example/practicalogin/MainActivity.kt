package com.example.practicalogin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch



class MainActivity : AppCompatActivity() {

    lateinit var name:TextInputEditText
    lateinit var pass:TextInputEditText
    lateinit var login:Button
    lateinit var registrar:TextView
    lateinit var db_ref: DatabaseReference
    lateinit var sto_ref: StorageReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        name=findViewById(R.id.main_et_nombre)
        pass=findViewById(R.id.main_et_contraseña)
        login=findViewById(R.id.main_btn_login)
        registrar=findViewById(R.id.main_tv_newAcc)

        db_ref= FirebaseDatabase.getInstance().reference
        sto_ref= FirebaseStorage.getInstance().reference

        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP_Login"
        var SP = getSharedPreferences(sp_name,0)


        login.setOnClickListener {

            db_ref.child("foodies")
                .child("usuarios")
                .orderByChild("nombre")
                .equalTo(name.text.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.hasChildren()){
                            var pojo_usuario= snapshot.children.iterator().next().getValue(Usuario::class.java)!!

                            if(pojo_usuario.nombre.equals(name.text.toString()) && pojo_usuario.contraseña.equals(pass.text.toString())){

                                with(SP.edit()){
                                    putString(
                                        getString(R.string.id),
                                        pojo_usuario.id
                                    )

                                    putString(
                                        getString(R.string.username),
                                        pojo_usuario.nombre
                                    )

                                    putString(
                                        getString(R.string.type),
                                        pojo_usuario.tipo
                                    )
                                    commit()
                                }



                                    val actividad = Intent(applicationContext,BienvenidoUsuario::class.java)
                                    startActivity (actividad)


                            }else if(name.text.toString().equals("") || pass.text.toString().equals("")){
                                Toast.makeText(applicationContext, "Por favor Rellene todos los datos", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(applicationContext, "Datos introducidos incorrectos", Toast.LENGTH_SHORT).show()

                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })





        }
        registrar.setOnClickListener {
            val actividad = Intent(applicationContext,Registro::class.java)
            startActivity (actividad)
        }
    }

}