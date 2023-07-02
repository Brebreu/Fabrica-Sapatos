package br.com.breninho.fabricasapatos.Pedido

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.breninho.fabricasapatos.model.Produto


@Suppress("UNCHECKED_CAST", "DEPRECATION")
class TelaEscolherProduto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = intent?.extras
        val contexto = LocalContext
        bundle?.apply {
            val produto: ArrayList<Produto> = getSerializable("lista_produto") as ArrayList<Produto>
            val produtoAdicionados: Map<Produto,Int> = (getSerializable("lista_produtos_adicionados") as ArrayList<HashMap<Produto,Int>>).get(0)
            setContent {
                Greeting11("Android", produtos = produto, produtosAdicionados = produtoAdicionados){
                                selectedProdutos ->
                    returnSelectedProdutos(selectedProdutos)
                }
            }
        }
    }
    private fun returnSelectedProdutos(selectedProdutos: Map<Produto, Int>) {
        val aux = ArrayList<Map<Produto, Int>>()
        aux.add(selectedProdutos)
        val resultIntent = Intent().apply {
            putExtra("lista_produtos_adicionados", ArrayList(aux))
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}

@Composable
fun Greeting11(
    name: String,
    modifier: Modifier = Modifier,
    produtos: ArrayList<Produto>,
    produtosAdicionados: Map<Produto, Int>,
    onProdutosSelected: (Map<Produto,Int>) -> Unit
) {
    val checkedProdutos = remember { SnapshotStateMap<Produto,Int>() }
    var flag = true

    if(flag){
        produtosAdicionados.forEach {item->
            checkedProdutos.put(key = item.key, value = item.value)
        }
        flag = false
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Escolher produtos.",)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        Log.i("map",checkedProdutos.toMap().toString())
                        onProdutosSelected(checkedProdutos.toMap())
                    }
                ) {
                    Text(text = "Adicionar Produtos.")
                }
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.Center
        ) {
            items(produtos) { produto ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    CardProduto(
                        produto = produto, checkedProdutos = checkedProdutos
                    )
                }
            }
        }
    }
}

@Composable
fun CardProduto(
    produto: Produto,
    checkedProdutos: SnapshotStateMap<Produto, Int>
) {
    val quantidade = remember { mutableStateOf(0) }
    if(checkedProdutos.containsKey(produto)){
        quantidade.value = checkedProdutos.getValue(produto)
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = checkedProdutos.containsKey(produto) && quantidade.value>0,
                    onCheckedChange = { checked ->
                        if (checked) {
                            checkedProdutos.put(key = produto, value = quantidade.value)
                        } else {
                            checkedProdutos.remove(produto)
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                Text(text = "ID: ${produto.id_Produto}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Descrição: ${produto.descricao}")
                Text(text = "Valor: ${produto.valor}")
            }
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = { quantidade.value++ },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "")
                }

                Spacer(modifier = Modifier.height(5.dp))
                Text(text = quantidade.value.toString())
                Spacer(modifier = Modifier.height(5.dp))

                if (quantidade.value > 0) {
                    IconButton(
                        onClick = { quantidade.value-- },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "")
                    }
                }
            }
        }
    }
}

