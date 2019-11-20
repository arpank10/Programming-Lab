m []= 0 -- product of sums is zero
m (list)=  product (map (sum) list)  -- otherwise map the sum function on list and call product on new list

greatest fun (list) = do 
						 let t = maximum (map (fun) list)    -- first find the maximum value of the func.
						 let f_list = filter (\x -> fun x == t) list -- find all lists which gives max value
						 print(head f_list) -- print the first item


 

data List a = Empty | Cons a (List a) deriving (Show, Read, Eq, Ord)  -- abstract data type List

toList :: [a] -> List a  --prototype conversion from haskell list to abstract List 

toList ([]) = Empty   -- base case 'Empty'
toList (x:xs) =  x `Cons` toList(xs)  -- recurse for remaining elements 

toHaskellList :: List a -> [a]  --prototype conversion from  abstract List to haskell list

toHaskellList (Empty) = []  --base case 'Empty' 

toHaskellList (Cons a b) = a:toHaskellList(b)  -- Cons equivalently transormed using ':' operator
