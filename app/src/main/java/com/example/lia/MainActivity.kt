package com.example.lia

import androidx.activity.compose.rememberLauncherForActivityResult // Add this import
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import coil.compose.AsyncImage
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> // Make sure this line is correct
        inputUri = uri
        enhancedBitmapUri = null

    }

    val context = LocalContext.current

    var beforeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var afterBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(inputUri) {
        inputUri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                beforeBitmap = BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } ?: run {
            beforeBitmap = null
        }
    }

    LaunchedEffect(enhancedBitmapUri) {
        enhancedBitmapUri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                afterBitmap = BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } ?: run {
            afterBitmap = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lia") },
                backgroundColor = Color(0xFF22C1C3),
                contentColor = Color.White
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { pickImage.launch("image/*") }) {
                    Text("Pick Image")
                }
                Button(onClick = {
                    enhancedBitmapUri?.let { uri ->
                        saveImageCopy(context, uri)
                    } ?: Toast.makeText(context, "No enhanced image to save", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Save as Copy")
                }
            }
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enhancement Mode")
                        Switch(checked = modeBalanced, onCheckedChange = { modeBalanced = it })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Max Quality")
                        Switch(checked = maxQuality, onCheckedChange = { maxQuality = it })
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Noise Reduction: ${(noiseLevel * 100).toInt()}%")
                    Slider(value = noiseLevel, onValueChange = { noiseLevel = it })
                    Spacer(Modifier.height(8.dp))
                    Text("Strength: ${(strength * 100).toInt()}%")
                    Slider(value = strength, onValueChange = { strength = it })
                }
            }
            Spacer(Modifier.height(12.dp))
            inputUri?.let { uri ->
                Text("Original:")
                AsyncImage(
                    model = uri,
                    contentDescription = "input",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    // Placeholder for actual enhancement logic
                    enhancedBitmapUri = uri
                }) {
                    Text("Enhance")
                }
            }
            Spacer(Modifier.height(12.dp))

            if (beforeBitmap != null && afterBitmap != null) {
                Text("Before / After Preview:")
                BeforeAfterSlider(
                    beforeImage = beforeBitmap!!,
                    afterImage = afterBitmap!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            } else if (enhancedBitmapUri != null) {
                AsyncImage(
                    model = enhancedBitmapUri,
                    contentDescription = "enhanced",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
            }
        }
    }
}

@Composable
fun BeforeAfterSlider(
    beforeImage: ImageBitmap,
    afterImage: ImageBitmap,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableStateOf(0.5f) }

    Box(modifier = modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()
            sliderPosition = (sliderPosition + dragAmount.x / size.width).coerceIn(0f, 1f)
        }
    }) {
        // Draw before image fully
        Image(
            bitmap = beforeImage,
            contentDescription = "Before",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        // Draw after image clipped on left side by sliderPosition
        Canvas(modifier = Modifier.matchParentSize()) {
            val clipWidth = size.width * sliderPosition
            val clipPath = Path().apply {
                addRect(Rect(0f, 0f, clipWidth, size.height))
            }
            clipPath(clipPath) {
                drawImage(
                    afterImage,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
                )
            }
        }
    }
}

fun saveImageCopy(context: android.content.Context, imageUri: Uri) {
    try {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val filename = "Lia_Enhanced_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.png"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Lia")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let { outUri ->
            resolver.openOutputStream(outUri)?.use { outputStream -> // Use .use for auto-closing
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                // outputStream will be closed automatically here
            } // If openOutputStream returns null, this block won't execute

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(outUri, values, null, null)

            Toast.makeText(context, "Image saved to Pictures/Lia", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "Failed to create image file for saving", Toast.LENGTH_SHORT).show()
        }


    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving image: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
