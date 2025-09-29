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
// Hilo para escuchar solicitudes especiales del servidor
Thread hiloEscucha = new Thread(() -> {
    try {
        String mensaje;
        while ((mensaje = lector.readLine()) != null) {
            if (mensaje.startsWith("SOLICITUD_LISTA_ARCHIVOS:")) {
                manejarSolicitudListaArchivos();
            } else if (mensaje.startsWith("SOLICITUD_ARCHIVO:")) {
                String[] partes = mensaje.split(":");
                if (partes.length >= 3) {
                    manejarSolicitudArchivo(partes[1], partes[2]);
                }
            }
        }
    } catch (IOException e) {
        // Cliente desconectado
    }
});
hiloEscucha.setDaemon(true);
hiloEscucha.start();


            while (true) {
                // Opciones de acción
                System.out.println("=== BIENVENIDO AL SISTEMA ===");
System.out.println("Seleccione una opción:");
System.out.println("1. Registrarse");
System.out.println("2. Iniciar sesión");
System.out.println("3. Dar de baja usuario");
System.out.println("4. Salir");                     
System.out.print("Selecciona una opción (1, 2, 3 o 4): ");
                String opcion = teclado.readLine();
                escritor.println(opcion);
                    
                
                
                if (opcion.equals("5")) {
                    String respuesta = lector.readLine();
                    System.out.println(respuesta);
                    break; // Salir del programa
                }
                if (opcion.equals("4")) {
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
                // Para dar de baja, mostrar advertencia adicional
if (opcion.equals("3")) {
    System.out.println("\n️ ADVERTENCIA: Esta acción eliminará permanentemente tu cuenta y todos tus mensajes.");
    System.out.print("¿Estás seguro? Escribe 'CONFIRMAR' para continuar: ");
    String confirmacion = teclado.readLine();
    
    if (!confirmacion.equals("CONFIRMAR")) {
        System.out.println("Operación cancelada.");
        continue;
    }
}

                
                // Recibir mensaje del servidor
                String mensaje = lector.readLine();
                System.out.println("Servidor: " + mensaje);
                
                // Si el login fue exitoso (SOLO para opción 2)
if (opcion.equals("2") && mensaje.contains("Bienvenido al servidor")) {
                    
                    // Menú de mensajes
                    while (true) {
System.out.println("1. Ver bandeja de entrada");
System.out.println("2. Enviar mensaje");
System.out.println("3. Borrar mensaje");
System.out.println("4. Ver usuarios registrados");         
System.out.println("5. Cerrar sesión");
System.out.println("6. Listar archivos de otro cliente");
System.out.println("7. Transferir archivo de otro cliente");
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
    // Borrar mensaje
    System.out.println("\n=== BORRAR MENSAJE ===");
    String respuesta = lector.readLine();
    
    if (!respuesta.equals("No tienes mensajes para borrar.")) {
        System.out.println(respuesta);
        String siguienteMensaje;
        while ((siguienteMensaje = lector.readLine()) != null && 
               !siguienteMensaje.equals("FIN_LISTA_BORRAR")) {
            System.out.println(siguienteMensaje);
        }
        
        System.out.print("Número del mensaje a borrar: ");
        String numeroMensaje = teclado.readLine();
        escritor.println(numeroMensaje);
        
        String resultado = lector.readLine();
        System.out.println(resultado.contains("exitosamente") ? "✓ " + resultado : "✗ " + resultado);
    } else {
        System.out.println(respuesta);
    }
    
                        } else if (opcionMenu.equals("4")) {
    // Ver lista de usuarios
    System.out.println("\n=== LISTA DE USUARIOS ===");
    String respuesta;
    while ((respuesta = lector.readLine()) != null && 
           !respuesta.equals("FIN_LISTA_USUARIOS")) {
        System.out.println(respuesta);
    }
    } else if (opcionMenu.equals("6")) {
    // Listar archivos de otro cliente
    System.out.print("Nombre del cliente: ");
    String clienteObjetivo = teclado.readLine();
    escritor.println(clienteObjetivo);
    
    String respuesta = lector.readLine();
    if (respuesta.equals("ERROR_USUARIO_NO_EXISTE")) {
        System.out.println("✗ El usuario no existe.");
    } else if (respuesta.equals("ERROR_CLIENTE_NO_CONECTADO")) {
        System.out.println("✗ El cliente no está conectado.");
    }
    
} else if (opcionMenu.equals("7")) {
    // Transferir archivo de otro cliente
    System.out.print("Cliente origen: ");
    String clienteOrigen = teclado.readLine();
    escritor.println(clienteOrigen);
    
    System.out.print("Nombre del archivo: ");
    String archivo = teclado.readLine();
    escritor.println(archivo);
    
    String respuesta = lector.readLine();
    System.out.println(respuesta);
                             
                        } else if (opcionMenu.equals("5")) {
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
                
                // Si fue dar de baja exitoso
                if (opcion.equals("3") && mensaje.contains("dado de baja exitosamente")) {
                    System.out.println("Tu cuenta ha sido eliminada permanentemente.");
                    break;
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
    private static void manejarSolicitudListaArchivos() {
    System.out.println("\n[INFO] Otro cliente solicita la lista de tus archivos .txt");
    File directorio = new File(".");
    File[] archivos = directorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
    
    if (archivos != null && archivos.length > 0) {
        System.out.println("Archivos .txt encontrados:");
        for (File archivo : archivos) {
            System.out.println("- " + archivo.getName());
        }
    } else {
        System.out.println("No se encontraron archivos .txt");
    }
}

private static void manejarSolicitudArchivo(String nombreArchivo, String solicitante) {
    System.out.println("\n[INFO] " + solicitante + " solicita el archivo: " + nombreArchivo);
    File archivo = new File(nombreArchivo);
    
    if (archivo.exists() && archivo.isFile() && nombreArchivo.toLowerCase().endsWith(".txt")) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            System.out.println("Enviando contenido del archivo " + nombreArchivo + ":");
            String linea;
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
            System.out.println("[Archivo enviado exitosamente]");
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    } else {
        System.out.println("El archivo no existe o no es un archivo .txt");
    }
}
  
}