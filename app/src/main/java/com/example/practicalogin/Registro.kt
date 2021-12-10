package com.example.practicalogin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.CountDownLatch

class Registro : AppCompatActivity() {

    lateinit var img:ImageView
    lateinit var nombre:TextInputEditText
    lateinit var pass:TextInputEditText
    lateinit var pass2:TextInputEditText
    lateinit var platoEstrella:TextInputEditText
    lateinit var registrarse:Button
    lateinit var volver:Button
    var url_usuario: Uri?=null

    lateinit var db_ref: DatabaseReference
    lateinit var sto_ref: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        img=findViewById(R.id.registro_iv_foto)
        nombre=findViewById(R.id.registro_et_nombre)
        pass=findViewById(R.id.registro_et_pass)
        pass2=findViewById(R.id.registro_et_pass2)
        platoEstrella=findViewById(R.id.registro_et_platoEstrella)
        registrarse=findViewById(R.id.registro_btn)
        volver=findViewById(R.id.registro_volver)

        db_ref= FirebaseDatabase.getInstance().reference
        sto_ref= FirebaseStorage.getInstance().reference


        volver.setOnClickListener{
            val actividad = Intent(applicationContext,MainActivity::class.java)
            startActivity (actividad)
        }

        registrarse.setOnClickListener {
            if(pass.text.toString()!=pass2.text.toString()){
                Toast.makeText(applicationContext,
                    "Las contraseñas deben coincidir",
                    Toast.LENGTH_SHORT).show()
            }else{

                val identificador=db_ref.child("foodies")
                    .child("usuarios").push().key


                val fecha= Calendar.getInstance()
                val today=("${fecha.get(Calendar.YEAR)}-${fecha.get(Calendar.MONTH)+1}-${fecha.get(
                    Calendar.DAY_OF_MONTH)}").toString()

                if(url_usuario!=null){

                    GlobalScope.launch(Dispatchers.IO){


                        val url_firebase=sto_ref.child("foodies")
                            .child("usuarios")
                            .child(identificador!!)
                            .putFile(url_usuario!!)
                            .await().storage.downloadUrl.await()


                        val nuevo_usuario=Usuario(identificador,
                            nombre.text.toString().trim(),
                            pass.text.toString().trim(),
                            "1",
                            today.toString(),
                            false,
                            platoEstrella.text.toString().trim(),
                            url_firebase.toString())
                        if( existe_usuario(nombre.text.toString().trim())/*esto tiene que ir dentro de un global Scope*/ ){
                            tostadaCorrutina("El usuario introducido no está disponible")

                        }else{
                            db_ref.child("foodies")
                                .child("usuarios")
                                .child(identificador!!)
                                .setValue(nuevo_usuario)

                            tostadaCorrutina("Usuario Registrado")


                            val actividad = Intent(applicationContext,MainActivity::class.java)
                            startActivity (actividad)
                        }

                    }

                }else{
                    Toast.makeText(applicationContext,
                        "Tienes que poner una foto tuya!",
                        Toast.LENGTH_SHORT).show()
                }

            }
        }

        img.setOnClickListener {
            obtener_url.launch("image/*")

        }

    }

    val obtener_url= registerForActivityResult(ActivityResultContracts.GetContent())
    {
            uri:Uri?->

        if (uri==null){
            Toast.makeText(applicationContext,"No has seleccionado una imagen",
                Toast.LENGTH_SHORT).show()
        }else{

            url_usuario=uri

            img.setImageURI(uri)

            Toast.makeText(applicationContext,"Imagen seleccionada",
                Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun existe_usuario(nombre:String):Boolean{
        var resultado:Boolean?=false

        val semaforo= CountDownLatch(1)
        db_ref.child("foodies")
            .child("usuarios")
            .orderByChild("nombre")
            .equalTo(nombre)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){
                        resultado=true;
                    }
                    semaforo.countDown()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        semaforo.await();

        return resultado!!;
    }



    suspend fun insertarimg(id:String,imagen:Uri):String{
        return ""
    }


    suspend fun insertarusuario(id:String,nombre:String,contraseña:String,tipo:String,today:String,privado:Boolean,platoEstrella:String,url_firebase:String){


    }

    suspend fun tostadaCorrutina(texto:String){
        runOnUiThread({
            Toast.makeText(
                applicationContext,
                texto,
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}