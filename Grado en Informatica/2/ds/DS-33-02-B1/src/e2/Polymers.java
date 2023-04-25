package e2;


public class Polymers {

    public static String processPolymer(String polymer) {
        //está bien el .tostring¿?¿
        if (polymer == null) {
            throw new IllegalArgumentException("parametro nulo");
        } else {
            int i;
            boolean cambio = true;
            StringBuilder poly = new StringBuilder(polymer);
            while (cambio) {
                cambio = false;
                i = 0;
                while (i < poly.length() - 1) { //0 - max-1  //ejecutar hasta que no haya cambio
                    if ((poly.toString().toLowerCase().charAt(i) == poly.toString().toLowerCase().charAt(i + 1)) && !(poly.charAt(i) == poly.charAt(i + 1))) {
                        poly.deleteCharAt(i);
                        poly.deleteCharAt(i);
                        cambio = true;
                    }
                    i++;
                }
            }
            return poly.toString();
        }
    }

    public static char minProcessedPolymer(String polymer) {

        if (polymer == null) {
            throw new IllegalArgumentException("parametro nulo");
        } else {
            if ((polymer.length() == 0)) {
                throw new IllegalArgumentException("string paramétrico vacío");
            } else {
                
                //podemos ir sacando strings de 2 formas: recorriendo el abecedario o mirando los 
                //caracteres que tiene el string polimero (me inclino por esta)
                
                StringBuilder aux = new StringBuilder("");  //string auxiliar donde introduciremos las letras
                char letra_min = polymer.charAt(0);
                int min = polymer.length();                 
                int j;
                        
                //vamos letra por letra y si no esta en el string auxiliar ejecutamos codigo
                for (int i = 0; i < polymer.length(); i++) {
                    String letra = String.valueOf(polymer.toLowerCase().charAt(i));
                    if (!aux.toString().contains(letra)) {  //lo pasamos a string para usar el metodo contains
                        aux.append(letra);
                        StringBuilder poly = new StringBuilder(polymer);
                       
                        //borramos el caracter letra mayuscula y minuscula
                        j = 0;
                        while (j < poly.length()) {
                            if (poly.charAt(j) == letra.charAt(0) || poly.charAt(j) == letra.toUpperCase().charAt(0)) {
                                poly.deleteCharAt(j);
                            } else {
                                j++;
                            }
                        }

                        //evaluar cual es el mas pequeño
                        if (min > Polymers.processPolymer(poly.toString()).length()) {
                            min = Polymers.processPolymer(poly.toString()).length();
                            letra_min = letra.charAt(0);
                        } else 
                        //en caso de empate ponemos la letra con orden alfabético mas bajo
                        if ((min == Polymers.processPolymer(poly.toString()).length()) && Character.compare(letra_min, letra.charAt(0)) < 0) { //empate
                            min = Polymers.processPolymer(poly.toString()).length();
                            letra_min = letra.charAt(0);
                        }
                    }
                }
                return letra_min;
            }
        }
    }

}
