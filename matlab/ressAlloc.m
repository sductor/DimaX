
function [x,fval] =ressAlloc()
clear()
%5 hote 2agents
n=3;
m=5;


fail    = [0.31,0.92,0.13,0.54,0.75];
capproc = [1.251, 1.252, 1.253, 1.254, 1.255];
capmem  = [1.251, 1.252, 1.253, 1.254, 1.255];

crit    = [0.31, 0.72, 0.53];
demproc = [1.21, 1.22, 1.23];
demmem  = [0.51, 0.52, 0.53];

A = getA(n,m,demproc,demmem);
b = getB(n,m,capproc,capmem);
lb = getLb(n,m);
ub = getUb(n,m);

myOpt=@(x)optUtil(x,crit,fail);

display(A);
display(b);
display(lb);
display(ub);


%xTest = [1,1,0,0,1,1,0,0,1,0,1,1,0,1,0];

%i=1;
%display(xTest);
%display(xTest((i-1)*m+1:i*m));
%display(dispo(xTest((i-1)*m+1:i*m),fail));
%display(fiabUtil(xTest((i-1)*m+1:i*m),crit(i),fail));
%display(optUtil(xTest,crit,fail));

[x,fval] = fmincon(myOpt,int16(zeros(n*m,1)), A, b, [], [], lb, ub);
display(x);
display(fval);


%

%Optimisation
%

%disponibility of a
%alloca the current allocation of a
%fail the vector of fail prob
function f = dispo(alloca, fail)
l=1;
for i = 1:size(alloca)
    if alloca(i)==1
    l = l * fail(i);
    end
end
f = 1 - l;

function f = fiabUtil(alloca, crita, fail)
f = crita * dispo(alloca, fail);

function f = fiabMin(alloca, crita, fail)
f = dispo(alloca, fail) / crita;

function f = fiabNash(alloca, crita, fail)
f = dispo(alloca, fail);

function f = fiabNash2(alloca, crita, fail)
f = dispo(alloca, fail) + 1 / crita;

%utilitarist social welfare
% alloc 1..m (->a1), 1..m (->a2), ..., 1..m (->an)
% alloc 1..m (->a1), m+1..2m (->a2), ..., (n-1)*m+1..n*m (->an)
function f = optUtil(alloc, crit, fail)
n = size(crit);
m = size(fail);
r = 0;
for i=1:n
    r = r +  fiabUtil(alloc((i-1)*m+1:i*m),crit(i),fail);
end
f=-r;

%nash social welfare
% alloc 1..m (->a1), 1..m (->a2), ..., 1..m (->an)
% alloc 1..m (->a1), m+1..2m (->a2), ..., (n-1)*m+1..n*m (->an)
function f = optNash(alloc, crit, fail)
n = size(crit);
m = size(fail);
r = 0;
for i=1:n
    r = r +  fiabNash(alloc((i-1)*m+1:i*m),crit(i),fail);
end
f=-r;

%nash2 social welfare
% alloc 1..m (->a1), 1..m (->a2), ..., 1..m (->an)
function f = optNash2(alloc, crit, fail)
n = size(crit);
m = size(fail);
r = 0;
for i=1:n
    r = r +  fiabNash2(alloc((i-1)*m+1:i*m),crit(i),fail);
end
f=-r;

%
% contraintes
% 

% alloc 1..m (->a1), 1..m (->a2), ..., 1..m (->an)
function A = getA(n, m, demproc, demmem)
A=zeros(2*m+n, m*n);
%n premières lignes = survies des n agents :
for i = 1:n
    for j=1:m
     A(i,(i-1)*m+j)=-1;
    end
end
%m lignes suivantes = contraintes en capacité mémoire
for i = 1:m
    for j=1:n
        A(n+i,(j-1)*m+i)=demmem(j);
    end
end
%m lignes suivantes = contraintes en capacité processeurs
for i = 1:m
    for j=1:n
        A(n+m+i,(j-1)*m+i)=demproc(j);
    end
end
A;

function b = getB(n, m, capproc, capmem)
b=ones(2*m+n,1);
%n premières lignes = survies des n agents : la matrice vaut 1
%m lignes suivantes = contraintes en capacité mémoire
for i = 1:n
    b(i)=-1;
end
for i = 1:m
    b(n+i)=capmem(i);
end
%m lignes suivantes = contraintes en capacité processeurs
for i = 1:m
    b(m+n+i)=capproc(i);
end
b;


function lb = getLb(n,m)
lb = zeros(n*m,1);

function ub = getUb(n,m)
ub = ones(n*m,1);






