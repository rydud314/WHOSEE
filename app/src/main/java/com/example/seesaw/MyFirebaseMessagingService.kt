package com.example.seesaw

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // 수신부
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("MessageActivity", "Data: ${message.data}")

        // 포그라운드 상태에서도 알림을 생성하기 위해 `notification`이 아닌 `data` 필드를 사용
        val title = message.data["title"] ?: getString(R.string.app_name)
        val body = message.data["body"] ?: "메시지가 도착했습니다."
        val chatRoomUid = message.data["chatRoomUid"] // FCM 메시지에 포함된 chatRoomUid
        val destinationUid = message.data["destinationUid"] // 수신자의 UID
        val click_action = message.data["click_action"]

        // 알림 클릭 시 MessageActivity로 이동
        val intent = Intent("OPEN_CHAT").apply {
            putExtra("chatRoomUid", chatRoomUid) // 채팅방 ID 전달
            putExtra("destinationUid", destinationUid) // 상대방 UID 전달
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            Log.d("MessageActivity", "$chatRoomUid 와 $destinationUid 채팅방으로 이동합니다.")
//            Log.d(TAG, "FCM Data received: chatRoomUid=$chatRoomUid, destinationUid=$destinationUid")
        }

        // PendingIntent 생성
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // NotificationChannel을 API 레벨 26 이상에서만 생성하도록 조건 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 채널 이름
            val name = "채팅 알림"
            // 채널 설명
            val descriptionText = "채팅 알림 입니다"
            // 중요도에 따라 배터리 관리(수면 모드) -> 디폴트
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            // 채널 생성
            val mChannel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                name,
                importance
            )
            mChannel.description = descriptionText
            // 시스템에 채널 등록
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            // 알림 생성
            val notificationBuilder = NotificationCompat.Builder(
                applicationContext,
                getString(R.string.default_notification_channel_id)
            )
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val notificationId = System.currentTimeMillis().toInt()
            // 알림 표시
            notificationManager.notify(notificationId, notificationBuilder.build())
        } else {
            // API 레벨 26 미만에서도 알림 생성 (NotificationChannel이 필요하지 않음)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val notificationBuilder = NotificationCompat.Builder(
                applicationContext,
                getString(R.string.default_notification_channel_id)
            )
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 새로운 토큰 처리 (서버로 전송 등)
    }
}