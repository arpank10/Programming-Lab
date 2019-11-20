m []= 0
m (list)=  product (map (sum) list)

greatest fun (list) = do
						 let t = maximum (map (fun) list)
						 let f_list = filter (\x -> fun x == t) list
						 print(head f_list)


 

data List a = Empty | Cons a (List a) deriving (Show, Read, Eq, Ord)

toList :: [a] -> List a

toList ([]) = Empty
toList (x:xs) =  x `Cons` toList(xs)

toHaskellList :: List a -> [a]

toHaskellList (Empty) = []

toHaskellList (Cons a b) = a:toHaskellList(b) 