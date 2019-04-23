# -*- coding: utf-8 -*-
"""
Created on Sun Nov  4 19:26:23 2018

@author: Rishav
"""

import dataset

from sklearn.model_selection import train_test_split,GridSearchCV
from sklearn.externals import joblib
from sklearn import svm
from sklearn.metrics import confusion_matrix
import numpy as np

def CreateModel(use_mid=False,from_raw=False):
    
    if from_raw:
        X,y,Decoder=dataset.createDataset()
    else:
        X,y=joblib.load("Dataset.pkl")
    if use_mid:
        accx_mid=X[:,2,:].reshape(100,1,50)
        accy_mid=X[:,7,:].reshape(100,1,50)
        accz_mid=X[:,12,:].reshape(100,1,50)
        gryx_mid=X[:,17,:].reshape(100,1,50)
        gyry_mid=X[:,22,:].reshape(100,1,50)
        gyrz_mid=X[:,27,:].reshape(100,1,50)
        X=np.concatenate((accx_mid,accy_mid,accz_mid,gryx_mid,gyry_mid,gyrz_mid),axis=1)
        print(X.shape)   
    X=X.reshape(X.shape[0],X.shape[1]*X.shape[2])
    x_train,x_test,y_train,y_test=train_test_split(X,y,test_size=0.25,random_state=24)
    print("split complete")
    params={'C':[0.001,0.01,0.1,1],'kernel':['linear','rbf','poly']}
    svc=svm.SVC(probability=True)
    clf=GridSearchCV(svc,params,verbose=10,n_jobs=8)
    print("fitting Model")
    clf.fit(x_train,y_train)
    
    print ("Confusion Matrix:")
    Y_predicted = clf.predict(x_test)
    print (confusion_matrix(y_test, Y_predicted))
    print ("\nBest estimator parameters: ")
    print (clf.best_estimator_)
    	
    	#Calculates the score of the best estimator found.
    score = clf.score(x_test, y_test)
    print ("\nSCORE: {score}\n".format(score = score))
    
    print("Saving model....")
    if use_mid:
    	joblib.dump(clf,"MODEL_MID.pkl")
    else:
    	joblib.dump(clf,"MODEL.pkl")

if __name__=="__main__":
    CreateModel()