package br.com.breninho.fabricasapatos.Produto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.breninho.fabricasapatos.model.Produto
import coil.compose.rememberImagePainter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class TelaInsereProdutos : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InsereProdutos()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsereProdutos() {
    var contexto = LocalContext.current
    val database = FirebaseDatabase.getInstance()
    val produtosRef = database.getReference("produtos")
    var idProduto by remember { mutableStateOf(0) }
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf(0f) }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    // Registrar o ActivityResultLauncher para a seleção de imagem
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fotoUri = uri
    }
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
            value = idProduto.toString(),
            onValueChange = { idProduto = it.toIntOrNull() ?: 0 },
            label = { Text("ID do Produto") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        TextField(
            value = valor.toString(),
            onValueChange = { valor = it.toFloatOrNull() ?: 0f },
            label = { Text("Valor") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Button(
            onClick = {
                imagePickerLauncher.launch("image/*") // Abrir a galeria de fotos
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Selecionar Foto")
        }
        fotoUri?.let { uri ->
            Image(
                painter = rememberImagePainter(uri),
                contentDescription = "Foto do Produto",
                modifier = Modifier
                    .size(128.dp)
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
            )
        }
        Button(
            onClick = {
                // Verifique se há uma foto selecionada
                if (fotoUri != null) {
                    // Obtenha a referência para o Storage do Firebase onde deseja salvar a foto
                    val storageRef = Firebase.storage.reference.child("produtos").child("${idProduto}.jpg")
                    // Carregue a foto no Storage
                    val uploadTask = storageRef.putFile(fotoUri!!)
                    uploadTask.addOnSuccessListener {
                        // A foto foi carregada com sucesso
                        // Obtenha a URL da foto no Storage
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Salve a URL da foto junto com os outros campos do produto no banco de dados
                            val produto = Produto(idProduto, descricao, valor, uri.toString())
                            produtosRef.child(idProduto.toString()).setValue(produto)

                            // Limpe os campos e a foto selecionada
                            idProduto = 0
                            descricao = ""
                            valor = 0f
                            fotoUri = null
                            Toast.makeText(contexto, "Produto inserido com sucesso!", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(contexto, "Ocorreu um erro ao carregar a foto", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Não há foto selecionada, salve o produto sem a foto
                    val produto = Produto(idProduto, descricao, valor, "")
                    produtosRef.child(idProduto.toString()).setValue(produto)
                    // Limpe os campos
                    idProduto = 0
                    descricao = ""
                    valor = 0f
                    Toast.makeText(contexto, "Produto inserido com sucesso!", Toast.LENGTH_LONG).show()
                }
            },
        ) {
            Text(text = "Enviar")
        }

        Button(
            onClick = { contexto.startActivity(Intent(contexto, TelaPedidos::class.java)) },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Voltar")
        }
    }
}