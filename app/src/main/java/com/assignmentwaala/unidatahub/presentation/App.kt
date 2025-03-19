package com.assignmentwaala.unidatahub.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.assignmentwaala.unidatahub.MainActivity.Companion.TAG
import com.assignmentwaala.unidatahub.R
import com.assignmentwaala.unidatahub.domain.models.DocumentModel
import com.assignmentwaala.unidatahub.presentation.screens.AddProductScreen
import com.assignmentwaala.unidatahub.presentation.screens.CommunityScreen
import com.assignmentwaala.unidatahub.presentation.screens.DocumentListScreen
import com.assignmentwaala.unidatahub.presentation.screens.HomeScreen
import com.assignmentwaala.unidatahub.presentation.screens.ProfileScreen
import com.assignmentwaala.unidatahub.presentation.components.BottomNavBar
import com.assignmentwaala.unidatahub.presentation.screens.DocumentDetailsScreen
import com.assignmentwaala.unidatahub.presentation.viewmodel.DocumentViewModel
import com.assignmentwaala.unidatahub.ui.theme.primaryBlue
import kotlin.reflect.typeOf

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: DocumentViewModel = hiltViewModel()
    var showBottomBar by remember { mutableStateOf(true) }
//    var showTopBar by remember { mutableStateOf(true) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    Log.d(TAG, "App called")
    LaunchedEffect(currentDestination) {
        Log.d(TAG, "Routes.DocumentDetails route:- ${Routes.DocumentDetails().route}\ncurrentDestination:- $currentDestination")
        when(currentDestination) {
            Routes.AddDocument.route,
            Routes.DocumentList().route,
            Routes.DocumentDetails().route -> {
                showBottomBar = false
            }
            else -> {
                showBottomBar = true

            }
        }
    }
    Scaffold(
        modifier = Modifier.background(primaryBlue).statusBarsPadding(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                BottomNavBar(navController)
            }
//            if(showBottomBar) {
//                BottomNavBar(navController)
//            }

        }
    ) { innerPadding->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ){
            NavHost(
                navController = navController,
                startDestination = Routes.Home,
            ) {
                composable<Routes.Home> {
                    HomeScreen(
                        onItemClick = {
                            navController.navigate(Routes.DocumentList(it))
                        },

                        onAddProductClick = {
                            navController.navigate(Routes.AddDocument)
                        },
                    )
                    Log.d(TAG, "HomeComposable NavHost called")

                }

                composable<Routes.Community> {
                    CommunityScreen()
                    Log.d(TAG, "CommunityComposable NavHost called")

                }

                composable<Routes.Profile> {
                    ProfileScreen()
                    Log.d(TAG, "ProfileComposable NavHost called")
                }

                composable<Routes.AddDocument> {
                    AddProductScreen {
                        navController.popBackStack()
                        navController.navigate(Routes.Home)
                    }
                    Log.d(TAG, "AddDocumentComposable NavHost called")

                }

                composable<Routes.DocumentList> {
                    val data = it.toRoute<Routes.DocumentList>()
                    DocumentListScreen(
                        category = data.category,
                        onClickItem = {document ->
                            navController.navigate(Routes.DocumentDetails(
                                title = document.title,
                                description = document.description,
                                url = document.url,
                                category = document.category,
                                author = document.author,
                                uploadBy = document.uploadBy,
                                date = document.date
                            ))

                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                    Log.d(TAG, "DocumentListComposable NavHost called")

                }

                composable<Routes.DocumentDetails> {
                    val data = it.toRoute<Routes.DocumentDetails>()
                    DocumentDetailsScreen(data) {
                        navController.popBackStack()
                    }
                    Log.d(TAG, "DocumentDetailsComposable NavHost called")

                }
            }
        }
    }
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TopBar(viewModel: DocumentViewModel, navController: NavHostController) {
//
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentDestination = navBackStackEntry?.destination?.route
//    var headingText by rememberSaveable { mutableStateOf("UniDataHub") }
//
//    LaunchedEffect(currentDestination) {
//        headingText = when (currentDestination) {
//            Routes.AddDocument::class.qualifiedName -> {
//                "Add Document"
//            }
//
//            Routes.DocumentList::class.qualifiedName.toString() + "/{category}" -> {
//                navBackStackEntry?.toRoute<Routes.DocumentList>()?.category ?: "UniDataHub"
//            }
//
//            else -> {
//                "UniDataHub"
//            }
//        }
//    }
//
//    TopAppBar(
//        title = {
//            Log.d(TAG, "TopBar: currentDestination")
//
//            Text(
//                text = headingText,
//                fontWeight = FontWeight.Bold,
//                fontSize = 22.sp,
//                color = Color.White
//            )
//
//        },
//
//        navigationIcon = {
//            when (navController.currentDestination?.route) {
//                Routes.AddDocument::class.qualifiedName -> {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            modifier = Modifier.size(28.dp),
//                            tint = Color.White
//                        )
//                    }
//                    Log.d(
//                        TAG,
//                        "Add Document Screen: qualifiedName: ${Routes.AddDocument::class.qualifiedName}"
//                    )
//                }
//
//                Routes.DocumentList::class.qualifiedName.toString() + "/{category}" -> {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            modifier = Modifier.size(28.dp),
//                            tint = Color.White
//                        )
//                    }
//                }
//
//            }
//        },
//
//        actions = {
//            when (navController.currentDestination?.route) {
//                Routes.AddDocument::class.qualifiedName -> {}
//                else -> {
//                    IconButton(onClick = { navController.navigate(Routes.AddDocument) }) {
//                        Icon(
//                            imageVector = ImageVector.vectorResource(R.drawable.ic_upload_arrow),
//                            contentDescription = "Upload document",
//                            modifier = Modifier.size(38.dp).padding(4.dp),
//                            tint = Color.White
//                        )
//                    }
//                }
//            }
//        },
//
//        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = Color(0xFF3D5AF1),
//            titleContentColor = Color.White,
//            actionIconContentColor = Color.White
//        )
//    )
//}
