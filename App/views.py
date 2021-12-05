from django.shortcuts import render,get_object_or_404
from django.http import HttpResponse
from django.http import JsonResponse
from django.core.files.storage import FileSystemStorage
from django.views.decorators.csrf import csrf_exempt
from django.core.files.base import ContentFile
from django.contrib.staticfiles.storage import staticfiles_storage
from .models import Medication,Treatment,Symptom,Resistantvarient,Causativeorganism
import base64
import pickle
import random
import numpy as np 
import logging
import cv2
from keras.preprocessing.image import img_to_array
from keras.models import load_model 
from tensorflow.keras.models import model_from_json
from tensorflow.keras.optimizers import Adam
from sklearn.preprocessing import LabelBinarizer 
# Create your views here.



@csrf_exempt
def index(request):
	if(request.method=='POST'):
		image = request.POST.get("image","Nope")
		data = ContentFile(base64.b64decode(image))
		fs = FileSystemStorage();
		random_num = random.randint(0,10000000000)
		file_name = "testimage_"+str(random_num)+".jpg"
		fs.save(file_name,data)

		# with open("media/testimage_3335971255.jpg","rb") as rd:
		# 	ret = base64.b64encode(rd.read())

		image_read = cv2.imread("media/"+file_name)
		image_resized = cv2.resize(image_read,(256,256))
		image_array = img_to_array(image_resized)
		image_array_np = np.array(image_array,dtype=np.float16)/255.0
		image_array_np = np.expand_dims(image_array_np,axis=0)

		with open("C:/Users/mahes/Downloads/model.json","r") as rdjs:
			loaded_model_json = rdjs.read()

		loaded_model = model_from_json(loaded_model_json)

		loaded_model.load_weights("C:/Users/mahes/Downloads/first_try.h5")

		# with open("C:/Users/mahes/Downloads/plant_disease_detection.pkl","rb") as rdpkl:
		# 	model = pickle.load(rdpkl)
		# model = load_weights("C:/Users/mahes/Downloads/first_try.h5")		
		
		INIT_LR=0.001
		EPOCHS=10
		opt = Adam(lr=INIT_LR,decay=INIT_LR/EPOCHS)
		loaded_model.compile(loss="binary_crossentropy",optimizer=opt,metrics=["accuracy"])

		op = loaded_model.predict(image_array_np)
		sorting  = (-op).argsort()

		with open("C:/Users/mahes/Downloads/label_transform.pkl","rb") as rd_pkl:
			dct = pickle.load(rd_pkl) 

		your_classes  = dct.classes_
		sorted_ = sorting[0][:1]
		chk = " ".join([str(i) for i in op])
		for value in sorted_:
			disease = your_classes[value]
		# with fs.open(file_name,"r") as rd:
		# 	rd = cv2.resize(rd,(256,256))
		# 	image_array = img_to_array([rd])
		# 	image_array = np.array(image_array,dtype=np.float16)/255.0
		# 	image_array = np.expand_dims(image_array,axis=0)
		# 	op = model.predict(image_array)
		# ret = str(ret)

		precautions = get_object_or_404(Medication,diseaseName="Apple__Apple_scab")
		precautions_json = {}
		precautions_json["plantName"] = precautions.plantName
		precautions_json["diseaseNameReadable"]=precautions.diseaseNameReadable
		precautions_json["irrigation"]=precautions.irrigation

		precautions_json["treatment_list"] = []
		for ir in precautions.treatment_set.all():
			precautions_json["treatment_list"].append(ir.treatment_text)

		precautions_json["symptoms_list"] = [] 
		for sy in precautions.symptom_set.all():
			precautions_json["symptoms_list"].append(sy.symptom_text)

		precautions_json["resistantVarient_list"]= []
		for rvl in precautions.resistantvarient_set.all():
			precautions_json["resistantVarient_list"].append(rvl.resistantVarient_name)

		precautions_json["causativeorganism_list"]=[]
		for col in precautions.causativeorganism_set.all():
			precautions_json["causativeorganism_list"].append(col.causativeOrganism_name)

		response = JsonResponse(precautions_json)

	return response
