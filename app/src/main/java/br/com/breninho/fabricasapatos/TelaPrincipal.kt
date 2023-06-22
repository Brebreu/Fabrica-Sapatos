package br.com.breninho.fabricasapatos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.unit.dp
import br.com.breninho.fabricasapatos.Produto.TelaPedidos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelaInicial();
            MenuTresPontos()
            BoasVindas()
        }
    }
}
@Composable
fun TelaInicial() {
    val contexto : Context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaClientes::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Clientes")
        }
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaPedidos::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Produtos")
        }
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaClientes::class.java))
            },
            modifier = Modifier.width(300.dp),
        ) {
            Text(text = "Pedidos")
        }
    }
}

@Composable
fun BoasVindas() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Bem-vindo(a) vendedor(a)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            Image(
                painter = painterResource(R.drawable.logo_fabrica),
                contentDescription = "Imagem",
                modifier = Modifier
                    .size(160.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun MenuTresPontos(){
    val contexto : Context = LocalContext.current
    var isOpened : Boolean by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.TopEnd)){
        IconButton(onClick = {isOpened = !isOpened}) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More vert"
            )
        }
        DropdownMenu(expanded = isOpened, onDismissRequest = {isOpened = false}) {
           DropdownMenuItem(text = { Text(text = "Créditos")}, onClick = {Toast.makeText(contexto,"Folhas Secas", Toast.LENGTH_LONG).show()
                isOpened = !isOpened
           })
            DropdownMenuItem(text = { Text(text = "Avaliar App")}, onClick = {Toast.makeText(contexto,"Em breve.....", Toast.LENGTH_LONG).show()
                isOpened = !isOpened
            })
        }
    }
}
