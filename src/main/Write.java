package main;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Écrit au fichier spécifié.
 */
public class Write {

	private String destination = "";
	
	/**
	 * Constructeur de Write.
	 * 
	 * @param destination Endroit que le fichier vas être créé/remplacé.
	 */
	public Write(String destination){
		this.destination = destination;
	}
	
	/**
	 * Écrit du texte dans le fichier spécifié.
	 * 
	 * @param input Ce qui vais être enregistré dans le fichier.
	 */
	public void write(String input){
		try(BufferedWriter br = new BufferedWriter(new FileWriter(new File(destination), true))){
			if (input != null){
				br.write(input);
				br.newLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + destination);
		} catch (IOException e) {
			System.out.println("Unable to read file: " + destination);
		}
	}
	
}
