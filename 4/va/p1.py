import numpy as np
import cv2 as cv
from matplotlib import pyplot as plt
import math

def read(dir):
    img = cv.imread(dir,cv.IMREAD_GRAYSCALE)
    return np.array([np.float32(x/255) for x in img])
        
def save(img,out='salida.png'):
    cv.imwrite(out,img*255)


def print_histogram(img,range=[0,1]):
    plt.hist(img.ravel(),bins=256,range=range)
    plt.show()

def print_double_histogram(img1,img2,range=[0,1]):
    plt.figure(1)
    plt.hist(img1.ravel(),bins=256,range=range)
    plt.figure(2)
    plt.hist(img2.ravel(),bins=256,range=range)
    plt.show()

""" (Ejemplo)
inImage=p1.read("gatito2.png")
img2=p1.adjustIntensity(inImage)
p1.print_double_histogram(inImage,img2)
"""
def adjustIntensity(inImage, inRange=[], outRange=[0, 1]):
    if inRange==[]:
        inRange=[inImage.min(), inImage.max()]
    f = lambda x: outRange[0] + (((outRange[1]-outRange[0])*(x-inRange[0]))/(inRange[1]-inRange[0]))
    return np.array([f(x) for x in inImage])
        
    
# Precondición: img en rango [0,1]
# Realmente no haría falta implementarla porque en el enunciado pone 
# "También está permitido usar cualquier función no relacionada con el procesado de imagen (estadística)"
def histogram(img,bins=256):
    hist=np.zeros(bins)
    g = lambda x: bins-1 if x==1 else math.floor(x/(1/(bins)))
    for x in img.ravel():
        pos=g(x)
        hist[pos]=hist[pos]+1
    return hist

""" (Ejemplo)
inImage=p1.read("prueba/eq.png")
img2=p1.equalizeIntensity(inImage)
p1.print_double_histogram(inImage,img2)
"""
def equalizeIntensity(inImage, nBins=256):
    hst     = histogram(inImage,bins=nBins) #calculating histogram
    f_trans = (np.add.accumulate(hst) / (inImage.shape[0]*inImage.shape[1])) 
    #obtenemos índice
    g = lambda x: nBins-1 if  x==1 else math.floor(x/(1/(nBins)))
    #obtenemos valor en el índice
    f = lambda x: f_trans[g(x)]
    #Computamos resultado
    result = np.zeros([inImage.shape[0],inImage.shape[1]])
    for y in range(inImage.shape[0]):
        for x in range(inImage.shape[1]):
            result[y][x] = f(inImage[y][x])
    return result

"""
He decidido que si el kernel sobresale compute únicamente las posiciones que se encuentran dentro de la imagen. 
Esto puede provocar que se oscurezcan los bordes.

(Ejemplo)
inImage=p1.read("gatito2.png")
kernel=np.array([[0, 0, 0],[0, 1, 0],[0, 0, 0]])
img2=p1.filterImage(inImage, kernel)
(inImage==img2).all()
"""

def filterImage(inImage, kernel):
    km, kn = kernel.shape
    imm, imn = inImage.shape
    kcy, kcx     = math.floor(km/2),math.floor(kn/2) #centro kernel
    #Computamos resultado
    result = np.zeros([imm,imn])
    for y in range(imm):
        for x in range(imn):
            for yk in range(km):
                for xk in range(kn):
                    #comprobamos valores válidos
                    imgx=x-kcx+xk
                    imgy=y-kcy+yk
                    if ((imgx>=0 and imgx<imn) and (imgy>=0 and imgy<imm)):
                        result[y][x] = result[y][x] + (kernel[yk][xk]*inImage[imgy][imgx])
            
    return result


def gaussKernel1D(sigma):
    #Size(2⌈3σ⌉+1)
    n = 2*(math.ceil(3*sigma))+1 
    #Center
    c = math.floor(n/2)
    f = lambda x: (1/(math.sqrt(2*math.pi)*sigma))*math.pow(math.e,-(math.pow(x,2)/(2*math.pow(sigma,2))))
    result = np.array(list(range(n)))-c
    return np.array([[f(x) for x in result]])


"""(Ejemplo)
inImage=p1.read("prueba/deltas.png")
img2=p1.gaussianFilter(inImage, 1)
p1.save(img2)
"""
def gaussianFilter(inImage, sigma):
    k=gaussKernel1D(sigma)
    img=filterImage(inImage, k)
    img=filterImage(img, k.T)
    return img


