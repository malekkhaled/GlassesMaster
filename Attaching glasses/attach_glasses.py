import dlib
import cv2
from urllib.parse import urlparse

from PIL import Image, ImageDraw
from azure.cognitiveservices.vision.face import FaceClient
from msrest.authentication import CognitiveServicesCredentials
from azure.cognitiveservices.vision.face.models import TrainingStatusType, Person, QualityForRecognition
import numpy as np



# This key will serve all examples in this document.
KEY = "aa078d0723c24d7eb5310421bc1e5a2e"

# This endpoint will be used in all examples in this quickstart.
ENDPOINT = "https://usf-capstone-face-info.cognitiveservices.azure.com/"


im = open(r'C:\Users\malek\Desktop\GlassesMaster\Attaching glasses/face-3019.png', 'rb')

# Create an authenticated FaceClient.
#detection_model='detection_01',
face_client = FaceClient(ENDPOINT, CognitiveServicesCredentials(KEY))
detected_faces = face_client.face.detect_with_stream(image=im,  recognition_model='recognition_04', return_face_attributes=['age','gender','glasses','qualityForRecognition'], return_face_landmarks=True)

print(vars(detected_faces[0].face_landmarks).keys())
print("\n\n\n\n")

print(detected_faces[0].face_attributes)
print("\n\n\n\n")

print(detected_faces[0].face_landmarks)

glasses = cv2.imread("My_project-5.png", -1)
