//Pedro Guijas Bravo 
//Hector Padín Torrente 
//export OMPI_MCA_btl=self,tcp
//export PMIX_MCA_gds=hash
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <mpi.h>

#define DEBUG 0

/* Translation of the DNA bases
   A -> 0
   C -> 1
   G -> 2
   T -> 3
   N -> 4*/


#define M  1000 // Number of sequences
#define N  200000  // Number of bases per sequence

// The distance between two bases
int base_distance(int base1, int base2){

  if((base1 == 4) || (base2 == 4)){
    return 3;
  }

  if(base1 == base2) {
    return 0;
  }

  if((base1 == 0) && (base2 == 3)) {
    return 1;
  }

  if((base2 == 0) && (base1 == 3)) {
    return 1;
  }

  if((base1 == 1) && (base2 == 2)) {
    return 1;
  }

  if((base2 == 2) && (base1 == 1)) {
    return 1;
  }

  return 2;
}

int main(int argc, char *argv[]) {

  int i, j, numprocs, rank, division, code, offset;
  int *data1, *data2, *data1proc, *data2proc;
  int *result, *resultproc, *times;
  int *sendcnts, *displss, *displsg, *recvcount;
  struct timeval tv0, tv1, tv2, tv3;

  // inicializamos MPI
  MPI_Init(&argc, &argv);
  MPI_Comm_size(MPI_COMM_WORLD, &numprocs);
  MPI_Comm_rank(MPI_COMM_WORLD, &rank);

  /* Initialize Matrices 
     0 only 
  */
  if (rank==0){
    data1  = (int *) malloc(M*N*sizeof(int));
    data2  = (int *) malloc(M*N*sizeof(int));
    result = (int *) malloc(M*sizeof(int));
    times = (int *) malloc(numprocs*2*sizeof(int));
    
    for(i=0;i<M;i++) {
      for(j=0;j<N;j++) {
        data1[i*N+j] = (i+j)%5;
        data2[i*N+j] = ((i-j)*(i-j))%5;
      }
    }
  }
  
  //Calculamos parametros para la asignación y posterior repartición
  sendcnts = (int *) malloc(numprocs*sizeof(int));  
  recvcount = (int *) malloc(numprocs*sizeof(int)); 
  displss = (int *) malloc(numprocs*sizeof(int));
  displsg = (int *) malloc(numprocs*sizeof(int));
  division=M/numprocs;
  offset=0;
  for (i = 0; i < numprocs; i++){
    if (M%numprocs>=i+1){
      sendcnts[i]=(division+1)*N;
      recvcount[i]=division+1;
    }else{
      sendcnts[i]=division*N;  
      recvcount[i]=division;
    }
    displss[i]=offset*N;
    displsg[i]=offset;
    offset=offset+recvcount[i];
  }
  
  //Reservamos espacio para cada proc
  data1proc  = (int *) malloc(sendcnts[rank]*sizeof(int));
  data2proc  = (int *) malloc(sendcnts[rank]*sizeof(int));
  resultproc = (int *) malloc(sendcnts[rank]*sizeof(int));

  gettimeofday(&tv0, NULL);
  
  //Repartimos las matrices generadas por el proc 0
  code=MPI_Scatterv(data1, sendcnts, displss, MPI_INT, data1proc, sendcnts[rank], MPI_INT, 0, MPI_COMM_WORLD);
  if (code != MPI_SUCCESS) {
    perror("Error at function MPI_Scatter\n");
    MPI_Finalize();
    exit(EXIT_FAILURE);
  }
  code=MPI_Scatterv(data2, sendcnts, displss, MPI_INT, data2proc, sendcnts[rank], MPI_INT, 0, MPI_COMM_WORLD);
  if (code != MPI_SUCCESS) {
    perror("Error at function MPI_Scatter\n");
    MPI_Finalize();
    exit(EXIT_FAILURE);
  }
  
  //Liberamos (tiempo despreciable)
  if (rank==0){
    free(data1); free(data2);
  }
  free(displss);

  gettimeofday(&tv1, NULL);

  //Calculamos
  for(i=0;i<sendcnts[rank]/N;i++) {
    resultproc[i]=0;
    for(j=0;j<N;j++) {
      resultproc[i] += base_distance(data1proc[i*N+j], data2proc[i*N+j]);
    }
  }
  
  //Liberamos (tiempo despreciable)
  free(data1proc); free(data2proc); free(sendcnts); 

  gettimeofday(&tv2, NULL);
  
  //Captamos Resultados
  code=MPI_Gatherv(resultproc, recvcount[rank], MPI_INT, result, recvcount, displsg, MPI_INT, 0, MPI_COMM_WORLD);
  if (code != MPI_SUCCESS) {
    perror("Error at function MPI_Gatherv\n");
    MPI_Finalize();
    exit(EXIT_FAILURE);
  }

  gettimeofday(&tv3, NULL);

  //Cálculo de tiempos  
  int time[2];
  time[0] = (tv2.tv_usec - tv1.tv_usec)+ 1000000 * (tv2.tv_sec - tv1.tv_sec);
  time[1] = 
    (tv1.tv_usec - tv0.tv_usec)+ 1000000 * (tv1.tv_sec - tv0.tv_sec) + 
    (tv3.tv_usec - tv2.tv_usec)+ 1000000 * (tv3.tv_sec - tv2.tv_sec);
  
  //Envio de tiempos
  code=MPI_Gather(&time, 2, MPI_INT, times, 2, MPI_INT, 0, MPI_COMM_WORLD);
  if (code != MPI_SUCCESS) {
    perror("Error at function MPI_Gather\n");
    MPI_Finalize();
    exit(EXIT_FAILURE);
  }

  /* Display Results 
     0 only 
  */
  if (rank==0){
    if (DEBUG){
      for(i=0;i<M;i++) 
        printf(" %d \t ",result[i]);      
    } else{ 
      for (i=0;i<numprocs;i++)
        printf ("Proc %i: Tiempo Calculo = %lf Tiempo Comunicacion = %lf (seconds)\n", i, (double) times[i*2]/1E6, (double) times[i*2+1]/1E6);
    } 
  }
  
  //Liberamos
  if (rank==0){
    free(result); free(times);
  }
  free(resultproc); 
  free(recvcount); free(displsg); 

  
  MPI_Finalize();
  return 0;
}

