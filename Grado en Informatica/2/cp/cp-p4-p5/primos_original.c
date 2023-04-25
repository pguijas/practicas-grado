#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int main(int argc, char *argv[])
{
    int i, j, prime, done = 0, n, count;

    while (!done)
    {
        printf("Enter the maximum number to check for primes: (0 quits) \n");
        scanf("%d",&n);
    
        if (n == 0) break;

        count = 0;  

        for (i = 2; i < n; i++) {
            prime = 1;

            // Check if any number lower than i is multiple
            for (j = 2; j < i; j++) {
                if((i%j) == 0) {
                   prime = 0;
                   break;
                }
            }
            count += prime;
        }

	printf("The number of primes lower than %d is %d\n", n, count);
    }
}
