# LocShare
![HackAMUCover](https://user-images.githubusercontent.com/38679082/56091329-285bfd00-5ecb-11e9-9443-d947606b63ac.png)

>An android application which I build in a hackathon event *'HackAMU'* in my college annual fest *'ZARF'*.


## *Features:*
- Login/ Signup as a student.
- Chat with any teacher as a student.
- Get any teacher's location if he or she has enabled it.
- Student can see the distance between him/her & the teacher if he/she has made location visible.
- Login/ Signup as faculty/teacher.
- Chat with any student as a teacher/faculty.
- Enable/Hide your location if you are a faculty/teacher.
- Offline Login/SignUp system implemented via Room Database.
- Push notifications (Without Firebase i.e. manual notifications) on several events like message recieved, self location shared or location shared by any faculty/teacher.




## *Libraries Used:*
- [Retrofit](https://github.com/square/retrofit)
- [Material Dialogs](https://github.com/afollestad/material-dialogs)
- [Room](https://developer.android.com/topic/libraries/architecture/room)
- [Data Binding (Android Architectural Components)](https://developer.android.com/topic/libraries/data-binding)
- [Firebase & its Services](https://firebase.google.com/docs/android/setup)
- [Google Play Services](https://developers.google.com/android/guides/setup)


## *Note:*
- This app has two faces, one for students & one for faculty. If you want to test all the functionalities, then two android mobile phones with two different emails are required at the same time. One for the student & another for the faculty. After this, start location sharing with the device signed up as faculty, the other device will be automatically notified & can start tracking faculty. Chat services can also be tested.
- Location & distance may not be too accurate as it depends on several factors like internet connection speed, location mode etc. Accuracy improves by time.

## *Screenshots:*
<img src="https://user-images.githubusercontent.com/38679082/58202271-aeead200-7cf4-11e9-943d-7203f6be1a77.png" alt="Location Activity" width="250"/> .    <img src="https://user-images.githubusercontent.com/38679082/58202274-aeead200-7cf4-11e9-9d80-b477ed379bd6.png" alt="Navigation Drawer" width="250"/> .    <img src="https://user-images.githubusercontent.com/38679082/58202278-b01bff00-7cf4-11e9-8874-f466a3dfb220.jpeg" alt="Faculty Activity Location On" width="250"/> .    <img src="https://user-images.githubusercontent.com/38679082/58202277-af836880-7cf4-11e9-8f7f-e1e7ad119a08.jpeg" alt="Faculty Activity" width="250"/> .    <img src="https://user-images.githubusercontent.com/38679082/58202275-af836880-7cf4-11e9-8c06-2a60882b6bac.png" alt="Students Activity" width="250"/> .    <img src="https://user-images.githubusercontent.com/38679082/58202279-b01bff00-7cf4-11e9-9c4e-46574220d948.jpeg" alt="Chat Activity" width="250"/>

## *Debug APK Link:*
- https://drive.google.com/open?id=1V5_eDVspxKGIzv9TErT0a6Nmq7kNsbHc
