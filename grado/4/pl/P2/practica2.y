%{
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
int yylex();
void yyerror (char const *);
extern int yylineno;
extern void yyclearin;
char * str_error_diff_id(char * encontrado, char * esperaba);
char * str_error_id(char * id);
void check_ap(char * id1,char * id2);
char * free_str = NULL;
%}

%union{
	char * t_str;
}

%token <t_str> COMENTARIO CABECERA BAD_CABECERA TEXTO APERTURA BAD_APERTURA CIERRE BAD_CIERRE;
%type <t_str> apertura cierre

%start S

%error-verbose

%%

/*
	Cada elemento es encargado contemplar comentarios posteriores e intermedios
*/

S :  
      cabecera comment 
	| cabecera comment sobr_a el sobr_d 
;

//Me gustaría haber usado el término error.
sobr_d: 	| CABECERA 			{yyerror("Error, cabecera fuera del nodo raiz."); yyclearin;}
			| BAD_CABECERA		{yyerror("Error, cabecera fuera del nodo raiz."); yyclearin;}
			| TEXTO				{yyerror("Error, texto fuera del nodo raiz."); yyclearin;}
			| cierre			{yyerror("Error, cierre fuera del nodo raiz."); yyclearin;}
			| apertura			{yyerror("Error, apertura fuera del nodo raiz."); yyclearin;}
;

sobr_a:		| TEXTO				{yyerror("Error, texto antes del nodo raiz."); yyclearin;}
			| cierre			{yyerror("Error, cierre antes del nodo raiz."); yyclearin;}			
;

//TEXTO no se considera un el dado que un XML con solo un línea de texto no sería válido
l_el:	
		 el 
		| l_el el
		| TEXTO comment
		| l_el TEXTO comment
;

el: 
	apertura comment l_el cierre comment { check_ap($1,$4);}
	| apertura comment cierre comment { check_ap($1,$3); }
;

apertura: 	APERTURA
			| BAD_APERTURA {yyerror(str_error_id($1)); yyclearin;}
;

cierre:		CIERRE
			| BAD_CIERRE {yyerror(str_error_id($1)); yyclearin;}
;

comment: 	/*vacio*/
			|comment COMENTARIO
;

cabecera:	/*vacio*/ { yyerror("Sintaxis XML incorrecta. Falta la cabecera."); yyclearin; } 
			| CABECERA 
			| BAD_CABECERA { yyerror("Sintaxis XML incorrecta. Cabecera mal formada."); yyclearin; }
;
	
%%

int main() {
	yyparse();
	printf("Sintaxis XML correcta.\n");
	return 0;
}

void yyerror (char const *message) { 
	fprintf (stderr, "Sintaxis XML incorrecta. Error cercano a la línea %i: %s\n", yylineno, message);
	if (free_str!=NULL)
		free(free_str);
	exit(0);
}

char * str_error_diff_id(char * encontrado, char * esperaba){
	char * str = malloc(255*sizeof(char));
	sprintf(str, "Encontrado \"</%s>\" y se esperaba \"</%s>\"." , encontrado, esperaba);
	free_str=str;
	return str;	
}

char * str_error_id(char * id){
	char * str = malloc(255*sizeof(char));
	sprintf(str, "Id no válido (%s)." , id);
	free_str=str;
	return str;
}

void check_ap(char * id1,char * id2){
	if (strcmp(id1,id2)!=0){
		yyerror(str_error_diff_id(id2,id1));
		yyclearin;
	}
}


