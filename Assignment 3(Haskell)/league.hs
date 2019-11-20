import System.Random
import Data.List

teams = ["BS","CM","CH","CV","CS","DS","EE","HU","MA","ME","PH","ST"]
seed = 42

get_all_matches :: [[Char]] -> Int -> Int -> Int -> [([Char],[Char ],Int, Int)]
newRand :: Int -> Int -> Int
fixture:: [Char] -> IO()
print_matches::[([Char],[Char],Int, Int)] -> IO()
print_match::[Char]->[([Char],[Char],Int, Int)] -> IO()
print_next_match::Int->[([Char],[Char],Int, Int)] -> IO()

teamA (a,_,_,_) = a
teamB (_,b,_,_) = b
date (_,_,t,_)
 | t==1 = "1/11"
 | t==2 = "2/11"
 | otherwise = "3/11"
 
time (_,_,_,t)
 | t==0 = "9:30 AM"
 | otherwise = "7:30 PM"

newRand a b = head(take 1(randomRs (a,b) (mkStdGen seed) :: [Int]))

draw [] = []

draw teams = do
 let n = length teams
 let num = newRand 0 (n-1)
 let team = teams!!num
 let new_teams = delete team teams
 team:(draw new_teams)

get_all_matches teams 6 d t = []

get_all_matches teams i d t = do
  let team1 = teams!!(i*2)
  let team2 = teams!!(i*2+1)
  if t==0
  then (team1, team2, d, t):get_all_matches teams (i+1) d (1-t)
  else (team1, team2, d, t):get_all_matches teams (i+1) (d+1) (1-t)


print_matches [] = return ()

print_matches (x:xs) = do
 let current_match = (teamA x)++ " VS " ++ (teamB x)++ " " ++ (date x)++ " " ++ (time x)
 putStrLn $ current_match
 print_matches xs

print_match team (x:xs) = do
 if team == (teamA x) || team == (teamB x)
 then  putStrLn $ ((teamA x)++ " VS " ++ (teamB x)++ " " ++ (date x)++ " " ++ (time x))
 else print_match team xs 

print_match team [] = putStrLn $ "No such team found"

print_next_match index fixtures = do
 let x = fixtures!!index
 putStrLn $ ((teamA x)++ " VS " ++ (teamB x)++ " " ++ (date x)++ " " ++ (time x)) 

fixture t
   | t == "all" = print_matches(get_all_matches (draw teams) i d time)
   | otherwise = print_match t (get_all_matches (draw teams) i d time )
   where {i = 0;
          d = 1;
          time = 0 }

next_match date time
   | index >= 6 = putStrLn $ "No More Matches" 
   | time > 24.00 || time < 0.00 = putStrLn $ "Enter valid time"
   | otherwise =  print_next_match index ((get_all_matches (draw teams) i d t))
   where {index = (date - 1) * 2 + fromEnum(time > 9.30) + fromEnum(time > 19.30);
          i=0;d=1;t=0}