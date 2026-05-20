package org.xmsleep.app.ui.flipclock

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.xmsleep.app.R

/**
 * 翻页时钟字体枚举
 */
enum class ClockFont(
    val displayName: String,
    val fontFamily: FontFamily,
    val descriptionResId: Int,
    val verticalOffset: Int = 0, // 垂直偏移量（dp）
    val fontSize: Int = 240 // 主数字字号（sp）
) {
    BEBAS_NEUE(
        displayName = "Bebas Neue",
        fontFamily = FontFamily(Font(R.font.bebas_neue, FontWeight.Normal)),
        descriptionResId = R.string.flip_clock_font_bebas_neue_desc,
        verticalOffset = 12, // 向下偏移12dp
        fontSize = 260 // 字号放大40sp
    ),
    OSWALD(
        displayName = "Oswald",
        fontFamily = FontFamily(Font(R.font.oswald_bold, FontWeight.Bold)),
        descriptionResId = R.string.flip_clock_font_oswald_desc,
        verticalOffset = -6, // 向上偏移6dp
        fontSize = 230 // 字号稍小
    );

    companion object {
        fun fromOrdinal(ordinal: Int): ClockFont {
            return entries.getOrElse(ordinal) { OSWALD }
        }
    }
}
