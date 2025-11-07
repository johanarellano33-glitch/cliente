package com.mycompany.cliente2;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Cliente2 {
    private static PrintWriter escritor;
    private static BufferedReader lector;
    // Usaremos un Scanner local para sincronizar el acceso a la consola
    private static final Scanner CONSOLA = new Scanner(System.in); 
    private static final Semaphore SEMAFORO_LECTURA = new Semaphore(1); // Controla el acceso a la consola

    private static String usuarioActual = null;

    public static void main(String[] args) {
        try {
            Socket salida = new Socket("localhost", 8080); 
            escritor = new PrintWriter(salida.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(salida.getInputStream()));

            Thread hiloEscucha = new Thread(() -> escucharServidor());
            hiloEscucha.setDaemon(true);
            hiloEscucha.start();

            while (true) {
                // Adquirir el semáforo antes de cualquier interacción del menú
                SEMAFORO_LECTURA.acquire(); 
                
                System.out.println("\n=== BIENVENIDO AL SISTEMA ===");
                System.out.println("1. Registrarse");
                System.out.println("2. Iniciar sesion");
                System.out.println("3. Dar de baja usuario");
                System.out.println("4. Salir");
                System.out.print("Selecciona una opcion: ");
                
                String opcion = CONSOLA.nextLine();
                
                SEMAFORO_LECTURA.release(); // Liberar el semáforo después de la lectura

                // Opción 4 (Salir) se maneja inmediatamente
                if (opcion.equals("4")) {
                    escritor.println(opcion);
                    String respuesta = lector.readLine();
                    System.out.println(respuesta);
                    break;
                }
                
                if (!opcion.matches("[1-3]")) {
                     System.out.println("Opcion invalida. Intenta de nuevo.");
                     continue;
                }
                
                escritor.println(opcion);

                // Lectura de credenciales (usando el Semáforo)
                SEMAFORO_LECTURA.acquire();
                System.out.print("Usuario: ");
                String usuario = CONSOLA.nextLine();
                System.out.print("Contrasena: ");
                String contrasena = CONSOLA.nextLine();
                SEMAFORO_LECTURA.release();
                
                escritor.println(usuario);
                escritor.println(contrasena);

                if (opcion.equals("3")) {
                    SEMAFORO_LECTURA.acquire();
                    System.out.println("\nADVERTENCIA: Esta accion eliminara permanentemente tu cuenta.");
                    System.out.print("Estas seguro? Escribe 'CONFIRMAR': ");
                    String confirmacion = CONSOLA.nextLine();
                    SEMAFORO_LECTURA.release();
                    
                    if (!confirmacion.equals("CONFIRMAR")) {
                        escritor.println("CANCELAR"); 
                        System.out.println("Operacion cancelada.");
                        continue;
                    }
                    escritor.println("CONFIRMAR");
                }

                String mensaje = lector.readLine();
                System.out.println("Servidor: " + mensaje);

                if (opcion.equals("2") && mensaje.contains("Bienvenido al servidor")) {
                    usuarioActual = usuario;
                    menuPrincipal();
                }

                if (opcion.equals("3") && mensaje.contains("dado de baja exitosamente")) {
                    break;
                }
            }

            salida.close();
            System.out.println("Conexion cerrada.");
        } catch (Exception e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        } finally {
            if (CONSOLA != null) {
                // CONSOLA.close(); // closing System.in is generally discouraged
            }
        }
    }

    private static void menuPrincipal() {
        try {
            boolean sesionActiva = true;
            while (sesionActiva) {
                // Adquirir el semáforo antes de cualquier interacción del menú
                SEMAFORO_LECTURA.acquire(); 
                
                imprimirMenuPrincipal();

                String opcion = CONSOLA.nextLine();
                SEMAFORO_LECTURA.release(); // Liberar el semáforo después de la lectura

                if (opcion == null) break;

                switch (opcion) {
                    case "1":
                        escritor.println(opcion);
                        verBandejaEntrada();
                        break;
                    case "2":
                        escritor.println(opcion);
                        enviarMensaje();
                        break;
                    case "3":
                        escritor.println(opcion);
                        borrarMensaje();
                        break;
                    case "4":
                        escritor.println(opcion);
                        verUsuarios();
                        break;
                    case "5":
                        menuArchivos();
                        break;
                    case "6":
                        escritor.println("8"); 
                        String respuesta = lector.readLine();
                        System.out.println(respuesta);
                        sesionActiva = false;
                        usuarioActual = null;
                        break;
                    default:
                        System.out.println("Opcion invalida");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en menu principal: " + e.getMessage());
        }
    }
    
    // Método auxiliar para imprimir el menú principal
    private static void imprimirMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Ver bandeja de entrada");
        System.out.println("2. Enviar mensaje");
        System.out.println("3. Borrar mensaje");
        System.out.println("4. Ver usuarios registrados");
        System.out.println("5. Gestion de archivos");
        System.out.println("6. Cerrar sesion");
        System.out.print("Selecciona una opcion: ");
    }

    private static void menuArchivos() {
        try {
            boolean enMenuArchivos = true;
            while (enMenuArchivos) {
                SEMAFORO_LECTURA.acquire();
                
                imprimirMenuArchivos();

                String opcion = CONSOLA.nextLine();
                SEMAFORO_LECTURA.release(); 
                
                if (opcion == null) break;

                switch (opcion) {
                    case "1":
                        listarMisArchivos();
                        break;
                    case "2":
                        crearArchivo();
                        break;
                    case "3":
                        editarArchivo();
                        break;
                    case "4":
                        borrarArchivo();
                        break;
                    case "5":
                        escritor.println("6"); 
                        listarArchivosOtroCliente();
                        break;
                    case "6":
                        escritor.println("7"); 
                        transferirArchivo();
                        break;
                    case "7":
                        enMenuArchivos = false;
                        break;
                    default:
                        System.out.println("Opcion invalida");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en menu de archivos: " + e.getMessage());
        }
    }
    
    // Método auxiliar para imprimir el menú de archivos
    private static void imprimirMenuArchivos() {
        System.out.println("\n=== GESTION DE ARCHIVOS ===");
        System.out.println("1. Listar mis archivos .txt (Local)");
        System.out.println("2. Crear archivo .txt (Local)");
        System.out.println("3. Editar archivo .txt (Local)");
        System.out.println("4. Borrar archivo .txt (Local)");
        System.out.println("5. Listar archivos de otro cliente (Red)");
        System.out.println("6. Transferir archivo de otro cliente (Red)");
        System.out.println("7. Volver al menu principal");
        System.out.print("Selecciona una opcion: ");
    }

    // === MÉTODOS DE ARCHIVOS LOCALES (Se asume que usan SEMAFORO_LECTURA dentro si necesitan input) ===
    
    private static void listarMisArchivos() {
        System.out.println("\n=== MIS ARCHIVOS .TXT ===");
        File directorio = new File(".");
        File[] archivos = directorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (archivos != null && archivos.length > 0) {
            for (int i = 0; i < archivos.length; i++) {
                System.out.println((i + 1) + ". " + archivos[i].getName() + 
                                 " (" + archivos[i].length() + " bytes)");
            }
        } else {
            System.out.println("No tienes archivos .txt");
        }
    }

    private static void crearArchivo() {
        try {
            SEMAFORO_LECTURA.acquire();
            System.out.print("Nombre del archivo (sin extension): ");
            String nombre = CONSOLA.nextLine();
            
            File archivo = new File(nombre + ".txt");

            if (archivo.exists()) {
                System.out.println("El archivo ya existe.");
                SEMAFORO_LECTURA.release();
                return;
            }

            System.out.println("Escribe el contenido (escribe 'FIN' en una linea para terminar):");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
                String linea;
                while (!(linea = CONSOLA.nextLine()).equals("FIN")) {
                    writer.write(linea);
                    writer.newLine();
                }
            }
            System.out.println("Archivo creado exitosamente");
            SEMAFORO_LECTURA.release();
        } catch (Exception e) {
            // Asegurar que se libere el semáforo si ocurre una excepción
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
            System.out.println("Error al crear archivo: " + e.getMessage());
        }
    }

    private static void editarArchivo() {
        try {
            listarMisArchivos();
            SEMAFORO_LECTURA.acquire();
            System.out.print("Nombre del archivo a editar: ");
            String nombre = CONSOLA.nextLine();
            SEMAFORO_LECTURA.release();
            
            if (nombre == null) return;
            if (!nombre.endsWith(".txt")) nombre += ".txt";

            File archivo = new File(nombre);
            if (!archivo.exists()) {
                System.out.println("El archivo no existe.");
                return;
            }

            System.out.println("Contenido actual:");
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    System.out.println(linea);
                }
            }

            SEMAFORO_LECTURA.acquire();
            System.out.println("\nNuevo contenido (escribe 'FIN' para terminar):");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, false))) { 
                String linea;
                while (!(linea = CONSOLA.nextLine()).equals("FIN")) {
                    writer.write(linea);
                    writer.newLine();
                }
            }
            System.out.println("Archivo editado exitosamente");
            SEMAFORO_LECTURA.release();
        } catch (Exception e) {
            System.out.println("Error al editar archivo: " + e.getMessage());
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
        }
    }

    private static void borrarArchivo() {
        try {
            listarMisArchivos();
            SEMAFORO_LECTURA.acquire();
            System.out.print("Nombre del archivo a borrar: ");
            String nombre = CONSOLA.nextLine();
            SEMAFORO_LECTURA.release();
            
            if (nombre == null) return;
            if (!nombre.endsWith(".txt")) nombre += ".txt";

            File archivo = new File(nombre);
            if (archivo.exists() && archivo.delete()) {
                System.out.println("Archivo borrado exitosamente");
            } else {
                System.out.println("Error al borrar el archivo o el archivo no existe.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
        }
    }
    
    // === MÉTODOS DE ARCHIVOS DE RED ===

    private static void listarArchivosOtroCliente() {
        try {
            SEMAFORO_LECTURA.acquire();
            System.out.print("Nombre del cliente a listar: ");
            String cliente = CONSOLA.nextLine();
            SEMAFORO_LECTURA.release();
            
            if (cliente == null) return;
            
            escritor.println(cliente);

            String respuesta = lector.readLine();
            
            if (respuesta == null) throw new IOException("Conexion terminada.");

            if (respuesta.startsWith("ERROR")) {
                System.out.println("Error: " + respuesta.substring(6));
            } else if (respuesta.startsWith("OK")) {
                System.out.println("Solicitud enviada a " + cliente + ". Esperando respuesta...");
            } else {
                 System.out.println("Respuesta desconocida del servidor: " + respuesta);
            }
            
        } catch (Exception e) {
            System.out.println("Error al solicitar lista de archivos: " + e.getMessage());
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
        }
    }

    private static void transferirArchivo() {
        try {
            SEMAFORO_LECTURA.acquire();
            System.out.print("Cliente origen: ");
            String clienteOrigen = CONSOLA.nextLine();
            escritor.println(clienteOrigen);
            
            System.out.print("Nombre del archivo: ");
            String nombreArchivo = CONSOLA.nextLine();
            escritor.println(nombreArchivo);
            SEMAFORO_LECTURA.release();
            
            if (clienteOrigen == null || nombreArchivo == null) return;

            String respuesta = lector.readLine();

            if (respuesta == null) throw new IOException("Conexion terminada.");

            if (respuesta.startsWith("ERROR")) {
                System.out.println("Error: " + respuesta.substring(6));
            } else if (respuesta.startsWith("OK")) {
                System.out.println("Solicitud de transferencia enviada. Esperando respuesta...");
            } else {
                System.out.println("Respuesta desconocida del servidor: " + respuesta);
            }
            
        } catch (Exception e) {
            System.out.println("Error al solicitar transferencia: " + e.getMessage());
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
        }
    }
    
    // === HILO DE ESCUCHA ASÍNCRONO ===

    private static void escucharServidor() {
        try {
            while (true) {
                if (lector.ready()) {
                    String mensaje = lector.readLine();
                    
                    if (mensaje == null) break; 
                    
                    if (usuarioActual != null) {
                        if (mensaje.startsWith("LISTAR_ARCHIVOS_REQUEST:")) {
                            manejarSolicitudListaArchivos(mensaje);
                        } else if (mensaje.startsWith("SOLICITUD_ARCHIVO:")) {
                            manejarSolicitudArchivo(mensaje);
                        }
                        else if (mensaje.equals("RESPUESTA_ARCHIVOS_INICIO")) {
                            manejarRespuestaListaArchivos();
                        } else if (mensaje.equals("INICIO_TRANSFERENCIA")) {
                            manejarRespuestaTransferencia();
                        } else if (mensaje.startsWith("ERROR:") && mensaje.contains("TRANSFERENCIA")) {
                             System.out.println("\n[ERROR DE TRANSFERENCIA ASÍNCRONA]: " + mensaje.substring(6));
                        }
                    }
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
             System.err.println("\n[ERROR EN HILO ESCUCHA]: " + e.getMessage());
        }
    }
    
    private static void manejarRespuestaListaArchivos() {
    try {
        System.out.println("\n\n=== ARCHIVOS RECIBIDOS DE OTRO CLIENTE ==="); // <-- Encabezado
        String linea;
        
        while ((linea = lector.readLine()) != null) {
            if (linea.equals("RESPUESTA_ARCHIVOS_FIN")) {
                break;
            } else if (linea.startsWith("ARCHIVO:")) {
                System.out.println("- " + linea.substring(8)); // <-- Impresión de cada archivo
            } else if (linea.equals("SOLICITUD_RECHAZADA")) {
                System.out.println("El usuario rechazó tu solicitud.");
                break;
            }
        }
        
        System.out.println("========================================\n");
        
    } catch (IOException e) {
        System.err.println("Error al recibir lista de archivos: " + e.getMessage());
    }
}
    private static void manejarRespuestaTransferencia() {
        try {
            System.out.println("\n\n=== RECIBIENDO ARCHIVO ===");
            String nombreArchivo = "recibido_" + System.currentTimeMillis() + ".txt";
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
                String linea;
                while (!(linea = lector.readLine()).equals("FIN_TRANSFERENCIA")) {
                    if (linea.startsWith("ERROR")) {
                        System.out.println("Error durante la transferencia: " + linea);
                        return;
                    }
                    writer.write(linea);
                    writer.newLine(); 
                }
            }
            
            System.out.println("Archivo guardado como: " + nombreArchivo);
            System.out.println("===========================\n");
            
        } catch (IOException e) {
            System.err.println("Error al recibir archivo: " + e.getMessage());
        }
    }
    
    private static void manejarSolicitudListaArchivos(String mensaje) {
        try {
            String[] partes = mensaje.split(":");
            String solicitante = partes.length > 1 ? partes[1] : "desconocido";

            // 1. Bloquear la consola y obtener la entrada
            SEMAFORO_LECTURA.acquire(); 
            
            System.out.println("\n\n========================================");
            System.out.println("[SOLICITUD] " + solicitante + " quiere ver tus archivos");
            System.out.println("========================================");
            System.out.print("Permitir? (s/n): ");
            
            String decision = CONSOLA.nextLine();
            
            // 2. Enviar respuesta al servidor (que luego la reenviará al solicitante)
            escritor.println("RESPUESTA_ARCHIVOS:" + solicitante); 
            
            if (decision.equalsIgnoreCase("s")) {
                File directorio = new File(".");
                File[] archivos = directorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

                if (archivos != null && archivos.length > 0) {
                    for (File archivo : archivos) {
                        escritor.println("ARCHIVO:" + archivo.getName());
                    }
                } else {
                    escritor.println("NO_ARCHIVOS");
                }
                escritor.println("FIN_LISTA_ARCHIVOS");
                System.out.println("Lista enviada exitosamente\n");
            } else {
                escritor.println("SOLICITUD_RECHAZADA");
                escritor.println("FIN_LISTA_ARCHIVOS");
                System.out.println("Solicitud rechazada\n");
            }
            
            // 3. Volver a imprimir el menú para que el usuario sepa dónde está
            imprimirMenuPrincipal();
            
            // 4. Liberar el semáforo para que el hilo principal pueda leer la siguiente opción
            SEMAFORO_LECTURA.release(); 
            
        } catch (Exception e) {
            System.err.println("Error al manejar solicitud de lista: " + e.getMessage());
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
        }
    }

    private static void manejarSolicitudArchivo(String mensaje) {
        try {
            String[] partes = mensaje.split(":");
            if (partes.length < 3) return;
            String archivo = partes[1];
            String solicitante = partes[2];

            // 1. Bloquear la consola y obtener la entrada
            SEMAFORO_LECTURA.acquire(); 

            System.out.println("\n\n========================================");
            System.out.println("[SOLICITUD] " + solicitante + " solicita: " + archivo);
            System.out.println("========================================");
            System.out.print("Permitir transferencia? (s/n): ");

            String decision = CONSOLA.nextLine();
            
            // 2. Enviar respuesta al servidor (que luego la reenviará al solicitante)
            escritor.println("RESPUESTA_TRANSFERENCIA:" + solicitante);

            if (decision.equalsIgnoreCase("s")) {
                File file = new File(archivo);
                if (file.exists() && file.isFile()) {
                    escritor.println("INICIO_TRANSFERENCIA");
                    
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String linea;
                        while ((linea = reader.readLine()) != null) {
                            escritor.println(linea);
                        }
                    }
                    escritor.println("FIN_TRANSFERENCIA");
                    System.out.println("Archivo enviado exitosamente\n");
                } else {
                    escritor.println("ERROR:ARCHIVO_NO_EXISTE");
                    System.out.println("El archivo solicitado no existe localmente.\n");
                }
            } else {
                escritor.println("ERROR:TRANSFERENCIA_RECHAZADA");
                System.out.println("Transferencia rechazada\n");
            }
            
            // 3. Volver a imprimir el menú para que el usuario sepa dónde está
            imprimirMenuPrincipal();

            // 4. Liberar el semáforo
            SEMAFORO_LECTURA.release(); 
            
        } catch (Exception e) {
            System.err.println("Error al manejar solicitud de archivo: " + e.getMessage());
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
        }
    }
    
    // === MÉTODOS DE MENSAJERÍA ===

    private static void verBandejaEntrada() {
        // ... (código sin cambios relevantes para el problema actual)
        try {
            System.out.println("\n=== BANDEJA DE ENTRADA ===");
            String respuesta = lector.readLine();
            
            if (respuesta == null) return;
            
            if (respuesta.equals("0 mensajes.")) {
                System.out.println("No tienes mensajes.");
            } else {
                System.out.println(respuesta);
                String linea;
                while ((linea = lector.readLine()) != null && !linea.equals("FIN_MENSAJES")) {
                    System.out.println(linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al ver bandeja: " + e.getMessage());
        }
    }

    private static void enviarMensaje() {
        try {
            SEMAFORO_LECTURA.acquire();
            System.out.print("Destinatario: ");
            String destinatario = CONSOLA.nextLine();
            escritor.println(destinatario);

            System.out.print("Mensaje: ");
            String mensaje = CONSOLA.nextLine();
            escritor.println(mensaje);
            SEMAFORO_LECTURA.release();

            if (destinatario == null || mensaje == null) return;

            String respuesta = lector.readLine();
            System.out.println(respuesta);
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
        }
    }

    private static void borrarMensaje() {
        try {
            System.out.println("\n=== BORRAR MENSAJE ===");
            String respuesta = lector.readLine();

            if (respuesta == null) return;
            
            if (!respuesta.equals("No tienes mensajes para borrar.")) {
                System.out.println(respuesta);
                String linea;
                while ((linea = lector.readLine()) != null && !linea.equals("FIN_LISTA_BORRAR")) {
                    System.out.println(linea);
                }

                SEMAFORO_LECTURA.acquire();
                System.out.print("Numero del mensaje: ");
                String numero = CONSOLA.nextLine();
                SEMAFORO_LECTURA.release();
                
                if (numero == null) return;
                escritor.println(numero);

                String resultado = lector.readLine();
                System.out.println(resultado);
            } else {
                System.out.println(respuesta);
            }
        } catch (Exception e) {
            System.err.println("Error al borrar mensaje: " + e.getMessage());
            if (SEMAFORO_LECTURA.availablePermits() == 0) SEMAFORO_LECTURA.release();
        }
    }

    private static void verUsuarios() {
        try {
            System.out.println("\n=== USUARIOS REGISTRADOS ===");
            String linea;
            while ((linea = lector.readLine()) != null && !linea.equals("FIN_LISTA_USUARIOS")) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al ver usuarios: " + e.getMessage());
        }
    }
}