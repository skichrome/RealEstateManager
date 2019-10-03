# RealEstateManager
This project is an Android App developed for a formation on [OpenClassrooms](https://openclassrooms.com/fr/)

## Api keys configuration
To be able to compile this project from sources, you will need to provides your api credentials from
Google Cloud Platform. You will need a Google Map Android Sdk Key[(see details here)](https://developers.google.com/maps/documentation/android-sdk/intro) and 
a Google Geocoding Api key [(details here)](https://developers.google.com/maps/documentation/geocoding/start).

After you have activated your Api and created your credentials, you have to create a file in the root level of
the project named apikey.properties, place this content into it and replace values with your Api keys.
Release Google Map Api key is optional if you want to build the debug variant (and debug key is optional if 
you build the release variant).

```properties
# This file is dedicated to credentials that must be not versioned to Git.
# To get this file working you need to tweak app level build.gradle file.

# Google Map SDK For Android
android_google_map_sdk_api_debug=MAP_DEBUG_KEY_HERE
android_google_map_sdk_api_release=MAP_RELEASE_KEY_HERE

# Google Geocoding API
google_geocoding_api="GEOCODING_KEY_HERE"
```

## Release build configuration
To be able to compile this project with release build variant, you must sign the generated apk with
a keystore file. To do this, you have to create a file named 'keystore.properties' in root level of this
project, and put this content into it :

```properties
# This file is dedicated to Keystore credentials that must be not versioned to Git.
# To get this file working you need to tweak app level build.gradle file.

# Keystore credentials
keystore_file=ABSOLUTE_PATH_TO_YOUR_KEYSTORE_FILE
keystore_file_pwd=KEYSTORE_FILE_PASSWORD
keystore_alias=KEY_ALIAS
keystore_pwd=KEY_PASSWORD
```