#include "p1.h"

int main(int argc, char *argv[]){
	for (int i = 1; i < argc; ++i){
		info(argv[i]);
	}
	return 0;
}