function convolucion(equis,hache)
%*************************************************************************
%
%	Nombre: convolucion.m
%
%	Objetivo: calcula la convoluci'on de dos senhales paso
%	a paso visualizando los resultados intermedios
%
%	Version: 15 de marzo de 2020
%
%*************************************************************************
figure;

Lx=length(equis);
Lh=length(hache); 
ndib=-Lh:Lx+Lh;

%	Construccion y dibujo de x(k)

xdek=[zeros(1,Lh) equis zeros(1,Lh+1)];
subplot(411)
stem(ndib,xdek);
title('x(k)')
xlabel('k')
grid;


for n=0:Lx+Lh-1

        %   Construccion y dibujo de h(n-k)

        hdenmenosk=[zeros(1,n+1) hache(Lh:-1:1) zeros(1,Lx+Lh-n)];
        subplot(412)
        stem(ndib,hdenmenosk);
        title('h(n-k)')
        xlabel('k')
        grid;
	

	%	Dibujo de x(k)h(n-k)

	subplot(413)
	stem(ndib,xdek.*hdenmenosk);
	title('x(k)h(n-k)')
	xlabel('k')
	grid;

	%	Obtencion y dibujo de y(n)

	yden(n+1)=sum(xdek.*hdenmenosk);
	subplot(414)
       	%stem(-1:Lx+Lh,[0 yden zeros(1,Lx+Lh-n)]);
        stem(ndib,[zeros(1,Lh) yden zeros(1,Lx+Lh-n)]);

	title('y(n)')
	xlabel('n')
	grid;

        disp('Pulse una tecla...')
        pause;
end;

	
