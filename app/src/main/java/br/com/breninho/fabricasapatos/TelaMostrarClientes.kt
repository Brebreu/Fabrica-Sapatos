package br.com.breninho.fabricasapatos

import android.annotation.SuppressLint
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import br.com.breninho.fabricasapatos.model.Cliente
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TelaMostrarClientes : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            mostrarClientes()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun mostrarClientes() {
    val listState: LazyListState = rememberLazyListState()
    val contexto: Context = LocalContext.current
    val listaClientes: MutableList<Cliente> = remember { mutableStateListOf() }

    // Referência ao nó "clientes" no Firebase Realtime Database
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val clientesRef: DatabaseReference = database.reference.child("clientes")

    // Função para carregar os clientes do Firebase Realtime Database
    fun carregarClientes() {
        clientesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val clientes: MutableList<Cliente> = mutableListOf()

                for (clienteSnapshot in snapshot.children) {
                    val cpf = clienteSnapshot.child("cpf").value.toString()
                    val nome = clienteSnapshot.child("nome").value.toString()
                    val telefone = clienteSnapshot.child("telefone").value.toString()
                    val endereco = clienteSnapshot.child("endereco").value.toString()
                    val insta = clienteSnapshot.child("insta").value.toString()

                    val cliente = Cliente(cpf, nome, telefone, endereco, insta)
                    clientes.add(cliente)
                }
                listaClientes.clear()
                listaClientes.addAll(clientes)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tratamento de erro, se necessário
            }
        })
    }

    // Carregar os clientes ao entrar na tela
    LaunchedEffect(Unit) {
        carregarClientes()
    }

    Column(Modifier.padding(40.dp)) {
        Text(text = "Tela de Apresentação de clientes", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(25.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(listaClientes) { index, cliente ->
                Card(
                    modifier = Modifier.padding(8.dp),
                    onClick = { /* Ação ao clicar no card */ }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "CPF: ${cliente.cpf}")
                        Text(text = "Nome: ${cliente.nome}")
                        Text(text = "Telefone: ${cliente.telefone}")
                        Text(text = "Endereço: ${cliente.endereco}")
                        Text(text = "Instagram: ${cliente.insta}")

                        MenuTresPontosOpcoes(cliente)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTresPontosOpcoes(cliente: Cliente) {
    val contexto: Context = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val clientesRef = database.getReference("clientes")
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
                intent.putExtra("cliente", cliente)
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
        ExibirDialogExclusaoCliente(contexto,cliente,clientesRef)
    }
}


@Composable
fun ExibirDialogExclusaoCliente(contexto: Context, cliente: Cliente, clientesRef: DatabaseReference) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Excluir cliente") },
            text = { Text("Tem certeza de que deseja excluir este cliente?") },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        val cpfClean = cliente.cpf.replace(".", "").replace("-", "")
                        clientesRef.child(cpfClean).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    snapshot.ref.removeValue()
                                    Toast.makeText(
                                        contexto,
                                        "Cliente excluído com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        contexto,
                                        "Cliente não encontrado.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    contexto,
                                    "Erro ao excluir o cliente.",
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