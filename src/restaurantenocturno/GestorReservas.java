package restaurantenocturno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

// Esta clase contiene toda la logica principal de las reservaciones e muy importante
public class GestorReservas {

    static final int MAX_RESERVAS = 500;
    static final int MAX_MESAS = 8;
    static final int MAX_PERSONAS = 4;
    static final int DIAS_MAXIMOS = 7;
    static final int HORAS_LIMITE_CLIENTE = 4;
    static final String ACTIVA = "ACTIVA";
    static final String CANCELADA_RESERVADOR = "CANCELADA_POR_RESERVADOR";
    static final String CANCELADA_EMPLEADO = "CANCELADA_POR_EMPLEADO";
    static final String NO_ACTIVA = "NO_ACTIVA";
    static Reserva[] reservas = new Reserva[MAX_RESERVAS];
    static int cantidadReservas = 0;

    // Carga las reservaciones guardadas y actualiza
    public static void iniciar() {
        cantidadReservas = Archivos.cargarReservas(reservas);
        actualizarEstados();
    }

    // Permite al cliente crear una reservacion si no tiene otra activa claro
    public static void crearReservaCliente(Usuario cliente, Scanner entrada) {
        actualizarEstados();
        if (buscarReservaActivaCliente(cliente.usuario) != null) {
            System.out.println("Ya tiene una reservacion activa");
            return;
        }
        crearReservaPara(cliente, entrada);
    }

    // Permite al empleado crear una reservacion para un cliente
    public static void crearReservaEmpleado(Usuario cliente, Scanner entrada) {
        actualizarEstados();
        if (buscarReservaActivaCliente(cliente.usuario) != null) {
            System.out.println("Ese cliente ya tiene una reservacion activa");
            return;
        }
        crearReservaPara(cliente, entrada);
    }

    // Crea y guarda una nueva reservacion con los datos elejidos
    public static void crearReservaPara(Usuario cliente, Scanner entrada) {
        if (cantidadReservas >= reservas.length) {
            System.out.println("No se pueden guardar mas reservaciones.");
            return;
        }
        System.out.println("\n=== NUEVA RESERVACION ===");
        LocalDate fecha = leerFechaReserva(entrada);
        int horaInicio = leerHorario(fecha, entrada);
        if (horaInicio == 0) {
            return;
        }
        int personas = Validaciones.leerEntero(entrada, "Cantidad de personas (1-4): ", 1, MAX_PERSONAS);
        if (!hayMesaDisponible(fecha, horaInicio, null)) {
            System.out.println("No quedan mesas disponibles.");
            return;
        }
        mostrarMesasDisponibles(fecha, horaInicio, null);
        int mesa;
        do {
            mesa = Validaciones.leerEntero(entrada, "Seleccione mesa (1-8): ", 1, MAX_MESAS);
            if (!mesaDisponible(fecha, horaInicio, mesa, null)) {
                System.out.println("Esa mesa ya esta ocupada.");
            }
        } while (!mesaDisponible(fecha, horaInicio, mesa, null));
        Reserva nueva = new Reserva();
        nueva.id = generarId();
        nueva.usuarioCliente = cliente.usuario;
        nueva.nombreCliente = cliente.nombre;
        nueva.fecha = fecha;
        nueva.mesa = mesa;
        nueva.horaInicio = horaInicio;
        nueva.personas = personas;
        nueva.estado = ACTIVA;
        reservas[cantidadReservas] = nueva;
        cantidadReservas++;
        guardar();
        System.out.println("Reservacion creada correctamente.");
        mostrarReserva(nueva);
    }

    // Muestra la reservacion activa del cliente a detalle
    public static void verReservaCliente(Usuario cliente) {
        actualizarEstados();
        Reserva reserva = buscarReservaActivaCliente(cliente.usuario);
        if (reserva == null) {
            System.out.println("No tiene una reservacion activa.");
            return;
        }
        mostrarReserva(reserva);
    }

    // Permite al cliente modificar su reservacion dentro del tiempo permitido que son 4 horas jeje
    public static void modificarReservaCliente(Usuario cliente, Scanner entrada) {
        actualizarEstados();
        Reserva reserva = buscarReservaActivaCliente(cliente.usuario);
        if (reserva == null) {
            System.out.println("No tiene una reservacion activa.");
            return;
        }
        LocalDateTime limite = fechaHoraInicio(reserva).minusHours(HORAS_LIMITE_CLIENTE);
        if (!LocalDateTime.now().isBefore(limite)) {
            System.out.println("Ya no puede modificar la reservacion.");
            System.out.println("El limite es 4 horas antes del inicio.");
            return;
        }
        modificarReserva(reserva, entrada);
    }

