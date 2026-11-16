:- encoding(utf8).

% ==========================================================================
%  SISTEMA DE INTEGRALES DEFINIDAS EN PROLOG
%  Calcula el area bajo la curva de f(x) entre dos limites a y b.
% ==========================================================================
%
%  Las funciones se escriben como terminos de Prolog, con la variable 'x':
%
%     5            constante numerica          (k)
%     x            la variable                 (x)
%     x^3          potencia entera             (x^n)
%     2*sin(x)     constante por funcion       (k*u)
%     x + cos(x)   suma de funciones           (u+v)
%     x - cos(x)   resta de funciones          (u-v)
%     2^x          exponencial de base const.  (a^x)
%     sin(x), cos(x), tan(x), sinh(x), cosh(x)
%
%  PREDICADO PRINCIPAL (el que usara la interfaz):
%
%     definida(Funcion, A, B, Resultado)
%         Integral definida de Funcion entre A y B.
%         A y B pueden ser numeros o expresiones como pi, pi/2, 2*pi.
%
%  PREDICADO CON EL DETALLE DEL PROCEDIMIENTO (util para mostrarlo en pantalla):
%
%     definida_detalle(Funcion, A, B, Primitiva, ValorA, ValorB, Resultado)
%
%  METODO: Teorema Fundamental del Calculo
%
%     integral de A a B de f(x) dx = F(B) - F(A),   donde F' = f
%
%  La primitiva F se obtiene con las 12 reglas de la PARTE 1.
%
%  LAS 12 REGLAS (con el inciso del enunciado):
%     R1  a)  k            ->  k*x
%     R2  b)  x            ->  x^2/2
%     R3  d)  x^n          ->  x^(n+1)/(n+1),   n entero y n != -1
%     R4  f)  k*u          ->  k * (primitiva de u)     (incluye c: k*x)
%     R5  g)  u+v          ->  primitiva de u + primitiva de v
%     R6  h)  u-v          ->  primitiva de u - primitiva de v
%     R7  i)  a^x          ->  a^x / ln(a),             a > 0 y a != 1
%     R8  k)  sin(x)       ->  -cos(x)
%     R9  l)  cos(x)       ->  sin(x)
%     R10 m)  tan(x)       ->  -ln|cos(x)|
%     R11 q)  sinh(x)      ->  cosh(x)
%     R12 r)  cosh(x)      ->  sinh(x)
% ==========================================================================


% --------------------------------------------------------------------------
%  PARTE 1: REGLAS PARA OBTENER LA PRIMITIVA  (primitiva/2)
%  primitiva(F, P): P es una antiderivada de F (sin la constante C, que se
%  cancela al restar F(B) - F(A)).
% --------------------------------------------------------------------------

% R1 (a): primitiva de una constante k  =  k*x
primitiva(K, K*x) :-
    number(K).

% R2 (b): primitiva de x  =  x^2/2
primitiva(x, x^2/2).

% R3 (d): regla de la potencia, x^n  ->  x^(n+1)/(n+1)
%         (n entero y distinto de -1, porque dividiria entre cero)
primitiva(x^N, x^M/M) :-
    integer(N),
    N =\= -1,
    M is N + 1.

% R4 (f): una constante sale de la integral:  k*u  ->  k * primitiva(u)
primitiva(K*U, K*P) :-
    number(K),
    primitiva(U, P).

% R5 (g): la primitiva de una suma es la suma de las primitivas
primitiva(U+V, P1+P2) :-
    primitiva(U, P1),
    primitiva(V, P2).

% R6 (h): la primitiva de una resta es la resta de las primitivas
primitiva(U-V, P1-P2) :-
    primitiva(U, P1),
    primitiva(V, P2).

% R7 (i): a^x  ->  a^x / ln(a)   (a constante positiva y distinta de 1)
primitiva(A^x, A^x/log(A)) :-
    number(A),
    A > 0,
    A =\= 1.

