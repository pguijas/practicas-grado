%{

/*********************************************************************
 * Intérprete de lenguaje pseudonatural para ejecucion de acciones
 *
 * Authors: Héctor Padín Torrente, hector.padin@udc.es
 *          Pedro Guijas Bravo, p.guijas@udc.es
 * 
 ********************************************************************/

// ------------------------libraries------------------------

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// ------------------------globar variables------------------------
 
int choice = 0;

// ------------------------definitions------------------------

int yylex(void);
void yyerror(char *);
int checkAction(char* action, char* object);
char * mixStrings(char * msg, char * str);

%}

%union {
	char* string;
}

// ------------------------terminal-symbols------------------------

%token <string> TACTION
%token <string> TOBJECT
%token <string> TCD
%token <string> TBE
%token <string> THORA
%token <string> TMEDTMP
%token <string> TTEMP
%token <string> TDIAS
%token <string> TTIME
%token <string> TUNRECOG
%token THELP TDISP TCMD TEJ

%type <string> action phrase condition routine

// ------------------------start of grammar------------------------

%start S

%%

// ------------------------grammar rules------------------------


S  
	: phrase {
		int choice = rand()%4;
		printf("Orden recibida '%s'.\n", $1);
		if (choice==0)
			fprintf(stdout, "Entendido!\n");
		else if (choice==1)
			fprintf(stdout, "Ok, me pongo a ello.\n");
		else if (choice==2)
			fprintf(stdout, "Mmmmmm... ok!\n");
		else
			fprintf(stdout, "Ahora voy.\n");
	}
	| phrase error {
		yyerror("syntax error");
	}
	| THELP {
		fprintf(stdout, "\n\t·disp para listar los dispositivos actuales.\n\t·cmd para recbir ayuda sobre comandos\n\t·ej para sacar ejemplos de ejecución");
	} 
	| TDISP {
		fprintf(stdout, "Dispositivos sobre los que se pueden ejecutar acciones:\n\t·Lampara.\n\t·Luz.\n\t·Termómetro.\n\t·Calefacción.\n\t·Ventana.\n\t·Persiana.\n\t·Televisión.\n");
	} 
	| TCMD {
		fprintf(stdout, "Tipos de órdenes:\n\t·<acción>: enciende la calefacción.\n\t·<acción> y <acción> y ...: enciende la calefacción y cierra la ventana.\n\t·<accion> cuando <condicion>: apaga la tele cuando sean las 11:35:00 pm.\n\t·<acción> <rutina>: enciende la luz todos los dias a las 7:15 am.\n\t·<rutina> <acción>: cada 12 horas 2 minutos y 20 segundos enciende la calefacción.\n");
	} 
	| TEJ {
		fprintf(stdout, "\n> <acción>\n> abre la ventana\n> enciende la luz\n> cierra la ventana enciende la luz y apaga la estufa\n> <acción> cuando <condición>\n> enciende la calefacción a cuando la temperatura sea 18º\n> enciende la luz cuando den las 22:30:55\n> cierra la ventana cuando la temperatura sea de 18º\n> cierra la ventana cuando el termometro marque 18º\n> cierra la ventana cuando el termometro ponga 18º\n> cierra la ventana cuando el termometro ponga 18º por semana\n> cierra la ventana y enciende la luz cuando el termometro ponga 18º por semana\n> <acción> <rutina>\n> abre la ventana todos los dias a las 12 pm\n> enciende la luz por semana a las 7:15 am\n> cierra la ventana y enciende la calefacción los lunes a las 22:00 \n> <rutina> <acción>\n> cada 12 horas abre la ventana y apaga la calefacción\n> cada 12 horas 2 minutos y 20 segundos enciende la calefacción");
	}
;

phrase 
	: action {$$ = $1;}
	| action TCD condition {sprintf($$, "%s %s", $1, $3);}
	| action condition {yyerror(mixStrings("No he entendido cuando tengo que hacer '%s'.\n", $1));}
	| routine {$$ = $1;}
	| TCD condition action {sprintf($$, "%s %s", $2, $3);}
	| TTIME action {
		//Nos aseguramos que de especifiquen la unidad temporal
		if (strcmp($1,"cada ")==0)
			yyerror(mixStrings("No te he entendido, ¿cada cuanto tengo que hacer '%s'?\n", $2));
		sprintf($$, "%s %s", $1, $2);
	}
	| action TTIME {
		//Nos aseguramos que de especifiquen la unidad temporal
		if (strcmp($1,"cada ")==0)
			yyerror(mixStrings("No te he entendido, ¿cada cuanto tengo que hacer '%s'?\n", $2));
		sprintf($$, "%s %s", $1, $2);
	}
