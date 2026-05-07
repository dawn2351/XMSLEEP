package org.xmsleep.app.ui.tomato

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.xmsleep.app.R
import org.xmsleep.app.ui.settings.TomatoTimerView

/**
 * 番茄时钟二级页面
 * 包含顶部返回栏 + TomatoTimerView 主体
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TomatoTimerScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tomato_timer_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = context.getString(R.string.flip_clock_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        TomatoTimerView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
