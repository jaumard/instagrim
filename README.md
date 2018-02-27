# Instagrim

Small Android demo application asked for an interview.

To make run you need to put your Instagram Secret and ClientId under your local.properties, if you don't it will not compile.

## Project info
The project was done with Android Studio and Kotlin with MVVM and data binding architecture.

The project use proguard to optimize apk size by shrinking code and resources.

Vector images are use as much as possible to also reduce apk side.

[Demo](https://vimeo.com/258537164)

### Sdk versions:
compileSdkVersion=27

minSdkVersion=16

targetSdkVersion=27

### Libraries
- Retrofit
- Rx JAVA 2
- Rx Kotlin
- Rx Android
- Glide
- Android architecture Room
- Android architecture ViewModels
- Constraint layout
- Android appcompat support libraries
- Android recycler/design libraries
- Recycler View Binding
- Dagger 2
- Timber
- Mockito
- Mockito Kotlin
- JUnit

### Addon
- Shared element animations between activities
- swipe to refresh list

### Tests
There some unit and functional tests, to run functional tests please be sure to disable any animation on device/emulator before running them.

## Limitation of Instagram in sandbox mode :
- it return only 20 photos even if the account has more
- allow 500 request per hour, after that a 429 is returned