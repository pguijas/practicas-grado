%{
#include <stdlib.h>
#include <string.h>
#include "practica2.tab.h"
char * id_from_open(char * str);
char * id_from_close(char * str);
%}

%option yylineno

atrib_v (" "+"version"=\"[0-9]+\.[0-9]+\")
encode "ISO-8859-1"|"iso-8859-1"|"UTF-8"|"utf-8"
atrib_e (" "+"encoding"=\"{encode}+\")
atrib_opc (" "+{id}=\"[^\"\']+\")
atr {atrib_opc}*{atrib_v}{atrib_opc}*{atrib_e}{atrib_opc}*|{atrib_opc}*{atrib_v}{atrib_opc}*{atrib_e}{atrib_opc}* 
cabecera \<\?"xml"{atr}\?\> 
bad_cabecera \<\?"xml"[^(\?\>)]*\?\> 
id ([a-zA-Z]|"_")([a-zA-Z]|"_"|"-"|[0-9])*
bad_id [^"/"">"][^">"]*
apertura \<{id}\>
bad_apertura \<{bad_id}\>
cierre \<"/"{id}\>
bad_cierre \<"/"{bad_id}\>
comentario \<\!--[^(\>)]*--\>
delim [ \t\n]+
texto [^<]+    

%%


{delim}         {/*ignoremos delimitadores*/}
{cabecera}      {return CABECERA;}
{bad_cabecera}  {return BAD_CABECERA;}
{comentario}    {return COMENTARIO;}
{apertura}      {yylval.t_str=id_from_open(yytext);  return APERTURA;}
{bad_apertura}  {yylval.t_str=id_from_open(yytext);  return BAD_APERTURA;}
{cierre}        {yylval.t_str=id_from_close(yytext); return CIERRE;}
{bad_cierre}    {yylval.t_str=id_from_close(yytext); return BAD_CIERRE;}
{texto}         {return TEXTO;}
.               {printf("Error con el caracter \'%s\' (linea %i).\n", yytext, yylineno); exit(0);}


%%


char * id_from_open(char * str){
    int len = strlen(str);
    char * subtext = malloc(len*sizeof(char)); //acuerdate de hacerle el free
    strncpy(subtext, &str[1], len-2);
    return subtext;
}

char * id_from_close(char * str){
    int len = strlen(str);
    char * subtext = malloc(len*sizeof(char)); //acuerdate de hacerle el free
    strncpy(subtext, &str[2], len-3);
    return subtext;
}