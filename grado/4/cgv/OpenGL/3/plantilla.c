#if defined(__APPLE__)
  #define GL_SILENCE_DEPRECATION 
  #include <GLUT/glut.h>
#else
  #include <GL/glut.h>
#endif
#include <math.h>
#include <stdlib.h>
#include <stdio.h>

#define PROFUNDIDAD -15.f
#define FREC_REND 50


//---------//Variables//-----------//
GLint ancho=800;
GLint alto=800;
int hazPerspectiva = 0;
int cte_rotacion = 5;

GLfloat anguloFigura[3] = {0.0f, 0.0f, 0.0f};
GLfloat color[3] = {0.3f, 0.1f, 0.7f};
int shift=0;
int mov_auto = 1;
//---------//--------//-----------//

//Reescalado
void reshape(int width, int height){
  glViewport(0, 0, width, height);
  glMatrixMode(GL_PROJECTION);
  glLoadIdentity();
    
  if(hazPerspectiva)
    gluPerspective(70.0f, (GLfloat)width/(GLfloat)height, 1.f, 20.0f);
  else       
    glOrtho(-10,10, -10, 10, 1, 20);

  glMatrixMode(GL_MODELVIEW);
 
  ancho = width;
  alto = height;
}

//---------//Dibujado//-----------//

//Rotación
void rotate(GLfloat anguloCubo[]){
  //Rotacion Z
  glRotatef(anguloCubo[2], 0.0f, 0.0f, 1.0f);

  //Rotacion Y
  glRotatef(anguloCubo[1], 0.0f, 1.0f, 0.0f);

  //Rotacion X
  glRotatef(anguloCubo[0], 1.0f, 0.0f, 0.0f);
}

//Impresión de elementos
void display(){ 
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  glLoadIdentity();

  glTranslatef(0.,0.,PROFUNDIDAD);
  rotate(anguloFigura);



  glFlush();
  glutSwapBuffers();

}
//---------//---------//-----------//


//Inicialicación
void init(){
  glClearColor(0,0,0,0);
  glEnable(GL_DEPTH_TEST);
  ancho = 400;
  alto = 400;
}
 
//---------//Handlers//-----------//
void keyboardHandler(unsigned char key, int x, int y ){

  switch(key){
    
    //Perspectiva 
    case 'p':
    case 'P':
      if (hazPerspectiva==1)
        hazPerspectiva=0;
      else
        hazPerspectiva=1;
      reshape(ancho,alto);
      break;

    //Stop
    case 's':
    case 'S':
      if (mov_auto==1)
        mov_auto=0;
      else
        mov_auto=1;
      break;

    default:
      break; 
  }
}

//Cambiar Ángulos Figura facilmente (facilita visión)
void motionHandler(int x, int y){
  if (shift==0)
    anguloFigura[0]=y;
  else
    anguloFigura[1]=x;
}

void mouseHandler(int button, int state, int x, int y){
  if (button==GLUT_LEFT_BUTTON){
    if (glutGetModifiers() == GLUT_ACTIVE_SHIFT) 
      shift=1;
    else
      shift=0;
  }
}

//---------//--------//-----------//


//Renderizamos Cada X ms
void renderizaciao(int valor_indiferente){  
  //Movimiento automático
  if (mov_auto==1){
    /*
    fmod((),360)
    anguloCubos[2][0][1]= anguloCubos[2][0][1]+cte_rotacion;//Y
    anguloCubos[3][0][1]= anguloCubos[3][0][1]+cte_rotacion;//Y
    */
  }
  glutPostRedisplay();
  glutTimerFunc(FREC_REND, renderizaciao,0);
}


//---------//Menus//-----------//

void menu_velocidades(int id){
  switch (id){
    case 0:
      cte_rotacion=10;
      break;

    case 1:
      cte_rotacion=5;
      break;

    case 2:
      cte_rotacion=1;
      break;
  }
}

void menu_color(int id){
  switch (id){
    case 0:
      color[0]=0.3;
      color[1]=0.1;
      color[2]=0.7;
      break;

    case 1:
      color[0]=0.4;
      color[1]=0.1;
      color[2]=0.4;
      break;

    case 2:
      color[0]=0.6;
      color[1]=0.2;
      color[2]=0.1;
      break;
    
    case 3:
      color[0]=0.1;
      color[1]=0.9;
      color[2]=0.1;
      break;
  }
}


void menu_main( int id){
  if (id==0)
    exit(-1);
}

//---------//-----//-----------//



int main(int argc, char **argv){

  glutInit(&argc, argv);
  glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB| GLUT_DEPTH);
  glutInitWindowPosition(100, 100);
  glutInitWindowSize(ancho, alto);
  glutCreateWindow("Examen");
  init();
  glutDisplayFunc(display);
  glutReshapeFunc(reshape);

  glutTimerFunc(FREC_REND, renderizaciao,0); //iniciamos bucle renderizado
  glutKeyboardFunc(keyboardHandler);
  glutMotionFunc(motionHandler);
	glutMouseFunc(mouseHandler);

  //Menu
  int sm_velo = glutCreateMenu(menu_velocidades);
  glutAddMenuEntry("Rápida", 0);
  glutAddMenuEntry("Media", 1);
  glutAddMenuEntry("Lenta", 2);

  int sm_color = glutCreateMenu(menu_color);
  glutAddMenuEntry("Color 1", 0);
  glutAddMenuEntry("Color 2", 1);
  glutAddMenuEntry("Color 3", 2);
  glutAddMenuEntry("Color 4", 3);

  glutCreateMenu(menu_main);
  glutAddSubMenu("Velocidad",sm_velo);
  glutAddSubMenu("Colores",sm_color);
  glutAddMenuEntry("Salir", 0);
  glutAttachMenu(GLUT_RIGHT_BUTTON);

  glutMainLoop();
  return 0;
}