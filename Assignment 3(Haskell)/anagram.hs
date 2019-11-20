import Data.List  -- For doing operations on list

check_anagram a b = (sort a == sort b)  -- Anagrams have equal sorted representation

find_anagram sub n= do 
 i <- [0..n-2]  -- -- compare i'th substring
 let res = []
 (do 
   j <- [i+1..n-1]  -- with j'th substring
   let s1 = sub!!i 
   let s2 = sub!!j
   if check_anagram s1 s2  -- if anagrams, append (i,j) pair to the list of answers
   then [(s1,s2)]:res
   else res)
 
 

getSub str n = do 
 i <- [1..n]  -- iterate over lengths from 1 to n
 let res = [] -- store all substrings
 let k = n - i  -- no. of drops to be made for length i
 (do 
   j <- [0..k]  -- drop j items from front each time and take i items(gives all i length substring)
   take i(drop j str):res)

anagram [list1, list2] = do 
 let str = list1 ++ list2  -- append the two lists inputed for simplicity
 let len = length str  -- length of the string after concatenation
 let subs = getSub str len  -- find all substrings 
 let anagrams = find_anagram subs (length subs) -- find all pairs of anagrams among the substrings 
 print(length anagrams)
 print(anagrams)
