package br.com.breninho.fabricasapatos.model

import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Produto (var id_Produto : Int, var descricao : String, var valor : Float, var foto : String){
}