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

/*
  -Acabar ejs
  -Revisar construcción silice
*/

//---------//Variables//-----------//
GLint ancho=800;
GLint alto=800;
int hazPerspectiva = 0;
int cte_rotacion = 5;

GLfloat anguloFigura[3] = {0.0f, 0.0f, 0.0f};
GLfloat angulobaseY = 0.0f;
GLfloat anguloCabinaV = 0.0f;
GLfloat anguloCabinaH = 0.0f;
int shift=0;
int mov_auto = 1;
int subiendo = 1;
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


// Dibuja un circulo
void drawCircle(float radio) {
    float alfa;
    glBegin(GL_POLYGON);

    for (int i = 0; i < 360; i++) {
        alfa = i * 3.1416 / 180;
        glVertex3f(radio * cos(alfa), 0.0f, radio * sin(alfa));
    }
    glEnd();
}

void drawCilinder(float base, float top){
  //Cilindro
  glPushMatrix();
  glRotatef(90.,1.,0.,0.);
  glColor3f(0.2f, 0.2f, 0.2f);
  GLUquadric* qobj = gluNewQuadric();
  gluCylinder(qobj, base, base, top, 32, 32);
  glPopMatrix();

  //Tapas
  glColor3f(0.7f, 0.7f, 0.7f);
  drawCircle(base);
  glPushMatrix();
  glTranslatef(0.,-top,0.);
  drawCircle(base);
  glPopMatrix();

}

void drawCone(float base, float top){
  glColor3f(1.,.5,.5);
  glPushMatrix();
  glRotatef(-90,1,0,0);
  glutSolidCone(base,top,16,16);
  glPopMatrix();
}

void drawArm(){
  //Brazo
  glPushMatrix();
  glColor3f(.1,.7,.1);

  //Rotación vertical (bajamos la figura al 0,0 la rotamos y l subimos)
  glTranslatef(0,4.25,0);
  glRotatef(anguloCabinaV,0,0,1);
  glTranslatef(0,-4.25,0);
  
  glPushMatrix();
  glTranslatef(2,4.25,0);
  glScalef(4,.5,.5);
  glutSolidCube(1);
  glPopMatrix();

  //Plataforma
  glColor3f(.8,.7,.8);
  glPushMatrix();//
  glTranslatef(4,4.75,0);
  glRotatef(-anguloCabinaV,0,0,1);
  glRotatef(anguloCabinaH,0,1,0);

  glRotatef(90,1,0,0);
  glutSolidCone(1,1,16,16);
  glRotatef(-90.,1.,0.,0.);
  drawCircle(1);

  glColor3f(1.,.2,.5);
  
  glPushMatrix();
  glTranslatef(0.5,0.2,0.5);
  glutSolidCube(.4);
  glPopMatrix();

  glPushMatrix();
  glTranslatef(0.5,0.2,-0.5);
  glutSolidCube(.4);
  glPopMatrix();

  glPushMatrix();
  glTranslatef(-0.5,0.2,0.5);
  glutSolidCube(.4);
  glPopMatrix();

  glPushMatrix();
  glTranslatef(-0.5,0.2,-0.5);
  glutSolidCube(.4);
  glPopMatrix();

  glPopMatrix();//
  glPopMatrix();
}

//Impresión de elementos
void display(){ 
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  glLoadIdentity();

  glTranslatef(0.,0.,PROFUNDIDAD);
  rotate(anguloFigura);

  //Base
  drawCilinder(5,1);
  drawCone(4.5,4);

  //Brazo
  glPushMatrix();
  glRotatef(angulobaseY,0,1,0);
  drawArm();
  glPopMatrix();

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
      if (mov_auto==1){
        mov_auto=0;
        anguloCabinaV=-30;
      } else
        mov_auto=1;
      break;

    //Revert 
    case 'r':
    case 'R':
      cte_rotacion=-cte_rotacion;

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
    angulobaseY = angulobaseY+cte_rotacion;//Y
    anguloCabinaH = anguloCabinaH+cte_rotacion;//Y
    //Acotamos los posibles valores
    if (fabs(anguloCabinaV)>30.){
      if (subiendo==1)
        subiendo=0;
      else
        subiendo=1;
    }
    
    if (subiendo==1){
      anguloCabinaV = anguloCabinaV+cte_rotacion;//Y
    } else 
      anguloCabinaV = anguloCabinaV-cte_rotacion;//Y
    
      

    
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

  glutCreateMenu(menu_main);
  glutAddSubMenu("Velocidad",sm_velo);
  glutAddMenuEntry("Salir", 0);
  glutAttachMenu(GLUT_RIGHT_BUTTON);

  glutMainLoop();
  return 0;
}