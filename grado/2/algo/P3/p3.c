#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <stdbool.h>
#include <math.h>
#define TAM 256000

typedef struct {
	int vector[TAM];
	int ultimo;
} monticulo;

void hundir(monticulo *m, int i){
	int hijoIzq, hijoDer;
	int j, aux;
	do{
		hijoIzq = (i+1)*2-1;
		hijoDer = (i+1)*2;
		j=i;
		//si el hijo está dentro de rango y hijo es menor que el padre
		if((hijoDer <= m->ultimo) && (m->vector[hijoDer] < m->vector[i])){
			i = hijoDer;
		}
		if((hijoIzq <= m->ultimo) && (m->vector[hijoIzq] < m->vector[i])){
			i = hijoIzq;
		}
		//intercambiamos
		aux = m->vector[i];
		m->vector[i] = m->vector[j];
		m->vector[j] = aux;
	}while(j != i);//hasta que no haya cambios
}

void crearMonticulo(int * v, int n, monticulo * m){
	int i;
	//copiamos vector en monticulo 
	for (i = 0; i < n; ++i){
		m->vector[i]=v[i];
	}
	m->ultimo=n-1;
	//la mitad son hojas
	for (i = ((m->ultimo+1)/2)-1; i >= 0; --i){
		hundir(m,i);
	}
}

int consultarMenor(const monticulo *m){
	return (m->vector)[0];
}

void quitarMenor(monticulo *m){
	if(m->ultimo<0)
		printf("Error monticulo vacio\n");
 	else{
		m->vector[0]=m->vector[(m->ultimo)--];	
		hundir(m,0);
	}
}

//Lista vector
void listar_vector(int v [], int n){
	int j;
	printf("{ ");
	for (j = 0; j < n; ++j){
		printf("%3i ", v[j]);
	}
	printf("}");
}

void ordM(int * v, int n){
	int i;
	monticulo m;
	crearMonticulo(v,n,&m);
	for(i=0; i<n; i++){
		v[i]=consultarMenor(&m);
		quitarMenor(&m);
	}
}


//comprueba si un vector está ordenado
int ordenado(int v [], int n){
	int i;
	for (i = 0; i < n-1; ++i){
		if (v[i]>v[i+1]){
			return 0;
		}
	}
	return 1;
}

//comprueba si es montículo
int esMonticulo(int v [], int n){
	int i;
	for (i = (n/2)-1; i >= 0; --i){
		if( v[i]>v[(i+1)*2-1] || v[i]>v[(i+1)*2]){
			return 0;
		}
	}
	return 1;
}

//Para los números pseudoaleatorios
void inicializar_semilla() {
	srand(time(NULL));
}


//Llena el vector con enteros (aleatorios)
void aleatorio(int v [], int n){
	int i, m=2*n+1;
	for (i=0; i < n; i++)
		v[i] = (rand() % m) - n;
}


//Llena el vector con enteros (0..n-1)
void ascendente(int v [], int n){
	int i;
	for (i=0; i < n; i++)
		v[i] = i;
}

//Llena el vector con enteros (n..1)
void descendente(int v [], int n){
	int i;
	for (i=0; i < n; i++)
		v[i] = n-i;
}

//Obtiene la hora del sistema en microsegundos 
double microsegundos() { 
	struct timeval t;
	if (gettimeofday(&t, NULL) < 0 ) return 0.0;
		return (t.tv_usec + t.tv_sec * 1000000.0);
}

void testCreaMonticulo(){
	int v[15];
	monticulo m;

	printf("Inicializacion alearotia:\n");
	aleatorio(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));	

	printf("\nCreacion Monticulo:\n");
	crearMonticulo(v,15,&m);
	listar_vector((&m)->vector,15);
	printf("\nordenado? %i\n", ordenado((&m)->vector,15));
	printf("monticulo? %i\n", esMonticulo((&m)->vector,15));
	
	printf("\nInicializacion descendente:\n");
	descendente(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));	
	printf("monticulo? %i\n", esMonticulo(v,15));
	
	printf("\nCreacion Monticulo:\n");
	crearMonticulo(v,15,&m);
	listar_vector((&m)->vector,15);
	printf("\nordenado? %i\n", ordenado((&m)->vector,15));
	printf("monticulo? %i\n", esMonticulo((&m)->vector,15));
}

