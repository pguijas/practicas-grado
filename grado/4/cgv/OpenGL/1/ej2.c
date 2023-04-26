#if defined(__APPLE__)
  #define GL_SILENCE_DEPRECATION 
  #include <GLUT/glut.h>
#else
  #include <GL/glut.h>
#endif
#include <math.h>
#include <stdlib.h>
#include <stdio.h>

GLint ancho=400;
GLint alto=400;

GLfloat anguloCuboX = 0.0f;
GLfloat anguloCuboY = 0.0f;
GLfloat anguloCuboZ = 0.0f;

/*
  Los prints no se me ven wtf
  cambiar unidad de rotaciones
*/


//Reescalado
void reshape(int width, int height)
{
    glViewport(0, 0, width, height);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    
    glOrtho(-4,4, -4, 4, 1, 10);

    glMatrixMode(GL_MODELVIEW);
 
    ancho = width;
    alto = height;
}

void display()
{ 
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glLoadIdentity();

    //Draw Sphere 1 (estática)
    glTranslatef(0.0f, 0.0f, -5.0f);
    glColor3f(1.0f, 1.0f, 1.0f);
    glutWireSphere(0.5,8,8); //los otros params??
 
    glLoadIdentity();
 
    //Draw Cube
    glTranslatef(0.0f, .0f, -5.0f);
    glColor3f(1.0f, .0f, .0f);

    //Rotacion Z
    glRotatef(anguloCuboZ, 0.0f, 0.0f, 1.0f);

    //Rotacion Y
    glRotatef(anguloCuboY, 0.0f, 1.0f, 0.0f);

    //Rotacion X
    glRotatef(anguloCuboX, 1.0f, 0.0f, 0.0f);
    glTranslatef(0.0f, -1.0f, 0.0f);

    glutWireCube(1); 
    
    /*
      Jugamos con no limpiar la matriz de transformaciones para que se apliquen las mismas a la 2º esfera.
    */
 
    //Draw Sphere 2 (Dinámica)
    glTranslatef(0.0f, -1.0f, 0.0f);
    glColor3f(1.0f, 1.0f, 1.0f);
    glutWireSphere(0.5,8,8); //los otros params??

    glFlush();
    glutSwapBuffers();
 
    //anguloCuboX+=0.1f;
    //anguloCuboY+=0.1f;
    //anguloEsfera+=0.2f;
}

void init()
{
    glClearColor(0,0,0,0);
    glEnable(GL_DEPTH_TEST);
    ancho = 400;
    alto = 400;
}
 
void idle()
{
    display();
}
 
void keyboardHandler(unsigned char key, int x, int y ){
  switch(key){
    //X
    case 'q':
    case 'Q':
      anguloCuboX=anguloCuboX+5;
      break;
    
    case 'a':
    case 'A':
      anguloCuboX=anguloCuboX-5;
      break;
    
    //Y
    case 'w':
    case 'W':
      anguloCuboY=anguloCuboY+5;
      break;
    
    case 's':
    case 'S':        
      anguloCuboY=anguloCuboY-5;
      break; 

    //Z   
    case 'e':
    case 'E':
      anguloCuboZ=anguloCuboZ+5;
      break;
    
    case 'd':
    case 'D':        
      anguloCuboZ=anguloCuboZ-5;
      break; 
    
    default:
      break; 
  }
}


int main(int argc, char **argv){

     
    printf("Arrancamos...");

    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB| GLUT_DEPTH);
    glutInitWindowPosition(100, 100);
    glutInitWindowSize(ancho, alto);
    glutCreateWindow("Cubo");
    init();
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    glutIdleFunc(idle);
    glutKeyboardFunc(keyboardHandler);
    glutMainLoop();
    return 0;
}