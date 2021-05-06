# Traveller
 **Android app, made as a project for mobile programming course**

App was created in Android Studio in JAVA programming language.
Main purpose of the app is to manage list of places visited by user. App uses firebase for storage.

## App consists of several activities:
- **Login/authentication:**
Login by e-mail and password is supported. App also supports identity provider for login. User that doesn't have account can create one.
- **Visited places:**
Displays list of visited places for logged user. Each element in the list has sevral properties: name, circumference and optional picture.
- **Details:**
Displays photo of the place or deafult graphics, name of the place and short description added by the user.
- **Add/edit:**
User can add new place to his list (by choosing location from google maps or using current location) with appropriate circumference, description (<500 characters) and photo (using phone camera). User can also modify previousy stored location or remove it.
- **Map:**
Displays Google Maps with marked user places (markers with areas defined by circumference).



App uses notifications to inform user when entering known location (checked by Geofence).


## **Known issues:**
- App crashes when dispaying map and no places are stored in db.
- New place can be stored without setting circumference (validation needed).
- Photos of places are stored as base64 strings in db (firebase storage would be better). 
