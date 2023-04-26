#if defined(__APPLE__)
  #define GL_SILENCE_DEPRECATION 
  #include <GLUT/glut.h>
#else
  #include <GL/glut.h>
#endif
#include <math.h>
#include <stdlib.h>
#include <stdio.h>

#define CTE_ROTACION 1

GLint ancho=800;
GLint alto=800;

int hazPerspectiva = 0;

GLfloat anguloCubo[2][3] = {{0.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f}};

//Reescalado
void reshape(int width, int height){
  glViewport(0, 0, width, height);
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
    
  if(hazPerspectiva)
    gluPerspective(60.0f, (GLfloat)width/(GLfloat)height, 1.0f, 20.0f);
  else       
    glOrtho(-4,4, -4, 4, 1, 20);

  glMatrixMode(GL_MODELVIEW);
 
  ancho = width;
  alto = height;
}

//Rotación
void rotate(GLfloat anguloCubo[]){
  //Rotacion Z
  glRotatef(anguloCubo[2], 0.0f, 0.0f, 1.0f);

  //Rotacion Y
  glRotatef(anguloCubo[1], 0.0f, 1.0f, 0.0f);

  //Rotacion X
  glRotatef(anguloCubo[0], 1.0f, 0.0f, 0.0f);
}

//Printea Esfera Blanca
void print_sphere(){
  glColor3f(1.0f, 1.0f, 1.0f);
  glutWireSphere(0.5,8,8); 
}

//Printea Cubo Rojo Rotado y transaladado una -1 unidad en el eje Y
void print_cube(GLfloat anguloCubo[]){
  glColor3f(1.0f, .0f, .0f);
  rotate(anguloCubo); //rotamos cubo
  glTranslatef(0.0f, -1.0f, 0.0f);
  glutWireCube(1); 
}

//Impresión de elementos
void display(){ 
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  glLoadIdentity();

  //Draw Sphere 1 (estática)
  glTranslatef(0.0f, 0.0f, -10.0f);
  print_sphere();
 
  /*
    Jugamos con no limpiar la matriz de transformaciones para que se apliquen las mismas a la 2º esfera.
  */

  //Draw Cube 1
  print_cube(anguloCubo[0]);
    
  //Draw Sphere 2 (Dinámica)
  glTranslatef(0.0f, -1.0f, 0.0f);
  print_sphere(); 

  //Draw Cube 2    
  print_cube(anguloCubo[1]); 

  glFlush();
  glutSwapBuffers();

}

//Inicialicación
void init(){
  glClearColor(0,0,0,0);
  glEnable(GL_DEPTH_TEST);
  ancho = 400;
  alto = 400;
}
 
void keyboardHandler(unsigned char key, int x, int y ){
  GLfloat * cubo = anguloCubo[0];
  switch(key){

    //Ángulo

    //X
    case 'Q':
      cubo=anguloCubo[1];
    case 'q':
      cubo[0]=cubo[0]+CTE_ROTACION;
      break;
    
    case 'A':
      cubo=anguloCubo[1];
    case 'a':
      cubo[0]=cubo[0]-CTE_ROTACION;
      break;
    
    //Y
    case 'W':
      cubo=anguloCubo[1];
    case 'w':
      cubo[1]=cubo[1]+CTE_ROTACION;
      break;
    
    case 'S':  
      cubo=anguloCubo[1]; 
    case 's':      
      cubo[1]=cubo[1]-CTE_ROTACION;
      break; 

    //Z   
    case 'E':
      cubo=anguloCubo[1];
    case 'e':
      cubo[2]=cubo[2]+CTE_ROTACION;
      break;
    
    case 'D': 
      cubo=anguloCubo[1];
    case 'd':   
      cubo[2]=cubo[2]-CTE_ROTACION;
      break; 

    //Perspectiva 
    case 'p':
    case 'P':
      if (hazPerspectiva==1)
        hazPerspectiva=0;
      else
        hazPerspectiva=1;
      reshape(ancho,alto);
      break;

    default:
      break; 
  }

  glutPostRedisplay(); //Actualizamos pantalla
}


int main(int argc, char **argv){
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB| GLUT_DEPTH);
    glutInitWindowPosition(100, 100);
    glutInitWindowSize(ancho, alto);
    glutCreateWindow("Ejercicio");
    init();
    glutDisplayFunc(display);
    glutReshapeFunc(reshape);
    //glutIdleFunc(idle); destacar que repintamos todo cuando handleamos una acción del usuario mediante el teclado (nos olvidamos de Idle)
    glutKeyboardFunc(keyboardHandler);
    glutMainLoop();
    return 0;
}