package br.com.breninho.fabricasapatos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Produto (var id_Produto : Int, var descricao : String, var valor : Float, var foto : String) : Parcelable
