package br.com.breninho.fabricasapatos

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import br.com.breninho.fabricasapatos.model.Cliente
import com.google.firebase.database.FirebaseDatabase

class TelaInsereClientes : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            InsereClientes()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsereClientes() {
    var contexto = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val clientesRef = database.getReference("clientes")
    var cpf by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var instagram by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Insira os seguintes dados:",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = cpf,
            onValueChange = { newValue ->
                val maskedValue = maskCpf(newValue)
                cpf = maskedValue
            },
            label = { Text("CPF") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle.Default.copy(fontFeatureSettings = "'tnum'"),
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
                val cliente = Cliente(cpf, nome, telefone, endereco, instagram)
                val cpfClean = cpf.replace(".", "").replace("-", "")
                clientesRef.child(cpfClean).setValue(cliente)
                // Limpar os campos de texto
                cpf = ""
                nome = ""
                telefone = ""
                endereco = ""
                instagram = ""
            },
            enabled = cpf.isNotEmpty() && nome.isNotEmpty() && telefone.isNotEmpty() && endereco.isNotEmpty() && instagram.isNotEmpty(),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Enviar")
        }

        Button(
            onClick = {contexto.startActivity(Intent(contexto, TelaClientes::class.java))},//onVoltarClick()
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Voltar")
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
