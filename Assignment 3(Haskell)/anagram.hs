import Data.List

check_anagram a b = (sort a == sort b)

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
 
 

getSub str n = do 
 i <- [1..n]
 let res = []
 let k = n - i
 (do 
   j <- [0..k]
   take i(drop j str):res)

anagram [list1, list2] = do
 let str = list1 ++ list2
 let len = length str
 let subs = getSub str len
 let anagrams = find_anagram subs (length subs)
 print(length anagrams)
 print(anagrams)