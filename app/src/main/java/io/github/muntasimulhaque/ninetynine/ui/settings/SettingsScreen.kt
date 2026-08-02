package io.github.muntasimulhaque.ninetynine.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.muntasimulhaque.ninetynine.BuildConfig
import io.github.muntasimulhaque.ninetynine.R
import io.github.muntasimulhaque.ninetynine.data.ThemeMode
import io.github.muntasimulhaque.ninetynine.ui.NamesViewModel
import io.github.muntasimulhaque.ninetynine.ui.theme.Motion
import io.github.muntasimulhaque.ninetynine.ui.theme.components.BackButton
import io.github.muntasimulhaque.ninetynine.ui.theme.components.MixedText
import io.github.muntasimulhaque.ninetynine.ui.theme.components.NavRow
import io.github.muntasimulhaque.ninetynine.ui.theme.components.PageRule
import io.github.muntasimulhaque.ninetynine.ui.theme.components.ScreenLabel
import io.github.muntasimulhaque.ninetynine.ui.theme.components.paperTopBarColors
import io.github.muntasimulhaque.ninetynine.ui.theme.components.SectionLabel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

private const val SCALE_MIN = 0.85f
private const val SCALE_MAX = 1.4f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NamesViewModel,
    onAbout: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val textScale by viewModel.textScale.collectAsStateWithLifecycle()
    val dailyEnabled by viewModel.dailyEnabled.collectAsStateWithLifecycle()
    val dailyTime by viewModel.dailyTime.collectAsStateWithLifecycle()

    var sliderValue by remember(textScale) { mutableFloatStateOf(textScale) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showBlockedDialog by rememberSaveable { mutableStateOf(false) }

    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        // Say something on denial. Android stops showing the system dialog
        // after the second refusal, so without this the switch would simply do
        // nothing, for ever, with no way for the reader to find out why.
        if (granted) viewModel.setDailyEnabled(true) else showBlockedDialog = true
    }

    // The permission can also be withdrawn in system settings long after it was
    // granted, which would leave this screen saying the reminder is on while
    // nothing is ever posted.
    LifecycleResumeEffect(dailyEnabled) {
        if (dailyEnabled && !notificationsAllowed(context)) viewModel.setDailyEnabled(false)
        onPauseOrDispose {}
    }

    Scaffold(
        topBar = {
            // A pushed screen since the gear replaced the tab, so it is titled
            // and left the same way as About, Flashcards and Quiz.
            TopAppBar(
                colors = paperTopBarColors(),
                title = { ScreenLabel(stringResource(R.string.settings)) },
                navigationIcon = { BackButton(onBack) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(10.dp))
            SectionLabel(stringResource(R.string.theme_section))
            Spacer(Modifier.height(10.dp))
            Column(Modifier.selectableGroup()) {
                ThemeOption(ThemeMode.SYSTEM, R.string.theme_system, themeMode, viewModel::setThemeMode)
                ThemeOption(ThemeMode.LIGHT, R.string.theme_light, themeMode, viewModel::setThemeMode)
                ThemeOption(ThemeMode.DARK, R.string.theme_dark, themeMode, viewModel::setThemeMode)
                ThemeOption(ThemeMode.BLACK, R.string.theme_black, themeMode, viewModel::setThemeMode)
            }

            SectionBreak()
            SectionLabel(stringResource(R.string.text_size))
            Spacer(Modifier.height(22.dp))
            // The specimen itself is the preview — no box around it.
            MixedText(
                text = stringResource(R.string.text_size_preview),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(22.dp))
            HairlineSlider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { viewModel.setTextScale(sliderValue) },
            )

            // The slider's own touch target already leaves air beneath the track.
            SectionBreak(top = 6.dp)
            SectionLabel(stringResource(R.string.daily_section))
            Spacer(Modifier.height(4.dp))
            // Toggling lives on the row, not the Switch: a bare Switch has no
            // accessible name of its own, because its label is a sibling.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = dailyEnabled,
                        role = Role.Switch,
                        onValueChange = { enable ->
                            if (enable && Build.VERSION.SDK_INT >= 33) {
                                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.setDailyEnabled(enable)
                            }
                        },
                    )
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.daily_reminder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Switch(checked = dailyEnabled, onCheckedChange = null)
            }
            AnimatedVisibility(
                visible = dailyEnabled,
                enter = fadeIn(tween(Motion.GENTLE)) + expandVertically(tween(Motion.GENTLE)),
                exit = fadeOut(tween(Motion.QUICK)) + shrinkVertically(tween(Motion.QUICK)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.reminder_time),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = formatTime(context, dailyTime.first, dailyTime.second),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            SectionBreak()
            SectionLabel(stringResource(R.string.memorization_section))
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showResetDialog = true }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.reset_progress),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            // The About row brings its own padding — no space needed under the rule.
            SectionBreak(bottom = 0.dp)
            NavRow(
                title = stringResource(R.string.about),
                subtitle = stringResource(R.string.about_row_subtitle),
                onClick = onAbout,
            )
            PageRule()
            Spacer(Modifier.height(22.dp))
            // Compile-time constant: no PackageManager call, and no fallback
            // string to go stale one release after somebody forgets it.
            // A datum, not a heading. SectionLabel is the app's heading style, so
            // this rendered "VERSION 3.3" as a gold section label with no
            // section under it — and the only gold on the page announcing
            // nothing. Same treatment as the closing line on About.
            Text(
                text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(28.dp))
        }
    }

    if (showTimePicker) {
        // Material 3 time picker, themed with the app — not the legacy dialog.
        val timeState = rememberTimePickerState(
            initialHour = dailyTime.first,
            initialMinute = dailyTime.second,
            is24Hour = DateFormat.is24HourFormat(context),
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(R.string.reminder_time)) },
            text = {
                TimePicker(state = timeState)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setDailyTime(timeState.hour, timeState.minute)
                    showTimePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.reset_progress)) },
            text = { Text(stringResource(R.string.reset_dialog_text)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetProgress()
                    showResetDialog = false
                }) {
                    Text(
                        stringResource(R.string.reset),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showBlockedDialog) {
        AlertDialog(
            onDismissRequest = { showBlockedDialog = false },
            title = { Text(stringResource(R.string.notifications_blocked_title)) },
            text = { Text(stringResource(R.string.notifications_blocked_body)) },
            confirmButton = {
                TextButton(onClick = {
                    showBlockedDialog = false
                    openNotificationSettings(context)
                }) {
                    Text(stringResource(R.string.open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockedDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

/** Whether Android will actually let the daily name be posted. */
private fun notificationsAllowed(context: android.content.Context): Boolean =
    Build.VERSION.SDK_INT < 33 ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

/** Takes the reader to the one place a blocked permission can be granted again. */
private fun openNotificationSettings(context: android.content.Context) {
    val toNotifications = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    val toAppDetails = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null),
    )
    // Both are hand-offs to the system; a device with neither should not crash.
    runCatching { context.startActivity(toNotifications) }
        .onFailure { runCatching { context.startActivity(toAppDetails) } }
}

/**
 * The space and rule that separate one group of choices from the next.
 * Controls that carry their own touch padding pass a smaller [top].
 */
@Composable
private fun SectionBreak(top: Dp = 30.dp, bottom: Dp = 30.dp) {
    Spacer(Modifier.height(top))
    PageRule()
    Spacer(Modifier.height(bottom))
}

/**
 * One theme, chosen typographically: the current one steps up in weight and
 * ink and takes a gold check. No radio, no container.
 */
@Composable
private fun ThemeOption(
    mode: ThemeMode,
    labelRes: Int,
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    val selected = current == mode
    // Fading the check keeps the row from shifting as the choice moves.
    val checkAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(Motion.QUICK),
        label = "themeCheck",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = { onSelect(mode) },
                role = Role.RadioButton,
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.Filled.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer { alpha = checkAlpha },
        )
    }
}

/** A gold bead on a hairline — the Material slider stripped to the app's line. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HairlineSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    val gold = MaterialTheme.colorScheme.secondary
    val track = MaterialTheme.colorScheme.outlineVariant
    val fraction = ((value - SCALE_MIN) / (SCALE_MAX - SCALE_MIN)).coerceIn(0f, 1f)
    val label = stringResource(R.string.text_size)
    val percent = stringResource(R.string.percent, (value * 100).roundToInt())
    Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = SCALE_MIN..SCALE_MAX,
        // A bare Slider announces "seek control, 27 percent" with no subject
        // and no unit; the hairline also leaves only a 16dp focus rectangle.
        modifier = Modifier
            .heightIn(min = 48.dp)
            .semantics {
                contentDescription = label
                stateDescription = percent
            },
        thumb = {
            Box(
                Modifier
                    .size(14.dp)
                    .background(gold, CircleShape)
            )
        },
        track = { _ ->
            Box(Modifier.fillMaxWidth().height(1.dp)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(track)
                )
                Box(
                    Modifier
                        .fillMaxWidth(fraction)
                        .height(1.dp)
                        .background(gold)
                )
            }
        },
    )
}

private fun formatTime(context: android.content.Context, hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    val pattern = if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm a"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.time)
}
