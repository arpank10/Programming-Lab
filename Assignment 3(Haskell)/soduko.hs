import Data.List

rows = [[0..4], [4..7], [8..11], [12..15]]
cols = [[0,4..12], [1,5..13], [2,6..14], [3,7..15]]
grid = [[0,1,4,5], [2,3,6,7], [8,9,12,13], [10,11,14,15]]


solution:: [Int] -> IO()
solution soduko 
 | get_solution soduko == [] = putStrLn $ "No solution"
 | otherwise = putStrLn $ show(get_solution soduko)
 
 
get_solution:: [Int] -> [[Int]]
get_solution soduko
 | is_valid_soduko soduko == False = []
 | find (== 0) soduko /= Nothing = change_element soduko
 | otherwise = [soduko] 


get_new_soduko val pos soduko = do
 let l1 = take pos soduko
 let l2 = drop (pos+1) soduko
 l1++(val:l2)

	
change_element soduko = do
 let Just position = findIndex (== 0) soduko
 i <- [1..4]
 get_solution (get_new_soduko i position soduko)
	


is_valid_soduko soduko = (foldl (&&) True(check_soduko rows soduko)) && (foldl (&&) True(check_soduko cols soduko)) && (foldl (&&) True(check_soduko grid soduko))

check_soduko:: [[Int]]->[Int]->[Bool]
check_soduko structure soduko = do
 i <- structure
 let res = []
 (foldl (&&) True (check_each i soduko)):res



check_each pos soduko = do
 i <- [0..3]
 let res = []
 let elem1 = soduko!!(pos!!i)
 (do 
   j <- [(i+1)..3]
   let elem2 = soduko!!(pos!!j)
   let cond1 = elem1 == 0 || elem2 ==0
   let cond2 = (elem1 /=0 && elem2 /= 0) && elem1 /= elem2
   (cond1 || cond2):res)