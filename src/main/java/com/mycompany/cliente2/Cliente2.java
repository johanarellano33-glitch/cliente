package com.mycompany.cliente2;

import java.io.*;
import java.net.*;

public class Cliente2 {

    public static void main(String[] args) {
        try {
            // Conexión al servidor
            Socket salida = new Socket("localhost", 8080);
            PrintWriter escritor = new PrintWriter(salida.getOutputStream(), true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(salida.getInputStream()));
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            String cadena;

            while (true) {
                // Opciones de acción
                System.out.println("Bienvenido al sistema");
                System.out.println("Seleccione una opción:");
                System.out.println("1. Registrarse");
                System.out.println("2. Iniciar sesión");
                System.out.println("3. Salir");
                System.out.print("Selecciona una opción (1, 2 o 3): ");
                String opcion = teclado.readLine();
                escritor.println(opcion);
                
                if (opcion.equals("3")) {
                    break;
                }
                
                // Solicitar usuario y contraseña
                System.out.print("Usuario: ");
                String usuario = teclado.readLine();
                escritor.println(usuario);
                
                System.out.print("Contraseña: ");
                String contrasena = teclado.readLine();
                escritor.println(contrasena);
                
                // Recibir mensaje del servidor
                String mensaje = lector.readLine();
                System.out.println("Servidor: " + mensaje);
                
                // Si el cliente se logueó correctamente, le da la opción de salir
                if (mensaje.equals("Bienvenido al servidor, " + usuario + "!")) {
                    System.out.println("Escribe 'salir' para desconectarte.");
                    
                    // Mostrar bandeja de entrada
                    mostrarBandejaDeEntrada(lector);

                    // Enviar mensajes
                    enviarMensaje(escritor, teclado, lector);

                    while (!(cadena = teclado.readLine()).equalsIgnoreCase("salir")) {
                        System.out.println("Comando inválido, escribe 'salir' para desconectarte.");
                    }
                    System.out.println("Desconectando...");
                    break; // Termina el bucle y desconecta el cliente
                }
            }

            // Cerrar recursos
            salida.close();
            System.out.println("Conexión cerrada.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mostrar la bandeja de entrada del usuario
    private static void mostrarBandejaDeEntrada(BufferedReader lector) throws IOException {
        String mensaje;
        while (!(mensaje = lector.readLine()).equals("No tienes mensajes.") && !mensaje.equals("No tienes mensajes nuevos.")) {
            System.out.println(mensaje);
        }
    }

    // Enviar un mensaje a otro usuario
    private static void enviarMensaje(PrintWriter escritor, BufferedReader teclado, BufferedReader lector) throws IOException {
        System.out.println("Escribe el nombre de usuario al que deseas enviar un mensaje:");
        String destinatario = teclado.readLine();
        escritor.println(destinatario); // Enviar el nombre del destinatario al servidor

        System.out.println("Escribe el mensaje:");
        String mensaje = teclado.readLine();
        escritor.println(mensaje); // Enviar el mensaje al servidor

        // Esperar respuesta del servidor
        String respuesta = lector.readLine();
        System.out.println("Servidor: " + respuesta);
    }
}
