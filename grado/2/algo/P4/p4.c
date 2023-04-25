#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <math.h>
#define TAM_MAX 1024 
typedef int ** matriz;

void dijkstra(matriz grafo, matriz distancias, int tam) {
	int n, i, j, v=0;
	int *noVisitados = malloc(tam*sizeof(int));
	for (n=0; n<tam; n++) {
		for (i=0; i<tam; i++) {
			noVisitados[i] = 1; //reseteamos nodos visitados
			distancias[n][i] = grafo[n][i]; //copiamos fila matriz 
		}
		noVisitados[n] = 0;	//nodo del que partimos 1º en visitar
		for (i = 0; i < tam-2; ++i){
			v=0;
			while(v<tam && !(noVisitados[v])) v++;
			for (j = v; j < tam; ++j){
				if (noVisitados[j] && distancias[n][j]<distancias[n][v])
					v=j;
			}
			noVisitados[v] = 0; //nodo noVisitado que minimiza distancias

			for (j = 0; j < tam; ++j){
				if (noVisitados[j]){
					if (distancias[n][j]>(distancias[n][v]+grafo[v][j]))
						distancias[n][j]=distancias[n][v]+grafo[v][j];
				}
			}
		}
	}
	free(noVisitados);
}

//Creamos matriz (con mallocs)
matriz crearMatriz(int n) {
	int i;
	matriz aux;
	if ((aux = malloc(n*sizeof(int *))) == NULL)
		return NULL;
	for (i=0; i<n; i++)
		if ((aux[i] = malloc(n*sizeof(int))) == NULL)
			return NULL;
	return aux;
}

//Liberamos matriz (frees a mallocs)
void liberarMatriz(matriz m, int n) {
	int i;
	for (i=0; i<n; i++)
		free(m[i]);
	free(m);
}

//Para los números pseudoaleatorios
void inicializar_semilla() {
	srand(time(NULL));
}

/* Inicializacion pseudoaleatoria [1..TAM_MAX] de un grafo completo
no dirigido con n nodos, representado por su matriz de adayencia */
void iniMatriz(matriz m, int n) {
	int i, j;
	for (i=0; i<n; i++)
		for (j=i+1; j<n; j++)
			m[i][j] = rand() % TAM_MAX + 1;
	for (i=0; i<n; i++)
		for (j=0; j<=i; j++)
			if (i==j)
				m[i][j] = 0;
			else
				m[i][j] = m[j][i];
}

void mostrarMatriz(matriz m, int n){
	int i, j;
	for (i = 0; i < n; ++i){
		for (j = 0; j < n; ++j){
			printf("%4i ", m[i][j]);
		}
		printf("\n");
	}
}

void test(){
	matriz grafo, dist;
	grafo = crearMatriz(5);
	dist = crearMatriz(5);
	grafo[0][0]=0; grafo[0][1]=1; grafo[0][2]=8; grafo[0][3]=4; grafo[0][4]=7;
	grafo[1][0]=1; grafo[1][1]=0; grafo[1][2]=2; grafo[1][3]=6; grafo[1][4]=5;
	grafo[2][0]=8; grafo[2][1]=2; grafo[2][2]=0; grafo[2][3]=9; grafo[2][4]=5;
	grafo[3][0]=4; grafo[3][1]=6; grafo[3][2]=9; grafo[3][3]=0; grafo[3][4]=3;
	grafo[4][0]=7; grafo[4][1]=5; grafo[4][2]=5; grafo[4][3]=3; grafo[4][4]=0;
	dijkstra(grafo,dist,5);
	printf("Test:\nMatriz de adyacencia figura 1:\n");
	mostrarMatriz(grafo,5);
	printf("\n Distancias minimas:\n");
	mostrarMatriz(dist,5);

	printf("\nMatriz de adyacencia figura 2:\n");
	grafo[0][0]=0; grafo[0][1]=1; grafo[0][2]=4; grafo[0][3]=7;
	grafo[1][0]=1; grafo[1][1]=0; grafo[1][2]=2; grafo[1][3]=8;
	grafo[2][0]=4; grafo[2][1]=2; grafo[2][2]=0; grafo[2][3]=3;
	grafo[3][0]=7; grafo[3][1]=8; grafo[3][2]=3; grafo[3][3]=0;
	dijkstra(grafo,dist,4);
	mostrarMatriz(grafo,4);
	printf("\n Distancias minimas:\n");
	mostrarMatriz(dist,4);

	iniMatriz(grafo,5);
	printf("\nMatriz aleatorio con valores 0..%i:\n", TAM_MAX);
	mostrarMatriz(grafo,5);
	liberarMatriz(grafo,5);
	liberarMatriz(dist,5);
}

//Obtiene la hora del sistema en microsegundos 
double microsegundos() { 
	struct timeval t;
	if (gettimeofday(&t, NULL) < 0) 
		return 0.0;
	return (t.tv_usec + t.tv_sec * 1000000.0);
}

//Analizamos tiempos de ejecución para obtener empiricamente la complejidad
void tiempos(float c_sub, float c_aj, float c_sobre){
	matriz grafo, dist;
	grafo = crearMatriz(512);
	dist = crearMatriz(512);
	int n=8;
	int i, j;
	double t, ta, tb, t1, t2;
	printf("\nTiempos Dijktra:\n%15s%15s%11s%.2f%11s%.2f%11s%.2f\n",
		 "n", "t(n)", "t(n)/n^", c_sub, 
		 "t(n)/n^" , c_aj, "t(n)/n^" , c_sobre);
	for (i = 1; i <= 7; ++i){
		iniMatriz(grafo,n);
		ta=microsegundos();
		dijkstra(grafo,dist,n);
		tb=microsegundos();
		t=tb-ta;
		if (t<500){
			printf("%10s", "(*) ");
			ta=microsegundos();
			for (j = 0; j < 1000; ++j){
				iniMatriz(grafo,n);
				dijkstra(grafo,dist,n);
			}
			tb=microsegundos();
			t1=tb-ta;
			ta=microsegundos();			
			for (j = 0; j < 1000; ++j){
				iniMatriz(grafo,n);
			}
			tb=microsegundos();
			t2=tb-ta;
			t=(t1-t2)/1000;
		} else printf("%10s", "");
		printf("%5i%15.3f%15.6f%15.6f%15.6f\n",
			 n, t, t/(pow(n,c_sub)), t/((pow(n,c_aj))), t/(pow(n,c_sobre)));
		n=n*2;
	}
	liberarMatriz(grafo,512);
	liberarMatriz(dist,512);
}


int main(){
	int i;
	inicializar_semilla();
	test();
	for (i = 0; i < 3; ++i)
		tiempos(2.6,2.8,3);
	return 0;
}