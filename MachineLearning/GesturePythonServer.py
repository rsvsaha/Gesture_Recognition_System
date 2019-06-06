from flask import Flask
from flask import request
import json
from predict import *
import dataset
import train
import os
from dataset import *
from train import *
import subprocess


#App URL=https://pacific-bayou-35154.herokuapp.com

Root="Predict"
SampleRoot="Sample"
predict_filename="predfile.txt"
app=Flask(__name__)
@app.route('/predict', methods = ['POST'])
def Predict():
    print (request.is_json)
    readings=request.get_json()['Data']
    f=open(Root+os.sep+predict_filename,"w")
    f.write(readings)
    f.close()
    process=subprocess.Popen(['python','predict.py','-f=predfile.txt'],stdout=subprocess.PIPE)
    predicted_word,err=process.communicate()
    print(predicted_word)
    return predicted_word
@app.route('/train', methods = ['POST'])
def GatherData():
    print (request.is_json)
    readings=request.get_json()['Data']
    reading_fileName=request.get_json()['FileName']
    f=open(SampleRoot+os.sep+reading_fileName+".txt","w")
    f.write(readings)
    f.close()
    return "RECEIVED "+reading_fileName

@app.route('/trainModel', methods = ['POST'])
def TrainModel():
    print (request.is_json)
    choice=request.get_json()['TrainModel']
    if(choice=="YES"):
    	createDataset()
    	os.system("python train.py > modelaccuracy.txt")
    	f=open("modelaccuracy.txt","r")
    	for line in f.readlines():
    		if("SCORE" in line):
    			f.close()
    			return line
    return "ERROR"

@app.route('/clearDataset', methods = ['POST'])
def ClearDataset():
    print (request.is_json)
    choice=request.get_json()['Clear']
    if(choice=="YES"):
    	print("Deleting Dataset")
    return "ERROR"
@app.route('/helloApp',methods=['GET'])
def helloApp():
    return "Hello App"
@app.route('/GetFileStructure',methods=['GET'])
def getFileStructure():
    p=os.listdir()
    return p




