/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package restaurantenocturno;

import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 *
 * @author User
 */
public class RestauranteNocturno {

    /**
     * @param args the command line arguments
     */
    static Scanner entrada = new Scanner(System.in);

    static Usuario[] usuarios = new Usuario[50];
    static int cantidadUsuarios = 0;
    static Reserva[] reservas = new Reserva[200];
    static int cantidadReservas = 0;

    public static void main(String[] args) {

        prepararAlmacen();
        cargarUsuarios();
        cargarReservas();
        actualizarEstadosReservas();

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
                    System.out.println("Programa finalizado.");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 3);
    }

    // =========================
    // REGISTRO
    // =========================
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

        if (cantidadUsuarios >= usuarios.length) {

            System.out.println("No se pueden registrar mas usuarios.");
            return;
        }

        Usuario nuevo = new Usuario();

        // NOMBRE
        do {

            System.out.print("Nombre completo: ");
            nuevo.nombre = entrada.nextLine();

            if (!nombreValido(nuevo.nombre)) {
                System.out.println(
                        "Solo se permiten letras y espacios."
                );
            }

        } while (!nombreValido(nuevo.nombre));

        // USUARIO
        boolean disponible;

        do {

            disponible = true;

            System.out.print("Nombre de usuario: ");
            nuevo.usuario = entrada.nextLine();

            if (!usuarioValido(nuevo.usuario)) {

                System.out.println(
                        "Debe tener minimo 4 caracteres."
                );
                System.out.println(
                        "Solo letras, numeros y _"
                );

                disponible = false;

            } else {

                for (int i = 0; i < cantidadUsuarios; i++) {

                    if (usuarios[i].usuario.equals(nuevo.usuario)) {

                        System.out.println(
                                "Ese usuario ya existe."
                        );

                        disponible = false;
                        break;
                    }
                }
            }

        } while (!disponible);

        // CONTRASEÑA
        do {

            System.out.print("Contrasena: ");
            nuevo.contrasena = entrada.nextLine();

            if (!contrasenaValida(nuevo.contrasena)) {

                System.out.println(
                        "Debe tener minimo 8 caracteres,"
                );

                System.out.println(
                        "una letra y un numero."
                );
            }

        } while (!contrasenaValida(nuevo.contrasena));

        // TELEFONO
        do {

            System.out.print("Telefono: ");
            nuevo.telefono = entrada.nextLine();

            if (!telefonoValido(nuevo.telefono)) {

                System.out.println(
                        "Debe contener exactamente 8 numeros."
                );
            }

        } while (!telefonoValido(nuevo.telefono));

        // TIPO DE CUENTA
        if (tipoRegistro == 1) {

            nuevo.tipo = "CLIENTE";

        } else {

            System.out.print(
                    "Codigo especial de empleado: "
            );

            String codigo = entrada.nextLine();

            if (!codigo.equals("12398346")) {

                System.out.println(
                        "Codigo incorrecto. Registro cancelado."
                );

                return;
            }

            nuevo.tipo = "EMPLEADO";
        }

        usuarios[cantidadUsuarios] = nuevo;
        cantidadUsuarios++;

        guardarUsuarios();

        System.out.println(
                "Usuario registrado correctamente."
        );
    }

    // =========================
    // INICIO DE SESION
    // =========================
    public static void iniciarSesion() {

        System.out.println("\n=== INICIO DE SESION ===");

        System.out.print("Usuario: ");
        String usuario = entrada.nextLine();

        System.out.print("Contrasena: ");
        String contrasena = entrada.nextLine();

        for (int i = 0; i < cantidadUsuarios; i++) {

            if (usuarios[i].usuario.equals(usuario)
                    && usuarios[i].contrasena.equals(contrasena)) {

                System.out.println(
                        "\nBienvenido, " + usuarios[i].nombre
                );

                if (usuarios[i].tipo.equals("CLIENTE")) {

                    menuCliente(usuarios[i]);

                } else {

                    menuEmpleado(usuarios[i]);
                }

                return;
            }
        }

        System.out.println(
                "Usuario o contrasena incorrectos."
        );
    }

    // =========================
    // MENU CLIENTE
    // =========================
    public static void menuCliente(Usuario cliente) {

        int opcion;

        do {

            System.out.println("\n============================");
            System.out.println("       MENU DEL CLIENTE");
            System.out.println("============================");
            System.out.println("Cliente: " + cliente.nombre);
            System.out.println();
            System.out.println("1. Crear reservacion");
            System.out.println("2. Ver mi reservacion");
            System.out.println("3. Modificar mi reservacion");
            System.out.println("4. Cancelar mi reservacion");
            System.out.println("5. Cerrar sesion");
            System.out.print("Seleccione una opcion: ");

            opcion = entrada.nextInt();
            entrada.nextLine();

            switch (opcion) {

                case 1:
                    crearReservacion(cliente);
                    break;

                case 2:
                    verReservacion(cliente);
                    break;

                case 3:
                    modificarReservacion(cliente);
                    break;

                case 4:
                    cancelarReservacion(cliente);
                    break;

                case 5:
                    System.out.println("Sesion cerrada.");
                    break;

                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 5);
    }

    // =========================
    // MENU EMPLEADO
    // =========================
    public static void menuEmpleado(Usuario empleado) {

        int opcion;

        do {

            System.out.println("\n============================");
            System.out.println("      MENU DEL EMPLEADO");
            System.out.println("============================");
            System.out.println("Empleado: " + empleado.nombre);
            System.out.println();
            System.out.println("1. Ver reservaciones activas");
            System.out.println("2. Ver historial");
            System.out.println("3. Crear reservacion");
            System.out.println("4. Modificar reservacion");
            System.out.println("5. Cancelar reservacion");
            System.out.println("6. Cerrar sesion");
            System.out.print("Seleccione una opcion: ");

            opcion = entrada.nextInt();
            entrada.nextLine();

            switch (opcion) {

                case 1:
                    System.out.println(
                            "Ver reservaciones activas."
                    );
                    break;

                case 2:
                    System.out.println(
                            "Ver historial."
                    );
                    break;

                case 3:
                    System.out.println(
                            "Crear reservacion."
                    );
                    break;

                case 4:
                    System.out.println(
                            "Modificar reservacion."
                    );
                    break;

                case 5:
                    System.out.println(
                            "Cancelar reservacion."
                    );
                    break;

                case 6:
                    System.out.println(
                            "Sesion cerrada."
                    );
                    break;

                default:
                    System.out.println(
                            "Opcion no valida."
                    );
            }

        } while (opcion != 6);
    }

    // =========================
    // ARCHIVOS
    // =========================
    public static void prepararAlmacen() {

        File carpeta = new File("almacen");

        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        File archivo = new File(
                "almacen/usuarios.txt"
        );

        try {

            archivo.createNewFile();

        } catch (IOException e) {

            System.out.println(
                    "Error al preparar usuarios.txt."
            );
        }
    }

    public static void guardarUsuarios() {

        try {

            FileWriter escritor
                    = new FileWriter(
                            "almacen/usuarios.txt"
                    );

            for (int i = 0; i < cantidadUsuarios; i++) {

                escritor.write(
                        usuarios[i].nombre + "|"
                        + usuarios[i].usuario + "|"
                        + usuarios[i].contrasena + "|"
                        + usuarios[i].telefono + "|"
                        + usuarios[i].tipo + "\n"
                );
            }

            escritor.close();

        } catch (IOException e) {

            System.out.println(
                    "Error al guardar los usuarios."
            );
        }
    }

    public static void cargarUsuarios() {

        try {

            BufferedReader lector
                    = new BufferedReader(
                            new FileReader(
                                    "almacen/usuarios.txt"
                            )
                    );

            String linea;

            while ((linea = lector.readLine()) != null) {

                String[] datos
                        = linea.split("\\|");

                if (datos.length == 5) {

                    Usuario usuario = new Usuario();

                    usuario.nombre = datos[0];
                    usuario.usuario = datos[1];
                    usuario.contrasena = datos[2];
                    usuario.telefono = datos[3];
                    usuario.tipo = datos[4];

                    usuarios[cantidadUsuarios] = usuario;
                    cantidadUsuarios++;
                }
            }

            lector.close();

        } catch (IOException e) {

            System.out.println(
                    "Error al cargar usuarios."
            );
        }
    }

    // =========================
    // VALIDACIONES
    // =========================
    public static boolean nombreValido(String nombre) {

        if (nombre.trim().isEmpty()) {
            return false;
        }

        for (int i = 0; i < nombre.length(); i++) {

            char letra = nombre.charAt(i);

            if (!Character.isLetter(letra)
                    && letra != ' ') {

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

            if (!Character.isLetterOrDigit(caracter)
                    && caracter != '_') {

                return false;
            }
        }

        return true;
    }

    public static boolean contrasenaValida(String contrasena) {

        if (contrasena.length() < 8) {
            return false;
        }

        boolean letra = false;
        boolean numero = false;

        for (int i = 0;
                i < contrasena.length();
                i++) {

            char caracter
                    = contrasena.charAt(i);

            if (Character.isLetter(caracter)) {
                letra = true;
            }

            if (Character.isDigit(caracter)) {
                numero = true;
            }
        }

        return letra && numero;
    }

    public static boolean telefonoValido(String telefono) {

        if (telefono.length() != 8) {
            return false;
        }

        for (int i = 0;
                i < telefono.length();
                i++) {

            if (!Character.isDigit(
                    telefono.charAt(i))) {

                return false;
            }
        }

        return true;
    }
}
