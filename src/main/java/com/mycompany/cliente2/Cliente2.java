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

            while (true) {
                // Opciones de acción
                System.out.println("=== BIENVENIDO AL SISTEMA ===");
                System.out.println("Seleccione una opción:");
                System.out.println("1. Registrarse");
                System.out.println("2. Iniciar sesión");
                System.out.println("3. Salir");
                System.out.print("Selecciona una opción (1, 2 o 3): ");
                String opcion = teclado.readLine();
                escritor.println(opcion);
                
                if (opcion.equals("3")) {
                    String respuesta = lector.readLine();
                    System.out.println(respuesta);
                    break; // Salir del programa
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
                
                // Si el login fue exitoso
                if (mensaje.contains("Bienvenido al servidor")) {
                    
                    // Menú de mensajes
                    while (true) {
                        System.out.println("\n=== MENÚ DE MENSAJES ===");
                        System.out.println("1. Ver bandeja de entrada");
                        System.out.println("2. Enviar mensaje");
                        System.out.println("3. Cerrar sesión");
                        System.out.print("Selecciona una opción: ");

                        String opcionMenu = teclado.readLine();
                        escritor.println(opcionMenu);

                        if (opcionMenu.equals("1")) {
                            // Ver bandeja de entrada
                            System.out.println("\n=== BANDEJA DE ENTRADA ===");
                            String respuesta = lector.readLine();
                            
                            if (respuesta.equals("0 mensajes.")) {
                                System.out.println("No tienes mensajes nuevos.");
                            } else {
                                System.out.println(respuesta);
                                // Leer mensajes adicionales si los hay
                                String siguienteMensaje;
                                while ((siguienteMensaje = lector.readLine()) != null && 
                                       !siguienteMensaje.equals("FIN_MENSAJES")) {
                                    System.out.println(siguienteMensaje);
                                }
                            }
                            
                        } else if (opcionMenu.equals("2")) {
                            // Enviar mensaje
                            System.out.print("Escribe el destinatario: ");
                            String destinatario = teclado.readLine();
                            escritor.println(destinatario);

                            System.out.print("Escribe tu mensaje: ");
                            String mensajeEnvio = teclado.readLine();
                            escritor.println(mensajeEnvio);

                            // Recibir confirmación de envío del mensaje
                            String respuesta = lector.readLine();
                            if (respuesta.contains("Mensaje enviado")) {
                                System.out.println("✓ " + respuesta);
                            } else {
                                System.out.println("✗ " + respuesta);
                            }
                            
                        } else if (opcionMenu.equals("3")) {
                            // Cerrar sesión
                            String respuesta = lector.readLine();
                            System.out.println(respuesta);
                            break; // Volver al menú principal
                            
                        } else {
                            String respuesta = lector.readLine();
                            System.out.println(respuesta);
                        }
                    }
                }
                // Si hay error en credenciales, el bucle principal continúa
            }

            // Cerrar recursos
            salida.close();
            System.out.println("Conexión cerrada.");
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}