package com.xinto.opencord.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.xinto.opc.OverlappingPanels
import com.xinto.opc.OverlappingPanelsValue
import com.xinto.opc.rememberOverlappingPanelsState
import com.xinto.opencord.ui.util.animateCornerBasedShapeAsState
import com.xinto.opencord.ui.viewmodel.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(
    onSettingsClick: () -> Unit,
    onPinsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val panelState = rememberOverlappingPanelsState(OverlappingPanelsValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val currentUserViewModel: CurrentUserViewModel = getViewModel()
    val chatViewModel: ChatViewModel = getViewModel()
    val guildsViewModel: GuildsViewModel = getViewModel()
    val channelsViewModel: ChannelsViewModel = getViewModel()
    val membersViewModel: MembersViewModel = getViewModel()
    val channelPinsViewModel: ChannelPinsViewModel = getViewModel()

    Surface(modifier = modifier) {
        OverlappingPanels(
            modifier = Modifier.fillMaxSize(),
            panelsState = panelState,
            panelStart = {
                GuildsChannelsScreen(
                    onGuildSelect = {
                        channelsViewModel.load()
                        membersViewModel.load()
                    },
                    onChannelSelect = chatViewModel::load,
                    guildsViewModel = guildsViewModel,
                    channelsViewModel = channelsViewModel,
                    currentUserViewModel = currentUserViewModel,
                    onSettingsClick = onSettingsClick,
                )
            },
            panelCenter = {
                val shape by animateCornerBasedShapeAsState(
                    if (panelState.offsetNotZero)
                        MaterialTheme.shapes.large
                    else
                        RoundedCornerShape(0.dp)
                )
                ChatScreen(
                    onChannelsButtonClick = {
                        coroutineScope.launch {
                            panelState.openStartPanel()
                        }
                    },
                    onMembersButtonClick = {
                        coroutineScope.launch {
                            panelState.openEndPanel()
                        }
                    },
                    onPinsButtonClick = {
                        channelPinsViewModel.load()
                        onPinsClick()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape),
                    viewModel = chatViewModel
                )
            },
            panelEnd = {
                ChannelMembersScreen(
                    viewModel = membersViewModel
                )
            }
        )
    }
}