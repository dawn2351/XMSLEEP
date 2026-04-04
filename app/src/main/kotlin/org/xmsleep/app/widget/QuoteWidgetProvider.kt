package org.xmsleep.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.xmsleep.app.R
import org.xmsleep.app.quote.QuoteManager
import org.xmsleep.app.utils.Logger
import java.util.Calendar
import kotlin.random.Random

/**
 * 一言一句小组件
 * 显示当前时间、治愈标语和刷新按钮
 */
class QuoteWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TAG = "QuoteWidgetProvider"
        private const val ACTION_REFRESH = "org.xmsleep.app.widget.REFRESH"
        
        // 小组件独立的本地标语，不依赖应用内一言一句
        private val widgetQuotes = listOf(
            "生活不止眼前的苟且，还有诗和远方。",
            "星光不问赶路人，岁月不负有心人。",
            "愿你出走半生，归来仍是少年。",
            "凡是过往，皆为序章。",
            "山有木兮木有枝，心悦君兮君不知。",
            "人生若只如初见，何事秋风悲画扇。",
            "岁月静好，现世安稳。",
            "愿你被这个世界温柔以待。",
            "心有猛虎，细嗅蔷薇。",
            "且将新火试新茶，诗酒趁年华。",
            "愿你眼里有光，心中有爱。",
            "世间所有的相遇，都是久别重逢。",
            "不乱于心，不困于情，不畏将来，不念过往。",
            "愿你一生努力，一生被爱。",
            "生活明朗，万物可爱。"
        )
        
        /**
         * 获取随机本地标语
         */
        fun getRandomWidgetQuote(): Pair<String, String> {
            val quote = widgetQuotes[Random.nextInt(widgetQuotes.size)]
            return Pair(quote, "一言一句")
        }
        
        /**
         * 更新所有小组件
         */
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, QuoteWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            
            if (appWidgetIds.isNotEmpty()) {
                Logger.d(TAG, "更新所有小组件: ${appWidgetIds.size} 个")
                // 发送广播触发 onUpdate
                val intent = Intent(context, QuoteWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Logger.d(TAG, "onUpdate: ${appWidgetIds.size} 个小组件")
        
        // 为每个小组件更新视图
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_REFRESH -> {
                Logger.d(TAG, "收到刷新广播")
                // 更新所有小组件（刷新会获取新的名言）
                updateAllWidgets(context)
            }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        Logger.d(TAG, "小组件首次添加")
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        Logger.d(TAG, "最后一个小组件被移除")
    }

    /**
     * 更新单个小组件
     */
    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Logger.d(TAG, "更新小组件: $appWidgetId")
        
        // 创建 RemoteViews
        val views = RemoteViews(context.packageName, R.layout.widget_quote)
        
        // 设置时间
        val timeText = getCurrentTime()
        views.setTextViewText(R.id.widget_time, timeText)
        
        // 设置刷新按钮点击事件
        val refreshIntent = Intent(context, QuoteWidgetProvider::class.java).apply {
            action = ACTION_REFRESH
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_refresh, refreshPendingIntent)
        
        // 先显示本地随机标语，避免空白
        val (localQuote, localAuthor) = getRandomWidgetQuote()
        views.setTextViewText(R.id.widget_quote, localQuote)
        views.setTextViewText(R.id.widget_nickname, "—— $localAuthor")
        appWidgetManager.updateAppWidget(appWidgetId, views)
        
        // 异步尝试从API获取真正的名言
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val quote = withTimeoutOrNull(5000) {
                    QuoteManager.getInstance(context).getTodayQuote()
                }
                
                if (quote != null) {
                    withContext(Dispatchers.Main) {
                        val updateViews = RemoteViews(context.packageName, R.layout.widget_quote)
                        updateViews.setTextViewText(R.id.widget_time, timeText)
                        updateViews.setTextViewText(R.id.widget_quote, quote.text)
                        updateViews.setTextViewText(R.id.widget_nickname, "—— ${quote.author}")
                        updateViews.setOnClickPendingIntent(R.id.widget_refresh, refreshPendingIntent)
                        appWidgetManager.updateAppWidget(appWidgetId, updateViews)
                        Logger.d(TAG, "小组件从API更新成功: ${quote.text}")
                    }
                } else {
                    Logger.w(TAG, "API获取超时或失败，保持本地标语")
                }
            } catch (e: Exception) {
                Logger.e(TAG, "API请求失败: ${e.message}")
            }
        }
    }

    /**
     * 获取当前时间
     */
    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return String.format("%02d:%02d", hour, minute)
    }
}