"""(Ejemplo)
inImage=p1.read("gatito2.png")
img2=p1.medianFilter(inImage, 5)
p1.save(img2)
"""
def medianFilter(inImage, filterSize):
    k=np.ones([filterSize,filterSize])/(filterSize*filterSize)   
    img=filterImage(inImage, k)
    return img

"""
a=1 -> laplaciano
a<1 -> img invertida (se le  anade la suavizada a la negativa)
a>1 -> img realzada, cuanto más grande sea a menos se ensalzará (mas info se retiene)

(Ejemplo)
inImage=p1.read("gatito2.png")
img2=p1.highBoost(inImage, 2,"median",3)
p1.save(img2)
"""
def highBoost(inImage, a, method, param):
    if method=="gaussian":
        suavizada=gaussianFilter(inImage, param)
    elif method=="median":
        suavizada=medianFilter(inImage, param)
    else:
        print("Amigo, debes introducir un método válido...")
        return
    return adjustIntensity((inImage*a)-suavizada) #Af−fsmooth

        
#################################################################################################################
#    Trataremos las imágenes binarias como fondo=0 y no_fondo=1. Para evitar el aliasing le haremos            #
#     un round, de forma que todo lo que no sea completamente blanco(=1) pasará a ser negro.                    #
#################################################################################################################


"""
A la hora de encontrarnos con bordes he decidio implementar la opción menos destructiva, estos no se tratarán ni como objeto ni como no objeto,  
el valor de la erosión en un pixel cuyo EE desborde dependerá únicamente de los elementos del EE que no desborden. (bordes ignorados)

(Ejemplo)
inImage=p1.read("prueba/morph.png")
ee=np.ones([3,3])
img2=p1.erode(inImage, ee)
p1.save(img2)
"""
def erode(inImage, se, center=[]):
    p, q = se.shape
    if center==[]:
        center=[math.floor(p/2),math.floor(q/2)]
    imy, imx   = inImage.shape
    secy, secx = center
    img=np.round(inImage) 
    result = np.ones([imy,imx])
    for y in range(imy):
        for x in range(imx):
            flag=False
            for yk in range(p):
                if flag:
                    break
                for xk in range(q):
                    #comprobamos valores válidos
                    posx=x-secx+xk
                    posy=y-secy+yk
                    if ((posx>=0 and posx<imx) and (posy>=0 and posy<imy)): 
                        if (se[yk][xk]==1) and (img[posy][posx]==0):    
                            #si en el Elemento estructurante desplazado encontramos un 0 erosionamos
                            result[y][x]=0
                            flag=True
                            break 
    return result

"""
Tratamiento menos destructivo. (bordes ignorados)

(Ejemplo)
inImage=p1.read("morfo.png")
ee=np.ones([3,3])
img2=p1.dilate(inImage, ee)
p1.save(img2)
"""
def dilate(inImage, se, center=[]):
    p, q = se.shape
    if center==[]:
        center=[math.floor(p/2),math.floor(q/2)]
    imy, imx   = inImage.shape
    secy, secx = center
    img=np.round(inImage) 
    result = np.zeros([imy,imx])
    for y in range(imy):
        for x in range(imx):
            flag=False
            for yk in range(p):
                if flag:
                    break
                for xk in range(q):
                    #comprobamos valores válidos
                    posx=x-secx+xk
                    posy=y-secy+yk
                    if ((posx>=0 and posx<imx) and (posy>=0 and posy<imy)): 
                        if (se[yk][xk]==1) and (img[posy][posx]==1):    
                            #si en el Elemento estructurante desplazado encontramos un 1 dilatamos
                            result[y][x]=1 
                            flag=True
                            break 
                            
    return result


"""(Ejemplo)
inImage=p1.read("morfo.png")
ee=np.ones([3,3])
img2=p1.opening(inImage, ee)
p1.save(img2)
"""
def opening(inImage, se, center=[]):
    img = erode(inImage, se, center=[])
    return dilate(img, se, center=[])

"""(Ejemplo)
inImage=p1.read("closing.png")
ee=np.ones([3,3])
img2=p1.closing(inImage, ee)
p1.save(img2)
"""
def closing(inImage, se, center=[]):
    img = dilate(inImage, se, center=[])
    return erode(img, se, center=[])


