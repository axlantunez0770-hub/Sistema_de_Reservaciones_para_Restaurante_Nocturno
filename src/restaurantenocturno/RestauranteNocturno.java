package restaurantenocturno;

import java.util.Scanner;

// Maneja el acceso al sistema, el registro de usuarios y los menus principales y eso.
public class RestauranteNocturno {

    static final int MAX_USUARIOS = 100;
    // Ing. necesita esto para registrar un empleado
    static final String CODIGO_EMPLEADO = "12398346";
    static Scanner entrada = new Scanner(System.in);
    static Usuario[] usuarios = new Usuario[MAX_USUARIOS];
    static int cantidadUsuarios = 0;

    // Aqui se preparan los archivos, se cargan los usuarios y pone en marcha el sistema de reservas:v
    public static void main(String[] args) {
        Archivos.prepararAlmacen();
        cantidadUsuarios = Archivos.cargarUsuarios(usuarios);
        GestorReservas.iniciar();
        menuPrincipal();
    }

    public static void menuPrincipal() {
        int opcion;

        do {
            System.out.println("\n==============================");
            System.out.println(" RESTAURANTE NOCTURNO");
            System.out.println("==============================");
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");

            opcion = Validaciones.leerEntero(entrada, "Seleccione una opcion: ", 1, 3);
            
            //aqui puse las opciones del menu principal
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
            }
        } while (opcion != 3);
    }

    // Este registra clientes o empleados y guarda sus datos en el archivo de usuarios.
    public static void registrarUsuario() {
        if (cantidadUsuarios >= usuarios.length) {
            System.out.println("No se pueden registrar mas usuarios.");
            return;
        }

        System.out.println("\n=== REGISTRO ===");
        System.out.println("1. Cliente");
        System.out.println("2. Empleado");
        System.out.println("3. Volver");

        int tipoRegistro = Validaciones.leerEntero(entrada, "Seleccione una opcion: ", 1, 3);

        if (tipoRegistro == 3) {
            return;
        }

        Usuario nuevo = new Usuario();

        pedirNombre(nuevo);
        pedirNombreUsuario(nuevo);
        pedirContrasena(nuevo);
        pedirTelefono(nuevo);

        if (tipoRegistro == 1) {
            nuevo.tipo = "CLIENTE";
        } else {
            if (!validarCodigoEmpleado()) {
                return;
            }
            nuevo.tipo = "EMPLEADO";
        }

        usuarios[cantidadUsuarios] = nuevo;
        cantidadUsuarios++;
        Archivos.guardarUsuarios(usuarios, cantidadUsuarios);

        System.out.println("Usuario registrado correctamente.");
    }
    
    //Aqui se ingresa el nombre del usuario
    public static void pedirNombre(Usuario nuevo) {
        do {
            System.out.print("Nombre completo: ");
            nuevo.nombre = entrada.nextLine().trim();

            if (!Validaciones.nombreValido(nuevo.nombre)) {
                System.out.println("Solo se permiten letras y espacios.");
            }
        } while (!Validaciones.nombreValido(nuevo.nombre));
    }
    
    //en este se le pide el nombre del usuario 
    public static void pedirNombreUsuario(Usuario nuevo) {
        boolean disponible;

        do {
            disponible = true;
            System.out.print("Nombre de usuario: ");
            nuevo.usuario = entrada.nextLine().trim();

            if (!Validaciones.usuarioValido(nuevo.usuario)) {
                System.out.println("Minimo 4 caracteres. " + "Solo letras, numeros y _");
                disponible = false;
            } else if (buscarUsuario(nuevo.usuario) != null) {
                System.out.println("Ese nombre de usuario ya existe.");
                disponible = false;
            }
        } while (!disponible);
    }
    
    //aqui se le debe proporcionar la contraseña y que esta cumpla los parametros
    public static void pedirContrasena(Usuario nuevo) {
        do {
            System.out.print("Contrasena: ");
            nuevo.contrasena = entrada.nextLine();

            if (!Validaciones.contrasenaValida(nuevo.contrasena)) {
                System.out.println("Debe tener mas de 8 caracteres, " + "al menos una letra y un numero.");
            }
        } while (!Validaciones.contrasenaValida(nuevo.contrasena));
    }
    
    //sirve para que el usuario proorcione su numero de telefono
    public static void pedirTelefono(Usuario nuevo) {
        do {
            System.out.print("Telefono: ");
            nuevo.telefono = entrada.nextLine().trim();

            if (!Validaciones.telefonoValido(nuevo.telefono)) {
                System.out.println("El telefono debe contener " + "exactamente 8 numeros.");
            }
        } while (!Validaciones.telefonoValido(nuevo.telefono));
    }
    
    //aqui se valida si el codigo exclusivo del empleado es correcto
    public static boolean validarCodigoEmpleado() {
        System.out.print("Codigo especial de empleado: ");
        String codigo = entrada.nextLine().trim();

        if (!codigo.equals(CODIGO_EMPLEADO)) {
            System.out.println("Codigo incorrecto. Registro cancelado.");
            return false;
        }

        return true;
    }

    // verifica las credenciales y envia al usuario al menu correspondiente a su tipo de cuenta
    public static void iniciarSesion() {
        System.out.println("\n=== INICIO DE SESION ===");
        System.out.print("Usuario: ");
        String usuario = entrada.nextLine().trim();

        System.out.print("Contrasena: ");
        String contrasena = entrada.nextLine();

        Usuario encontrado = autenticarUsuario(usuario, contrasena);

        if (encontrado == null) {
            System.out.println("Usuario o contrasena incorrectos.");
            return;
        }

        System.out.println("Bienvenido, " + encontrado.nombre);

        if (encontrado.tipo.equals("CLIENTE")) {
            menuCliente(encontrado);
        } else {
            menuEmpleado(encontrado);
        }
    }
    //autentifica el usuario
    public static Usuario autenticarUsuario(String usuario, String contrasena) {
        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].usuario.equals(usuario) && usuarios[i].contrasena.equals(contrasena)) {
                return usuarios[i];
            }
        }

        return null;
    }

    // Menu con las opciones que puede realizar un cliente
    public static void menuCliente(Usuario cliente) {
        int opcion;

        do {
            GestorReservas.actualizarEstados();

            System.out.println("\n=== MENU DEL CLIENTE ===");
            System.out.println("1. Crear reservacion");
            System.out.println("2. Ver mi reservacion");
            System.out.println("3. Modificar mi reservacion");
            System.out.println("4. Cancelar mi reservacion");
            System.out.println("5. Cerrar sesion");

            opcion = Validaciones.leerEntero(entrada, "Seleccione una opcion: ", 1, 5);

            switch (opcion) {
                case 1:
                    GestorReservas.crearReservaCliente(cliente, entrada);
                    break;
                case 2:
                    GestorReservas.verReservaCliente(cliente);
                    break;
                case 3:
                    GestorReservas.modificarReservaCliente(cliente, entrada);
                    break;
                case 4:
                    GestorReservas.cancelarReservaCliente(cliente, entrada);
                    break;
                case 5:
                    System.out.println("Sesion cerrada.");
                    break;
            }
        } while (opcion != 5);
    }

    // Menu del empleado para consultar y administrar las reservaciones del sistema
    public static void menuEmpleado(Usuario empleado) {
        int opcion;

        do {
            GestorReservas.actualizarEstados();

            System.out.println("\n=== MENU DEL EMPLEADO ===");
            System.out.println("Empleado: " + empleado.nombre);
            System.out.println("1. Ver reservaciones activas");
            System.out.println("2. Ver historial completo");
            System.out.println("3. Crear reservacion para cliente");
            System.out.println("4. Modificar reservacion");
            System.out.println("5. Cancelar reservacion");
            System.out.println("6. Cerrar sesion");

            opcion = Validaciones.leerEntero(entrada, "Seleccione una opcion: ", 1, 6);

            switch (opcion) {
                case 1:
                    GestorReservas.listarReservasActivas();
                    break;
                case 2:
                    GestorReservas.listarHistorial();
                    break;
                case 3:
                    crearReservaEmpleado();
                    break;
                case 4:
                    GestorReservas.modificarReservaEmpleado(entrada);
                    break;
                case 5:
                    GestorReservas.cancelarReservaEmpleado(entrada);
                    break;
                case 6:
                    System.out.println("Sesion cerrada.");
                    break;
            }
        } while (opcion != 6);
    }

    // Permite al empleado crear una reservacion para un cliente registrado
    public static void crearReservaEmpleado() {
        System.out.print("Usuario del cliente: ");
        String nombreUsuario = entrada.nextLine().trim();
        Usuario cliente = buscarUsuario(nombreUsuario);

        if (cliente == null || !cliente.tipo.equals("CLIENTE")) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        GestorReservas.crearReservaEmpleado(cliente, entrada);
    }
    
    public static Usuario buscarUsuario(String nombreUsuario) {
        for (int i = 0; i < cantidadUsuarios; i++) {
            if (usuarios[i].usuario.equals(nombreUsuario)) {
                return usuarios[i];
            }
        }

        return null;
    }
}
