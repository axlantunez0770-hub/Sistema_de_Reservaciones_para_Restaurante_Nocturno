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
    static Usuario[] usuarios = new Usuario[50];
    static int cantidadUsuarios = 0;
    static Scanner entrada = new Scanner(System.in);

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
                    iniciarSesion();
                    break;
                case 2:
                    registrarUsuario();
                    break;

                case 3:
                    System.out.println("Programa finalizado");
                    break;

                default:
                    System.out.println("Opcion no valida");
            }

        } while (opcion != 3);

    }

    public static void registrarUsuario() {

        System.out.println("\n=== REGISTRO ===");
        System.out.println("1. Cliente");
        System.out.println("2. Empleado");
        System.out.println("3. Volver");
        System.out.print("Seleccione una opcion: ");

        int tipoRegistro = entrada.nextInt();
        entrada.nextLine();

        if (tipoRegistro == 3) {
            return;
        }

        if (tipoRegistro != 1 && tipoRegistro != 2) {
            System.out.println("Opcion no valida.");
            return;
        }

        Usuario nuevoUsuario = new Usuario();

        System.out.print("Nombre completo: ");
        nuevoUsuario.nombre = entrada.nextLine();

        System.out.print("Nombre de usuario: ");
        nuevoUsuario.usuario = entrada.nextLine();

        System.out.print("Contrasena: ");
        nuevoUsuario.contrasena = entrada.nextLine();

        System.out.print("Telefono: ");
        nuevoUsuario.telefono = entrada.nextLine();

        if (tipoRegistro == 1) {

            nuevoUsuario.tipo = "CLIENTE";

        } else {

            System.out.print("Ingrese el codigo especial de empleado: ");
            String codigo = entrada.nextLine();

            if (codigo.equals("12398346")) {
                nuevoUsuario.tipo = "EMPLEADO";
            } else {
                System.out.println("Codigo incorrecto. Registro cancelado.");
                return;
            }
        }

        for (int i = 0; i < cantidadUsuarios; i++) {

            if (usuarios[i].usuario.equals(nuevoUsuario.usuario)) {
                System.out.println("Ese nombre de usuario ya existe.");
                return;
            }
        }

        usuarios[cantidadUsuarios] = nuevoUsuario;
        cantidadUsuarios++;

        System.out.println("Usuario registrado correctamente.");
    }

    public static void iniciarSesion() {

        System.out.println("\n=== INICIO DE SESION ===");

        System.out.print("Usuario: ");
        String usuarioIngresado = entrada.nextLine();

        System.out.print("Contrasena: ");
        String contrasenaIngresada = entrada.nextLine();

        boolean encontrado = false;

        for (int i = 0; i < cantidadUsuarios; i++) {

            if (usuarios[i].usuario.equals(usuarioIngresado)
                    && usuarios[i].contrasena.equals(contrasenaIngresada)) {

                encontrado = true;

                System.out.println("Inicio de sesion correcto.");
                System.out.println("Bienvenido, " + usuarios[i].nombre);

                if (usuarios[i].tipo.equals("CLIENTE")) {
                    System.out.println("Tipo de cuenta: Cliente");
                } else {
                    System.out.println("Tipo de cuenta: Empleado");
                }

                break;
            }
        }

        if (!encontrado) {
            System.out.println("Usuario o contrasena incorrectos.");
        }
    }
}
