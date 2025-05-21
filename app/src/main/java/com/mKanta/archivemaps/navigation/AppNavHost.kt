package com.mKanta.archivemaps.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mKanta.archivemaps.domain.model.LatLngSerializable
import com.mKanta.archivemaps.ui.screen.AuthScreen
import com.mKanta.archivemaps.ui.screen.PermissionDeniedScreen
import com.mKanta.archivemaps.ui.screen.SignUpScreen
import com.mKanta.archivemaps.ui.screen.map.MapScreen
import com.mKanta.archivemaps.ui.screen.markerList.DetailSearchScreen
import com.mKanta.archivemaps.ui.screen.markerList.MarkerListScreen
import com.mKanta.archivemaps.ui.stateholder.AuthViewModel
import com.mKanta.archivemaps.ui.stateholder.ListViewModel
import com.mKanta.archivemaps.ui.stateholder.MapViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "marker",
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        navigation(startDestination = "map/{latitude}/{longitude}", route = "marker") {
            composable("map/{latitude}/{longitude}") { backStackEntry ->
                val parentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry("marker")
                    }

                val markerViewModel: ListViewModel = hiltViewModel(parentEntry)
                val mapViewModel: MapViewModel = hiltViewModel(parentEntry)
                val authViewModel: AuthViewModel = hiltViewModel(parentEntry)

                val uiState by mapViewModel.uiState.collectAsState()
                val listState by markerViewModel.listState.collectAsState()
                val authState by authViewModel.uiState.collectAsState()

                val latitude =
                    backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull()
                        ?: uiState.lastCameraPosition?.target?.latitude
                        ?: 0.0

                val longitude =
                    backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull()
                        ?: uiState.lastCameraPosition?.target?.longitude
                        ?: 0.0

                Log.d("testes", authState.accountId)
                if (authState.accountId == "") {
                    mapViewModel.changeIsGuestMode(true)
                } else {
                    mapViewModel.changeIsGuestMode(false)
                }

                MapScreen(
                    onNavigateToMarkerList = { navController.navigate("marker_list") },
                    changeLastCameraPosition = { mapViewModel.changeLastCameraPosition(it) },
                    latitude = latitude,
                    longitude = longitude,
                    uiState = uiState,
                    startDate = listState.startDate ?: "",
                    endDate = listState.endDate ?: "",
                    markerName = listState.markerName ?: "",
                    memo = listState.memo ?: "",
                    changeShowConfirmDialog = { mapViewModel.changeShowConfirmDialog() },
                    changeIsFollowing = { mapViewModel.changeIsFollowing() },
                    changeIsEditPanelOpen = { mapViewModel.changeIsEditPanelOpen() },
                    changeIsPanelOpen = { mapViewModel.changeIsPanelOpen() },
                    changeIsSearchOpen = { mapViewModel.changeIsSearchOpen() },
                    changeTitleQuery = { mapViewModel.changeTitleQuery(it) },
                    changeMemoQuery = { mapViewModel.changeMemoQuery(it) },
                    changeSelectedMarker = { mapViewModel.changeSelectedMarker(it) },
                    changeTempMarkerName = { mapViewModel.changeTempMarkerName(it) },
                    changeTempMarkerMemo = { mapViewModel.changeTempMarkerMemo(it) },
                    changeTempMarkerPosition = { mapViewModel.changeTempMarkerPosition(it) },
                    changeShowMapIntro = { mapViewModel.changeShowMapIntro() },
                    updateVisibleMarkers = { cameraPositionState, permanentMarkers ->
                        mapViewModel.updateVisibleMarkers(
                            cameraPositionState,
                            permanentMarkers,
                        )
                    },
                    removeVisibleMarkers = { mapViewModel.removeVisibleMarkers(it) },
                    addAllVisibleMarkers = { mapViewModel.addAllVisibleMarkers(it) },
                    changeUserLocation = { mapViewModel.changeUserLocation(it) },
                    changePanelOpen = { mapViewModel.changePanelOpen(it) },
                    loadMarkers = { mapViewModel.loadMarkers() },
                    saveMarkers = { mapViewModel.saveMarkers() },
                    updateMarker = { mapViewModel.updateMarker(it) },
                    updateMarkerMemoEmbedding = { marker, newMemo ->
                        mapViewModel.updateMarkerMemoEmbedding(marker, newMemo)
                    },
                    addMarker = { mapViewModel.addMarker(it) },
                    removeMarker = { mapViewModel.removeMarker(it) },
                    toggleFollowing = { mapViewModel.toggleFollowing() },
                    startLocationUpdates = { context, cameraPositionState ->
                        mapViewModel.startLocationUpdates(
                            context,
                            cameraPositionState,
                        )
                    },
                    fetchAddressForLatLng = { lat, lon ->
                        mapViewModel.fetchAddressForLatLng(lat, lon)
                    },
                    setVisibleMarkers = { mapViewModel.setVisibleMarkers(it) },
                    updateSearchList = { titleQuery, memoQuery, visibleMarkers ->
                        mapViewModel.updateSearchList(
                            titleQuery,
                            memoQuery,
                            visibleMarkers,
                        )
                    },
                    selectedAddress = mapViewModel.selectedAddress,
                    permanentMarkers = uiState.permanentMarkers,
                    changeGoogleMapState = { mapViewModel.changeGoogleMapState(it) },
                    filterMarkers = { markers, bounds, startDate, endDate, markerName, memo, similarMarkerIds ->
                        markerViewModel.filterMarkers(
                            markers,
                            bounds,
                            startDate,
                            endDate,
                            markerName,
                            memo,
                            similarMarkerIds,
                        )
                    },
                    onAccountSheetOpenChange = { mapViewModel.changeIsAccountSheetOpen() },
                    onSignOut = { authViewModel.signOut() },
                    onDeleteAccount = { authViewModel.deleteAccount() },
                    onAccountNameChange = { authViewModel.changeAccountName(it) },
                    isSignOut = authState.isSignOut,
                    isAccountLoading = authState.isLoading,
                    accountName = authState.accountName,
                    accountId = authState.accountId,
                    onNavigateToAuth = {
                        navController.navigate("auth") {
                            {
                                popUpTo("marker") { inclusive = true }
                            }
                        }
                    },
                )
            }
            navigation(startDestination = "marker_list", route = "list") {
                composable("marker_list") { backStackEntry ->
                    val listParentEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry("list")
                        }

                    val markerViewModel: MapViewModel = hiltViewModel(listParentEntry)
                    val listViewModel: ListViewModel = hiltViewModel(listParentEntry)

                    val listUIState by listViewModel.listUIState.collectAsState()
                    val uiState by markerViewModel.uiState.collectAsState()
                    val embeddingUiState by listViewModel.embeddingUiState.collectAsState()
                    MarkerListScreen(
                        onNavigateToMap = { navController.navigate("map/{latitude}/{longitude}") },
                        onNavigateToMarker = { position: LatLngSerializable ->
                            navController.navigate("map/${position.latitude}/${position.longitude}")
                        },
                        onNavigateToDetailSearch = { navController.navigate("detail_search") },
                        onNavigateBack = { navController.popBackStack() },
                        markerName = listViewModel.listState.value.markerName ?: "",
                        startDate = listViewModel.listState.value.startDate ?: "",
                        endDate = listViewModel.listState.value.endDate ?: "",
                        memo = listViewModel.listState.value.memo ?: "",
                        embeddingMemo = listViewModel.listState.value.embeddingMemo ?: "",
                        showListIntro = listViewModel.listState.value.showListIntro,
                        permanentMarkers = uiState.permanentMarkers,
                        similarMarkerIds = listViewModel.listState.value.similarMarkerIds,
                        changeEmbeddingMemo = { embeddingMemo ->
                            listViewModel.changeEmbeddingMemo(embeddingMemo)
                        },
                        searchSimilarMarkers = { listViewModel.searchSimilarMarkers() },
                        changeShowListIntro = { listViewModel.changeShowListIntro() },
                        checkListUIState = { filteredMarkerList ->
                            listViewModel.checkListUIState(filteredMarkerList)
                        },
                        listUIState = listUIState,
                        embeddingUiState = embeddingUiState,
                        filterMarkers = { markers, bounds, startDate, endDate, markerName, memo, similarMarkerIds ->
                            listViewModel.filterMarkers(
                                markers,
                                bounds,
                                startDate,
                                endDate,
                                markerName,
                                memo,
                                similarMarkerIds,
                            )
                        },
                    )
                }
                composable("detail_search") { backStackEntry ->
                    val listParentEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry("list")
                        }

                    val parentEntry =
                        remember(backStackEntry) {
                            navController.getBackStackEntry("marker")
                        }

                    val markerViewModel: MapViewModel = hiltViewModel(parentEntry)
                    val listViewModel: ListViewModel = hiltViewModel(listParentEntry)

                    val listState by listViewModel.listState.collectAsState()
                    DetailSearchScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToMarkerList = { navController.navigate("marker_list") },
                        listState = listState,
                        changeStartDatePicker = { listViewModel.changeStartDatePicker() },
                        changeEndDatePicker = { listViewModel.changeEndDatePicker() },
                        changeMarkerName = { listViewModel.changeMarkerName(it) },
                        changeStartDate = { listViewModel.changeStartDate(it) },
                        changeEndDate = { listViewModel.changeEndDate(it) },
                        changeEmbeddingMemo = { listViewModel.changeEmbeddingMemo(it) },
                        changeMemo = { listViewModel.changeMemo(it) },
                        changeShowDetailIntro = { listViewModel.changeShowDetailIntro() },
                        isGuestMode = markerViewModel.uiState.value.isGuestMode,
                    )
                }
            }
        }
        composable("Location_Permission") {
            PermissionDeniedScreen()
        }
        navigation(startDestination = "auth", route = "logIn") {
            composable("auth") { backStackEntry ->
                val authParentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry("logIn")
                    }

                val logInViewModel: AuthViewModel = hiltViewModel(authParentEntry)
                val uiState by logInViewModel.uiState.collectAsState()
                AuthScreen(
                    onLoginSuccess = {
                        navController.navigate("map/0.0/0.0") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    uiState = uiState,
                    signIn = { email, password -> logInViewModel.signIn(email, password) },
                    onNavigateToSignUp = { navController.navigate("signUp") },
                    startGuestMode = { logInViewModel.startGuestMode() },
                )
            }

            composable("signUp") { backStackEntry ->
                val authParentEntry =
                    remember(backStackEntry) {
                        navController.getBackStackEntry("logIn")
                    }

                val logInViewModel: AuthViewModel = hiltViewModel(authParentEntry)
                val uiState by logInViewModel.uiState.collectAsState()
                SignUpScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSignUpSuccess = {
                        navController.navigate("map/0.0/0.0") {
                            popUpTo("signUp") { inclusive = true }
                        }
                    },
                    uiState = uiState,
                    signUp = { email, password -> logInViewModel.signUp(email, password) },
                )
            }
        }
    }
}
