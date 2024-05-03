package com.example.jetpackcompose

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetpackcompose.ui.theme.ListViewAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListViewAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(innerPadding)
                        )
                        ButtonClick()
                        Buttons()
                    }
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListViewAppTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonClick() {
    var clickCounter = remember { mutableStateOf(0) }
    Column {
        Button(onClick = {clickCounter.value++ }) {
            Text(text = "Kliknięto: ${clickCounter.value}")
        }
    }
}

@Composable
fun Buttons(text: String = "Kliknij") {
    var my_text by remember { mutableStateOf(text) }
    Column{
        FilledTonalButton(onClick = { my_text= "Ok" },
            Modifier.fillMaxWidth())
        {
            Text(text = my_text)
        }
    }
}
@Preview
@Composable
fun ButtronPrev() {
    Buttons()
}