    // Cancela la reservacion activa del cliente 
    public static void cancelarReservaCliente(Usuario cliente, Scanner entrada) {
        actualizarEstados();
        Reserva reserva = buscarReservaActivaCliente(cliente.usuario);
        if (reserva == null) {
            System.out.println("No tiene una reservacion activa.");
            return;
        }
        mostrarReserva(reserva);
        System.out.print("Confirmar cancelacion (S/N): ");
        String respuesta = entrada.nextLine().trim();
        if (respuesta.equalsIgnoreCase("S")) {
            reserva.estado = CANCELADA_RESERVADOR;
            guardar();
            System.out.println("Reservacion cancelada.");
        } else {
            System.out.println("Cancelacion detenida.");
        }
    }

    // Muestra las reservaciones, solo las activas cabe aclarar
    public static void listarReservasActivas() {
        listarReservas(false);
    }

    // Muestra el historial completo de reservaciones, absolutamente todas
    public static void listarHistorial() {
        listarReservas(true);
    }

    // Lista las reservaciones segun la opcion seleccionada
    public static void listarReservas(boolean mostrarTodas) {
        actualizarEstados();
        boolean encontrada = false;
        if (mostrarTodas) {
            System.out.println("\n=== HISTORIAL DE RESERVACIONES ===");
        } else {
            System.out.println("\n=== RESERVACIONES ACTIVAS ===");
        }
        for (int i = 0; i < cantidadReservas; i++) {
            if (mostrarTodas || reservas[i].estado.equals(ACTIVA)) {
                mostrarReserva(reservas[i]);
                encontrada = true;
            }
        }
        if (!encontrada) {
            System.out.println("No hay reservaciones para mostrar.");
        }
    }

    // hace que el empleado pueda modificar una reservacion activa 
    public static void modificarReservaEmpleado(Scanner entrada) {
        Reserva reserva = pedirReservaActivaPorId(entrada);
        if (reserva != null) {
            modificarReserva(reserva, entrada);
        }
    }

    // deja que el trabajador cancele una reservacion activa.
    public static void cancelarReservaEmpleado(Scanner entrada) {
        Reserva reserva = pedirReservaActivaPorId(entrada);
        if (reserva == null) {
            return;
        }
        mostrarReserva(reserva);
        System.out.print("Confirmar cancelacion (S/N): ");
        String respuesta = entrada.nextLine().trim();
        if (respuesta.equalsIgnoreCase("S")) {
            reserva.estado = CANCELADA_EMPLEADO;
            guardar();
            System.out.println("Reservacion cancelada por empleado.");
        } else {
            System.out.println("Cancelacion detenida.");
        }
    }

    // son las opciones que se pueden modificar de una reservacioneichon
    public static void modificarReserva(Reserva reserva, Scanner entrada) {
        int opcion;
        do {
            System.out.println("\n=== MODIFICAR RESERVACION ===");
            mostrarReserva(reserva);
            System.out.println("1. Cambiar fecha");
            System.out.println("2. Cambiar horario");
            System.out.println("3. Cambiar mesa");
            System.out.println("4. Cambiar cantidad de personas");
            System.out.println("5. Volver");
            opcion = Validaciones.leerEntero(entrada, "Seleccione una opcion: ", 1, 5);
            switch (opcion) {
                case 1:
                    cambiarFecha(reserva, entrada);
                    break;
                case 2:
                    cambiarHorario(reserva, entrada);
                    break;
                case 3:
                    cambiarMesa(reserva, entrada);
                    break;
                case 4:
                    cambiarPersonas(reserva, entrada);
                    break;
                case 5:
                    break;
            }
        } while (opcion != 5);
    }

    // Cambia la fecha de una reservacion si sigue disponible:)
    public static void cambiarFecha(Reserva reserva, Scanner entrada) {
        LocalDate nuevaFecha = leerFechaReserva(entrada);
        if (nuevaFecha.equals(reserva.fecha)) {
            System.out.println("La fecha seleccionada es la misma.");
            return;
        }
        if (nuevaFecha.equals(LocalDate.now()) && !LocalTime.now().isBefore(LocalTime.of(reserva.horaInicio, 0))) {
            System.out.println("El horario de esa fecha ya comenzo.");
            return;
        }
        if (!mesaDisponible(nuevaFecha, reserva.horaInicio, reserva.mesa, reserva)) {
            System.out.println("La mesa actual esta ocupada " + "en esa fecha.");
            return;
        }
        reserva.fecha = nuevaFecha;
        guardar();
        System.out.println("Fecha actualizada.");
    }

