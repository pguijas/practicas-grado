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
  FUNCIONAMIENTO:
    1º: Selector de extremidad {1;2;3;4}
    2º: Movimiento de extremidad {{q,a},{w,s},{e,d}}
    3º: Movimiento de 2º segmento de la extremidad SHIFT
    4º: Movimiento de Pierna con raton (SHIFT PARA MOVER LA OTRA)

    Perspectiva P
*/


//---------//Variables//-----------//
GLint ancho=800;
GLint alto=800;
int hazPerspectiva = 0;
int solid = 0;
int cte_rotacion = 5;
GLfloat anguloCubos[4][2][3] = {{{0.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f}},
                                {{0.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f}},
                                {{0.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f}},
                                {{0.0f, 0.0f, 0.0f}, {0.0f, 0.0f, 0.0f}}};
int extremidad_actual=0;
int selector_pierna_raton=1;
int mov_auto = 0;
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

//Printea Esfera Blanca
void print_sphere(){
  glColor3f(1.0f, 1.0f, 1.0f);
  if (solid==0)
    glutWireSphere(0.5,16,16); 
  else
    glutSolidSphere(0.5,30,30);
}

//Printea Cubo Rojo Rotado y transaladado una -1 unidad en el eje Y
void print_cube(GLfloat anguloCubo[]){
  glColor3f(1.0f, .0f, .0f);
  rotate(anguloCubo); //rotamos cubo
  glTranslatef(0.0f, -1.5f, 0.0f);
  //Añadimos escalado y luego lo sacamos
  glPushMatrix();
  glScalef(1.0f,2.0f,1.0f);
  if (solid==0)
    glutWireCube(1); 
  else
    glutSolidCube(1); 
  glPopMatrix();
}

//Como vamos a printear muchas articulaciones depende del que invoque esta funcion moverlas
void print_articulation(int art_num){
  //Draw Sphere 1 (estática)
  print_sphere();
  //Jugamos con no limpiar la matriz de transformaciones para que se apliquen las mismas a la 2º esfera.
  
  //Draw Cube 1
  print_cube(anguloCubos[art_num][0]);
    
  //Draw Sphere 2 (Dinámica)
  glTranslatef(0.0f, -1.5f, 0.0f);
  print_sphere(); 

  //Draw Cube 2    
  print_cube(anguloCubos[art_num][1]); 

  glLoadIdentity();
}

void print_cabeza(){
  glColor3f(0.9f, 0.8f, 0.5f);
  //Cuello
  glTranslatef(0.,3.125,PROFUNDIDAD);
  glScalef(1.,0.25,1.);
  if (solid==0)
    glutWireCube(1); 
  else
    glutSolidCube(1); 
    
  glLoadIdentity();
  //Cabeza
  glColor3f(0.95f, 0.85f, 0.6f);
  glTranslatef(0.,4.25,PROFUNDIDAD);
  if (solid==0)
    glutWireCube(2); 
  else
    glutSolidCube(2); 
}

void print_cuerpo(){
  glTranslatef(0.0f, 0.0f, PROFUNDIDAD);
  glScalef(3.,6.,1.);
  glColor3f(0.9f, 0.8f, 0.5f);
  if (solid==0)
    glutWireCube(1); 
  else
    glutSolidCube(1);
  glLoadIdentity();
}

