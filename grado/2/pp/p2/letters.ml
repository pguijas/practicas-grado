

let uppercase c = if ((Char.code c < 91) && (Char.code c > 64)) (*si ya es una letra mayuscula*)
	then c 
	else if ((Char.code c > 191) && (Char.code c < 222)) 
		then c
		else char_of_int (Char.code c-32);;


let lowercase c = if ((Char.code c < 91) && (Char.code c > 64)) (*si es una letra mayuscula*)
	then char_of_int (Char.code c + 32) 
	else if ((Char.code c > 191) && (Char.code c < 222)) 
		then char_of_int (Char.code c + 32) 
		else c;;


