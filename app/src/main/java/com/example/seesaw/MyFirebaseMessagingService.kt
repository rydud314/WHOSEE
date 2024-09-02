package com.example.seesaw

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    //수신부
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // NotificationChannel을 API 레벨 26 이상에서만 생성하도록 조건 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 채널 이름
            val name = "채팅 알림"
            // 채널 설명
            val descriptionText = "채팅 알림 입니다"
            // 중요도에 따라 배터리 관리(수면 모드) -> 디폴트
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            // 채널 생성
            val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance)
            mChannel.description = descriptionText
            // 시스템에 채널 등록
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // 채널 연결
            notificationManager.createNotificationChannel(mChannel)

            val body = message.notification?.body ?: ""
            val notificationBuilder = NotificationCompat.Builder(applicationContext, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(body)

            //id -> notification의 아이디
            notificationManager.notify(0, notificationBuilder.build())

        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}