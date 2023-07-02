package br.com.breninho.fabricasapatos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pedido(
    var id_Pedido: String,
    var data: String,
    var fk_cpf: String,
    val listaProdutos: HashMap<String, Int>
) : Parcelable{
}
//Se der BO trocar para String