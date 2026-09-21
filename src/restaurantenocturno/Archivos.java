package restaurantenocturno;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

// Esta clase se encarga de crear, guardar y cargar los archivos ya saben
public class Archivos {

    // Aqui preparo la carpeta almacen y los archivos que voy a utilizar
    public static void prepararAlmacen() {

        File carpeta = new File("almacen");
        
        // Si la carpeta no existe la creo automaticamente
        if (!carpeta.exists()) {carpeta.mkdir();}

        crearArchivo("almacen/usuarios.txt");
        crearArchivo("almacen/reservaciones.txt");
    }

    // Este metodo crea el archivo si todavia no existe
    public static void crearArchivo(String ruta) {

        try {File archivo = new File(ruta);archivo.createNewFile();}

        catch (IOException e) {System.out.println("No se pudo preparar el archivo: " + ruta);}
    }

    // Aqui guardo todos los usuarios registrados dentro del archivo usuarios.txt
    public static void guardarUsuarios(Usuario[] usuarios, int cantidadUsuarios) {

        try {FileWriter escritor =new FileWriter("almacen/usuarios.txt");

            for (int i = 0; i < cantidadUsuarios; i++) {


                escritor.write( usuarios[i].nombre + "|"+ usuarios[i].usuario + "|"+ usuarios[i].contrasena + "|"+ usuarios[i].telefono + "|"+ usuarios[i].tipo + "\n");
            }


        } catch (IOException e) {
            System.out.println("Error al guardar los usuarios.");
        }
    }

    // Este metodo carga en el arreglo los usuarios que ya estaban guardados
    public static int cargarUsuarios(Usuario[] usuarios) {

        int cantidad = 0;

        try {BufferedReader lector = new BufferedReader(new FileReader("almacen/usuarios.txt"));

            String linea;

            while ((linea = lector.readLine()) != null && cantidad < usuarios.length) {

                String[] datos = linea.split("\\|", -1);

                if (datos.length == 5) {

                    Usuario usuario = new Usuario();
                    usuario.nombre = datos[0];
                    usuario.usuario = datos[1];
                    usuario.contrasena = datos[2];
                    usuario.telefono = datos[3];
                    usuario.tipo = datos[4];

                    usuarios[cantidad] = usuario;

                    cantidad++;
                }
            }

        } catch (IOException e) {
            System.out.println("Error al cargar los usuarios.");
        }

        return cantidad;
    }

    // Aqui guardo todas las reservaciones en su archivo 
    public static void guardarReservas(Reserva[] reservas, int cantidadReservas) {

        try {FileWriter escritor = new FileWriter("almacen/reservaciones.txt");

            // ete e para recorrer todas las reservaciones correspondientes
            for (int i = 0; i < cantidadReservas; i++) {

                escritor.write(
                        reservas[i].id + "|"
                        + reservas[i].usuarioCliente + "|"
                        + reservas[i].nombreCliente + "|"
                        + reservas[i].fecha + "|"
                        + reservas[i].mesa + "|"
                        + reservas[i].horaInicio + "|"
                        + reservas[i].personas + "|"
                        + reservas[i].estado + "\n"
                );
            }

        } catch (IOException e) {System.out.println("Error al guardar las reservaciones.");}
    }

    // Este carga todas las reservaciones guardadas anteriormente
    public static int cargarReservas(Reserva[] reservas) {

        int cantidad = 0;

        try {BufferedReader lector =new BufferedReader(new FileReader("almacen/reservaciones.txt"));

            String linea;

            while ((linea = lector.readLine()) != null && cantidad < reservas.length) {

                String[] datos = linea.split("\\|", -1);

                if (datos.length == 8) {

                    try {
                        Reserva reserva = new Reserva();
                        reserva.id = datos[0];
                        reserva.usuarioCliente = datos[1];
                        reserva.nombreCliente = datos[2];
                        reserva.fecha = LocalDate.parse(datos[3]);
                        reserva.mesa = Integer.parseInt(datos[4]);
                        reserva.horaInicio = Integer.parseInt(datos[5]);
                        reserva.personas = Integer.parseInt(datos[6]);
                        reserva.estado = datos[7];

                        reservas[cantidad] = reserva;

                        cantidad++;

                    } catch (RuntimeException e) {System.out.println("no se encontro una reservacion dañada.");
                    }
                }
            }



        } catch (IOException e) {System.out.println("Error al cargar las reservaciones.");}

        return cantidad;
    }
}