    // deja cambiar el horario de una reservacion si sigue disponible tambien
    public static void cambiarHorario(Reserva reserva, Scanner entrada) {
        int nuevaHora = leerHorario(reserva.fecha, entrada);
        if (nuevaHora == 0) {
            return;
        }
        if (nuevaHora == reserva.horaInicio) {
            System.out.println("El horario seleccionado es el mismo.");
            return;
        }
        if (!mesaDisponible(reserva.fecha, nuevaHora, reserva.mesa, reserva)) {
            System.out.println("La mesa actual esta ocupada " + "en ese horario.");
            return;
        }
        reserva.horaInicio = nuevaHora;
        guardar();
        System.out.println("Horario actualizado.");
    }

    // Cambia la mesa asignada a la reservacion por si apetece otra
    public static void cambiarMesa(Reserva reserva, Scanner entrada) {
        mostrarMesasDisponibles(reserva.fecha, reserva.horaInicio, reserva);
        int nuevaMesa = Validaciones.leerEntero(entrada, "Nueva mesa (1-8): ", 1, MAX_MESAS);
        if (!mesaDisponible(reserva.fecha, reserva.horaInicio, nuevaMesa, reserva)) {
            System.out.println("Esa mesa esta ocupada.");
            return;
        }
        reserva.mesa = nuevaMesa;
        guardar();
        System.out.println("Mesa actualizada.");
    }

    // permite sacar y meter personas a una reservacion editandola
    public static void cambiarPersonas(Reserva reserva, Scanner entrada) {
        int personas = Validaciones.leerEntero(entrada, "Nueva cantidad de personas (1-4): ", 1, MAX_PERSONAS);
        reserva.personas = personas;
        guardar();
        System.out.println("Cantidad de personas actualizada.");
    }

    // busca una reservacion activa mediante su codigo, el r000 
    public static Reserva pedirReservaActivaPorId(Scanner entrada) {
        listarReservasActivas();
        if (!existenReservasActivas()) {
            return null;
        }
        System.out.print("Codigo de reservacion: ");
        String id = entrada.nextLine().trim();
        Reserva reserva = buscarReservaPorId(id);
        if (reserva == null || !reserva.estado.equals(ACTIVA)) {
            System.out.println("Reservacion activa no encontrada.");
            return null;
        }
        return reserva;
    }

    // Comprueba si existe al menos una reservacion activa si no hay pos no hay
    public static boolean existenReservasActivas() {
        for (int i = 0; i < cantidadReservas; i++) {
            if (reservas[i].estado.equals(ACTIVA)) {
                return true;
            }
        }
        return false;
    }

    // Busca la reservacion activa de un cliente.
    public static Reserva buscarReservaActivaCliente(String usuario) {
        for (int i = 0; i < cantidadReservas; i++) {
            if (reservas[i].usuarioCliente.equals(usuario) && reservas[i].estado.equals(ACTIVA)) {
                return reservas[i];
            }
        }
        return null;
    }

    // esta busca una reservacion por su codigo
    public static Reserva buscarReservaPorId(String id) {
        for (int i = 0; i < cantidadReservas; i++) {
            if (reservas[i].id.equalsIgnoreCase(id)) {
                return reservas[i];
            }
        }
        return null;
    }

    // E pa ver si esta disponible la mesa
    public static boolean mesaDisponible(LocalDate fecha, int horaInicio, int mesa, Reserva ignorar) {
        for (int i = 0; i < cantidadReservas; i++) {
            Reserva actual = reservas[i];
            if (actual == ignorar) {
                continue;
            }
            if (actual.estado.equals(ACTIVA) && actual.fecha.equals(fecha) && actual.horaInicio == horaInicio && actual.mesa == mesa) {
                return false;
            }
        }
        return true;
    }

    // Verifica si almenos una mesa esta disponible
    public static boolean hayMesaDisponible(LocalDate fecha, int horaInicio, Reserva ignorar) {
        for (int mesa = 1; mesa <= MAX_MESAS; mesa++) {
            if (mesaDisponible(fecha, horaInicio, mesa, ignorar)) {
                return true;
            }
        }
        return false;
    }

    //Da las mesas disponibles para una fecha y horario 
    public static void mostrarMesasDisponibles(LocalDate fecha, int horaInicio, Reserva ignorar) {
        System.out.println("\nMesas disponibles:");
        for (int mesa = 1; mesa <= MAX_MESAS; mesa++) {
            if (mesaDisponible(fecha, horaInicio, mesa, ignorar)) {
                System.out.println( "- Mesa " + mesa);
            }
        }
    }

