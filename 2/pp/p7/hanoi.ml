let rec hanoi (o,a,d) n = 
	if n>0 then 
		hanoi (o,d,a) (n-1) @ (o,d) :: hanoi (a,o,d) (n-1)
	else []
;;