//Impresión de elementos
void display(){ 
  glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  glLoadIdentity();

  //Cabeza
  print_cabeza();
  glLoadIdentity();

  //Cuerpo
  print_cuerpo();

  //Brazo 1
  glTranslatef(-2.f, 2.5f, PROFUNDIDAD);
  print_articulation(0);

  //Brazo 2
  glTranslatef(2.f, 2.5f, PROFUNDIDAD);
  print_articulation(1);

  //Pierna 1
  glTranslatef(-1.f, -3.5f, PROFUNDIDAD);
  print_articulation(2);

  //Pierna 2
  glTranslatef(1.f, -3.5f, PROFUNDIDAD);
  print_articulation(3);

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
  GLfloat * cubo = NULL;
  
  //DESTACAR: en el switch captamos may/min pero realmente lo hago por comodidad 
  //          por si tengo el BLOC MAY activado (contaría como una tecla) 
  if (glutGetModifiers()==GLUT_ACTIVE_SHIFT)
    cubo=anguloCubos[extremidad_actual][1];
  else
    cubo=anguloCubos[extremidad_actual][0];
  

  switch(key){
    //Art Selector
    case '1':
      extremidad_actual=0;
      break;

    case '2':
      extremidad_actual=1;
      break;

    case '3':
      extremidad_actual=2;
      break;

    case '4':
      extremidad_actual=3;
      break;

    //Ángulo

    //X
    case 'Q':
    case 'q':
      cubo[0]=cubo[0]+cte_rotacion;
      break;
    
    case 'A':
    case 'a':
      cubo[0]=cubo[0]-cte_rotacion;
      break;
    
    //Y
    case 'W':
    case 'w':
      cubo[1]=cubo[1]+cte_rotacion;
      break;
    
    case 'S':  
    case 's':      
      cubo[1]=cubo[1]-cte_rotacion;
      break; 

    //Z   
    case 'E':
    case 'e':
      cubo[2]=cubo[2]+cte_rotacion;
      break;
    
    case 'D': 
    case 'd':   
      cubo[2]=cubo[2]-cte_rotacion;
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
}

void motionHandler(int x, int y){
  //Movemos piernas
  if (selector_pierna_raton==1){
    //Pierna derecha
    anguloCubos[2][0][1]=x;
    anguloCubos[2][0][0]=y;
    } else {
    //Pierna izquierda
    anguloCubos[3][0][1]=x;
    anguloCubos[3][0][0]=y;
  }
}

void mouseHandler(int button, int state, int x, int y){
  if (button==GLUT_LEFT_BUTTON){
    if (glutGetModifiers() == GLUT_ACTIVE_SHIFT) 
      selector_pierna_raton=0;
    else
      selector_pierna_raton=1;

  }
}
//---------//--------//-----------//


//Renderizamos Cada X ms
void renderizaciao(int valor_indiferente){  
  //Rotamos Piernas
  if (mov_auto==1){
    anguloCubos[2][0][1]= anguloCubos[2][0][1]+cte_rotacion;//Y
    anguloCubos[3][0][1]= anguloCubos[3][0][1]+cte_rotacion;//Y
  }
  glutPostRedisplay();
  glutTimerFunc(FREC_REND, renderizaciao,0);
}


//---------//Menus//-----------//
void menu_visual(int id){
  switch (id){
    case 0:
      solid=0;
      break;

    case 1:
      solid=1;
      break;
  }
}

void menu_mov(int id){
  mov_auto=id;
}

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
  glutCreateWindow("Ejercicio");
  init();
  glutDisplayFunc(display);
  glutReshapeFunc(reshape);
  //glutIdleFunc(idle); destacar que repintamos todo cuando handleamos una acción del usuario mediante el teclado (nos olvidamos de Idle)
  glutTimerFunc(FREC_REND, renderizaciao,0); //iniciamos bucle
  glutKeyboardFunc(keyboardHandler);
  glutMotionFunc(motionHandler);
	glutMouseFunc(mouseHandler);
  //Menu
  int sm_visual = glutCreateMenu(menu_visual);
  glutAddMenuEntry("Objetos Alambre", 0);
  glutAddMenuEntry("Objetos Sólidos", 1);
    
  int sm_mov = glutCreateMenu(menu_mov);
  glutAddMenuEntry("Desactivado", 0);
  glutAddMenuEntry("Activado", 1);

  int sm_velo = glutCreateMenu(menu_velocidades);
  glutAddMenuEntry("Rápida", 0);
  glutAddMenuEntry("Media", 1);
  glutAddMenuEntry("Lenta", 2);

  glutCreateMenu(menu_main);
  glutAddSubMenu("Visualización",sm_visual);
  glutAddSubMenu("Piernas Movimiento",sm_mov);
  glutAddSubMenu("Velocidad",sm_velo);
  glutAddMenuEntry("Salir", 0);
  glutAttachMenu(GLUT_RIGHT_BUTTON);
  glutMainLoop();
  return 0;
}