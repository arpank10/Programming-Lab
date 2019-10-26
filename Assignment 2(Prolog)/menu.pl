% Types of categories available according to hunger index.
menu("hungry", 1, 1, 1). 
menu("not_so_hungry", 1, 1, 0).
menu("not_so_hungry", 0, 1, 1).
menu("diet", 1, 0, 0).
menu("diet", 0, 1, 0).
menu("diet", 0, 0, 1).

% costs of each tems
starter("Corn Tikki", 30).
starter("Tomato Soup", 20).
starter("Chilli Paneer", 40).
starter("Crispy chicken", 40).
starter("Papdi Chaat", 20).
starter("Cold Drink", 20).

main_dish("Kadhai Paneer with Butter / Plain Naan", 50).
main_dish("Veg Korma with Butter / Plain Naan", 40).
main_dish("Murgh Lababdar with Butter /Plain Naan", 50).
main_dish("Veg Dum Biryani with Dal Tadka", 50).
main_dish("Steam Rice with Dal Tadka", 40).

dessert("Ice-cream", 20).
dessert("Malai Sandwich", 30).
dessert("Rasmalai", 10).

%recipes available for various categories.
starters(["Corn Tikki","Tomato Soup", "Chilli Paneer", "Crispy chicken", "Papdi Chaat", "Cold Drink" ]).

main_dishs(["Kadhai Paneer with Butter / Plain Naan", "Veg Korma with Butter / Plain Naan", "Murgh Lababdar with Butter /Plain Naan", "Veg Dum Biryani with Dal Tadka", "Steam Rice with Dal Tadka" ]).

desserts(["Ice-cream", "Malai Sandwich", "Rasmalai"]).


% Clause for finding the last element of the list.
last(X,[X]).
last(X,[_|Z]) :- last(X,Z).

value(X, Y, Z, H, V) :- X = 1, starter(H, V);
                        Y = 1, main_dish(H, V);
                        Z = 1, dessert(H, V).

get_menu_items(X, Y, Z, L, R, V) :- L = [H|L1], value(X, Y, Z, H, V1), V1 =< V, V2 is V - V1, append(R, [H], R1), get_menu_items(X, Y, Z, L1, R1, V2), subtract(R1, [H], R2), get_menu_items(X, Y, Z, L1, R2, V).

get_menu_items(X, Y, Z, L, R, V) :- L = [H|L1], value(X, Y, Z, H, V1), V1 > V, get_menu_items(X, Y, Z, L1, R, V).

get_menu_items(_, _, _, [], [], _) :- !.

get_menu_items(_, _, _, [], R, _) :- write("Items: "), p(R).


p([]).

p(R) :-  R = [R0|R1], last(R0, R), write(R0), writeln("."), p(R1);
         R = [R0|R1], not(last(R0, R)), write(R0), write(", "), p(R1).



hungry() :- starter(X, _), main_dish(Y, _), dessert(Z, _), write("Items: "),  write(X), write(", "), write(Y), write(", "), write(Z), writeln(".").

diet(X, Y, Z) :-
    X = 1, starters(S), get_menu_items(X, Y, Z, S, [], 40);
    Y = 1, main_dishs(M), get_menu_items(X, Y, Z, M, [], 40);
    Z = 1, desserts(D), get_menu_items(X, Y, Z, D, [], 40).


not_so_hungry(X, _, Z) :- X = 1, starter(S, V), main_dish(M, V1), V2 is V + V1, V2 =<80, write("Items: "),  write(S), write(", "), write(M), writeln(".");
                          Z = 1, main_dish(M, V), dessert(D, V1), V2 is V + V1, V2 =<80, write("Items: "),  write(M), write(", "), write(D), writeln(".").

% Possible combinations of items from the selected categories.
find_items(S, X, Y, Z) :-
    menu(S, X, Y, Z), S = "hungry", hungry(), false();
    menu(S, X, Y, Z), S = "diet", diet(X, Y, Z), false();
    menu("not_so_hungry", X, Y, Z), S = "not_so_hungry", not_so_hungry(X, Y, Z), false().

