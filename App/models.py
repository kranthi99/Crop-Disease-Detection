from django.db import models

# Create your models here.

class Medication(models.Model):
	plantName = models.CharField(max_length=100)
	diseaseName = models.CharField(max_length=100)
	diseaseNameReadable = models.CharField(max_length=100)
	irrigation = models.TextField()

	def __str__(self):
		return self.diseaseName
		
class Treatment(models.Model):
	medication = models.ForeignKey(Medication,on_delete=models.CASCADE)
	treatment_text = models.TextField()

class Symptom(models.Model):
	medication = models.ForeignKey(Medication,on_delete=models.CASCADE)
	symptom_text = models.TextField()

class Resistantvarient(models.Model):
	medication = models.ForeignKey(Medication,on_delete=models.CASCADE)
	resistantVarient_name = models.CharField(max_length=100) 

class Causativeorganism(models.Model):
	medication = models.ForeignKey(Medication,on_delete=models.CASCADE)
	causativeOrganism_name = models.CharField(max_length=100)