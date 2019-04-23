# -*- coding: utf-8 -*-
"""
Created on Wed Sep 19 22:45:56 2018

@author: Rishav
"""

from data_read import readfile
import os
import numpy as np
from sklearn.externals import joblib
from sklearn.preprocessing import scale
from scipy.interpolate import interp1d
import matplotlib.pyplot as plt
fileName="YES_12.txt"
def load_from_file(filename,scaleData=False):
            data_raw=[]
            acc_x=[]
            acc_y=[]
            acc_z=[]
            gyro_x=[]
            gyro_y=[]
            gyro_z=[]
            j=0
            f=open(filename,"r")
            f.readline()
            print(filename)
            data_raw=[]
            for i in f.readlines():
                s=i.replace('\x00','').strip()
                xsd=([*map(lambda x: int(x), s.split(",")[1:-1])])
                #print(len(xsd))
                data_raw.append(xsd)
            #print(data_raw)
            
            data=np.array(data_raw).astype(float)
            if scaleData:
                data=scale(data)
            for i in range(0,5):
                acc_x.append(data[:,j])
                acc_y.append(data[:,j+1])
                acc_z.append(data[:,j+2])
                gyro_x.append(data[:,j+3])
                gyro_y.append(data[:,j+4])
                gyro_z.append(data[:,j+5])
                j=j+6
            
            return (data,acc_x,acc_y,acc_z,gyro_x,gyro_y,gyro_z)
def inter_polate_data(acc_x,acc_y,acc_z,gyro_x,gyro_y,gyro_z,size_fit=50):
            acc_x_stretch=[]
            acc_y_stretch=[]
            acc_z_stretch=[]
            gyro_x_stretch=[]
            gyro_y_stretch=[]
            gyro_z_stretch=[]
            (facc_x,facc_y,facc_z,fgyro_x,fgyro_y,fgyro_z)=([],[],[],[],[],[])
            length_along_x_axis=np.linspace(0,data.shape[0],data.shape[0])
            
            
            for i in range(0,5):
                facc_x1=interp1d(length_along_x_axis,acc_x[i])
                facc_x.append(facc_x1)
                facc_y1=interp1d(length_along_x_axis,acc_y[i])
                facc_y.append(facc_y1)
                facc_z1=interp1d(length_along_x_axis,acc_z[i])
                facc_z.append(facc_z1)
                fgyro_x1=interp1d(length_along_x_axis,gyro_x[i])
                fgyro_x.append(fgyro_x1)
                fgyro_y1=interp1d(length_along_x_axis,gyro_y[i])
                fgyro_y.append(fgyro_y1)
                fgyro_z1=interp1d(length_along_x_axis,gyro_z[i])
                fgyro_z.append(fgyro_z1)
            
            new_length_along_x_axis=np.linspace(0,data.shape[0],size_fit)
            

            for i in range(0,5):
                acc_x_stretch.append(facc_x[i](new_length_along_x_axis))
                acc_y_stretch.append(facc_y[i](new_length_along_x_axis))
                acc_z_stretch.append(facc_z[i](new_length_along_x_axis))
                gyro_x_stretch.append(fgyro_x[i](new_length_along_x_axis))
                gyro_y_stretch.append(fgyro_y[i](new_length_along_x_axis))
                gyro_z_stretch.append(fgyro_z[i](new_length_along_x_axis))
            return (acc_x_stretch,acc_y_stretch,acc_z_stretch,gyro_x_stretch,gyro_y_stretch,gyro_z_stretch)

data,ax,ay,az,gx,gy,gz=load_from_file(fileName,scaleData=True)
sax,say,saz,sgx,sgy,sgz=inter_polate_data(ax,ay,az,gx,gy,gz)

def sensor_plot(sensor_number,plot_only=0,sensor_axis_name=None):
    actual_signal="Actual Signal"
    resampled_signal="Resampled Signal"
    x_original=[i for i in range(1,len(ax[sensor_number])+1)]
    x_new=[i for i in range(1,len(say[sensor_number])+1)]
    plt.figure(figsize=(10,5))
        
    #plt.figure(40,20)
    if(plot_only==0 or plot_only==1):
        plt.plot(x_original,ax[sensor_number],linestyle='--',color='r',label=actual_signal)
        plt.plot(x_new,sax[sensor_number],'r',label=resampled_signal)    
    if(plot_only==0 or plot_only==2):
        plt.plot(x_original,ay[sensor_number],linestyle='--',color='g',label=actual_signal)
        plt.plot(x_new,say[sensor_number],'g',label=resampled_signal)    
    if(plot_only==0 or plot_only==3):
        plt.plot(x_original,az[sensor_number],linestyle='--',color='b',label=actual_signal)
        plt.plot(x_new,saz[sensor_number],'b',label=resampled_signal)
    if(plot_only==0 or plot_only==4):    
        plt.plot(x_original,gx[sensor_number],linestyle='--',color='y',label=actual_signal)
        plt.plot(x_new,sgx[sensor_number],'y',label=resampled_signal)    
    if(plot_only==0 or plot_only==5):    
        plt.plot(x_original,gy[sensor_number],linestyle='--',color='m',label=actual_signal)
        plt.plot(x_new,sgy[sensor_number],'m',label=resampled_signal)
    if(plot_only==0 or plot_only==6):    
        plt.plot(x_original,gz[sensor_number],linestyle='--',color='k',label=actual_signal)
        plt.plot(x_new,sgz[sensor_number],'k',label=resampled_signal)
    plt.ylabel('Scaled Value')
    plt.xlabel('Time Instant')
    plt.title('Plot of Actual Value and Resampled Value vs Time for '+sensor_axis_name)
    plt.legend(loc="upper left")
    plt.savefig('Sensor'+str(sensor_number)+'axis'+str(plot_only)+'.png')
    plt.show()


sensor_axis_Names=["Accelerometer axis X","Accelerometer axis Y","Accelerometer axis Z","Gyroscope axis X","Gyroscope axis Y","Gyroscope axis Z"]
for i in range(len(sensor_axis_Names)):
    sensor_plot(0,i+1,sensor_axis_Names[i])