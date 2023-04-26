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
  Movimiento de la atracción:
    -Teclado: 1/2 sobre eje X | 3/4 sobre eje Y
    -Ratón: Pinchar y arrastrar sobre eje X | idem + SHIFT sobre eje Y

  Cambio Perspectiva:
    Presionando P/O swicheamos entre perspectiva ortogonal

  Cambio ángulo giro (R)

  Stop de la atracción (S) -> destacar que se ubica el brazo en la base para que los pasajero puedan bajar

*/

//---------//Variables//-----------//
GLint ancho=800;
GLint alto=800;
int hazPerspectiva = 0;
int cte_rotacion = 5;

GLfloat anguloFigura[3] = {0.0f, 0.0f, 0.0f};
GLfloat anguloBrazo = 0.0f;
GLfloat colorGente[3] = {0.5f, 0.5f, 0.5f};
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

void drawSlice(float base, float height, float volume) {
    glBegin(GL_TRIANGLES);

    glColor3f(0.35f, 0.35f, 1.0f); // Cara delantera 
    glVertex3f(-0.5 * base, 0.0f, 0.5 * volume);
    glVertex3f(0.0f, height, 0.5 * volume);
    glVertex3f(0.5 * base, 0.0f, 0.5 * volume);

    glColor3f(0.35f, 0.35f, 1.0f); // Cara trasera
    glVertex3f(-0.5 * base, 0.0f, -0.5 * volume);
    glVertex3f(0.0f, height, -0.5 * volume);
    glVertex3f(0.5 * base, 0.0f, -0.5 * volume);

    glEnd();

    glBegin(GL_QUADS);

    glColor3f(0.25f, 0.25f, 1.0f); // Cara izquierda
    glVertex3f(-0.5 * base, 0.0f, 0.5 * volume);
    glVertex3f(0.0f, height, 0.5 * volume);
    glVertex3f(0.0f, height, -0.5 * volume);
    glVertex3f(-0.5 * base, 0.0f, -0.5 * volume);

    glColor3f(0.25f, 0.25f, 1.0f); // Cara derecha
    glVertex3f( 0.5 * base, 0.0f, 0.5 * volume);
    glVertex3f( 0.0, height, 0.5 * volume);
    glVertex3f( 0.0, height, -0.5 * volume);
    glVertex3f( 0.5 * base, 0.0f, -0.5 * volume);

    glColor3f(0.25f, 0.25f, 1.0f); // Cara inferior
    glVertex3f(-0.5 * base, 0.0f,  0.5 * volume);
    glVertex3f( 0.5 * base, 0.0f,  0.5 * volume);
    glVertex3f( 0.5 * base, 0.0f, -0.5 * volume);
    glVertex3f(-0.5 * base, 0.0f, -0.5 * volume);

    glEnd();
}

void drawBrazo(){
  
  //Brazo
  glColor3f(0.7f, 0.1f, 0.7f);
  glPushMatrix();
  glTranslatef(0.,3.,.75);
  glScalef(1.,8.,1.);
  glutSolidCube(.5);
  glPopMatrix();

  //Mano
  glColor3f(0.7f, 0.1f, 0.2f);
  glPushMatrix(); //-
  glTranslatef(0.,1.,2.);
  glRotatef(-anguloBrazo,0,0,1);

  glPushMatrix();
  glScalef(1.,.25,1.);
  glutSolidCube(2);
  glPopMatrix();

  //Personas
  glColor3f(colorGente[0], colorGente[1], colorGente[2]);

  glPushMatrix();
  glTranslatef(-0.5,0.5,0.5);
  glutSolidCube(.5);
  glPopMatrix();

  glPushMatrix();
  glTranslatef(-0.5,0.5,-0.5);
  glutSolidCube(.5);
  glPopMatrix();

  glPushMatrix();
  glTranslatef(0.5,0.5,0.5);
  glutSolidCube(.5);
  glPopMatrix();

  glPushMatrix();
  glTranslatef(0.5,0.5,-0.5);
  glutSolidCube(.5);
  glPopMatrix();

  
  glPopMatrix(); //-
}


//Impresión de elementos
void display(){ 
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  glLoadIdentity();

  //Profundidad y angulo de toda la figura
  glTranslatef(0.,0.,PROFUNDIDAD);
  rotate(anguloFigura);

  //Base
  drawCilinder(5.,1.);
  drawSlice(9,5,1);

  //Brazo
  glPushMatrix();
  //Rotacion Brazo
  glTranslatef(0,5,0);
  glRotatef(anguloBrazo,0,0,1);
  glTranslatef(0,-5,0);
  drawBrazo();
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
  
    case 'S':
    case 's':
      if (mov_auto==1){
        mov_auto=0;
        anguloBrazo=0;
      } else
        mov_auto=1;    
      break;
    
    //Perspectiva 
    case 'p':
    case 'P':
    case 'o':
    case 'O':
      if (hazPerspectiva==1)
        hazPerspectiva=0;
      else
        hazPerspectiva=1;
      reshape(ancho,alto);
      break;
    
    //Invertir 
    case 'r':
    case 'R':
      cte_rotacion=-cte_rotacion;
      break;

    //Mover 
    //X
    case '1':
      anguloFigura[0]=anguloFigura[0]+cte_rotacion;
      break;
    
    case '2':
      anguloFigura[0]=anguloFigura[0]-cte_rotacion;
      break;
    
    //Y
    case '3':
      anguloFigura[1]=anguloFigura[1]+cte_rotacion;
      break;
    
    case '4':      
      anguloFigura[1]=anguloFigura[1]-cte_rotacion;
      break; 

    default:
      break; 
  }
}

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
    float rotacion = cte_rotacion;
    if (fabs(anguloBrazo)>180){
      rotacion=2*rotacion;
    }
    anguloBrazo=fmod((anguloBrazo+rotacion),360);
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
      colorGente[0]=0.1;
      colorGente[1]=0.1;
      colorGente[2]=0.1;
      break;

    case 1:
      colorGente[0]=0.4;
      colorGente[1]=0.1;
      colorGente[2]=0.4;
      break;

    case 2:
      colorGente[0]=0.6;
      colorGente[1]=0.2;
      colorGente[2]=0.1;
      break;
    
    case 3:
      colorGente[0]=0.1;
      colorGente[1]=0.9;
      colorGente[2]=0.1;
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