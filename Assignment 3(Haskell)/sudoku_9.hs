import Data.List

rows = [[i*9..i*9+8]| i <- [0..8]]
 
cols = [[i,i+9..80]| i<-[0..8]]
 
grid = [[i..i+2]++[i+9..i+11]++[i+18..i+20]|  i <- [0,3,6,27,30,33,54,57,60]]
 

solution:: [Int] -> IO()
solution sudoku 
 | get_solution sudoku == [] = putStrLn $ "No solution"
 | otherwise = putStrLn $ show(get_solution sudoku)
 
 
get_solution:: [Int] -> [[Int]]
get_solution sudoku
 | is_valid_sudoku sudoku == False = []
 | find (== 0) sudoku /= Nothing = change_element sudoku
 | otherwise = [sudoku] 


get_new_sudoku val pos sudoku = do
 let l1 = take pos sudoku
 let l2 = drop (pos+1) sudoku
 l1++(val:l2)

	
change_element sudoku = do
 let Just position = findIndex (== 0) sudoku
 i <- [1..9]
 get_solution (get_new_sudoku i position sudoku)
	


is_valid_sudoku sudoku = (foldl (&&) True(check_sudoku rows sudoku)) && (foldl (&&) True(check_sudoku cols sudoku)) && (foldl (&&) True(check_sudoku grid sudoku))

check_sudoku:: [[Int]]->[Int]->[Bool]
check_sudoku structure sudoku = do
 i <- structure
 let res = []
 (foldl (&&) True (check_each i sudoku)):res



check_each pos sudoku = do
 i <- [0..8]
 let res = []
 let elem1 = sudoku!!(pos!!i)
 (do 
   j <- [(i+1)..8]
   let elem2 = sudoku!!(pos!!j)
   let cond1 = elem1 == 0 || elem2 ==0
   let cond2 = (elem1 /=0 && elem2 /= 0) && elem1 /= elem2
   (cond1 || cond2):res)