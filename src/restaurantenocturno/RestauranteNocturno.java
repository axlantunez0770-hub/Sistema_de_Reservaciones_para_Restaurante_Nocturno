/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package restaurantenocturno;

import java.util.Scanner;

/**
 *
 * @author User
 */
public class RestauranteNocturno {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);

        int opcion;

        do {

            System.out.println("\n==============================");
            System.out.println("     RESTAURANTE NOCTURNO");
            System.out.println("==============================");
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opcion: ");

            opcion = entrada.nextInt();
            entrada.nextLine();

            switch (opcion) {

                case 1:
                    System.out.println("Inicio de sesion");
                    break;

                case 2:
                    System.out.println("Registro de usuario");
                    break;

                case 3:
                    System.out.println("Programa finalizado.");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 3);

        entrada.close();
    }
}

