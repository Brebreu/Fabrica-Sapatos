package br.com.breninho.fabricasapatos.model

data class ItemPedido(var id_itemPedido : Int, var fk_idPedido : Int, var fk_idProduto : Int, var quantidade : Int) {
}