% R8 (k): sin(x)  ->  -cos(x)
primitiva(sin(x), -cos(x)).

% R9 (l): cos(x)  ->  sin(x)
primitiva(cos(x), sin(x)).

% R10 (m): tan(x)  ->  -ln|cos(x)|
primitiva(tan(x), -log(abs(cos(x)))).

% R11 (q): sinh(x)  ->  cosh(x)
primitiva(sinh(x), cosh(x)).

% R12 (r): cosh(x)  ->  sinh(x)
primitiva(cosh(x), sinh(x)).


% --------------------------------------------------------------------------
%  PARTE 2: INTEGRAL DEFINIDA  (definida/4)
% --------------------------------------------------------------------------

% definida(F, A, B, R): R = F(B) - F(A)
definida(F, A, B, Resultado) :-
    definida_detalle(F, A, B, _, _, _, Resultado).

% Version con el detalle de cada paso, para mostrarlo en la interfaz.
definida_detalle(F, A, B, Primitiva, ValorA, ValorB, Resultado) :-
    primitiva(F, Primitiva),
    verificar_continua(F, A, B),
    evaluar(Primitiva, A, ValorA),
    evaluar(Primitiva, B, ValorB),
    Resultado is ValorB - ValorA.


% --------------------------------------------------------------------------
%  PARTE 2.1: VERIFICAR QUE LA FUNCION SEA CONTINUA EN [A, B]
% --------------------------------------------------------------------------
%  El Teorema Fundamental del Calculo solo vale si f es continua en [A, B].
%  Hay dos casos en que las reglas de arriba dejarian de serlo:
%     - tan(x) no existe en x = pi/2 + k*pi
%     - x^n con n negativo no existe en x = 0
%  Si el intervalo contiene alguno de esos puntos, se lanza discontinuidad(F)
%  en lugar de devolver un numero falso.

verificar_continua(F, A, B) :-
    VA is A,
    VB is B,
    Inferior is min(VA, VB),
    Superior is max(VA, VB),
    (   tiene_discontinuidad(F, Inferior, Superior)
    ->  throw(discontinuidad(F))
    ;   true
    ).

% tan(x): hay un punto pi/2 + k*pi (k entero) dentro del intervalo
tiene_discontinuidad(tan(x), Inferior, Superior) :-
    K1 is ceiling((Inferior - pi/2) / pi - 1.0e-9),
    K2 is floor((Superior - pi/2) / pi + 1.0e-9),
    K1 =< K2.
% x^n con n negativo: el intervalo contiene a x = 0
tiene_discontinuidad(x^N, Inferior, Superior) :-
    integer(N),
    N < 0,
    Inferior =< 0,
    0 =< Superior.
% funcion compuesta: revisar sus partes (sumas, restas, k*u)
tiene_discontinuidad(F, Inferior, Superior) :-
    compound(F),
    F =.. [_ | Argumentos],
    member(Argumento, Argumentos),
    tiene_discontinuidad(Argumento, Inferior, Superior),
    !.


% --------------------------------------------------------------------------
%  PARTE 3: EVALUACION NUMERICA
% --------------------------------------------------------------------------

% evaluar(Expresion, Valor, Resultado): reemplaza 'x' por Valor y calcula.
evaluar(Expresion, Valor, Resultado) :-
    sustituir(Expresion, Valor, ExpresionNumerica),
    Resultado is ExpresionNumerica.

% sustituir(Expresion, Valor, Nueva): cambia cada 'x' por Valor.
sustituir(x, Valor, Valor) :- !.
sustituir(N, _, N) :-
    number(N), !.
sustituir(Termino, Valor, Nuevo) :-
    Termino =.. [Funtor | Args],
    sustituir_lista(Args, Valor, NuevosArgs),
    Nuevo =.. [Funtor | NuevosArgs].

sustituir_lista([], _, []).
sustituir_lista([A | Resto], Valor, [B | NuevoResto]) :-
    sustituir(A, Valor, B),
    sustituir_lista(Resto, Valor, NuevoResto).


