%{

/*********************************************************************
 * Intérprete de lenguaje pseudonatural para ejecucion de acciones
 *
 * Authors: Héctor Padín Torrente, hector.padin@udc.es
 *          Pedro Guijas Bravo, p.guijas@udc.es
 * 
 ********************************************************************/

// ------------------------libraries------------------------

#include <stdlib.h>
#include "lex.tab.h"


// ------------------------flex definitions------------------------
%}

%option noyywrap

ws              [' ''\t''\r''\n']+
art             "el"|"la"|"les"|"lo"|"los"|"las"
prep            "a"|"con"|"de"|"desde"|"en"|"hacia"|"hasta"|"para"|"por"|"sin"
open            "abre"|"abrir"
close           "cierra"|"cerrar"
on              "enciende"|"encender"
off             "apaga"|"apagar"
up              "sube"
down            "baja"
action          {open}|{close}|{on}|{off}|{up}|{down}
object          "ventana"|"luz"|"lampara"|"termometro"|"persiana"|"tele"|"television"|"estufa"|"calefaccion"
ser             "ser"|"sean"|"den"|"sea"
cond_temp       "cuando"
medTmp          "haga"|"hagan"|"termometro "("marque"|"ponga")|("temperatura"{ws}{ser})
temp            [0-9]?[0-9]º
hora            ([01]?[0-9]|2[0-3])(:[0-5][0-9](:[0-5][0-9])?)?({ws}?(am|pm))?
time            "cada"{ws}([0-9]?[0-9]?[0-9]{ws}"horas"(({ws}"y"|",")?{ws})?)?([0-9]?[0-9]?[0-9]{ws}"minutos"(({ws}"y"|",")?{ws})?)?([0-9]?[0-9]?[0-9]{ws}"segundos")?
dias            "lunes"|"martes"|"miercoles"|"jueves"|"viernes"|"sabados"|"domingos"|"por semana"|"fin de semana"|"dias"

%%

{ws}                                    {}
{action}                                {yylval.string=strdup(yytext); return TACTION;}
{art}                                   {/* no aporta significado, actuan como decoradores*/}
{prep}                                  {/* no aporta significado, actuan como decoradores*/}
{object}              	                {yylval.string=strdup(yytext); return TOBJECT;}
{cond_temp}                             {yylval.string=strdup(yytext); return TCD;}
{medTmp}                                {yylval.string=strdup(yytext); return TMEDTMP;}
{temp}                                  {yylval.string=strdup(yytext); return TTEMP;}
{hora}                                  {yylval.string=strdup(yytext); return THORA;}
"todos"                                 {/* no aporta significado ( todos los lunes == los lunes )*/}
"y"                                     {/* no aporta significado (las acciones están bien definidas y se conoce cuando finalizan) */}
"help"                                  {return THELP;}
"disp"                                  {return TDISP;}
"cmd"                                   {return TCMD;}
"ej"                                    {return TEJ;}
{ser}                                   {yylval.string=strdup(yytext); return TBE;}
{dias}                                  {yylval.string=strdup(yytext); return TDIAS;}
{time}                                  {yylval.string=strdup(yytext); return TTIME;}

[^' ''\t''\r''\n']+                     {yylval.string=strdup(yytext); return TUNRECOG;}


%%