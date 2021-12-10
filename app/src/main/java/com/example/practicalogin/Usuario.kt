package com.example.practicalogin

import java.io.Serializable

data class Usuario(var id:String?=null,
                 var nombre: String? = null,
                 var contraseña:String?=null,
                 var tipo:String?=null,
                 var fecha:String?=null,
                 var privado:Boolean?=null,
                 var platoEstrella:String?=null,
                 var url_usuario:String?=null):Serializable
