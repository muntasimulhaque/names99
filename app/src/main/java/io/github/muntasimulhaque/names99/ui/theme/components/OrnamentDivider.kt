package io.github.muntasimulhaque.names99.ui.theme.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.muntasimulhaque.names99.R

/** Hairline — star — hairline, in the secondary (gold) tone. */
@Composable
fun OrnamentDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
) {
    Row(
        modifier = modifier.height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = color.copy(alpha = 0.55f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_ornament_star),
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .size(13.dp),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = color.copy(alpha = 0.55f),
        )
    }
}
