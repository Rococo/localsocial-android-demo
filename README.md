## LocalSocial Demo Android Application

This application demonstrates the basics of getting started with the LocalSocial SDK for Android.

1. Authenticate the device with LocalSocial 
1. Make a requests to the LocalSocial backend service.

### Initial setup

1. Go to http.//dev.mylocalsocial.com and register for API keys. You will need API keys for each App that you wish to use with the LocalSocial Library. 
All keys can me managed from your LocalSocial Developer Account.

1. Add the correct values for your app into "app/src/main/res/values/ls_config_strings.xml".  

### Build from the command line

```
./gradlew assembleDebug
```

### Import and Build from the Android Studio

1. Start the Android studio and select File | Import Project
1. Browse to and select this directory, all going well that should be it.
1. Shift & F10 to build the app.