from PIL import Image
import numpy as np

#Read the two images
image1 = Image.open('face-3019.png')
image2 = Image.open('prada.jpg')

image1_size = image1.size
image2_size = image2.size

img = Image.open('prada.jpg')
img = img.convert("RGBA")

imgnp = np.array(img)

white = np.sum(imgnp[:,:,:3], axis=2)
white_mask = np.where(white == 255*3, 1, 0)

alpha = np.where(white_mask, 0, imgnp[:,:,-1])

imgnp[:,:,-1] = alpha 

img = Image.fromarray(np.uint8(imgnp))
img.save("img2.png", "PNG")

new_image = Image.new('RGB',(image1_size[0], image1_size[1]), (250,250,250))
new_image.paste(image1,(0,0))
new_image.paste(image2,(0,0))
new_image.save("merged_image.jpg","JPEG")
new_image.show()