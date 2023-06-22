package br.com.breninho.fabricasapatos.model

import java.util.Date

data class Pedido(var id_Pedido : Int, var data : Date, var fk_cpf : Int) {
}