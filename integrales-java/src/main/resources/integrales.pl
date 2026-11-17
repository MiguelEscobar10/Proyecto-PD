:- encoding(utf8).

% Programa para calcular integrales definidas.

%Miguel Alberto Escobar Estrada 00401624
%Mario Alexander Molina Fuentes 00372624
%Ricardo Alberto Pineda Hernández 00378824

% Predicado para calcular la integral:
% definida(Funcion, A, B, Resultado)

% Predicado que devuelve los valores utilizados en el calculo:
% definida_detalle(Funcion, A, B, Primitiva, ValorA, ValorB, Resultado)

% Se utiliza el Teorema Fundamental del Calculo:
% Integral de A a B = F(B) - F(A)

% Reglas para obtener la primitiva de una funcion.

% R1 (a): Integral de una constante.
primitiva(K, K*x) :-
    number(K).

% R2 (b): Integral de x.
primitiva(x, x^2/2).

% R3 (d): Regla de la potencia.
primitiva(x^N, x^M/M) :-
    integer(N),
    N =\= -1,
    M is N + 1.

% R4 (f): Integral de una constante por una funcion.
primitiva(K*U, K*P) :-
    number(K),
    primitiva(U, P).

% R5 (g): Integral de una suma.
primitiva(U+V, P1+P2) :-
    primitiva(U, P1),
    primitiva(V, P2).

% R6 (h): Integral de una resta.
primitiva(U-V, P1-P2) :-
    primitiva(U, P1),
    primitiva(V, P2).

% R7 (i): Integral de una funcion exponencial.
primitiva(A^x, A^x/log(A)) :-
    number(A),
    A > 0,
    A =\= 1.

% R8 (k): Integral de seno.
primitiva(sin(x), -cos(x)).

% R9 (l): Integral de coseno.
primitiva(cos(x), sin(x)).

% R10 (m): Integral de tangente.
primitiva(tan(x), -log(abs(cos(x)))).

% R11 (q): Integral de seno hiperbolico.
primitiva(sinh(x), cosh(x)).

% R12 (r): Integral de coseno hiperbolico.
primitiva(cosh(x), sinh(x)).


% Calculo de integrales definidas.

% Se obtiene el resultado restando F(A) de F(B).
definida(F, A, B, Resultado) :-
    definida_detalle(F, A, B, _, _, _, Resultado).

% Calcula la primitiva y evalua los limites de integracion.
definida_detalle(F, A, B, Primitiva, ValorA, ValorB, Resultado) :-
    primitiva(F, Primitiva),
    verificar_continua(F, A, B),
    evaluar(Primitiva, A, ValorA),
    evaluar(Primitiva, B, ValorB),
    Resultado is ValorB - ValorA.


% Verifica que la funcion sea continua dentro del intervalo.

% Se revisa si existe algun punto donde la funcion no este definida.
verificar_continua(F, A, B) :-
    VA is A,
    VB is B,
    Inferior is min(VA, VB),
    Superior is max(VA, VB),
    (   tiene_discontinuidad(F, Inferior, Superior)
    ->  throw(discontinuidad(F))
    ;   true
    ).

% Comprueba si el intervalo contiene un punto de discontinuidad de tan(x).
tiene_discontinuidad(tan(x), Inferior, Superior) :-
    K1 is ceiling((Inferior - pi/2) / pi - 1.0e-9),
    K2 is floor((Superior - pi/2) / pi + 1.0e-9),
    K1 =< K2.

% Comprueba si el intervalo contiene x = 0 cuando el exponente es negativo.
tiene_discontinuidad(x^N, Inferior, Superior) :-
    integer(N),
    N < 0,
    Inferior =< 0,
    0 =< Superior.

% Revisa las partes de una funcion compuesta.
tiene_discontinuidad(F, Inferior, Superior) :-
    compound(F),
    F =.. [_ | Argumentos],
    member(Argumento, Argumentos),
    tiene_discontinuidad(Argumento, Inferior, Superior),
    !.


% Evaluacion de las expresiones.

% Sustituye x por el valor indicado y calcula la expresion.
evaluar(Expresion, Valor, Resultado) :-
    sustituir(Expresion, Valor, ExpresionNumerica),
    Resultado is ExpresionNumerica.

% Sustituye las apariciones de x dentro de la expresion.
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


% Pruebas de las reglas de integracion.

% ?- definida(5, 0, 3, R).                R = 15.
% ?- definida(x, 0, 4, R).                R = 8.
% ?- definida(x^3, 0, 2, R).              R = 4.
% ?- definida(3*x, 1, 2, R).              R = 4.5.
% ?- definida(x^2 + 4, 0, 3, R).          R = 21.
% ?- definida(x^2 - 4, 0, 3, R).          R = -3.
% ?- definida(2^x, 0, 3, R).              R = 10.098865286222743.
% ?- definida(sin(x), 0, pi, R).          R = 2.0.
% ?- definida(cos(x), 0, pi/2, R).        R = 1.0.
% ?- definida(tan(x), 0, 1, R).           R = 0.6156264703860141.
% ?- definida(sinh(x), 0, 1, R).          R = 0.5430806348152437.
% ?- definida(cosh(x), 0, 1, R).          R = 1.1752011936438014.

% Consulta para obtener el detalle del procedimiento:
% ?- definida_detalle(x^2, 0, 3, P, FA, FB, R).
% P = x^3/3, FA = 0, FB = 9, R = 9.

% Ejecuta una prueba para cada una de las reglas.
ejemplos :-
    forall(member(F-A-B,
                  [ 5-0-3, x-0-4, x^3-0-2, 3*x-1-2, (x^2+4)-0-3,
                    (x^2-4)-0-3, 2^x-0-3, sin(x)-0-pi, cos(x)-0-(pi/2),
                    tan(x)-0-1, sinh(x)-0-1, cosh(x)-0-1 ]),
           ( definida(F, A, B, R),
             format("integral de ~w, de ~w a ~w = ~w~n", [F, A, B, R]) )).


% Entrada de datos desde la interfaz de Java.

% SWI-Prolog recibe la funcion y los dos limites como argumentos.
% La respuesta se envia a Java mediante la salida estandar.

consultar_cli :-
    set_stream(user_output, encoding(utf8)),
    current_prolog_flag(argv, Argumentos),
    (   append(_, [TextoF, TextoA, TextoB], Argumentos)
    ->  catch(consultar_texto(TextoF, TextoA, TextoB), Error, reportar_error(Error))
    ;   writeln('ERROR'),
        writeln('otro')
    ).

% Convierte los datos recibidos en expresiones y realiza el calculo.
consultar_texto(TextoF, TextoA, TextoB) :-
    term_to_atom(F, TextoF),
    term_to_atom(A, TextoA),
    term_to_atom(B, TextoB),
    (   ground(F), ground(A), ground(B),
        definida_detalle(F, A, B, Primitiva, ValorA, ValorB, Resultado)
    ->  format("OK~n~w~n~w~n~w~n~w~n", [Primitiva, ValorA, ValorB, Resultado])
    ;   writeln('SIN_SOLUCION')
    ).

% Identifica el tipo de error para enviarlo a Java.
reportar_error(Error) :-
    tipo_error(Error, Tipo),
    writeln('ERROR'),
    writeln(Tipo).

tipo_error(discontinuidad(_), discontinuidad) :- !.
tipo_error(error(syntax_error(_), _), sintaxis) :- !.
tipo_error(error(evaluation_error(_), _), evaluacion) :- !.
tipo_error(error(type_error(evaluable, _), _), evaluacion) :- !.
tipo_error(_, otro).
