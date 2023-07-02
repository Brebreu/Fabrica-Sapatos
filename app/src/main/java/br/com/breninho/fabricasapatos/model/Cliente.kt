package br.com.breninho.fabricasapatos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cliente(var cpf : String,var nome : String, var telefone : String, var endereco : String, var insta : String ) : Parcelable
