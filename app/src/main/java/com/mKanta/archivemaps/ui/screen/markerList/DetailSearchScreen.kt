package com.mKanta.archivemaps.ui.screen.markerList

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.rememberNavController
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.mKanta.archivemaps.R
import com.mKanta.archivemaps.ui.state.ListState
import com.mKanta.archivemaps.ui.theme.ArchivemapsTheme
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSearchScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToMarkerList: () -> Unit = {},
    changeStartDatePicker: () -> Unit = {},
    changeEndDatePicker: () -> Unit = {},
    changeMarkerName: (String) -> Unit = {},
    changeStartDate: (String) -> Unit = {},
    changeEndDate: (String) -> Unit = {},
    changeEmbeddingMemo: (String) -> Unit = {},
    changeMemo: (String) -> Unit = {},
    listState: ListState,
    changeShowDetailIntro: () -> Unit = {},
    isGuestMode: Boolean = false,
) {
    BackHandler {
        changeMarkerName("")
        changeEmbeddingMemo("")
        changeMemo("")
        onNavigateBack()
    }

    LaunchedEffect(Unit) {
        changeStartDate("")
        changeEndDate("")
    }

    DatePicker(
        openStartDatePicker = listState.openStartDatePicker,
        openEndDatePicker = listState.openEndDatePicker,
        changeStartDatePicker = changeStartDatePicker,
        changeEndDatePicker = changeEndDatePicker,
        changeStartDate = changeStartDate,
        changeEndDate = changeEndDate,
    )

    SearchContents(
        markerName = listState.markerName,
        startDate = listState.startDate,
        endDate = listState.endDate,
        embeddingMemo = listState.embeddingMemo,
        memo = listState.memo,
        changeMarkerName = changeMarkerName,
        changeEmbeddingMemo = changeEmbeddingMemo,
        changeMemo = changeMemo,
        changeShowDetailIntro = changeShowDetailIntro,
        showDetailIntro = listState.showDetailIntro,
        changeStartDatePicker = changeStartDatePicker,
        changeEndDatePicker = changeEndDatePicker,
        onNavigateBack = onNavigateBack,
        onNavigateToMarkerList = onNavigateToMarkerList,
        isGuestMode = isGuestMode,
    )
}

