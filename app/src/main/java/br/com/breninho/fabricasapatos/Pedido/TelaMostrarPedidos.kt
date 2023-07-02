package br.com.breninho.fabricasapatos.Pedido

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import br.com.breninho.fabricasapatos.TelaEditarClientes
import br.com.breninho.fabricasapatos.model.Cliente
import br.com.breninho.fabricasapatos.model.Pedido
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TelaMostrarPedidos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            mostrarPedidos()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mostrarPedidos() {
    val listState: LazyListState = rememberLazyListState()
    val contexto: Context = LocalContext.current
    val listaPedidos: MutableList<Pedido> = remember { mutableStateListOf() }

    // Referência ao nó "pedidos" no Firebase Realtime Database
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val pedidosRef: DatabaseReference = database.reference.child("pedidos")

    // Função para carregar os pedidos do Firebase Realtime Database
    fun carregarPedidos() {
        pedidosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pedidos: MutableList<Pedido> = mutableListOf()

                for (pedidoSnapshot in snapshot.children) {
                    val id_Pedido = pedidoSnapshot.child("id_Pedido").value.toString()
                    val data = pedidoSnapshot.child("data").value.toString()
                    val fk_cpf = pedidoSnapshot.child("fk_cpf").value.toString()

                    // Para a lista de produtos, você precisará adaptar para o HashMap<String, Int>
                    val produtos: HashMap<String, Int> = hashMapOf()
                    val produtosSnapshot = pedidoSnapshot.child("listaProdutos")
                    for (produtoSnapshot in produtosSnapshot.children) {
                        val produtoId = produtoSnapshot.key.toString()
                        val quantidade = produtoSnapshot.value.toString().toInt()
                        produtos[produtoId] = quantidade
                    }

                    val pedido = Pedido(id_Pedido, data, fk_cpf, produtos)
                    pedidos.add(pedido)
                }
                listaPedidos.clear()
                listaPedidos.addAll(pedidos)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tratamento de erro, se necessário
            }
        })
    }

    // Carregar os pedidos ao entrar na tela
    LaunchedEffect(Unit) {
        carregarPedidos()
    }

    Column(Modifier.padding(40.dp)) {
        Text(text = "Tela de Apresentação de pedidos", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(25.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(listaPedidos) { index, pedido ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    onClick = { /* Ação ao clicar no card */ }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "ID: ${pedido.id_Pedido}")
                        Text(text = "Data: ${pedido.data}")
                        Text(text = "CPF: ${pedido.fk_cpf}")

                        // Mostrar a lista de produtos do pedido
                        for ((produtoId, quantidade) in pedido.listaProdutos) {
                            Text(text = "Produto: $produtoId, Quantidade: $quantidade")
                        }

                        // Restante do código...
                        MenuTresPontosOpcoes(pedido)
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTresPontosOpcoes(pedido: Pedido) {
    val contexto: Context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val pedidosRef = database.getReference("pedidos")
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
                val intent = Intent(contexto, TelaEditarClientes::class.java)
                intent.putExtra("pedido", pedido)
                contexto.startActivity(intent)
                isOpened = !isOpened
            })
            DropdownMenuItem(text = { Text(text = "Excluir") }, onClick = {
                //ExibirDialogExclusaoCliente(contexto, cliente, clientesRef)
                showDialog.value = true
                isOpened = !isOpened
            })
        }
    }
    if(showDialog.value){
        ExibirDialogExclusaoPedido(contexto,pedido,pedidosRef)
    }
}


@Composable
fun ExibirDialogExclusaoPedido(contexto: Context, pedido: Pedido, pedidosRef: DatabaseReference) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Excluir pedido") },
            text = { Text("Tem certeza de que deseja excluir este pedido?") },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        //val cpfClean = pedido.cpf.replace(".", "").replace("-", "")
                        val id_Pedido = pedido.id_Pedido.toString()
                        pedidosRef.child(id_Pedido).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    snapshot.ref.removeValue()
                                    Toast.makeText(
                                        contexto,
                                        "Pedido excluído com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        contexto,
                                        "Pedido não encontrado.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    contexto,
                                    "Erro ao excluir o pedido.",
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
