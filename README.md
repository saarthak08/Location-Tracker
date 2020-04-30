# Location-Tracker
![maps1](https://user-images.githubusercontent.com/38679082/62065492-b7a7d900-b24c-11e9-8a6c-2be57576282f.jpeg)
>An android application which I built in a hackathon event *'HackAMU'* in my college annual fest *'ZARF'*.


## *Features:*
- Login/ Signup as a student.
- Chat with any teacher as a student.
- Get any teacher's location if he or she has enabled it.
- Student can see the distance between him/her & the teacher if he/she has made location visible.
- Login/ Signup as faculty/teacher.
- Chat with any student as a teacher/faculty.
- Enable/Hide your location if you are a faculty/teacher.
- Offline Login/SignUp system implemented via Room Database.
- Push notifications (without Firebase i.e. manual notifications) on several events like message recieved, self location shared or location shared by any faculty/teacher.




## *Libraries Used:*
- [Material Dialogs](https://github.com/afollestad/material-dialogs)
- [Firebase & its Services](https://firebase.google.com/docs/android/setup)
- [Google Play Services](https://developers.google.com/android/guides/setup)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
- [Dagger-2](https://github.com/google/dagger)
- [Room](https://developer.android.com/topic/libraries/architecture/room)
- [zoomage](https://github.com/jsibbold/zoomage)


## *Note:*
- In order to build/run this application, you need to add your 'google-services.json' file in the app folder.
- This app has two faces, one for students & one for faculty. If you want to test all the functionalities, then two android mobile phones with two different emails are required at the same time. One for the student & another for the faculty. After this, start location sharing with the device signed up as faculty, the other device will be automatically notified & can start tracking faculty. Chat services can also be tested.
- Location & distance may not be too accurate as it depends on several factors like internet connection speed, location mode etc. Accuracy improves by time.


## *Debug APK Link:*
- https://drive.google.com/open?id=16WCOlxEvX_tteG0toMJ1gxlzt747BhQY

## *Screenshots:*
<img src="https://user-images.githubusercontent.com/38679082/62067466-72d27100-b251-11e9-8e51-ea6e4a2db3a0.jpeg" alt="Launcher Activity" width="250"/> .    <img src="https://user-images.githubusercontent.com/38679082/62067468-72d27100-b251-11e9-9234-ab8cbb5b5dc1.jpeg" alt="Location Activity" width="250"/> .    <img src="https://user-images.githubusercontent.com/38679082/58202274-aeead200-7cf4-11e9-9d80-b477ed379bd6.png" alt="Navigation Drawer" width="250"/> .    <img src="https://user-images.githubusercontent.com/38679082/62067465-7239da80-b251-11e9-8f60-674b46e1c4af.jpeg" alt="FacultyMainActivity" width="250"/>  .    <img src="https://user-images.githubusercontent.com/38679082/58437535-04a1ee80-80e8-11e9-945d-9ee4baacfe83.jpeg" alt="Chat Activity" width="250"/>
