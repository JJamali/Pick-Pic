package com.bmexcs.pickpic.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.bmexcs.pickpic.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(
    onClickHomePage: () -> Unit,
    onClickProfile: () -> Unit,
    onClickSupport: () -> Unit,
    onClickEvent: () -> Unit,
    onClickRanking: () -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                },
                actions = {
                    // RowScope here, so these icons will be placed horizontally
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Localized description")
                    }
                }
            )
            Image(
                painter = painterResource(R.drawable.pickpick_logo),
                contentDescription = "PickPic Logo",
                modifier = Modifier.size(1000.dp, 187.5.dp)
            )
            Spacer(modifier = Modifier.height(33.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElevatedButton(onClick = onClickEvent) {
                    Icon(
                        painter = painterResource(R.drawable.group_add_24px),
                        contentDescription = "Join Events Icon",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Join Event")
                }
                ElevatedButton(onClick = onClickEvent) {
                    Icon(
                        painter = painterResource(R.drawable.add_circle_24px),
                        contentDescription = "Create Event Icon",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Create Event")
                }
            }
            Spacer(modifier = Modifier.height(33.dp))
            Column {
                ListItem(
                    headlineContent = { Text("Two line list item with trailing") },
                    supportingContent = { Text("Secondary text") },
                    trailingContent = { Text("meta") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Localized description",
                        )
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
