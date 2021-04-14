package com.example.feedy1

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable


/**
 * Created by L on 10/05/2017.
 * Copyright (c) 2017 Centroida. All rights reserved.
 */
class LikeService : Service() {
    @Nullable
    override fun onBind(intent: Intent): IBinder? {

        //Saving action implementation
        return null
    }

    companion object {
        private const val NOTIFICATION_ID_EXTRA = "notificationId"
        private const val IMAGE_URL_EXTRA = "https://feedy.ly/public/dist/website/images/logo3.png"
    }
}