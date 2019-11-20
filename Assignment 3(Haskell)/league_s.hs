import System.Random
import Data.List

--List of teams
teams = ["BS","CM","CH","CV","CS","DS","EE","HU","MA","ME","PH","ST"]
--Seed value used to generate random fixtures,different seeds will generate different values

--All the function definitions
get_all_matches :: [[Char]] -> Int -> Int -> Int -> [([Char],[Char ],Int, Int)]
newRand :: Int -> Int -> Int -> Int
fixture:: [Char] -> Int -> IO()
print_matches::[([Char],[Char],Int, Int)] -> IO()
print_match::[Char]->[([Char],[Char],Int, Int)] -> IO()
print_next_match::Int->[([Char],[Char],Int, Int)] -> IO()


--Functions to get the values from the tuple ( Team 1, Team 2, Date, Time)
teamA (a,_,_,_) = a
teamB (_,b,_,_) = b
date (_,_,t,_)
 | t==1 = "1/11"
 | t==2 = "2/11"
 | otherwise = "3/11"
 
time (_,_,_,t)
 | t==0 = "9:30 AM"
 | otherwise = "7:30 PM"

--Random function generator to generate a number between a and b
newRand a b seed = head(take 1(randomRs (a,b) (mkStdGen seed) :: [Int]))

--Generates a random list of 12 teams
draw [] _ = []

draw teams seed = do
 let n = length teams
 let num = newRand 0 (n-1) seed
 let team = teams!!num
 let new_teams = delete team teams
 team:(draw new_teams seed)


--Returns all the fixtures in the form of list of tuples (team 1, team 2, date, time)
get_all_matches teams 6 d t = []

get_all_matches teams i d t = do
  let team1 = teams!!(i*2)
  let team2 = teams!!(i*2+1)
  if t==0
  then (team1, team2, d, t):get_all_matches teams (i+1) d (1-t)
  else (team1, team2, d, t):get_all_matches teams (i+1) (d+1) (1-t)

--Print function to print all the matches
print_matches [] = return ()

print_matches (x:xs) = do
 let current_match = (teamA x)++ " VS " ++ (teamB x)++ " " ++ (date x)++ " " ++ (time x)
 putStrLn $ current_match
 print_matches xs


--Print function to print a match based on the team
print_match team (x:xs) = do
 if team == (teamA x) || team == (teamB x)
 then  putStrLn $ ((teamA x)++ " VS " ++ (teamB x)++ " " ++ (date x)++ " " ++ (time x))
 else print_match team xs 

print_match team [] = putStrLn $ "No such team found"

--Print a match based on the index of the fixture
print_next_match index fixtures = do
 let x = fixtures!!index
 putStrLn $ ((teamA x)++ " VS " ++ (teamB x)++ " " ++ (date x)++ " " ++ (time x)) 


--Main fixture function which generates all or a particular team's fixtures
fixture t seed
   | t == "all" = print_matches(get_all_matches (draw teams seed) i d time)
   | otherwise = print_match t (get_all_matches (draw teams seed) i d time )
   where {i = 0;
          d = 1;
          time = 0 }

--Function to get the next match based on the given date and time
next_match date time seed
   | date < 1 || date >30 = putStrLn $ "Enter valid date"
   | index >= 6 = putStrLn $ "No More Matches" 
   | time > 24.00 || time < 0.00 = putStrLn $ "Enter valid time"
   | otherwise =  print_next_match index ((get_all_matches (draw teams seed) i d t))
   where {index = (date - 1) * 2 + fromEnum(time > 9.50) + fromEnum(time > 19.50);
          i=0;d=1;t=0}