;

routine
	: phrase TDIAS {sprintf($$, "%s %s", $1, $2);}
	| phrase TDIAS THORA {sprintf($$, "%s %s %s", $1, $2, $3);}
;

condition
	: TBE THORA {sprintf($$, "%s", $2);}
	| TMEDTMP TTEMP {sprintf($$, "%s %s", $1, $2);}
;

action
	: action TACTION TOBJECT { //concatenación de acciones
		if (checkAction($2, $3))
			yyerror(mixStrings("El dispositivo %s no cuenta con esa acción.\n", $2));
		sprintf($$, "%s %s %s", $1, $2, $3);
	}
	| action TUNRECOG {yyerror(mixStrings("No entiendo qué acción quieres que haga, no sé qué es %s.\n", $2));}
	| action TACTION TUNRECOG {yyerror(mixStrings("No he encontrado el dispositivo '%s' en casa.\n", $3));}
	| TACTION TOBJECT {
		if (checkAction($1, $2))
			yyerror(mixStrings("El dispositivo %s no cuenta con esa acción.\n", $2));
		sprintf($$, "%s %s", $1, $2);
	}
	| TUNRECOG {yyerror(mixStrings("No entiendo qué acción quieres que haga, no sé qué es %s.\n", $1));}
	| TACTION TUNRECOG {yyerror(mixStrings("No he encontrado el dispositivo '%s' en casa.\n", $2));}
;


%%

// ------------------------functions------------------------

void yyerror (char *message) {
	if (strcmp(message,"syntax error")==0) {
		choice = rand()%4;
		if (choice==0)
			fprintf(stderr, "Vaya, creo que no he entendido muy bien lo que quieres que haga...\n");
		else if (choice==1)
			fprintf(stderr, "Creo que no he entendido muy bien. ¿Podrías repetirlo?\n");
		else if (choice==2)
			fprintf(stderr, "Perdona... creo que no he entendido muy bien lo que querías decir\n");
		else
			fprintf(stderr, "No he entendido muy bien tu frase, problemas de ser un autómata...\n");
	} else {
		fprintf(stderr, "%s", message);
		free(message);
	}
	yyclearin;
	exit(0);
}

//Especificamos acciones posibles sobre objetos
/*
	Hemos decidido implementarlo en C y no sobre la gramática de Yacc debido a que consideramos 
	muy importante controlar los errores (el token error de yacc no nos estaba handleando errores y no funcionaba correctamente)
*/
int checkAction(char* action, char* object) {
	if (((strcmp(action,"enciende")==0) || (strcmp(action,"apaga")==0)) && ((strcmp(object,"luz")==0) || (strcmp(object,"lampara")==0) || (strcmp(object,"termometro")==0) || (strcmp(object,"tele")==0) || (strcmp(object,"television")==0) || (strcmp(object,"estufa")==0) || (strcmp(object,"calefaccion")==0)))
		return 0;
	else if (((strcmp(action,"cierra")==0) || (strcmp(action,"abre")==0)) && (strcmp(object,"ventana")==0))
		return 0;
	else if (((strcmp(action,"baja")==0) || (strcmp(action,"sube")==0)) && (strcmp(object,"persiana")==0))
		return 0;
	else
		return 1;
}

char * mixStrings(char * msg, char * str) {
	char * tmp1 = strdup(msg);
	char * tmp2 = strdup(str);
	char * string = malloc(sizeof(char)*(strlen(tmp1)+strlen(tmp2)));
	sprintf(string, tmp1, tmp2);
	return string;
}


int main(int argc, char *argv[]) {
    extern FILE *yyin;
	time_t t;
   	srand((unsigned) time(&t));
    switch (argc) {
        case 1: yyin=stdin;
            yyparse();
            break;
        case 2: yyin = fopen(argv[1], "r");
            if (yyin == NULL) {
                printf("ERROR: No se ha podido abrir el fichero.\n");
            }
            else {
                yyparse();
                fclose(yyin);
            }
            break;
        default: printf("ERROR: Demasiados argumentos.\nSintaxis: %s [fichero_entrada]\n\n", argv[0]);
    }
    return 0;
}