"""(Ejemplo)
inImage=p1.read("hitormiss.png")
objSE=np.array([[0,0,0],[1,1,0],[0,1,0]])
bgSE=np.array([[0,1,1],[0,0,1],[0,0,0]])
img2=p1.hit_or_miss(inImage, objSE, bgSE)
p1.save(img2)
"""
def hit_or_miss(inImage, objSE, bgSE, center=[]):  
    inverse = lambda x: 0 if x==1 else 1
    assert (intersec(objSE,bgSE)==0).all(), "Intersection between objSE and bgSE must be 0"
    img=np.round(inImage) 
    hit=erode(img,objSE,center)
    complementaria=np.array([np.array([inverse(x) for x in lx]) for lx in img])
    miss=erode(complementaria,bgSE,center)
    return intersec(hit,miss)

def intersec(m1,m2):
    assert m1.shape==m2.shape, "Size of objSE and bgSE must be the same"
    result=np.zeros(m1.shape)
    for y in range(m1.shape[0]):
        for x in range(m1.shape[1]):
            if m1[y,x]==m2[y,x]==1:
                result[y,x]=1
    return result


"""(Ejemplo)
inImage=p1.read("chicuela.png")
g=p1.gradientImage(inImage, "Sobel")
p1.save(p1.adjustIntensity(np.abs(g[0])+np.abs(g[1])))
"""
def gradientImage(inImage, operator):
    k_x=[]
    k_y=[]
    if operator=="Roberts":
        k_x=np.array([[-1,0],[0,1]])
        k_y=np.array([[0,-1],[1,0]])
    elif operator=="CentralDiff": #no se si estará bien
        k_x=np.array([[-1,0,1]])
        k_y=np.array([[-1],[0],[1]])
    elif operator=="Prewitt":
        k_x=np.array([[-1,0,1],[-1,0,1],[-1,0,1]])
        k_y=np.array([[-1,-1,-1],[0,0,0],[1,1,1]])
    elif operator=="Sobel":
        k_x=np.array([[-1,0,1],[-2,0,2],[-1,0,1]])
        k_y=np.array([[-1,-2,-1],[0,0,0],[1,2,1]])
    else:
        print("Pareses Parvo todo mal todo mal")
        return 
    return [filterImage(inImage,k_x), filterImage(inImage,k_y)]

"""(Ejemplo) -> ojo hacemos una normalización y los bordes son muy blancos por lo que tendremos que poner valores bajos en tlow/high
inImage=p1.read("nino.png")
img2=p1.edgeCanny(inImage,.7,.05,.1)
p1.save(p1.adjustIntensity(img2))
"""
def edgeCanny(inImage, sigma, tlow, thigh):
    img=gaussianFilter(inImage,sigma) #duda -> en las traspas como que ponen algo raro, mismos indices y un *
    [jx,jy] = gradientImage(img,"Sobel") 
    #Módulo
    mod=np.zeros(jx.shape)
    for y in range(jx.shape[0]):
        for x in range(jx.shape[1]):
            mod[y,x] = math.sqrt( (jx[y,x]**2) + (jy[y,x]**2) )
    #Ángulo
    ang=np.zeros(jx.shape)
    for y in range(jx.shape[0]):
        for x in range(jx.shape[1]):
            #que pasa si jx[y,x] es 0
            if (jx[y,x]==0):
                if (jy[y,x]>0): 
                    ang[y,x] = math.pi/2
                else:
                    ang[y,x] = -math.pi/2
            else:
                ang[y,x] = math.atan(jy[y,x]/jx[y,x])
    
    ang_=ang % math.pi #nos aseguramos de que esté acotado
    #Supresión No máxima
    s=supresion_no_max(mod, ang_)
    #Umbralización con histéresis (primero acotamos entre 0 y 1)
    s_norm=adjustIntensity(s)
    return umb_con_hist(s_norm,ang_,tlow,thigh)


