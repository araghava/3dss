package renderer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.GL20;

import system.Settings;

/**
 * ShaderController will manage shaders.
 * @author Max 
 */
public class ShaderController {

	private HashMap<String, Integer> shaderNameToID = null;
	private HashMap<Integer, Integer> shaderIDToType = null;
	
	private int projectionMatrixLocation = 0;
	private int viewMatrixLocation = 0;
	private int modelMatrixLocation = 0;
	
	private int currentProgram;

	public ShaderController() {
		shaderNameToID = new HashMap<>();
		shaderIDToType = new HashMap<>();
		currentProgram = 0;
	}

	
	/**
	 * Currently setProgram is written like this so different shaders can be 
	 * written for different graphics settings.
	 * 
	 * @return <code>true</code> if the program was successfully set, and false otherwise.
	 */
	public boolean setProgram(HashMap<String, Integer> shaders){
		if(currentProgram != 0){
			//TO DO: Cleanup
		}
		
		currentProgram = GL20.glCreateProgram();
		
		for(String file : shaders.keySet()){
			int shaderID = this.loadShader(file, shaders.get(file));
			GL20.glAttachShader(currentProgram, shaderID);
			shaderNameToID.put(file, shaderID);
			shaderIDToType.put(shaderID, shaders.get(file));
			System.out.println(shaderID);
		}
		
		
		// Binding attribute mappings defined in our Settings class.
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("in_Position"), "in_Position");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("in_Color"), "in_Color");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("in_TextureCoord"), "in_TextureCoord");
		GL20.glBindAttribLocation(currentProgram, Settings.getInteger("in_Normal"), "in_Normal");
		
		GL20.glLinkProgram(currentProgram);
		GL20.glValidateProgram(currentProgram);
		
		// Get matrices uniform locations
		projectionMatrixLocation = GL20.glGetUniformLocation(currentProgram, "projectionMatrix");
		viewMatrixLocation = GL20.glGetUniformLocation(currentProgram,  "viewMatrix");
		modelMatrixLocation = GL20.glGetUniformLocation(currentProgram,  "modelMatrix");
		
		
		System.out.println("PROJECTION: " + projectionMatrixLocation);
		System.out.println("VIEW: " + viewMatrixLocation);
		System.out.println("MODEL: " + modelMatrixLocation);
		System.out.println("SHADER: " + currentProgram);
		
		
		return true;
	}
	
	/**
	 * Getter function for the current program.
	 * 
	 * @return An integer defining the current program.
	 */
	public int getCurrentProgram(){
		return currentProgram;
	}
	
	public int getModelMatrixLocation(){
		return modelMatrixLocation;
	}
	
	public int getProjectionMatrixLocation(){
		return projectionMatrixLocation;
	}
	
	public int getViewMatrixLocation(){
		return viewMatrixLocation;
	}
	
	/**
	 * 
	 * @param filename Name of shader file.
	 * @param type 
	 * @return The shader UID.
	 */
	private int loadShader(String filename, int type) {
		
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}

		shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		return shaderID;
	}


}
