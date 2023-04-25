#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <math.h>

//Algoritmo 1
int sumaSubMax1(int v[], int n){
	int i,j,estaSuma;
	int sumMax=0;
	for (i = 0; i < n; ++i){
		estaSuma=0;
		for (j=i; j<n; ++j){
			estaSuma+=v[j];
			if (estaSuma > sumMax) sumMax = estaSuma;
		}	
	}
	return sumMax;
}

//Algoritmo 2
int sumaSubMax2(int v[], int n){
	int i;
	int estaSuma=0; 
	int sumaMax=0;
	for (i = 0; i < n; ++i){
		estaSuma=estaSuma+v[i];
		if (estaSuma>sumaMax) sumaMax= estaSuma;
		else if (estaSuma<0) estaSuma=0;
	}
	return sumaMax;
}


//Llena un arrays de numeros pseudoaleatorio -n y +n
void aleatorio(int v [], int n) {
	int i, m=2*n+1;
	for (i=0; i < n; i++)
		v[i] = (rand() % m) - n;
}

//Inicializamos semilla para los números pseudoaleatorios 
void inicializar_semilla() {
	srand(time(NULL));
}

//Lista vector
void listar_vector(int v [], int n){
	int j;
	printf("{ ");
	for (j = 0; j < n; ++j){
		printf("%2i ", v[j]);
	}
	printf("} ");
}

//Obtiene la hora del sistema en microsegundos 
double microsegundos() { 
	struct timeval t;
	if (gettimeofday(&t, NULL) < 0)
		return 0.0;
	return (t.tv_usec + t.tv_sec * 1000000.0);
}

//comprobamos que los algoritmos funcionen correctamente
void test1(){
	int i;
	int v[6][5]={
		{-9, 2, -5, -4, 6},
		{4, 0, 9, 2, 5 },
		{-2, -1, -9, -7, -1},
		{9, -2, 1, -7, -8},
		{15, -2, -5, -4, 16 },
		{7, -5, 6, 7, -7},
	};
	printf("\ntest1:\n");
	printf("%33s%15s%15s\n", "", "sumaSubMax1", "sumaSubMax2");
	for (i = 0; i < 6; ++i){
		listar_vector(v[i],5);
		printf("%27i ", sumaSubMax1(v[i],5));
		printf("%14i \n", sumaSubMax2(v[i],5));
	}
}

//volvemos a comprobar pero con números pseudoaleatorios
void test2() {
	int i, a, b;
	int v[9];
	printf("\ntest2: \n");
	printf("%33s%15s%15s\n", "", "sumaSubMax1", "sumaSubMax2");
	for (i=0; i<10; i++) {
		aleatorio(v, 9);
		listar_vector(v, 9);
		a = sumaSubMax1(v, 9);
		b = sumaSubMax2(v, 9);
		printf("%15d%15d\n", a, b);
	}
}

//mostramos tiempos de ejecución y cotas
void tiempos(int (* alg)(), float cota_aj, float cota_dif){
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
		aleatorio(v,n);
		ta=microsegundos();
		alg(v,n);
		tb=microsegundos();
		t=tb-ta;
		if (t<500){
			printf("%10s", "(*) ");
			ta=microsegundos();
			for (j = 0; j < 1000; ++j){
				aleatorio(v,n);
				alg(v,n);
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
			 n, t, t/(pow(n,cota_aj - cota_dif)), 
			 t/(pow(n,cota_aj)), t/(pow(n,cota_aj + cota_dif)));
		n=n*2;
	}
}


int main(){
	int i;
	inicializar_semilla();
	test1();
	test2();
	//llamamos 3 
	for (i = 0; i < 3; ++i){
		printf("\nSumaSubMax 1");
		tiempos(sumaSubMax1,2,0.2);
		printf("\nSumaSubMax 2");
		tiempos(sumaSubMax2,0.94,0.2);
	}
	return 0;
}

