package br.com.breninho.fabricasapatos.Produto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.breninho.fabricasapatos.model.Produto
import coil.compose.rememberImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TelaMostrarProdutos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            mostrarProdutos()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mostrarProdutos() {
    val listState: LazyListState = rememberLazyListState()
    val contexto: Context = LocalContext.current
    val listaProdutos: MutableList<Produto> = remember { mutableStateListOf() }

    // Referência ao nó "produtos" no Firebase Realtime Database
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val produtosRef: DatabaseReference = database.reference.child("produtos")

    // Função para carregar os produtos do Firebase Realtime Database
    fun carregarProdutos() {
        produtosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val produtos: MutableList<Produto> = mutableListOf()

                for (produtoSnapshot in snapshot.children) {
                    val id_Produto = produtoSnapshot.child("id_Produto").value.toString().toInt()
                    val descricao = produtoSnapshot.child("descricao").value.toString()
                    val valor = produtoSnapshot.child("valor").value.toString().toFloat()
                    val foto = produtoSnapshot.child("foto").value.toString()

                    val produto = Produto(id_Produto, descricao, valor, foto)
                    produtos.add(produto)
                }
                listaProdutos.clear()
                listaProdutos.addAll(produtos)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tratamento de erro, se necessário
            }
        })
    }


    // Carregar os produto ao entrar na tela
    LaunchedEffect(Unit) {
        carregarProdutos()
    }

    Column(Modifier.padding(40.dp)) {
        Text(text = "Tela de Apresentação de produtos", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(25.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(listaProdutos) { index, produto ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    onClick = { /* Ação ao clicar no card */ }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "ID: ${produto.id_Produto}")
                        Text(text = "Descricao: ${produto.descricao}")
                        Text(text = "Valor: ${produto.valor}")
                        if (produto.foto.isNotEmpty()) {
                            Image(
                                painter = rememberImagePainter(produto.foto),
                                contentDescription = "Foto do Produto",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }

                        MenuTresPontosOpcoes(produto)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTresPontosOpcoes(produto: Produto) {
    val contexto: Context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val produtosRef = database.getReference("produtos")
    var isOpened: Boolean by remember { mutableStateOf(false) }
    val showDialog = remember{ mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { isOpened = !isOpened }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More vert"
            )
        }
        DropdownMenu(expanded = isOpened, onDismissRequest = { isOpened = false }) {
            DropdownMenuItem(text = { Text(text = "Editar") }, onClick = {
                val intent = Intent(contexto, TelaEditarProdutos::class.java)
                intent.putExtra("produto", produto)
                contexto.startActivity(intent)
                isOpened = !isOpened
            })
            DropdownMenuItem(text = { Text(text = "Excluir") }, onClick = {
                //ExibirDialogExclusaoProduto(contexto, produto, produtosRef)
                showDialog.value = true
                isOpened = !isOpened
            })
        }
    }
    if(showDialog.value){
        br.com.breninho.fabricasapatos.Produto.ExibirDialogExclusaoProduto(contexto, produto, produtosRef)
    }
}

@Composable
fun ExibirDialogExclusaoProduto(contexto: Context, produto: Produto, produtosRef: DatabaseReference) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Excluir produto") },
            text = { Text("Tem certeza de que deseja excluir este produto?") },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        val id_Produto = produto.id_Produto.toString()
                        produtosRef.child(id_Produto).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    snapshot.ref.removeValue()
                                    Toast.makeText(
                                        contexto,
                                        "Produto excluído com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        contexto,
                                        "Produto não encontrado.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    contexto,
                                    "Erro ao excluir o produto.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })

                        showDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