void testOrdMonticulo(){
	int v[15];

	printf("\nInicializacion alearotia:\n");
	aleatorio(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));	
	printf("monticulo? %i\n", esMonticulo(v,15));

	printf("\nOrdenacion por Monticulo:\n");
	ordM(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));
	printf("monticulo? %i\n", esMonticulo(v,15));
	
	printf("\nInicializacion descendente:\n");
	descendente(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));	
	printf("monticulo? %i\n", esMonticulo(v,15));

	printf("\nOrdenacion por Monticulo:\n");
	ordM(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));
	printf("monticulo? %i\n", esMonticulo(v,15));
}

//timpos y breve análisis de ellos al crear montículo con numeros cualquiera 
void tiempos_CM(float c_sub, float c_aj, float c_sobre){
	monticulo m;
	int v[32000];
	int n=500;
	int i, j;
	double t, ta, tb, t1, t2;
	printf("\n%15s%15s%11s%.2f%11s%.2f%11s%.2f\n",
		 "n", "t(n)", 
		 "t(n)/n^", c_sub, 
		 "t(n)/n^" , c_aj, 
		 "t(n)/n^" , c_sobre);
	for (i = 1; i <= 7; ++i){
		aleatorio(v,n);
		ta=microsegundos();
		crearMonticulo(v,n,&m);
		tb=microsegundos();
		t=tb-ta;
		if (t<500){
			printf("%10s", "(*) ");
			ta=microsegundos();
			for (j = 0; j < 1000; ++j){
				aleatorio(v,n);
				crearMonticulo(v,n,&m);
			}
			tb=microsegundos();
			t1=tb-ta;
			ta=microsegundos();			
			for (j = 0; j < 1000; ++j){
				aleatorio(v,n);
			}
			tb=microsegundos();
			t2=tb-ta;
			t=(t1-t2)/1000;
		} else printf("%10s", "");
		printf("%5i%15.3f%15.6f%15.6f%15.6f\n",
			 n, t, t/(pow(n,c_sub)), 
			 t/(pow(n,c_aj)), t/(pow(n,c_sobre)));
		n=n*2;
	}
}

//tiempos y breve análisis de ellos en ordenación por montículos
void tiempos_ORD(void (* inici)(int [], int)){
	int v[TAM];
	int n=500;
	int i, j;
	double t, ta, tb, t1, t2;
	printf("\n%15s%15s%15s%15s%15s\n",
		 "n", "t(n)", 
		 "t(n)/n" , 
		 "t(n)/nlogn" , 
		 "t(n)/n^1.2" );
	for (i = 1; i <= 7; ++i){
		inici(v,n);
		ta=microsegundos();
		ordM(v,n);
		tb=microsegundos();
		t=tb-ta;
		if (t<500){
			printf("%10s", "(*) ");
			ta=microsegundos();
			for (j = 0; j < 1000; ++j){
				inici(v,n);
				ordM(v,n);
			}
			tb=microsegundos();
			t1=tb-ta;
			ta=microsegundos();			
			for (j = 0; j < 1000; ++j){
				inici(v,n);
			}
			tb=microsegundos();
			t2=tb-ta;
			t=(t1-t2)/1000;
		} else printf("%10s", "");
		printf("%5i%15.3f%15.6f%15.6f%15.6f\n",
			 n, t, t/(n), 
			 t/(n*log(n)), t/(pow(n,1.2)));
		n=n*2;
	}
}

int main(){
	int i;
	inicializar_semilla();
	printf("Test:\n");
	testCreaMonticulo();
	testOrdMonticulo();
	printf("\nTiempos:\n");
	for (i = 0; i < 3; ++i){
		printf("Creacion de un monticulo");
		tiempos_CM(0.9,1,1.1);
		printf("\nOrdenacion por monticulos con inicializacion ascendente:");
		tiempos_ORD(ascendente);
		printf("\nOrdenacion por monticulos con inicializacion descendente:");
		tiempos_ORD(descendente);
		printf("\nOrdenacion por monticulos con inicializacion aleatoria:");
		tiempos_ORD(aleatorio);
		printf("\n");
	}
	return 1;
}
