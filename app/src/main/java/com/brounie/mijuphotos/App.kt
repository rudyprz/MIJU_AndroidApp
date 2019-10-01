package com.brounie.mijuphotos

import android.app.Application
import com.parse.Parse

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("BrounieApp")
                .clientKey("C4suYZKkyRMYPGR7fEae")
                .server("https://museomiju.com/parse")
                .build()
        )
    }

}