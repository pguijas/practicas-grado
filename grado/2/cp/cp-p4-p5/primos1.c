//Pedro Guijas Bravo 
//Hector Padín Torrente 
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <mpi.h>

//export OMPI_MCA_btl=self,tcp
//export PMIX_MCA_gds=hash

int main(int argc, char *argv[]){
    int i, j, prime, n, count, numprocs, rank, tmp;

    //Inicializamos MPI
    MPI_Init(&argc,&argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numprocs);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    while (1){
        if (rank==0){
            printf("Enter the maximum number to check for primes: (0 quits) \n");
            scanf("%d",&n);
            //Mandamos n a todos los procesos (desde 0)
            for (i = 1; i < numprocs; i++)
                if (MPI_SUCCESS!=MPI_Send(&n,1,MPI_INT,i,0,MPI_COMM_WORLD)){
				    perror("Bad receiving: error at function MPI_Send: \n");    
                    MPI_Finalize();
                    exit(-1);
                }
        } else{
            //Recibimos n
			if (MPI_SUCCESS != MPI_Recv(&n, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE)) {
				perror("Bad receiving: error at function MPI_Recv: \n");
				break;
			}
        }
        //Si n==0 salimos
        if (n == 0) 
            break;
        
        //Calculamo los primos que le tocan
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
        
        if (rank==0){
            //Recibimos los resultados
            for (i = 1; i < numprocs; i++){
        		if (MPI_SUCCESS != MPI_Recv(&tmp, 1, MPI_INT, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE)) {
        			perror("Error at function MPI_Recv: \n");
        			MPI_Finalize();
					exit(-1);
        		}
                count=count+tmp;
            }
            printf("The number of primes lower than %d is %d\n", n, count);
        } else{
            //Enviamos los resultados
            if (MPI_SUCCESS != MPI_Send(&count,1,MPI_INT,0,0,MPI_COMM_WORLD)){
                perror("Error at function MPI_Send: \n");
                MPI_Finalize();
                exit(-1);
            }
        }
        
    }
    MPI_Finalize();
}
