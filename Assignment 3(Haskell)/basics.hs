--Finding the product of the sum of each list in the list of lists
m []= 0
m (list)=  product (map (sum) list)


--Finding the maximmum element among all the elements with the given function's maximum value
greatest:: (a->Int) -> [a] -> a
greatest fun [] = error "Empty List has no maximum"
greatest fun (list) = do
						 let t = maximum (map (fun) list)
						 let f_list = filter (\x -> fun x == t) list
						 head f_list


 

--Custom data type of custom list
data List a = Empty | Cons a (List a) deriving (Show, Read, Eq, Ord)

--Converting haskell list to custom list
toList :: [a] -> List a
toList ([]) = Empty
toList (x:xs) =  x `Cons` toList(xs)

--converting custom list to haskell list
toHaskellList :: List a -> [a]
toHaskellList (Empty) = []
toHaskellList (Cons a b) = a:toHaskellList(b) 