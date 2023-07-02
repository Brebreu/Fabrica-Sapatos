package br.com.breninho.fabricasapatos.Pedido

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.breninho.fabricasapatos.MainActivity
import br.com.breninho.fabricasapatos.Pedido.ui.theme.FabricaSapatosTheme
import br.com.breninho.fabricasapatos.Produto.TelaInsereProdutos
import br.com.breninho.fabricasapatos.Pedido.TelaMostrarPedidos
import br.com.breninho.fabricasapatos.Produto.TelaMostrarProdutos

class TelaPedidos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Produtos()
        }
    }
}

@Composable
fun Produtos() {
    var contexto = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Escolha sua opção:",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaInserePedidos::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Inserir")
        }
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaMostrarPedidos::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Mostrar")
        }
        Button(
            onClick = { contexto.startActivity(Intent(contexto, MainActivity::class.java)) },//onVoltarClick()
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Voltar")
        }
    }
}

