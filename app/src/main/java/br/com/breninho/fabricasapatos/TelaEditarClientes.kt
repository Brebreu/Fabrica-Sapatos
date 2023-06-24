package br.com.breninho.fabricasapatos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.breninho.fabricasapatos.model.Cliente
import com.google.firebase.database.FirebaseDatabase

class TelaEditarClientes : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val cliente = intent.getParcelableExtra<Cliente>("cliente")
            if (cliente != null) {
                EditaClientes(cliente)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditaClientes(cliente: Cliente) {
    var contexto = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val clientesRef = database.getReference("clientes")
    var cpf by remember { mutableStateOf(cliente.cpf) }
    var nome by remember { mutableStateOf(cliente.nome) }
    var telefone by remember { mutableStateOf(cliente.telefone) }
    var endereco by remember { mutableStateOf(cliente.Endereco) }
    var instagram by remember { mutableStateOf(cliente.insta) }


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
            text = cpf,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = telefone,
            onValueChange = { telefone = it },
            label = { Text("Telefone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = endereco,
            onValueChange = { endereco = it },
            label = { Text("Endereço") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = instagram,
            onValueChange = { instagram = it },
            label = { Text("Instagram") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Button(
            onClick = {
                val clienteAtualizado = Cliente(cpf, nome, telefone, endereco, instagram)
                val cpfClean = cpf.replace(".", "").replace("-", "")
                clientesRef.child(cpfClean).setValue(clienteAtualizado)
                // Voltar para a tela de clientes
                Toast.makeText(contexto,"Cliente editado com sucesso!", Toast.LENGTH_LONG).show()
                contexto.startActivity(Intent(contexto, TelaClientes::class.java))
            },
            enabled = cpf.isNotEmpty() && nome.isNotEmpty() && telefone.isNotEmpty() && endereco.isNotEmpty() && instagram.isNotEmpty(),
            modifier = Modifier.padding(16.dp)

        ) {
            Text(text = "Salvar")
        }

        Button(
            onClick = {
                // Voltar para a tela de clientes sem fazer alterações
                contexto.startActivity(Intent(contexto, TelaClientes::class.java))
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Cancelar")
        }
    }
}

private fun maskCpf(value: String): String {
    val rawValue = value.replace(Regex("[^0-9]"), "")
    val maskedValue = buildString {
        rawValue.take(11).forEachIndexed { index, char ->
            if (index == 3 || index == 6) {
                append('.')
            } else if (index == 9) {
                append('-')
            }
            append(char)
        }
    }
    return maskedValue
}
