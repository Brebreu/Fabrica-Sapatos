package br.com.breninho.fabricasapatos

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class TelaClientes() : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            Clientes()
        }
    }
}
@Composable
fun Clientes() {
    var contexto = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Escolha sua opção:",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaInsereClientes::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Inserir")
        }
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaMostrarClientes::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Mostrar")
        }
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaClientes::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Alterar")
        }
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaClientes::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Deletar")
        }
        Button(
            onClick = {contexto.startActivity(Intent(contexto, MainActivity::class.java))},//onVoltarClick()
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Voltar")
        }
    }
}