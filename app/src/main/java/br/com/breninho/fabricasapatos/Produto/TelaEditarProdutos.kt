package br.com.breninho.fabricasapatos.Produto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.breninho.fabricasapatos.Pedido.TelaPedidos
import br.com.breninho.fabricasapatos.model.Produto
import coil.compose.rememberImagePainter
import com.google.firebase.database.FirebaseDatabase


class TelaEditarProdutos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val produto = intent.getParcelableExtra<Produto>("produto")
            if (produto != null) {
                EditaProdutos(produto)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditaProdutos(produto: Produto) {
    var contexto = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val produtosRef = database.getReference("produtos")
    var id_Produto by remember { mutableStateOf(produto.id_Produto) }
    var descricao by remember { mutableStateOf(produto.descricao) }
    var valor by remember { mutableStateOf(produto.valor) }
    var foto by remember { mutableStateOf(produto.foto) }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Edite os seguintes dados:",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = id_Produto.toString(), // Convertendo para String,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descricao") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = valor.toString(),
            onValueChange = { valor = it.toFloatOrNull() ?: 0f },
            label = { Text("Valor") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        if (produto.foto.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(produto.foto),
                contentDescription = "Foto do Produto",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
//        TextField(
//            value = foto,
//            onValueChange = { foto = it },
//            label = { Text("Foto") },
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//
//        )


        Button(
            onClick = {
                val produtoAtualizado = Produto(id_Produto, descricao, valor, foto)
                produtosRef.child(id_Produto.toString()).setValue(produtoAtualizado)
                // Voltar para a tela de produtos
                Toast.makeText(contexto,"Produto editado com sucesso!", Toast.LENGTH_LONG).show()
                contexto.startActivity(Intent(contexto, TelaPedidos::class.java))
            },
            enabled = id_Produto.toString().isNotEmpty() && descricao.isNotEmpty() && valor.toString().isNotEmpty() && foto.isNotEmpty(),
            modifier = Modifier.padding(16.dp)

        ) {
            Text(text = "Salvar")
        }

        Button(
            onClick = {
                // Voltar para a tela de produtos sem fazer alterações
                contexto.startActivity(Intent(contexto, TelaProdutos::class.java))
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Cancelar")
        }
    }
}

