# -*- coding: utf-8 -*-
"""
Created on Wed Nov 28 01:58:59 2018

@author: Rishav
"""

import argparse
from process import *
import numpy as np
from sklearn.externals import joblib
import os
def predict(filename,use_mid=False):
    root="Predict"
    filename=root+os.sep+filename
    
    Decoder=joblib.load(".\\Int_to_Word_Dictionary.pkl")
    New_Sample=Sample.load_from_file(filename)
    new_accx=np.array(New_Sample.accx)
    new_accy=np.array(New_Sample.accy)
    new_accz=np.array(New_Sample.accz)
    new_gyx=np.array(New_Sample.gyx)
    new_gyy=np.array(New_Sample.gyy)
    new_gyz=np.array(New_Sample.gyz)
    ConcatSample=np.concatenate((new_accx,new_accy,new_accz,new_gyx,new_gyy,new_gyz),axis=0)
    
    if use_mid:
        ConcatSample=np.concatenate((new_accx[2,:],new_accy[2,:],new_accz[2,:],new_gyx[2,:],new_gyy[2,:],new_gyz[2,:]),axis=0).reshape(1,300)    
        print(ConcatSample.shape)
        Model=joblib.load("MODEL_MID.pkl")
    else:
        Model=joblib.load(".\\MODEL.pkl")

    Test=ConcatSample.reshape(1,ConcatSample.shape[0]*ConcatSample.shape[1])
    #print(Test.shape)
    word=Decoder[Model.predict(Test)[0]]
    
    return word

if __name__=="__main__":
    ap=argparse.ArgumentParser()
    ap.add_argument("-f","--filename",required=True,help="File to be predicted")
    args=vars(ap.parse_args())
    pred=predict(args["filename"])
    print(pred)
    