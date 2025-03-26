package com.bmexcs.pickpic.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bmexcs.pickpic.R
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.data.models.EventMetadata
import com.bmexcs.pickpic.navigation.Route
import com.bmexcs.pickpic.presentation.viewmodels.HomePageViewModel

@Composable
fun HomePageScreenView(
    navController: NavHostController,
    viewModel: HomePageViewModel = hiltViewModel(),
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val randomQuote by remember { mutableStateOf(viewModel.randomQuote) }


    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "\"$randomQuote\"",
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E88E5), // Customize color
                letterSpacing = 1.5.sp,
                shadow = Shadow(
                    color = Color.Black,
                    blurRadius = 4f
                ),
            ),
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InvitesButton(navController)
            CreateEventButton(navController)
        }
        Spacer(modifier = Modifier.height(32.dp))

        when {
            isLoading && events.isEmpty() -> {
                CircularProgressIndicator()
            }

            events.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Events Found")
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        errorMessage ?: "An unknown error occurred",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(events) { eventItem: EventMetadata ->
                        EventListing(
                            isOwner = viewModel.isCurrentUserOwner(eventItem.owner.id),
                            eventItem = eventItem,
                            onEnter = {
                                viewModel.setEvent(eventItem)
                                navController.navigate(Route.Event.route)
                            },
                            onDelete = {
                                viewModel.deleteEvent(eventItem.id)
                            },
                            onLeave = {
                                viewModel.leaveInvitedEvent(eventItem.id)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun InvitesButton(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate(Route.EventInvitation.route)
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.group_add_24px),
            contentDescription = "Join Events Icon",
            modifier = Modifier
                .size(36.dp)
                .padding(end = 8.dp)
        )
        Text(" Pending Invites")
    }
}

@Composable
fun CreateEventButton(navController: NavHostController) {
    Button(
        onClick = {
            navController.navigate(Route.CreateEvent.route)
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.add_circle_24px),
            contentDescription = "Create Event Icon",
            modifier = Modifier
                .size(36.dp)
                .padding(end = 8.dp)
        )
        Text("Create Event")
    }
}

@Composable
fun EventListing(
    isOwner: Boolean,
    eventItem: EventMetadata,
    onEnter: () -> Unit,
    onDelete: () -> Unit,

    // callback function for when user is not owner but would like to leave
    onLeave: () -> Unit
) {
    val expandFilter = remember { mutableStateOf(false) }

    ElevatedButton(
        onClick = onEnter,
        shape = RoundedCornerShape(16.dp),
    ) {
        ListItem(
            headlineContent = {
                Text(eventItem.name)
            },
            supportingContent = {
                Text(text = "Host: ${eventItem.owner.name}")
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isOwner) {
                        IconButton(onClick = { expandFilter.value = !expandFilter.value }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = null)

                            DropdownMenu(
                                expanded = expandFilter.value,
                                onDismissRequest = {
                                    expandFilter.value = !expandFilter.value
                                }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        onDelete()
                                        expandFilter.value = false
                                    }
                                )
                            }
                        }

                        Box(modifier = Modifier.size(24.dp)) {
                            Icon(
                                painter = painterResource(R.drawable.ic_crown),
                                contentDescription = "Owner",
                                tint = Color(0xFFD4AF37),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        IconButton(onClick = { expandFilter.value = !expandFilter.value }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = null)

                            DropdownMenu(
                                expanded = expandFilter.value,
                                onDismissRequest = {
                                    expandFilter.value = !expandFilter.value
                                }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Leave") },
                                    onClick = {
                                        onLeave()
                                        expandFilter.value = false
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(24.dp))
                    }
                }

            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}
