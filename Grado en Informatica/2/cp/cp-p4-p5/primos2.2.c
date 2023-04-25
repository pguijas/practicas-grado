//Pedro Guijas Bravo 
//Hector Padín Torrente 
#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

int MPI_BinomialBcast(void *buffer, int count, MPI_Datatype datatype, int root, MPI_Comm comm){
    //Asumimos: root=0
    
    int rank,size,code,exp=1;
    MPI_Status status;   
    //Cargamos informacion
    code = MPI_Comm_size(comm, &size);
    if (code!=MPI_SUCCESS)
        return code;
    code = MPI_Comm_rank(comm, &rank);
    if (code!=MPI_SUCCESS)
        return code;

    //Recivimos y propagamos (Root desencadena)
    if (rank!=0){
        code = MPI_Recv(buffer, count, datatype, MPI_ANY_SOURCE, MPI_ANY_TAG, comm, &status);
        if (code != MPI_SUCCESS)
        	return code;
        exp=(rank-status.MPI_SOURCE)*2;
    }
    while((rank+exp)<size){
        MPI_Send(buffer,count,datatype,rank+exp,0,MPI_COMM_WORLD);
        exp=exp*2;
    }
    return MPI_SUCCESS;
}

int MPI_FlattreeColectiva(void *sendbuf, void *recvbuf, int count, MPI_Datatype datatype, MPI_Op op, int root, MPI_Comm comm){
    //Asumimos: int, count=1

    int rank,size,i,code,tmp;
    //Cargamos informacion
    code = MPI_Comm_size(comm, &size);
    if (code!=MPI_SUCCESS)
        return code;
    code = MPI_Comm_rank(comm, &rank);
    if (code!=MPI_SUCCESS)
        return code;

    if (rank==root){
        //Recibe root
        *(int *)recvbuf=*(int *)sendbuf; //casteamos a int
        for (i = 1; i < size; i++){
            code = MPI_Recv(&tmp, 1, datatype, MPI_ANY_SOURCE, root, comm, MPI_STATUS_IGNORE);
            if (code!=MPI_SUCCESS)
                return code;
            *(int *)recvbuf=*(int *)recvbuf+tmp;
        }
    } else{
        //Envian no root
        code = MPI_Send(sendbuf,1,MPI_INT,0,0,MPI_COMM_WORLD);
        if (code!=MPI_SUCCESS)
            return code;
    }
    return MPI_SUCCESS;
}

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
        if (MPI_SUCCESS!=MPI_BinomialBcast(&n,1,MPI_INT,0,MPI_COMM_WORLD)){
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
        if (MPI_SUCCESS!=MPI_FlattreeColectiva(&count,&bff,1,MPI_INT,MPI_SUM,0,MPI_COMM_WORLD)){
            perror("Error at function MPI_BinomialBcast: \n");
            break;
        };
        if (rank==0)
            printf("The number of primes lower than %d is %d\n", n, bff);
        
    }
    MPI_Finalize();
}
