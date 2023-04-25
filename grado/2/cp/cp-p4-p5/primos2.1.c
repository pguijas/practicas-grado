//Pedro Guijas Bravo 
//Hector Padín Torrente 
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <mpi.h>

int main(int argc, char *argv[]){
    int i, j, prime, n, count, numprocs, rank, bff;

    //Inicializamos MPI
    MPI_Init(&argc,&argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numprocs);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    while (1){
        if (rank==0){
            printf("Enter the maximum number to check for primes: (0 quits) \n");
            scanf("%d",&n);
        } 
        if (MPI_SUCCESS!=MPI_Bcast(&n,1,MPI_INT,0,MPI_COMM_WORLD)){
            perror("Error at function MPI_BinomialBcast: \n");
            break;
        };
        //Si n==0 salimos
        if (n == 0) 
            break;

        //Calculamos los primos que le tocan
        count = 0;  
        for (i = 2+rank; i < n; i=i+numprocs) {
            prime = 1;  //suponemos que es primo
            // Check if any number lower than i is multiple
            for (j = 2; j < i; j++) {
                if((i%j) == 0) {
                    prime = 0;
                    break;
                }
            }
            count += prime;
        }
        if (MPI_SUCCESS!=MPI_Reduce(&count,&bff,1,MPI_INT,MPI_SUM,0,MPI_COMM_WORLD)){
            perror("Error at function MPI_BinomialBcast: \n");
            break;
        };
        if (rank==0)
            printf("The number of primes lower than %d is %d\n", n, bff);
        
    }
    MPI_Finalize();
}