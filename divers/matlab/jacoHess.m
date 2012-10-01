n= 5;
m= 7;

W = sym('W', [n 1]);
L=sym('Lambda', [m 1]);
alloc = sym('alloc',[n m]);

sym a h;

function f = myObj
symsum(W[a]*(1 - \symprod(L[h]^alloc[a][h],h,0,m)),a,0,n);