package br.com.breninho.fabricasapatos.Pedido

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import br.com.breninho.fabricasapatos.model.Cliente
import br.com.breninho.fabricasapatos.model.Pedido
import br.com.breninho.fabricasapatos.model.Produto
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate


class TelaInserePedidos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val produtosAdicionados = SnapshotStateMap<Produto,Int>()
        Log.i("Teste", "Teste0")
        val register =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.i("Teste", "Teste0")
                if (result.resultCode == ComponentActivity.RESULT_OK) {
                    result.data?.let {
                        if (it.hasExtra("lista_produtos_adicionados")) {
                            Log.i("Teste", "Teste1")
                            val listaRetorno =
                                it.getSerializableExtra("lista_produtos_adicionados") as? ArrayList<Map<Produto,Int>>
                            Log.i("Teste", "Teste1")
                            if (listaRetorno != null) {
                                produtosAdicionados.clear()
                                listaRetorno.forEach {item->
                                    item.forEach{itemMap->
                                        produtosAdicionados[itemMap.key] = itemMap.value
                                    }
                                }
                                Log.i("Teste", produtosAdicionados.toString())
                            }
                        }
                    }
                }
            }
        super.onCreate(savedInstanceState)
        setContent {
            InserePedidos("Android", produtosAdicionados = produtosAdicionados, register = register)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun InserePedidos(name: String, modifier: Modifier = Modifier, produtosAdicionados: SnapshotStateMap<Produto, Int>, register: ActivityResultLauncher<Intent>) {
    var id_Pedido by remember { mutableStateOf(TextFieldValue("")) }
    var data by remember { mutableStateOf(TextFieldValue("")) }
    var id_cliente by remember { mutableStateOf(TextFieldValue("")) }
    var listaProdutos = HashMap<String,Int>()
    var clienteOk by remember { mutableStateOf(false) }
    var carregamento by remember{ mutableStateOf(true)}
    var referencia: DatabaseReference = Firebase.database.getReference("/clientes")
    var referenciaProduto = Firebase.database.getReference("/produtos")
    val clientesState = remember { mutableStateListOf<Cliente>() }
    val listState = rememberLazyListState()
    var expanded by remember{ mutableStateOf(false) }
    var selectedText by remember { mutableStateOf<Cliente?>(null) }
    val contexto = LocalContext.current
    val produtoState = remember { mutableStateListOf<Produto>() }
    val produtoList = ArrayList<Produto>()
    val produtoSelecionados = HashMap<Produto,Int>()
    var referenciaPedido = Firebase.database.getReference("/pedidos")
    val activity = (LocalContext.current as? Activity)

    if(carregamento) {
        LoadingIndicator()
        LaunchedEffect(Unit){
            val snapshot = withContext(Dispatchers.IO) {
                referencia.get().await() // Obtém uma única vez os dados do banco de dados
            }
            if (snapshot.exists()) {
                val gson = Gson()
                for (i in snapshot.children) {
                    val json = gson.toJson(i.value)
                    val cliente = gson.fromJson(json, Cliente::class.java)
                    clientesState.add(
                        Cliente(
                            cliente.cpf,
                            cliente.nome,
                            cliente.telefone,
                            cliente.endereco,
                            cliente.insta
                        )
                    )

                }
                Log.i("listaBanco", clientesState.toString())
            }
        }
        LaunchedEffect(Unit) {
            val snapshot = withContext(Dispatchers.IO) {
                referenciaProduto.get().await() // Obtém uma única vez os dados do banco de dados
            }

            if (snapshot.exists()) {
                for (i in snapshot.children) {
                    val gson = Gson()
                    val json = gson.toJson(i.value)
                    val produto = gson.fromJson(json, Produto::class.java)
                    produtoState.add(
                        Produto(
                            produto.id_Produto,
                            produto.descricao,
                            produto.valor,
                            produto.foto
                        )
                    )
                    carregamento = false
                }
                Log.i("listaBanco", produtoState.toString())
            }
        }
    }
    else{

        if(!clienteOk){
            if (clientesState.isNotEmpty()) {
                if (selectedText == null) {
                    selectedText = clientesState[0]
                }
            }
            clienteOk = true
        }
        Row(
            modifier = Modifier
                .padding(top = 48.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center

        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Inserir pedido.")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Selecione o cliente.")
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {

                    TextField(
                        value = "id: "+ selectedText!!.cpf+", Nome: " + selectedText!!.nome,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false}
                    ) {
                        clientesState.forEach { item->
                            DropdownMenuItem(
                                text = {Text(text = "id: "+item.cpf+", Nome: " + item.nome)},
                                onClick = {
                                    selectedText = item
                                    expanded = false
                                    Toast.makeText(contexto, "Cliente selecionado", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = id_Pedido,
                    onValueChange = { newText ->
                        id_Pedido = newText
                    },
                    label = { Text(text = "Informe o id do pedido")}
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        produtoList.clear()
                        produtoSelecionados.clear()
                        produtoState.forEach { item->
                            produtoList.add(item)
//                            produtosAdicionados.add(item)
                        }
                        produtosAdicionados.forEach {item->
                            produtoSelecionados.put(item.key,item.value)
                        }
                        val aux = ArrayList<Map<Produto,Int>>()
                        aux.add(produtoSelecionados)
                        Log.i("map",produtoSelecionados.toString())

                        register.launch(
                            Intent(contexto,TelaEscolherProduto::class.java).let {
                                it.putExtra("lista_produto",produtoList)
                                it.putExtra("lista_produtos_adicionados",aux)
                            }
                        ) },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = "Escolher produtos")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        listaProdutos.clear()
                        produtosAdicionados.forEach {item->
                            listaProdutos[item.key.id_Produto.toString()] = item.value
                        }
                        val pedido = Pedido(id_Pedido.text,
                            LocalDate.now().toString(),selectedText!!.cpf,listaProdutos)
                        Log.i("map",pedido.toString())
                        Log.i("pedido_teste",pedido.toString())
                        referenciaPedido.child(pedido.id_Pedido).setValue(pedido)
                            .addOnSuccessListener {
                                Toast.makeText(contexto, "Pedido inserido com sucesso", Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            }
                            .addOnFailureListener{
                                Toast.makeText(contexto, "Falha ao inserir o pedido", Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            }
                    },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = "Inserir Pedido")
                }

            }
        }
    }
}

@Composable
fun LoadingIndicator(){
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )
    }
}