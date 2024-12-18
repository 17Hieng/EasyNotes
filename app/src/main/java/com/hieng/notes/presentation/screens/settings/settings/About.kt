package com.hieng.notes.presentation.screens.settings.settings

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.ContactSupport
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hieng.notes.R
import com.hieng.notes.core.constant.ConnectionConst
import com.hieng.notes.core.constant.SupportConst.getSupportersMap
import com.hieng.notes.presentation.screens.settings.BottomModal
import com.hieng.notes.presentation.screens.settings.SettingsScaffold
import com.hieng.notes.presentation.screens.settings.model.SettingsViewModel
import com.hieng.notes.presentation.screens.settings.widgets.ActionType
import com.hieng.notes.presentation.screens.settings.widgets.ListDialog
import com.hieng.notes.presentation.screens.settings.widgets.SettingCategory
import com.hieng.notes.presentation.screens.settings.widgets.SettingsBox


@Composable
fun AboutScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    SettingsScaffold(
        settingsViewModel = settingsViewModel,
        title = stringResource(id = R.string.about),
        onBackNavClicked = { navController.navigateUp() }
    ) {
        LazyColumn {
//            item {
//                SettingCategory(
//                    smallSetting = true,
//                    title = stringResource(id = R.string.support),
//                    subTitle = stringResource(id = R.string.support_description),
//                    icon = Icons.AutoMirrored.Rounded.ArrowForwardIos,
//                    shape = shapeManager(radius = settingsViewModel.settings.value.cornerRadius, isBoth = true),
//                    isLast = true,
//                    composableAction = { onExit -> BottomModal(navController = navController, settingsViewModel = settingsViewModel) { onExit() }}
//                )
//            }
            item {
                SettingsBox(
                    title = stringResource(id = R.string.support_list),
                    icon = Icons.Rounded.Coffee,
                    actionType = ActionType.CUSTOM,
                    radius = shapeManager(isBoth = true, radius = settingsViewModel.settings.value.cornerRadius),
                    customAction = { onExit -> ContributorsClicked(context, settingsViewModel = settingsViewModel) { onExit() } }
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
            item {
                SettingsBox(
                    title = stringResource(id = R.string.version),
                    description = settingsViewModel.version,
                    icon = Icons.Rounded.Info,
                    actionType = ActionType.TEXT,
                    radius = shapeManager(isBoth = true, radius = settingsViewModel.settings.value.cornerRadius),
                )
                Spacer(modifier = Modifier.height(18.dp))
            }
            item {
                SettingsBox(
                    title = stringResource(id = R.string.project_based),
                    description = "https://github.com/Kin69/EasyNotes",
                    icon = Icons.Rounded.Info,
                    actionType = ActionType.LINK,
                    radius = shapeManager(isFirst = true, radius = settingsViewModel.settings.value.cornerRadius),
                    linkClicked = { uriHandler.openUri("https://github.com/Kin69/EasyNotes/releases") }
                )
            }
            item {
                SettingsBox(
                    title = stringResource(id = R.string.source_code),
                    icon = Icons.Rounded.Download,
                    actionType = ActionType.LINK,
                    radius = shapeManager(isLast = true, radius = settingsViewModel.settings.value.cornerRadius),
                    linkClicked = { uriHandler.openUri("https://github.com/17Hieng/EasyNotes") }
                )
            }

//            item {
//                SettingsBox(
//                    title = stringResource(id = R.string.email),
//                    icon = Icons.Rounded.Email,
//                    clipboardText = ConnectionConst.SUPPORT_MAIL,
//                    actionType = ActionType.CLIPBOARD,
//                    radius = shapeManager(isFirst = true, radius = settingsViewModel.settings.value.cornerRadius),
//                )
//            }
//            item {
//                SettingsBox(
//                    isBig = true,
//                    title = stringResource(id = R.string.discord),
//                    icon = Icons.AutoMirrored.Rounded.ContactSupport,
//                    actionType = ActionType.LINK,
//                    linkClicked = { uriHandler.openUri(ConnectionConst.SUPPORT_DISCORD) },
//                    radius = shapeManager(radius = settingsViewModel.settings.value.cornerRadius),
//                )
//            }
//            item {
//                SettingsBox(
//                    isBig = true,
//                    title = stringResource(id = R.string.feature),
//                    icon = Icons.Rounded.BugReport,
//                    linkClicked = { uriHandler.openUri(ConnectionConst.GITHUB_FEATURE_REQUEST) },
//                    actionType = ActionType.LINK,
//                    radius = shapeManager(isLast = true, radius = settingsViewModel.settings.value.cornerRadius),
//                )
//            }
        }
    }

}

@Composable
fun ContributorsClicked(
    context: Context,
    settingsViewModel: SettingsViewModel,
    onExit: () -> Unit
) {
    fun contributors(context: Context): List<Pair<String, String>> {
        val map = getSupportersMap(context)
        return map.flatMap { (role, supporters) ->
            supporters.map { supporter -> Pair(supporter, role) }
        }
    }

    ListDialog(
        text = stringResource(R.string.support_list),
        list = contributors(context),
        settingsViewModel = settingsViewModel,
        onExit = onExit,
        extractDisplayData = { it }
    ) { isFirstItem, isLastItem, displayData ->
        SettingsBox(
            title = displayData.first,
            description = displayData.second,
            radius = shapeManager(isFirst = isFirstItem, isLast = isLastItem, radius = settingsViewModel.settings.value.cornerRadius),
            actionType = ActionType.TEXT,
            customText = "❤"
        )
    }
}


