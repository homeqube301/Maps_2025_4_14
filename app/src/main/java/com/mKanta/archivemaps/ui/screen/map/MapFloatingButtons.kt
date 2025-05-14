package com.mKanta.archivemaps.ui.screen.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canopas.lib.showcase.IntroShowcase
import com.canopas.lib.showcase.component.ShowcaseStyle
import com.mKanta.archivemaps.R

@Composable
fun MapFloatingButtons(
    modifier: Modifier = Modifier,
    showIntroShowCase: Boolean,
    changeShowMapIntro: () -> Unit,
    changeIsSearchOpen: () -> Unit,
    changeIsFollowing: () -> Unit,
    toggleFollowing: () -> Unit,
    isFollowing: Boolean,
    onNavigateToMarkerList: () -> Unit,
) {
    IntroShowcase(
        showIntroShowCase = showIntroShowCase,
        dismissOnClickOutside = true,
        onShowCaseCompleted = {
            changeShowMapIntro()
        },
    ) {
        Box(
            modifier =
                Modifier.Companion.fillMaxSize(),
        ) {
            Text(
                text = stringResource(R.string.map_tutorial),
                color = Color.Companion.Transparent,
                modifier =
                    Modifier.Companion
                        .introShowCaseTarget(
                            index = 3,
                            style =
                                ShowcaseStyle.Companion.Default.copy(
                                    backgroundColor = Color.Companion.Black,
                                    backgroundAlpha = 0.98f,
                                    targetCircleColor = Color.Companion.Gray,
                                ),
                            content = {
                                Column {
                                    Text(
                                        text = stringResource(R.string.map_marker_tutorial_title),
                                        color = Color.Companion.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Companion.Bold,
                                    )
                                    Text(
                                        text = stringResource(R.string.map_marker_tutorial_description),
                                        color = Color.Companion.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.Companion.height(15.dp))

                                    Text(
                                        text = stringResource(R.string.map_marker_tutorial_title2),
                                        color = Color.Companion.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Companion.Bold,
                                    )
                                    Text(
                                        text = stringResource(R.string.map_marker_tutorial_description2),
                                        color = Color.Companion.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.Companion.height(10.dp))

                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier =
                                            Modifier.Companion
                                                .size(80.dp)
                                                .align(Alignment.Companion.End),
                                        tint = Color.Companion.Transparent,
                                    )
                                }
                            },
                        ).align(Alignment.Companion.Center),
            )
        }

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            FloatingActionButton(
                onClick = { changeIsSearchOpen() },
                contentColor = Color.Companion.White,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier.Companion
                        .size(72.dp)
                        .introShowCaseTarget(
                            index = 2,
                            style =
                                ShowcaseStyle.Companion.Default.copy(
                                    backgroundColor = Color.Companion.Black,
                                    backgroundAlpha = 0.95f,
                                    targetCircleColor = Color.Companion.White,
                                ),
                            content = {
                                Column {
                                    Text(
                                        text = stringResource(R.string.map_search_tutorial_title),
                                        color = Color.Companion.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Companion.Bold,
                                    )
                                    Text(
                                        text = stringResource(R.string.map_search_tutorial_description),
                                        color = Color.Companion.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.Companion.height(10.dp))
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier =
                                            Modifier.Companion
                                                .size(80.dp)
                                                .align(Alignment.Companion.End),
                                        tint = Color.Companion.Transparent,
                                    )
                                }
                            },
                        ),
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.map_search_Button),
                    modifier = Modifier.Companion.size(32.dp),
                )
            }

            FloatingActionButton(
                onClick = {
                    toggleFollowing()
                    changeIsFollowing()
                },
                containerColor =
                    if (isFollowing) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Companion.White
                    },
                modifier =
                    Modifier.Companion
                        .size(72.dp)
                        .introShowCaseTarget(
                            index = 0,
                            style =
                                ShowcaseStyle.Companion.Default.copy(
                                    backgroundColor = Color.Companion.Black,
                                    backgroundAlpha = 0.95f,
                                    targetCircleColor = Color.Companion.White,
                                ),
                            content = {
                                Column {
                                    Text(
                                        text = stringResource(R.string.map_follow_tutorial_title),
                                        color = Color.Companion.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Companion.Bold,
                                    )
                                    Text(
                                        text = stringResource(R.string.map_follow_tutorial_description),
                                        color = Color.Companion.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.Companion.height(10.dp))
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier =
                                            Modifier.Companion
                                                .size(80.dp)
                                                .align(Alignment.Companion.End),
                                        tint = Color.Companion.Transparent,
                                    )
                                }
                            },
                        ),
            ) {
                Icon(
                    painterResource(id = R.drawable.location_searching_24px),
                    modifier = Modifier.Companion.size(32.dp),
                    contentDescription = stringResource(R.string.map_follow_Button),
                    tint =
                        if (isFollowing) {
                            Color.Companion.White
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                )
            }

            FloatingActionButton(
                onClick = onNavigateToMarkerList,
                contentColor = Color.Companion.White,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier.Companion
                        .size(72.dp)
                        .introShowCaseTarget(
                            index = 1,
                            style =
                                ShowcaseStyle.Companion.Default.copy(
                                    backgroundColor = Color.Companion.Black,
                                    backgroundAlpha = 0.95f,
                                    targetCircleColor = Color.Companion.White,
                                ),
                            content = {
                                Column {
                                    Text(
                                        text = stringResource(R.string.map_list_tutorial_title),
                                        color = Color.Companion.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Companion.Bold,
                                    )
                                    Text(
                                        text = stringResource(R.string.map_list_tutorial_description),
                                        color = Color.Companion.White,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.Companion.height(10.dp))
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier =
                                            Modifier.Companion
                                                .size(80.dp)
                                                .align(Alignment.Companion.End),
                                        tint = Color.Companion.Transparent,
                                    )
                                }
                            },
                        ),
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = stringResource(R.string.map_list_Button),
                    modifier = Modifier.Companion.size(32.dp),
                )
            }
        }
    }
}
