package br.com.breninho.fabricasapatos.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemPedido(var id_itemPedido : String, var fk_idPedido : String, var fk_idProduto : Int, var quantidade : Int): Parcelable {}