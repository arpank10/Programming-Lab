import Data.List

--Check anagram by sorting the two strings and checking if they are equal
check_anagram a b = (sort a == sort b)

--Checks between each two substrings if they are anagram or not
find_anagram sub n= do 
 i <- [0..n-2]
 let res = []
 (do 
   j <- [i+1..n-1]
   let s1 = sub!!i 
   let s2 = sub!!j
   if check_anagram s1 s2
   then [(s1,s2)]:res
   else res)
 
 
--Function to find all the substrings of the concatenated strings
getSub str n = do 
 i <- [1..n]
 let res = []
 let k = n - i
 (do 
   j <- [0..k]
   take i(drop j str):res)


--Main function to print count of all anagrams and also the anagrams
anagram [list1, list2] = do
 let str = list1 ++ list2
 let len = length str
 let subs = getSub str len
 let anagrams = find_anagram subs (length subs)
 print(length anagrams)
 print(anagrams)
 
--Error case 
anagram _ = putStrLn $ "Enter Valid input"