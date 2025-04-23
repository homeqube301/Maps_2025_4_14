package com.example.maps20250414.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.persistableBundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.maps20250414.data.MapsUiState
import com.example.maps20250414.model.LatLngSerializable
import com.example.maps20250414.model.MapViewModel
import com.example.maps20250414.model.NamedMarker
import com.example.maps20250414.model.PermanentMarkerViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState



@Composable
fun SearchMaker(
//    titleQuery: String,
//    memoQuery: String,
//    titleResults: List<NamedMarker>,
//    memoResults: List<NamedMarker>,
    onMarkerTapped: (NamedMarker) -> Unit,
    onMemoTapped: (NamedMarker) -> Unit,
    onTitleQueryChanged: (String) -> Unit,
    onMemoQueryChanged: (String) -> Unit,
    mapViewModel: MapViewModel = hiltViewModel(),
    cameraPositionState: CameraPositionState,
    uiState: MapsUiState,

) {

    Surface(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .widthIn(max = 300.dp)
            .padding(12.dp), shadowElevation = 4.dp
    ) {
        Column {

            // マーカー名検索
            OutlinedTextField(
                value = uiState.titleQuery ?: "",
                onValueChange = {
                    //mapViewModel.changeTitleQuery(it)
                    onTitleQueryChanged(it)
                },
                label = { Text("マーカー名で検索") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn {
                items(uiState.titleResults) { marker ->
                    Text(
                        text = marker.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                //selectedMarker = marker
                                //isEditPanelOpen = true
//                                mapViewModel.changeSelectedMarker(marker)
//                                mapViewModel.changeIsEditPanelOpen()
//
//                                cameraPositionState.move(
//                                    CameraUpdateFactory.newLatLngZoom(
//                                        marker.position.toLatLng(), 17f
//                                    )
//                                )
//                                //isSearchOpen = false
//                                mapViewModel.changeIsSearchOpen()
//                                //titleQuery = ""
//                                //memoQuery = ""
//                                mapViewModel.changeTitleQuery("")
//                                mapViewModel.changeMemoQuery("")
                                onMarkerTapped(marker)
                            }
                            .padding(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // メモ検索
            OutlinedTextField(
                value = uiState.memoQuery ?: "",
                onValueChange = {
                    //mapViewModel.changeMemoQuery(it)
                    onMemoQueryChanged(it)
                },
                label = { Text("メモ内容で検索") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn {
                items(uiState.memoResults) { marker ->
                    Text(
                        text = marker.title + "（メモ一致）",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                //selectedMarker = marker
                                //isEditPanelOpen = true
//                                mapViewModel.changeSelectedMarker(marker)
//                                mapViewModel.changeIsEditPanelOpen()
//                                cameraPositionState.move(
//                                    CameraUpdateFactory.newLatLngZoom(
//                                        marker.position.toLatLng(), 17f
//                                    )
//                                )
//                                //isSearchOpen = false
//                                mapViewModel.changeIsSearchOpen()
//                                mapViewModel.changeTitleQuery("")
//                                mapViewModel.changeMemoQuery("")
                                onMemoTapped(marker)
                            }
                            .padding(8.dp))
                }
            }
        }
    }
}


//    val uiState by mapViewModel.uiState.collectAsState()
//
//    Surface(
//        modifier = Modifier
//            .padding(top = 8.dp)
//            .background(Color.White, shape = RoundedCornerShape(8.dp))
//            .widthIn(max = 300.dp)
//            .padding(12.dp),
//        shadowElevation = 4.dp
//    ) {
//        Column {
//
//            // マーカー名検索
//            OutlinedTextField(
//                value = uiState.titleQuery ?: "",
//                onValueChange = {
//                    mapViewModel.changeTitleQuery(it)
//                },
//                label = { Text("マーカー名で検索") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            LazyColumn {
//                items(uiState.titleResults) { marker ->
//                    Text(
//                        text = marker.title,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                onMarkerTapped(marker)
//                            }
//                            .padding(8.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // メモ検索
//            OutlinedTextField(
//                value = uiState.memoQuery ?: "",
//                onValueChange = {
//                    mapViewModel.changeMemoQuery(it)
//                },
//                    //onMemoNameChanged,
//                label = { Text("メモ内容で検索") },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            LazyColumn {
//                items(uiState.memoResults) { marker ->
//                    Text(
//                        text = marker.title + "（メモ一致）",
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                onMemoTapped(marker)
//                            }
//                            .padding(8.dp)
//                    )
//                }
//            }
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun PreviewSearchMaker() {
    val dummyMarkers = listOf(
        NamedMarker(id = "1", title = "東京タワー", memo = "夜景がきれい", position = LatLngSerializable(35.3606, 138.7274), colorHue = 0f),
        NamedMarker(id = "2", title = "スカイツリー", memo = "観光地", position = LatLngSerializable(35.6252, 139.2430), colorHue = 120f)
    )
    val dummyState = MapsUiState(
        titleQuery = "東京",
        memoQuery = "観光",
        titleResults = dummyMarkers,
        memoResults = dummyMarkers,
        isSearchOpen = true
    )

    SearchMaker(
        uiState = dummyState,
        onTitleQueryChanged = {},
        onMemoQueryChanged = {},
        onMarkerTapped = {},
        onMemoTapped = {},
        cameraPositionState = rememberCameraPositionState()
    )
}

//@Preview
//@Composable
//fun SearchMakerPreview() {
//    SearchMaker(
//        titleQuery = "",
//        memoQuery = "",
//        titleResults = listOf(
//            NamedMarker(
//                position = LatLngSerializable(
//                    latitude = 35.681236,
//                    longitude = 139.767125
//                ),
//                title = "マーカー1",
//            ),
//        ),
//        memoResults = listOf(
//            NamedMarker(
//                position = LatLngSerializable(
//                    latitude = 35.681236,
//                    longitude = 139.767125
//                ),
//                title = "マーカー1",
//            ),
//        ),
//        onMarkerNameChanged = {},
//        onMarkerTapped = {},
//        onMemoNameChanged = {},
//        onMemoTapped = {},
//        cameraPositionState = 1
//    )
//}
