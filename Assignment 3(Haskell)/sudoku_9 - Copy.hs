import Data.List

--Indices that need to be checked
rows = [[i*9..i*9+8]| i <- [0..8]]
 
cols = [[i,i+9..80]| i<-[0..8]]
 
grid = [[i..i+2]++[i+9..i+11]++[i+18..i+20]|  i <- [0,3,6,27,30,33,54,57,60]]
 
--Print a particular solution
print_solution [] _ = return()

print_solution (x:xs) index = do
 if mod index 9 == 8
 then putStrLn $ show(x)++" "
 else putStr $ show(x)++" "
 print_solution xs (index+1)

--Get all the solutions and print accordingly
solution:: [Int] -> IO()
solution soduko 
 | length soduko /= 81 = putStrLn $ "Enter grid of valid length" 
 | get_solution soduko == [] = putStrLn $ "No solution"
 | otherwise = print_solution ((take 1(get_solution soduko))!!0) 0
 
--Get all the solution as a list of list
get_solution:: [Int] -> [[Int]]
get_solution sudoku
 | is_valid_sudoku sudoku == False = []
 | find (== 0) sudoku /= Nothing = change_element sudoku
 | otherwise = [sudoku] 

--Replace an element and get the new list
get_new_sudoku val pos sudoku = do
 let l1 = take pos sudoku
 let l2 = drop (pos+1) sudoku
 l1++(val:l2)

--Recursively replace all the zeroes and generate a solution by backtracking	
change_element sudoku = do
 let Just position = findIndex (== 0) sudoku
 i <- [1..9]
 get_solution (get_new_sudoku i position sudoku)
	

--Checking if a solution is valid by checking the rows, columns and grids if they satisfy the property
is_valid_sudoku sudoku = (foldl (&&) True(check_sudoku rows sudoku)) && (foldl (&&) True(check_sudoku cols sudoku)) && (foldl (&&) True(check_sudoku grid sudoku))

--Checks a particular direction: row, column or grid
check_sudoku:: [[Int]]->[Int]->[Bool]
check_sudoku structure sudoku = do
 i <- structure
 let res = []
 (foldl (&&) True (check_each i sudoku)):res


--Checks a particular row or column or grid
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