    // Lee y valida la fecha elegida para reservar una reservacion 
    public static LocalDate leerFechaReserva(Scanner entrada) {
        LocalDate hoy = LocalDate.now();
        while (true) {
            System.out.println("Fecha actual: " + hoy);
            System.out.println("Puede reservar hasta: " + hoy.plusDays(DIAS_MAXIMOS));
            System.out.print("Fecha (AAAA-MM-DD): ");
            String texto = entrada.nextLine().trim();
            try {
                LocalDate fecha = LocalDate.parse(texto);
                if (fecha.isBefore(hoy)) {
                    System.out.println("No se permiten fechas pasadas.");
                } else if (fecha.isAfter(hoy.plusDays(DIAS_MAXIMOS))) {
                    System.out.println("Solo puede reservar hasta 7 dias.");
                } else {
                    return fecha;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Fecha invalida. " + "Ejemplo: 2026-09-20");
            }
        }
    }

    // ve y valida el horario de la reservacion
    public static int leerHorario(LocalDate fecha, Scanner entrada) {
        while (true) {
            System.out.println("1. 7:00 PM - 9:00 PM");
            System.out.println("2. 9:00 PM - 11:00 PM");
            System.out.println("3. Volver");
            int opcion = Validaciones.leerEntero(entrada, "Seleccione horario: ", 1, 3);
            if (opcion == 3) {
                return 0;
            }
            int horaInicio;
            if (opcion == 1) {
                horaInicio = 19;
            } else {
                horaInicio = 21;
            }
            if (fecha.equals(LocalDate.now()) && !LocalTime.now().isBefore(LocalTime.of(horaInicio, 0))) {
                System.out.println("Ese horario ya comenzo.");
            } else {
                return horaInicio;
            }
        }
    }

    // Actualiza automaticamente las reservaciones que ya finalizaron y las marca como no_activa
    public static void actualizarEstados() {
        LocalDateTime ahora = LocalDateTime.now();
        boolean huboCambio = false;
        for (int i = 0; i < cantidadReservas; i++) {
            Reserva reserva = reservas[i];
            if (reserva.estado.equals(ACTIVA)) {
                int horaFinal;
                if (reserva.horaInicio == 19) {
                    horaFinal = 21;
                } else {
                    horaFinal = 23;
                }
                LocalDateTime finalReserva = LocalDateTime.of(reserva.fecha, LocalTime.of(horaFinal, 0));
                if (!ahora.isBefore(finalReserva)) {
                    reserva.estado = NO_ACTIVA;
                    huboCambio = true;
                }
            }
        }
        if (huboCambio) {
            guardar();
        }
    }

    // Fusiona la fecha y hora de inicio de una reservacion
    public static LocalDateTime fechaHoraInicio(Reserva reserva) {
        return LocalDateTime.of(reserva.fecha, LocalTime.of(reserva.horaInicio, 0));
    }

    // Da un codigo especial de reservacion
    public static String generarId() {
        int mayor = 0;
        for (int i = 0; i < cantidadReservas; i++) {
            try {
                String numeroTexto = reservas[i].id.replace("R", "");
                int numero = Integer.parseInt(numeroTexto);
                if (numero > mayor) {
                    mayor = numero;
                }
            } catch (NumberFormatException e) {
            }
        }
        return String.format("R%03d", mayor + 1);
    }

    // muestra los datos principales de una reservacion
    public static void mostrarReserva(Reserva reserva) {
        System.out.println("\n----------------------------");
        System.out.println("Codigo: " + reserva.id);
        System.out.println("Cliente: " + reserva.nombreCliente + " (" + reserva.usuarioCliente + ")");
        System.out.println("Fecha: " + reserva.fecha);
        System.out.println("Horario: " + horarioTexto(reserva.horaInicio));
        System.out.println("Mesa: " + reserva.mesa);
        System.out.println("Personas: " + reserva.personas);
        System.out.println("Estado: " + reserva.estado);
        System.out.println("----------------------------");
    }

    // Convierte la hora guardada
    public static String horarioTexto(int horaInicio) {
        if (horaInicio == 19) {
            return "7:00 PM - 9:00 PM";
        }
        return "9:00 PM - 11:00 PM";
    }

    // Guarda las reservaciones actuales en el archivo y ya...
    public static void guardar() {
        Archivos.guardarReservas(reservas, cantidadReservas);
    }

}
