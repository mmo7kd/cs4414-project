README
=============================================================
Ensure you have completed the following to run this project:

1) Install latest version of Android SDK, Google APIs, and Google play services
	-use the android sdk manager

2) Add the android-support-v7-appcomcat and google-play-services_lib to eclipse
	-import->existing android code, then navigate to the library folders in your android-sdk folder
	-you should see two new folders with the above names in your project
	
3) Link the two libraries mentioned above to your project
	-right click project -> properties -> android -> add libraries
	-you should see android-support-v4.jar in your libs folder
	
4) Ensure your project builds to android 4.4.2
	-project -> properties -> build target
	
4) Run the project on a physical android device or AVD (see below)
	- AVD Seems to be semi-working though this tutorial http://hmkcode.com/run-google-map-v2-on-android-emulator/
		-you get google play errors on the avd, but the app still runs
==============================================================
		
-Chris
	