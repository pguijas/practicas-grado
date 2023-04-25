#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <stdbool.h>
#include <math.h>

//algoritmo ordenacion por inserción
void ord_ins(int v[], int n){
	int x, j, i;
	for (i = 0; i < n; ++i){
		x=v[i];
		j=i-1;
		while (j>=0 && v[j]>x){//revisar j>0
			v[j+1] = v[j];
			j--;
		}
		v[j+1]=x;
	}
}

//algoritmo ordenación shell
void ord_shell (int v[], int n){
	int incremento = n;
	int i, j, tmp;
	bool seguir;
	do{
		incremento = incremento/2;
		for (i = incremento; i < n; ++i){
			tmp = v[i];
			j = i;
			seguir=true;
			while( (j-incremento >= 0) && seguir){
				if (tmp < v[j-incremento]){
					v[j]=v[j-incremento];
					j=j-incremento;
				} else seguir=false;
			}
			v[j]=tmp;
		}
	} while (incremento!=1);
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

//Para los números pseudoaleatorios
void inicializar_semilla() {
	srand(time(NULL));
}


//Llena el vector con enteros (aleatorios)
void aleatorio(int v [], int n){/* se generan números pseudoaleatorio entre -n y +n */
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

//Lista vector
void listar_vector(int v [], int n){
	int j;
	printf("{ ");
	for (j = 0; j < n; ++j){
		printf("%3i ", v[j]);
	}
	printf("}");
}

//Obtiene la hora del sistema en microsegundos 
double microsegundos() { 
	struct timeval t;
	if (gettimeofday(&t, NULL) < 0 ) 
		return 0.0;
	return (t.tv_usec + t.tv_sec * 1000000.0);
}

void test(){
	int v[15];
	
	printf("Test:\nInicializacion alearotia:\n");
	inicializar_semilla();
	aleatorio(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));

	printf("Ordenacion por insercion:\n");
	ord_ins(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));
	
	printf("Inicializacion descendente:\n");
	descendente(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));
	
	printf("Inicializacion ascendente:\n");
	ascendente(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));

	printf("Inicializacion alearotia:\n");
	inicializar_semilla();
	aleatorio(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));

	printf("Ordenacion por shell:\n");
	ord_shell(v,15);
	listar_vector(v,15);
	printf("\nordenado? %i\n", ordenado(v,15));
}

//extraemos los timpos 
void tiempos(void (* inici)(), void (* ord)(), float cota_aj, float cota_dif){
	int v[32000];
	int n=500;
	int i, j;
	double t, ta, tb, t1, t2;
	printf("\n%15s%15s%11s%.2f%11s%.2f%11s%.2f\n",
		 "n", "t(n)", 
		 "t(n)/n^", cota_aj - cota_dif, 
		 "t(n)/n^" , cota_aj , 
		 "t(n)/n^" , cota_aj + cota_dif);
	for (i = 1; i <= 7; ++i){
		inici(v,n);
		ta=microsegundos();
		ord(v,n);
		tb=microsegundos();
		t=tb-ta;
		if (t<500){
			printf("%10s", "(*) ");
			ta=microsegundos();
			for (j = 0; j < 1000; ++j){
				inici(v,n);
				ord(v,n);
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
			 n, t, t/(pow(n,cota_aj - cota_dif)), 
			 t/(pow(n,cota_aj)), t/(pow(n,cota_aj + cota_dif)));
		n=n*2;
	}
}

int main(){
	int i;
	test();
	for (i = 0; i < 3; ++i){
		printf("\nOrdenacion por insercion con inicializacion ascendente");
		tiempos(ascendente, ord_ins, 1, 0.1);
		printf("\nOrdenacion por insercion con inicializacion descendente");
		tiempos(descendente, ord_ins, 2, 0.2);
		printf("\nOrdenacion por insercion con inicializacion desordenado");
		tiempos(aleatorio, ord_ins, 2, 0.2);
		printf("\nOrdenacion shell con inicializacion ascendente");
		tiempos(ascendente, ord_shell, 1.14, 0.14);
		printf("\nOrdenacion shell con inicializacion descendente");
		tiempos(descendente, ord_shell, 1.15, 0.15);
		printf("\nOrdenacion shell con inicializacion desordenado");
		tiempos(aleatorio, ord_shell, 1.2, 0.2);
	}
	return 1;
}