% --------------------------------------------------------------------------
%  PARTE 4: EJEMPLOS DE CONSULTA
% --------------------------------------------------------------------------
%  ?- definida(5, 0, 3, R).                R = 15.
%  ?- definida(x, 0, 4, R).                R = 8.
%  ?- definida(x^3, 0, 2, R).              R = 4.
%  ?- definida(3*x, 1, 2, R).              R = 4.5.
%  ?- definida(x^2 + 4, 0, 3, R).          R = 21.
%  ?- definida(x^2 - 4, 0, 3, R).          R = -3.
%  ?- definida(2^x, 0, 3, R).              R = 10.098865286222743.
%  ?- definida(sin(x), 0, pi, R).          R = 2.0.
%  ?- definida(cos(x), 0, pi/2, R).        R = 1.0.
%  ?- definida(tan(x), 0, 1, R).           R = 0.6156264703860141.
%  ?- definida(sinh(x), 0, 1, R).          R = 0.5430806348152437.
%  ?- definida(cosh(x), 0, 1, R).          R = 1.1752011936438014.
%
%  ?- definida_detalle(x^2, 0, 3, P, FA, FB, R).
%        P = x^3/3, FA = 0, FB = 9, R = 9.

% ejemplos/0: ejecuta una consulta por cada regla y muestra el resultado.
ejemplos :-
    forall(member(F-A-B,
                  [ 5-0-3, x-0-4, x^3-0-2, 3*x-1-2, (x^2+4)-0-3,
                    (x^2-4)-0-3, 2^x-0-3, sin(x)-0-pi, cos(x)-0-(pi/2),
                    tan(x)-0-1, sinh(x)-0-1, cosh(x)-0-1 ]),
           ( definida(F, A, B, R),
             format("integral de ~w, de ~w a ~w = ~w~n", [F, A, B, R]) )).


% --------------------------------------------------------------------------
%  PARTE 5: ENTRADA POR LINEA DE COMANDOS (la usa la interfaz en Java)
% --------------------------------------------------------------------------
%  swipl -q -g consultar_cli -t halt integrales.pl -- "x^2+4" "0" "3"
%
%  Los tres argumentos llegan como texto: funcion, a, b.
%  Respuesta por pantalla (una cosa por linea):
%     OK, primitiva, F(a), F(b), resultado
%     SIN_SOLUCION                      (la funcion no se puede integrar)
%     ERROR, tipo        (tipo: sintaxis, evaluacion, discontinuidad, otro)

consultar_cli :-
    set_stream(user_output, encoding(utf8)),
    current_prolog_flag(argv, Argumentos),
    (   append(_, [TextoF, TextoA, TextoB], Argumentos)
    ->  catch(consultar_texto(TextoF, TextoA, TextoB), Error, reportar_error(Error))
    ;   writeln('ERROR'),
        writeln('otro')
    ).

consultar_texto(TextoF, TextoA, TextoB) :-
    term_to_atom(F, TextoF),
    term_to_atom(A, TextoA),
    term_to_atom(B, TextoB),
    (   ground(F), ground(A), ground(B),
        definida_detalle(F, A, B, Primitiva, ValorA, ValorB, Resultado)
    ->  format("OK~n~w~n~w~n~w~n~w~n", [Primitiva, ValorA, ValorB, Resultado])
    ;   writeln('SIN_SOLUCION')
    ).

reportar_error(Error) :-
    tipo_error(Error, Tipo),
    writeln('ERROR'),
    writeln(Tipo).

tipo_error(discontinuidad(_), discontinuidad) :- !.
tipo_error(error(syntax_error(_), _), sintaxis) :- !.
tipo_error(error(evaluation_error(_), _), evaluacion) :- !.
tipo_error(error(type_error(evaluable, _), _), evaluacion) :- !.
tipo_error(_, otro).
