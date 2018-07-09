## Table of Contents
- [Table of Contents](#table-of-contents)
- [DHDR-FHIR Android Sample](#dhdr-fhir-android-sample)
- [Android Features Used](#android-features-used)
- [Getting Started](#getting-started)
    - [**Prerequisites**](#prerequisites)
    - [**Setting up the Dev Environment**](#setting-up-the-dev-environment)
        - [Downloading and installing Android Studio](#downloading-and-installing-android-studio)
        - [Importing the DHDR-FHIR project into Android Studio](#importing-the-dhdr-fhir-project-into-android-studio)
        - [Setting up your debugging environment](#setting-up-your-debugging-environment)
        - [Intel Systems - Installing HAXM](#intel-systems---installing-haxm)
- [Important Notes For Developers](#important-notes-for-developers)
- [Authors](#authors)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## DHDR-FHIR Android Sample
This is an Android application prototype that makes use of the HL7 HAPI FHIR 3.3.0 Library to connect to [Innovation Lab's Digital Health Drug Repository (DHDR)](https://www.innovation-lab.ca/digital-health-drug-repository-dhdr). It also connects to [Innovation Lab's Provincial Client Registry (PCR) v3](https://www.innovation-lab.ca/Provincial-Client-Registry).

## Android Features Used
These are the features that were used to put together this project:

- Multiple Activities 
    - Using intents and extending the Parcelable interface to move data
- List views and custom listview adapters (and with custom XML layouts)
- Most layouts use the ConstraintLayout (Potrait and Landscape)
- Extending the AsyncTask class to execute network operations (DHDR and PCR)
    - Uses the ViewHolder pattern for better UI performance
    - Uses built-in HTTP libraries in Android Java to query PCR
- Displaying Custom Dialogs (popups) for showing progress bars and error messages
- Uses DatePickerDialog so that users can enter dates as query filters. Date manipulation with Date, SimpleDateFormat and Calendar objects
- Uses SQLiteOpenHelper to implement a basic local database.

## Getting Started
These instructions will get you a copy of the project up and running on your Android Studio's emulator or on your real Android device.
### **Prerequisites**

```
- Android Studio (v3.x)
- Intel systems may be required to install HAXM
- This Git Android Project
- HAPI FHIR Android Library, plus base and structures libraries (included in git project)
- Joda Time Library (included in git project)
```

### **Setting up the Dev Environment**
#### Downloading and installing Android Studio
First, [download the latest version of Android Studio](https://developer.android.com/studio/).
The installation process is straightforward, however if you get stuck somewhere, [follow the official installation instructions provided](https://developer.android.com/studio/install).

#### Importing the DHDR-FHIR project into Android Studio
After cloning or downloading this Git project, go to:
1. File -> Open in Android Studio's menu bar.
2. Find and select the project that you just cloned/downloaded and click "OK".

#### Setting up your debugging environment
Once you have installed Android Studio and you've imported the Android Studio project, you will then need to decide on how you plan on running/debugging your application. There are two ways of doing this:
1. [Run the app on your real Android device over USB](https://developer.android.com/studio/run/device).
2. Run the app in an emulator. This can either be a third-party emulator you find online or it can be [Android Studio's built-in emulator](https://developer.android.com/studio/run/emulator).

**Note:** If you're using an emulator on Android Studio, you first need to create a virtual device image via the [AVD manager](https://developer.android.com/studio/run/managing-avds).


#### Intel Systems - Installing HAXM
If you need or want Intel's hardware acceleration software to speed up your Android emulation on Android Studio, then [follow these instructions](https://developer.android.com/studio/run/emulator-acceleration). This is highly recommended for developers running Intel CPUs.

## Important Notes For Developers
- Ensure that you have the proper permissions given to your applicaiton by including the following code in your Android Manifest .xml. If you do not include these permissions, your app wont be able to execute network operations.
- E.G:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="xx.xxx.xxxxx.dhdr_fhirprototype">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
- You also need to include a few more objects in your build.gradle (Module:app) **android {...} block**
- You might need to change "JavaVersion.VERSION_1_8" to your version of Java
```
android {
    ...
    ...
    ...
    packagingOptions {
        exclude 'META-INF/DEPENDENCIES'
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```
<br/>

---
## Authors
**Houssein Khafaja** - *eHealth Ontario co-op* - [eHealth Ontario](https://www.ehealthontario.on.ca/en/)

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments
* Dawne Pierce - *Mohawk College MEDIC* - for provding [FHIR North Java Sample Code](https://www.innovation-lab.ca/repository/ViewRepository?id=1911d245-5c6c-4156-bd7d-2f0701d6ce28) ([Github source](https://github.com/EHO-Innovation-Lab/FHIRNorthJava))
* Antoaneta Stoyanova for lending her time to answer my questions - *eHealth Ontario DHDR Architect* - [eHealth Ontario](https://www.ehealthontario.on.ca/en/)
* Yulia Shtrachman and Igor Sirkovich - *MOHLTC* - [MOHLTC Website](http://www.health.gov.on.ca/en/)