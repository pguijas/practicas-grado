%*************************************************************************
%
%	Nombre: ejemplo.m
%
%	Objetivo: ejecuta el programa convolucion.m para un ejemplo
%
%	Version: 15 de marzo de 2020
%
%*************************************************************************
clear all
close all

%	Definicion de las senales a convolucionar


Lx=3;				%Longitud de x(n)
nx=0:Lx-1;
%equis=sin(pi*nx/12 + pi/4);	%x(n)
equis=ones(1,Lx);

Lh=4;                         %Longitud de h(n)
nh=0:Lh-1;		 	
%hache=[5 4 3 2 1];		%h(n)
hache=2.^nh.*ones(1,Lh)/Lh;

%	Dibujo de x(n) y h(n). Se anaden Lh ceros antes y despues 
%	para visualizar mejor las senales

ndib=-Lh:Lx+Lh;
figure(1)
subplot(211)
equisdib=[zeros(1,Lh) equis zeros(1,Lh+1)];
stem(ndib,equisdib);
title('x(n)');


subplot(212)
hachedib=[zeros(1,Lh) hache zeros(1,Lx+1)];
stem(ndib,hachedib);
title('h(n)');


disp('Pulse una tecla...')
pause;
close        

convolucion(equis,hache)