@Composable
private fun DatePicker(
    openStartDatePicker: Boolean,
    openEndDatePicker: Boolean,
    changeStartDatePicker: () -> Unit,
    changeEndDatePicker: () -> Unit,
    changeStartDate: (String) -> Unit,
    changeEndDate: (String) -> Unit,
) {
    if (openStartDatePicker) {
        ComposeDatePickerDialog(
            onDismissRequest = {
                changeStartDatePicker()
            },
            onDateSelected = { year, month, dayOfMonth ->
                changeStartDate(
                    String.format(
                        Locale.JAPAN,
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        dayOfMonth,
                    ),
                )
                changeStartDatePicker()
            },
        )
    }

    if (openEndDatePicker) {
        ComposeDatePickerDialog(
            onDismissRequest = {
                changeEndDatePicker()
            },
            onDateSelected = { year, month, dayOfMonth ->
                changeEndDate(
                    String.format(
                        Locale.JAPAN,
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        dayOfMonth,
                    ),
                )
                changeEndDatePicker()
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContents(
    onNavigateBack: () -> Unit,
    onNavigateToMarkerList: () -> Unit,
    markerName: String?,
    startDate: String?,
    endDate: String?,
    embeddingMemo: String?,
    memo: String?,
    changeMarkerName: (String) -> Unit,
    changeEmbeddingMemo: (String) -> Unit,
    changeMemo: (String) -> Unit,
    changeShowDetailIntro: () -> Unit,
    showDetailIntro: Boolean,
    changeStartDatePicker: () -> Unit,
    changeEndDatePicker: () -> Unit,
    isGuestMode: Boolean = false,
) {
    val isSearchEnabled =
        !markerName.isNullOrBlank() ||
            !startDate.isNullOrBlank() ||
            !endDate.isNullOrBlank() ||
            !embeddingMemo.isNullOrBlank() ||
            !memo.isNullOrBlank()

    IntroShowcase(
        showIntroShowCase = showDetailIntro,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            changeShowDetailIntro()
        },
    ) {
        ArchivemapsTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = R.string.detailSc_title),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.tertiary),
                        navigationIcon = {
                            IconButton(onClick = {
                                changeMarkerName("")
                                changeEmbeddingMemo("")
                                changeMemo("")
                                onNavigateBack()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back),
                                    tint = Color.White,
                                )
                            }
                        },
                    )
                },
            ) { paddingValues ->
                Column(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.secondary)
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    OutlinedTextField(
                        value = markerName ?: "",
                        onValueChange = {
                            changeMarkerName(it)
                        },
                        label = {
                            Text(
                                text = stringResource(id = R.string.detailSc_search_name),
                                color = Color.Gray,
                            )
                        },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                        modifier =
                            Modifier
                                .introShowCaseTarget(
                                    index = 0,
                                    style =
                                        ShowcaseStyle.Default.copy(
                                            backgroundColor = Color.Black,
                                            backgroundAlpha = 0.95f,
                                            targetCircleColor = Color.Gray,
                                        ),
                                    content = {
                                        Column {
                                            Text(
                                                text = stringResource(id = R.string.detailSc_search_name_title),
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = stringResource(id = R.string.detailSc_search_name_description),
                                                color = Color.White,
                                                fontSize = 16.sp,
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Icon(
                                                Icons.Default.Menu,
                                                contentDescription = null,
                                                modifier =
                                                    Modifier
                                                        .size(80.dp)
                                                        .align(Alignment.End),
                                                tint = Color.Transparent,
                                            )
                                        }
                                    },
                                ).fillMaxWidth(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedButton(
                            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                            onClick = {
                                changeStartDatePicker()
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .introShowCaseTarget(
                                        index = 1,
                                        style =
                                            ShowcaseStyle.Default.copy(
                                                backgroundColor = Color.Black,
                                                backgroundAlpha = 0.95f,
                                                targetCircleColor = Color.Gray,
                                            ),
                                        content = {
                                            Column {
                                                Text(
                                                    text = stringResource(id = R.string.detailSc_search_startDate_title),
                                                    color = Color.White,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Text(
                                                    text = stringResource(id = R.string.detailSc_search_startDate_description),
                                                    color = Color.White,
                                                    fontSize = 16.sp,
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Icon(
                                                    Icons.Default.Menu,
                                                    contentDescription = null,
                                                    modifier =
                                                        Modifier
                                                            .size(80.dp)
                                                            .align(Alignment.End),
                                                    tint = Color.Transparent,
                                                )
                                            }
                                        },
                                    ),
                        ) {
                            Text(
                                text =
                                    if (startDate.isNullOrEmpty()) {
                                        stringResource(id = R.string.detailSc_search_startDate_Button)
                                    } else {
                                        startDate
                                    },
                                color = Color.White,
                            )
                        }

                        Text(
                            "〜",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        OutlinedButton(
                            onClick = {
                                changeEndDatePicker()
                            },
                            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                            shape = MaterialTheme.shapes.medium,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .introShowCaseTarget(
                                        index = 2,
                                        style =
                                            ShowcaseStyle.Default.copy(
                                                backgroundColor = Color.Black,
                                                backgroundAlpha = 0.95f,
                                                targetCircleColor = Color.Gray,
                                            ),
                                        content = {
                                            Column {
                                                Text(
                                                    text = stringResource(id = R.string.detailSc_search_endDate_title),
                                                    color = Color.White,
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                                Text(
                                                    text = stringResource(id = R.string.detailSc_search_endDate_description),
                                                    color = Color.White,
                                                    fontSize = 16.sp,
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Icon(
                                                    Icons.Default.Menu,
                                                    contentDescription = null,
                                                    modifier =
                                                        Modifier
                                                            .size(80.dp)
                                                            .align(Alignment.End),
                                                    tint = Color.Transparent,
                                                )
                                            }
                                        },
                                    ),
                        ) {
                            Text(
                                text =
                                    if (endDate.isNullOrEmpty()) {
                                        stringResource(id = R.string.detailSc_search_endDate_Button)
                                    } else {
                                        endDate
                                    },
                                color = Color.White,
                            )
                        }
                    }

                    OutlinedTextField(
                        value = embeddingMemo ?: "",
                        onValueChange = {
                            changeEmbeddingMemo(it)
                        },
                        label = {
                            Text(
                                stringResource(id = R.string.detailSc_search_AImemo_field),
                                color = Color.Gray,
                            )
                        },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                        enabled = !isGuestMode,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .introShowCaseTarget(
                                    index = 3,
                                    style =
                                        ShowcaseStyle.Default.copy(
                                            backgroundColor = Color.Black,
                                            backgroundAlpha = 0.95f,
                                            targetCircleColor = Color.Gray,
                                        ),
                                    content = {
                                        Column {
                                            Text(
                                                text = stringResource(id = R.string.detailSc_search_AImemo_title),
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = stringResource(id = R.string.detailSc_search_AImemo_description),
                                                color = Color.White,
                                                fontSize = 16.sp,
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Icon(
                                                Icons.Default.Menu,
                                                contentDescription = null,
                                                modifier =
                                                    Modifier
                                                        .size(80.dp)
                                                        .align(Alignment.End),
                                                tint = Color.Transparent,
                                            )
                                        }
                                    },
                                ),
                    )

                    OutlinedTextField(
                        value = memo ?: "",
                        onValueChange = {
                            changeMemo(it)
                        },
                        label = {
                            Text(
                                stringResource(id = R.string.detailSc_search_memo_field),
                                color = Color.Gray,
                            )
                        },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                            ),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .introShowCaseTarget(
                                    index = 4,
                                    style =
                                        ShowcaseStyle.Default.copy(
                                            backgroundColor = Color.Black,
                                            backgroundAlpha = 0.95f,
                                            targetCircleColor = Color.Gray,
                                        ),
                                    content = {
                                        Column {
                                            Text(
                                                text = stringResource(id = R.string.detailSc_search_memo_title),
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Text(
                                                text = stringResource(id = R.string.detailSc_search_memo_description),
                                                color = Color.White,
                                                fontSize = 16.sp,
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Icon(
                                                Icons.Default.Menu,
                                                contentDescription = null,
                                                modifier =
                                                    Modifier
                                                        .size(80.dp)
                                                        .align(Alignment.End),
                                                tint = Color.Transparent,
                                            )
                                        }
                                    },
                                ),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToMarkerList,
                        enabled = isSearchEnabled,
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        Text(
                            stringResource(id = R.string.detailSc_search_Button),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeDatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    val darkColorScheme =
        darkColorScheme(
            primary = Color.White,
            onPrimary = colorResource(id = R.color.secondary_block),
            surface = colorResource(id = R.color.background_black),
            onSurface = Color.White,
            background = colorResource(id = R.color.secondary_block),
            onBackground = Color.White,
            surfaceContainerHigh = colorResource(id = R.color.background_black),
        )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        MaterialTheme(
            colorScheme = darkColorScheme,
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                modifier =
                    Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                ) {
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextButton(onClick = {
                            onDismissRequest()
                        }) {
                            Text(stringResource(id = R.string.detailSc_dialog_cancel))
                        }

                        TextButton(onClick = {
                            onDismissRequest()
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date =
                                    Instant
                                        .ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                onDateSelected(date.year, date.monthValue - 1, date.dayOfMonth)
                                onDismissRequest()
                            }
                        }) {
                            Text(stringResource(id = R.string.detailSc_dialog_OK))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailSearchScreenPreview() {
    val dummyNavController = rememberNavController()
    DetailSearchScreen(
        onNavigateBack = { dummyNavController.popBackStack() },
        onNavigateToMarkerList = { dummyNavController.navigate("marker_list") },
        changeStartDatePicker = {},
        changeEndDatePicker = {},
        changeMarkerName = {},
        changeStartDate = {},
        changeEndDate = {},
        changeEmbeddingMemo = {},
        changeMemo = {},
        listState =
            ListState(
                markerName = "テストマーカー",
                startDate = "2025-01-01",
                endDate = "2025-12-31",
                memo = "これはテスト用メモです",
                embeddingMemo = "意味的検索用テキスト",
                openStartDatePicker = false,
                openEndDatePicker = false,
                similarMarkerIds = listOf("id1", "id2", "id3"),
                showDetailIntro = false,
            ),
    )
}

@Preview(showBackground = true)
@Composable
fun DatePickerDialogPreview() {
    ComposeDatePickerDialog(
        onDismissRequest = {},
        onDateSelected = { _, _, _ -> },
    )
}
