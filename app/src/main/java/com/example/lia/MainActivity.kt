package com.example.lia

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.tensorflow.lite.Interpreter
import java.io.File

class MainActivity : ComponentActivity() {

    private var tflite: Interpreter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // model loading will be implemented in ModelEngine.kt (placeholder)
        setContent {
            LiaApp()
        }
    }
}

@Composable
fun LiaApp() {
    var inputUri by remember { mutableStateOf<Uri?>(null) }
    var enhancedBitmapUri by remember { mutableStateOf<Uri?>(null) }
    var modeBalanced by remember { mutableStateOf(true) }
    var maxQuality by remember { mutableStateOf(false) }
    var noiseLevel by remember { mutableStateOf(0.3f) }
    var strength by remember { mutableStateOf(0.8f) }

    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        inputUri = uri
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Lia") }, backgroundColor = Color(0xFF22C1C3), contentColor = Color.White) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { pickImage.launch("image/*") }) { Text("Pick Image") }
                Button(onClick = { /* Save as copy action */ Toast.makeText(LocalContext.current, "Saved (placeholder)", Toast.LENGTH_SHORT).show() }) { Text("Save as Copy") }
            }
            Spacer(Modifier.height(12.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Enhancement Mode")
                        Switch(checked = modeBalanced, onCheckedChange = { modeBalanced = it })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Max Quality")
                        Switch(checked = maxQuality, onCheckedChange = { maxQuality = it })
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Noise Reduction: ${(noiseLevel*100).toInt()}%")
                    Slider(value = noiseLevel, onValueChange = { noiseLevel = it })
                    Spacer(Modifier.height(8.dp))
                    Text("Strength: ${(strength*100).toInt()}%")
                    Slider(value = strength, onValueChange = { strength = it })
                }
            }
            Spacer(Modifier.height(12.dp))
            inputUri?.let { uri ->
                Text("Original:")
                AsyncImage(model = uri, contentDescription = "input", modifier = Modifier.fillMaxWidth().height(240.dp))
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    // Enhancement placeholder: in the real build, this will call ModelEngine.enhance()
                    enhancedBitmapUri = uri
                }) { Text("Enhance") }
            }
            Spacer(Modifier.height(12.dp))
            enhancedBitmapUri?.let { uri ->
                Text("Before / After Preview:")
                // A proper slider is implemented in PreviewSlider.kt in the full project
                AsyncImage(model = uri, contentDescription = "enhanced", modifier = Modifier.fillMaxWidth().height(240.dp))
            }
        }
    }
}