def supresion_no_max(mod, ang):
    img_snm=np.zeros(mod.shape)
    for y in range(ang.shape[0]):
        for x in range(ang.shape[1]):
            if 0<=ang[y,x]<(math.pi/8) or ((math.pi*7)/8)<=ang[y,x]<=math.pi: #Horizontal
                v1=y,x-1
                v2=y,x+1
            elif (math.pi/8)<ang[y,x]<=((math.pi*3)/8):                       #Diagonal 1
                v1=y+1,x+1
                v2=y-1,x-1
            elif ((math.pi*3)/8)<ang[y,x]<=((math.pi*5)/8):                   #Vertical
                v1=y-1,x
                v2=y+1,x
            elif ((math.pi*5)/8)<ang[y,x]<((math.pi*7)/8):                    #Diagonal 2
                v1=y+1,x-1
                v2=y-1,x+1
            else:
                print("Error!")
            #Seteamos vecinos
            if ((v1[0]>=0 and v1[0]<mod.shape[0]) and (v1[1]>=0 and v1[1]<mod.shape[1])):
                vecino1=mod[v1]
            else:
                vecino1=-math.inf
            if ((v2[0]>=0 and v2[0]<mod.shape[0]) and (v2[1]>=0 and v2[1]<mod.shape[1])):
                vecino2=mod[v2]
            else:
                vecino2=-math.inf
            #Aplicamos la Supresion
            if (mod[y,x]<vecino1) or (mod[y,x]<vecino2):
                img_snm[y,x]=0
            else:
                img_snm[y,x]=mod[y,x]
    return img_snm

def umb_con_hist(s,ang,tlow, thigh):
    r=np.zeros(s.shape)
    for y in range(s.shape[0]):
        for x in range(s.shape[1]):
            #Establecemos a 1 el pixel
            if s[y,x]>thigh:
                r[y,x]=1    
                #Calculamos Direcciones perpendiculares a la normal
                if 0<=ang[y,x]<(math.pi/8) or ((math.pi*7)/8)<=ang[y,x]<=math.pi: #Horizontal
                    d=1,0
                elif (math.pi/8)<ang[y,x]<=((math.pi*3)/8):                       #Diagonal 1
                    d=-1,1
                elif ((math.pi*3)/8)<ang[y,x]<=((math.pi*5)/8):                   #Vertical
                    d=0,1
                elif ((math.pi*5)/8)<ang[y,x]<((math.pi*7)/8):                    #Diagonal 2
                    d=1,1
                else:
                    print("Error!")
                    print(ang[y,x])
                #Miramos las direcciones
                #positiva
                for i in [-1,1]:
                    pos=(d[0]*i+y),d[1]*i+x
                    while pos[0]>=0 and pos[0]<s.shape[0] and pos[1]>=0 and pos[1]<s.shape[1]:
                        if s[pos]<=tlow:
                            break
                        r[pos]=1
                        i=i+1
                        pos=(d[0]*i+y),(d[1]*i+x)
    return r
            

"""(Ejemplo)
inImage=p1.read("susan.png")
[outCorners,usanArea]=p1.cornerSusan(inImage,3,.3)
p1.save(p1.adjustIntensity(outCorners),out="outCorners.png")
p1.save(p1.adjustIntensity(usanArea),out="usanArea.png")
"""
def create_susan_mask(r):
    return cv.circle(np.zeros([r*2+1,r*2+1]), (r,r), r, 1, -1)

def cornerSusan(inImage, r, t):
    g=3/4
    outCorners=np.zeros(inImage.shape)
    usanArea=np.zeros(inImage.shape)
    mask=create_susan_mask(r)
    mask_area=np.sum(mask)
    c = lambda x,y: 1 if abs(x-y)<=t else 0 #contamos los iguales
    for y in range(inImage.shape[0]):
        for x in range(inImage.shape[1]):
            count=0
            for yk in range(mask.shape[0]):
                for xk in range(mask.shape[1]):
                    #comprobamos valores válidos
                    imgx=x-r+xk
                    imgy=y-r+yk
                    #los bordes siempre tienen pocos elementos iguales -> que lo dejo asi?
                    if ((imgx>=0 and imgx<inImage.shape[1]) and (imgy>=0 and imgy<inImage.shape[0]) and mask[yk,xk]==1):
                        count=count+c(inImage[y,x],inImage[imgy,imgx])
            usanArea[y,x]=count
            if (count/mask_area)<(g): #moves este param al gusto
                outCorners[y,x]=(g)-(count/mask_area)
            else:
                outCorners[y,x]=0
    return [outCorners,usanArea]