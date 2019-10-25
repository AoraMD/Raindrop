package moe.aoramd.raindrop.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.session.MediaButtonReceiver
import moe.aoramd.lookinglass.manager.ContextManager
import moe.aoramd.raindrop.R
import moe.aoramd.raindrop.service.PlayService
import moe.aoramd.raindrop.view.play.PlayActivity

object NotifyManager {

    fun registerNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                ContextManager.systemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel =
                NotificationChannel(
                    channelId,
                    ContextManager.resourceString(R.string.app_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            manager.createNotificationChannel(channel)
        }
    }

    fun mediaStyleNotification(
        context: Context,
        channelId: String,
        session: MediaSessionCompat,
        playing: Boolean,
        like: Boolean
    ): Notification {
        val metadata = session.controller.metadata
        val builder = NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE))
            setContentText(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE))
            setSmallIcon(R.drawable.ic_icon_temp)
            setLargeIcon(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, PlayActivity::class.java),
                    0
                )
            )

            addAction(
                createNotificationAction(
                    context,
                    R.drawable.ic_skip_previous,
                    R.string.description_previous_song,
                    PlayService.ACTION_SKIP_TO_PREVIOUS
                )
            )
            addAction(
                createNotificationAction(
                    context,
                    if (playing) R.drawable.ic_pause else R.drawable.ic_play,
                    R.string.description_play,
                    if (playing) PlayService.ACTION_PAUSE else PlayService.ACTION_PLAY
                )
            )
            addAction(
                createNotificationAction(
                    context,
                    R.drawable.ic_skip_next,
                    R.string.description_next_song,
                    PlayService.ACTION_SKIP_TO_NEXT
                )
            )
            addAction(
                createNotificationAction(
                    context,
                    if (like) R.drawable.ic_favorite else R.drawable.ic_favorite_border,
                    R.string.description_like,
                    PlayService.ACTION_LIKE
                )
            )

            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(session.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
            )
        }
        return builder.build()
    }

    private fun createNotificationAction(
        context: Context,
        icon: Int,
        description: Int,
        pendingIntentAction: String
    ): NotificationCompat.Action =
        NotificationCompat.Action(
            icon,
            ContextManager.resourceString(description),
            PendingIntent.getBroadcast(context, 0, Intent(pendingIntentAction), 0)
        )
}