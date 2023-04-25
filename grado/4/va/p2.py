import numpy as np
import cv2
from skimage.filters import frangi
import math

def delete_noise(img,x):
    # Obtenemos componentes conectados
    nlabels, labels, stats, _ = cv2.connectedComponentsWithStats(img, None, None, None, 8, cv2.CV_32S)

    # Extraemos areas
    areas = stats[1:,cv2.CC_STAT_AREA]

    # Construimos resultado con areas superiores a x
    result = np.zeros(img.shape, np.uint8)
    for i in range(nlabels - 1):
        #Si mayor o igual lo mantenemos
        if areas[i] >= x:   
            result[labels == i + 1] = 255
    
    return result

def method1(img):

    ##################
    #  PREPROCESADO  #
    ##################

    img = cv2.resize(img, (0,0), fx=2, fy=2) 

    # Obtenemos Marco  
    ret,marco = cv2.threshold(img, 15,255, cv2.THRESH_BINARY) #erosionamos
    kernel = np.array([[1, 1, 1],[1, 1, 1], [1, 1, 1]],np.uint8)
    marco = cv2.erode(marco,kernel,iterations = 8)
    kernel = np.array([[0, 0, 0],[1, 1, 0], [0, 0, 0]],np.uint8)
    marco = cv2.erode(marco,kernel,iterations = 7)
    #cv2.imwrite('resultados/marco.png', marco)
    
    # Contraste
    clahe = cv2.createCLAHE(clipLimit=5,tileGridSize=(20,20))
    img = clahe.apply(img)

    # Filtro de Medianas
    img = cv2.medianBlur(img, 7)

    cv2.imwrite('resultados/preprocesado.png', img)

    #################
    #  METODOLOGÍA  #
    #################

    #Umbralizacion Adaptativa
    th = cv2.adaptiveThreshold(img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV, 15, 5)

    #Extraemos marco
    out = (th)*(marco//255)

    #Eliminación de Ruido
    out = delete_noise(out,100)
    
    #Reescalamos Resultado
    out = cv2.resize(out, (0,0), fx=0.5, fy=0.5) 
    _, out = cv2.threshold(out, 1, 255, cv2.THRESH_BINARY) #Unimos huecos (aliasing)

    #Cierre para mejorar continuidades en los árboles vasculares.
    kernel = np.array([[0, 1, 0],[1, 1, 1], [0, 1, 0]],np.uint8)
    out = cv2.morphologyEx(out, cv2.MORPH_CLOSE, kernel, iterations=2)

    return out

def method2(img):

    ##################
    #  PREPROCESADO  #
    ##################

    img = cv2.resize(img, (0,0), fx=2, fy=2) 

    # Obtenemos Marco  
    ret,marco = cv2.threshold(img, 15,255, cv2.THRESH_BINARY) #erosionamos
    kernel = np.array([[1, 1, 1],[1, 1, 1], [1, 1, 1]],np.uint8)
    marco = cv2.erode(marco,kernel,iterations = 8)
    kernel = np.array([[0, 0, 0],[1, 1, 0], [0, 0, 0]],np.uint8)
    marco = cv2.erode(marco,kernel,iterations = 8)
    #cv2.imwrite('resultados/marco.png', marco)
   
    #Extracción de quemaduras    
    clahe = cv2.createCLAHE(clipLimit=20,tileGridSize=(25,25))
    c = clahe.apply(img)
    _,quemaduras = cv2.threshold(c, c.max()-120,255, cv2.THRESH_BINARY_INV) 
    #cv2.imwrite('resultados/quemaduras.png', quemaduras)

    # clahe con menos contraste
    clahe = cv2.createCLAHE(clipLimit=1,tileGridSize=(25,25))
    img = clahe.apply(img)

    #Filtro Morfo -> similar a suavizado morfo
    kernel = np.ones((3,3),np.uint8)
    img = cv2.morphologyEx(img, cv2.MORPH_CLOSE, kernel)
    img = cv2.morphologyEx(img, cv2.MORPH_OPEN, kernel)

    # Filtro de Medias
    img = cv2.medianBlur(img,5)
    cv2.imwrite('resultados/preprocesado.png', img)

    #################
    #  METODOLOGÍA  #
    #################

    #Parámetros
    sigmas=range(1, 5, 2)   #Sigmas Para Filtro
    scale_range=None
    scale_step=None
    alpha=1                 #Ajusta el Filtro para plate-like structure
    beta=.5                 #Ajusta el Filtro para blob-like structure (gota)
    gamma=.1                #Ajusta sensibilidad del Filtro -> Cuanto Mas Bajo + Ruido
    black_ridges=True       #Detectar black ridges
    mode='reflect'          #Como tratar valores fuera de bordes
    cval=0                  #Combina con constant 
    
    #Frangi
    f=frangi(img, sigmas=sigmas, scale_range=scale_range, scale_step=scale_step, 
        alpha=alpha, beta=beta, gamma=gamma, black_ridges=black_ridges, mode=mode, cval=cval)
    f=np.array(f)
    #Adaptamos Formato
    f=np.divide(f,(f.max()/255))
    f=f.astype(np.uint8)
    f=cv2.equalizeHist(f)
    f = f*(marco//255)
    #cv2.imwrite("resultados/frangi.png",f)
 
    #Binarizamos
    _,out = cv2.threshold(f,3,255,cv2.THRESH_BINARY) 
    #Sacamos Quemaduras  
    out = out*(quemaduras//(255))
    out = delete_noise(out,400)

    #Reescalamos Resultado
    out = cv2.resize(out, (0,0), fx=0.5, fy=0.5) 
    _, out = cv2.threshold(out, 1, 255, cv2.THRESH_BINARY) #Unimos huecos (aliasing)

    return out


#Confusion matrix for background or not background
def get_matrix(esperado, recibido):
    esperado = np.round(esperado/255)
    recibido = np.round(recibido/255)
    assert esperado.shape == recibido.shape
    # Matrix
    confusion=np.zeros((2,2))
    esperados0s=esperado==0
    confusion[0][0]=np.sum(recibido[esperados0s]==0)
    confusion[0][1]=np.sum(recibido[esperados0s]==1)
    esperados1s=esperado==1
    confusion[1][0]=np.sum(recibido[esperados1s]==0)
    confusion[1][1]=np.sum(recibido[esperados1s]==1)
    
    return confusion
    
def evaluate(m):
    vn=m[0][0]
    fn=m[0][1]
    fp=m[1][0]
    vp=m[1][1]
    
    """
    print("| ", vn, " ", fn, " |")
    print("| ", fp, " ", vp, " |\n")

    e=np.sum(m)
    print("| ", vn/e, " ", fn/e, " |")
    print("| ", fp/e, " ", vp/e, " |\n")
    """
  
    s=(vp)/(vp+fp)
    print("\t s:  ", s ,"%")

    p=(vp)/(vp+fn)
    print("\t p:  ", p ,"% ")

    sim=(1-(math.sqrt((1-p)**2+(1-s)**2)/math.sqrt(2)))
    print("\t sim:  ", sim ,"% ")

    dsc=(2*vp)/(2*vp+fp+fn)
    print("\t dsc:  ", dsc ,"% ")

    return (s,p,sim,dsc)

def run(image,method):
    #Folder Info
    folder="imgs/"
    image=str(image)
    og_image=folder+image+"_training.tif"
    perfect_result=folder+image+"_manual1.png"

    #Reading images
    img = cv2.imread(og_image)
    _, img, _ = cv2.split(img) #green channel
    ground_truth = cv2.imread(perfect_result,cv2.IMREAD_GRAYSCALE)

    if method==1:
        result=method1(img)
    elif method==2:
        result=method2(img)
    else:
        print("Solo existe método 1 y método 2")

    #Guardamos
    cv2.imwrite('resultados/original.png', img)   
    cv2.imwrite('resultados/resultado.png', result)   

    return evaluate(get_matrix(ground_truth,result))

def evaluate_all(method):
    img0=21
    imgn=29
    sensibilidad, precision, similitud, dsc = 0,0,0,0
    for img in range(img0,imgn):
        print(img,": ")
        s, p, sim, d  = run(img,method)
        sensibilidad += s
        precision += p
        similitud += sim
        dsc += d
       
    diff = imgn-img0

    print("\nBondades Totales")
    print("\t s:  ", sensibilidad/diff ,"%")
    print("\t p:  ", precision/diff ,"% ")
    print("\t sim:  ", similitud/diff ,"% ")
    print("\t dsc:  ", dsc/diff ,"% ")


if __name__ == '__main__':
    print("----------------------------------------------------------------")    
    evaluate_all(2) 
    print("----------------------------------------------------------------")    
    #run(22,2)    
    print("----------------------------------------------------------------")    

    