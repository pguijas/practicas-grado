%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% TEMA 1: Representacion de senales en el dominio temporal
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
clear
close all
% Ejercicio 7

n = -10:10;
t = -10:0.001:10;

% a) Escalon unidad

xt = 2*(t>0);
xn = 2*(n>=0);

figure;
plot(t,xt,'r'); hold on;
stem(n,xn);
title('Escalon Unitario (de amplitud 2)',"FontSize",8);
xlabel('Tiempo',"FontSize",8);
ylabel('Amplitud',"FontSize",8);
l=legend('continua','discreta');
set(l,"fontsize",7)
set(l,"location","northwest");
set(gca,"FontSize",10);

% b) Pulso

A = 3; 
T = 4;
N = 4;

xt = A*((t>0) & (t<T));
xn = A*((n>=0) & (n<N));

figure;
plot(t,xt,'r'); hold on;
stem(n,xn);
title('Pulso Unidad (T=4, A=3)',"FontSize",8);
xlabel('Tiempo',"FontSize",8);
ylabel('Amplitud',"FontSize",8);
l=legend('continua','discreta');
set(l,"fontsize",7)
set(l,"location","northwest");
set(gca,"FontSize",10);


% c) Rampa

xt = t.*(t>0);
xn = n.*(n>0);

figure;
plot(t,xt,'r'); hold on;
stem(n,xn);
title('Señal Rampa',"FontSize",8);
xlabel('Tiempo',"FontSize",8);
ylabel('Amplitud',"FontSize",8);
l=legend('continua','discreta');
set(l,"fontsize",7)
set(l,"location","northwest");
set(gca,"FontSize",10);


% d) Exponencial

a = 1;
xt = exp(a*t).*(t>0);
xn = exp(a*n).*(n>=0);

figure;
subplot(2,1,1)
plot(t,xt,'r'); hold on;
stem(n,xn);
title('Exponencial unilateral (a=-1)',"FontSize",8);
xlabel('Tiempo',"FontSize",8);
ylabel('Amplitud',"FontSize",8)
l=legend('continua','discreta');
set(l,"fontsize",7)
set(l,"location","west");
set(gca,"FontSize",10);
a = -1;
xt = exp(a*t).*(t>0);
xn = exp(a*n).*(n>=0);

subplot(2,1,2)
plot(t,xt,'r'); hold on;
stem(n,xn);

title('Exponencial unilateral (a=1)',"FontSize",8);
xlabel('Tiempo',"FontSize",8);
ylabel('Amplitud',"FontSize",8)
l=legend({"continua","discreta"});
set(l,"fontsize",7)
set(l,"location","west");
set(gca,"FontSize",10);


% Ejercicio 8

% a) Coseno
t = 0:0.001:0.1;
n = 0:0.001:0.1;
f = 30;

xt = cos(2*pi*f*t);
xn = cos(2*pi*f*t);

figure;
plot(t,xt,'r'); hold on;
stem(n,xn);
title('Señal Coseno (Frecuencia 20 Hz)',"FontSize",8)
xlabel('Tiempo',"FontSize",8);
ylabel('Amplitud',"FontSize",8)
l=legend('continua','discreta');
set(gca,"FontSize",10)
% b) sinc
t = -10:0.1:10;  
n = -10:0.1:10;

xt = sin(pi*t)./(pi*t);
pos = find(t==0);
xt(pos) = 1;

xn = sin(pi*n)./(pi*n);
pos = find(n==0);
xn(pos) = 1;

figure;
plot(t,xt,'r'); hold on;
stem(n,xn);
title('Señal Sinc',"FontSize",8)
xlabel('Tiempo',"FontSize",8);
ylabel('Amplitud',"FontSize",8)
l=legend('continua','discreta');
set(gca,"FontSize",10);


% Ejercicio 9
% Vamos a resolver todos los apartados juntos

z = [1 -1 j -j 1+2*j -1+2*j];


%Modulo
abs(z)

%Fase
angle(z)*180/pi    % Se podria haber utilizadorad2deg(angle(z))


%Grafica
figure;
plot(z,'or')
axis([-2 2 -2 2]);
xlabel('Parte real')
ylabel('Parte imaginaria');
str = {'local max','local min'};
text(real(z)+0.1,imag(z)+0.1,(cellstr(num2str(z.'))));
