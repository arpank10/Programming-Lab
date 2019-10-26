range(X, W) :- X >= 1 , X =< 26, W is 1.
range(X, W) :- X>26,W is 0; X = 0, W is 0.

get_number_of_strings([], W, S) :- W is 1, atom_codes(S1, S), writeln(S1) .

get_number_of_strings(L, W, S) :- L = [X|L1], L1 = [Y|L2], atom_number(X, N1), atom_number(Y, N2), N1 > 0, N3 is N1*10+N2, 
                               step(L1, N1, W1, S), step(L2, N3, W2, S), W is W1 + W2.

get_number_of_strings(L, W, S) :- L = [X|L1], atom_number(X, N1), step(L1, N1, W1, S), W is W1.

step(L, N, W, S) :- range(N, R), R = 1, N1 is N+64, append(S, [N1], S1), get_number_of_strings(L, W1, S1), W is W1*R;
                    range(N, R), R = 0, W is 0.


decode(S,W) :-
    string_chars(S,L),
    get_number_of_strings(L, W, []).

