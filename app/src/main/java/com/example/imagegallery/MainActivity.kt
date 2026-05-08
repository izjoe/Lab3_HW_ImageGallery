package com.example.imagegallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.imagegallery.model.ImageItem
import com.example.imagegallery.ui.DetailScreen
import com.example.imagegallery.ui.GalleryScreen
import com.example.imagegallery.ui.theme.ImageGalleryTheme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageGalleryTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "gallery") {
        composable("gallery") {
            GalleryScreen(
                onImageClick = { image ->
                    val json = Gson().toJson(image)
                    val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
                    navController.navigate("detail/$encodedJson")
                }
            )
        }
        composable(
            route = "detail/{imageJson}",
            arguments = listOf(navArgument("imageJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("imageJson")
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
            val imageItem = Gson().fromJson(decodedJson, ImageItem::class.java)
            
            DetailScreen(
                imageItem = imageItem,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
