package restaurantenocturno;

import java.util.Scanner;

public class Validaciones {

    public static int leerEntero(Scanner entrada, String mensaje, int minimo, int maximo) {

        while (true) {

            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();

            try {
                int numero = Integer.parseInt(texto);

                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }

            } catch (NumberFormatException e) {
                // Ahi abajo sale el mensaje de error
            }

            System.out.println(
                    "No mi loc@ ingrese un numero entre "
                    + minimo + " y " + maximo + "."
            );
        }
    }

    public static boolean nombreValido(String nombre) {

        if (nombre.isEmpty()) {
            return false;
        }

        for (int i = 0; i < nombre.length(); i++) {

            char caracter = nombre.charAt(i);

            if (!Character.isLetter(caracter)&& caracter != ' ') {
                return false;
            }
        }

        return true;
    }

    public static boolean usuarioValido(String usuario) {

        if (usuario.length() < 4) {
            return false;
        }

        for (int i = 0; i < usuario.length(); i++) {

            char caracter = usuario.charAt(i);

            if (!Character.isLetterOrDigit(caracter)&& caracter != '_') {

                return false;
            }
        }

        return true;
    }

    public static boolean contrasenaValida(String contrasena) {

        // Este e para que ingrese una contraseña de ma de 8
        if (contrasena.length() <= 8) {
            return false;
        }

        boolean tieneLetra = false;
        boolean tieneNumero = false;

        for (int i = 0; i < contrasena.length(); i++) {

            char caracter = contrasena.charAt(i);

            if (Character.isLetter(caracter)) {tieneLetra = true;}

            if (Character.isDigit(caracter)) {tieneNumero = true;}
        }

        return tieneLetra && tieneNumero;
    }

    public static boolean telefonoValido(String telefono) {

        if (telefono.length() != 8) {
            return false;
        }

        for (int i = 0; i < telefono.length(); i++) {
            if (!Character.isDigit(telefono.charAt(i))) {
                return false;}
        }

        return true;
    }
}
