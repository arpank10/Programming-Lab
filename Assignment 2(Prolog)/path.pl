% All undirected edges(and their weights) in the graph.
distance(g1, g5, 4).
distance(g2, g5, 6).
distance(g3, g5, 8).
distance(g4, g5, 9).
distance(g1, g6, 10).
distance(g2, g6, 9).
distance(g3, g6, 3).
distance(g4, g6, 5).
distance(g5, g7, 3).
distance(g5, g10, 4).
distance(g5, g11, 6).
distance(g5, g12, 7).
distance(g5, g6, 7).
distance(g5, g8, 9).
distance(g6, g8, 2).
distance(g6, g12, 3).
distance(g6, g11, 5).
distance(g6, g10, 9).
distance(g6, g7, 10).
distance(g7, g10, 2).
distance(g7, g11, 5).
distance(g7, g12, 7).
distance(g7, g8, 10).
distance(g8, g9, 3).
distance(g8, g12, 3).
distance(g8, g11, 4).
distance(g8, g10, 8).
distance(g10, g15, 5).
distance(g10, g11, 2).
distance(g10, g12, 5).
distance(g11, g15, 4).
distance(g11, g13, 5).
distance(g11, g12, 4).
distance(g12, g13, 7).
distance(g12, g14, 8).
distance(g15, g13, 3).
distance(g13, g14, 4).
distance(g14, g17, 5).
distance(g14, g18, 4).
distance(g17, g18, 8).

% valid start points.
check_valid_start_point(R) :- R = g1; R = g2; R = g3; R = g4.
% Check for valid path, given the nodes in the path sequence.
valid(R) :- R = [R0|R1], check_valid_start_point(R0), check_valid(R0, R1).

% Recursively check the valid edges.
check_valid(g17, []).
check_valid(R0, R) :- R = [R1|R2], distance(R0, R1, _), check_valid(R1, R2);R = [R1|R2], distance(R1, R0, _), check_valid(R1, R2).

% Find all possible paths, by just ignoring the weights and appending valid edge nodes.
possible_paths(g17, V) :- writeln(V).

possible_paths(N, V) :- N \= g17, distance(N, N1, _), not(member(N1, V)), append(V, [N1], V1), possible_paths(N1, V1);
						N \= g17, distance(N1, N, _), not(member(N1, V)), append(V, [N1], V1), possible_paths(N1, V1).

all_possible_paths() :- possible_paths(g1, [g1]);possible_paths(g2, [g2]);possible_paths(g3, [g3]);possible_paths(g4, [g4]).

list_min([L|Ls], Min) :- foldl(num_num_min, Ls, L, Min).

num_num_min(X, Y, Min) :- Min is min(X, Y).

%Finf optimal path starting from N, visited nodes in V, for total path length Val.
optimum_path_calc(g17, _, 0).

optimum_path_calc(N, V, Val) :- N \= g17, distance(N, N1, D), not(member(N1, V)), append(V, [N1], V1), optimum_path_calc(N1, V1, Val1), Val is Val1 + D;
								N \= g17, distance(N1, N, D), not(member(N1, V)), append(V, [N1], V1), optimum_path_calc(N1, V1, Val1), Val is Val1 + D.

% Find all paths for the given path length val.

all_paths_with_length(g17, V, 0) :- writeln(V).

all_paths_with_length(N, V, Min) :- N \= g17, distance(N, N1, D), not(member(N1, V)), append(V, [N1], V1), Val1 is Min - D, all_paths_with_length(N1, V1, Val1);
								    N \= g17, distance(N1, N, D), not(member(N1, V)), append(V, [N1], V1), Val1 is Min - D, all_paths_with_length(N1, V1, Val1).


paths_with_length(Val) :- all_paths_with_length(g1, [g1], Val);all_paths_with_length(g2, [g2], Val);all_paths_with_length(g3, [g3], Val);all_paths_with_length(g4, [g4], Val).


optimum_path(Val) :- optimum_path_calc(g1, [g1], Val);optimum_path_calc(g2, [g2], Val);optimum_path_calc(g3, [g3], Val);optimum_path_calc(g4, [g4], Val).

% Get optimal path over all possible paths; recursing over all valid paths with length Val.
get_optimum_path() :- findall(Val, optimum_path(Val), Res), list_min(Res, Min), paths_with_length(Min).


