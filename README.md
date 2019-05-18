# Gesture_Recognition_System

# Requirements of this project

Hardware Requirements
1.STM32F10C86 MCU
2.HC-05 BT Module with BaudRate set as 115200 bps
3.5 MPU6050 Hex Axial IMUs
6.5V Power Supply.

Software Requirements
1.Python
2.Node js

# Steps to set up this project

Hardware part: Download the repo and unzip the Drivers.rar in the HardWareCode.Then open the project in Keil and upload the code to the MCU.Set Up the Hardware as per the schematic.

Android part: Download and install the APK.

ML part: In the ML Folder Empty the Sample and Predict Folders.Then Run the Server.

# How to Use
1.Open the Android App and connect it to the Bluetooth Module.
2.Enter the Server IP of the ML Part.
3.Name a gesture and set the number of samples.
4.Do the gesture required no. of times.
5.Repeat steps 2-5 for the number gestures you want to record.
6.Open the ML Folder and Run dataset.py
7.Open the ML Folder and Run train.py
8.Open the Android App in Normal Mode.
9.Enter the Server IP.
10.Do the Gestures and see the system correctly predicting it (Mostly :p )


