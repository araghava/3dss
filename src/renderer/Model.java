package renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


/*
 * Model class is an abstraction used by Renderer. This will use interleaving for vertex properties.
 * @author Max
 */
public class Model {
		
	private int vboiID; //vertex indices VBO (GL_ELEMENT_ARRAY_BUFFER)
	private int vaoID; //vertex array object 
	private int indicesCount = 0;
	
//	private VertexData[] vertices = null;
//	private ByteBuffer verticesByteBuffer = null;
	
	/*
	 * Generate a model from an array of vertex data
	 * Still not sure how glDrawElements is supposed to work...
	 */
	public Model(VertexData[] vd, byte [] idx){
		/*
		 * TO DO: Take in vertices and process them
		 * @return vboiID and vaoID
		 */
		
//		vertices = vd;
//		
//		// Put each 'Vertex' in one FloatBuffer
//		verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length
//				* VertexData.stride);
//		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
//		for (int i = 0; i < vertices.length; i++) {
//			// Add position, color and texture floats to the buffer
//			verticesFloatBuffer.put(vertices[i].getElements());
//		}
//		verticesFloatBuffer.flip();
		
	}
	
	
	public Model(List<Vector3f> v, List<Vector3f> vn, List<Vector2f> vt, List<Face> f){	
		/*
		 * Set up shit here
		 */
		// Put each 'Vertex' in one FloatBuffer
		ByteBuffer verticesByteBuffer = BufferUtils.createByteBuffer(f.size() * 5 *  VertexData.stride);            
		FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
		HashMap<VertexData, Byte> vboIndexMap = new HashMap<>();
		List<Byte> vboIndex = new ArrayList<>();
		VertexData tempVertexData;
		Vector3f tempColor = new Vector3f(1f, 1f, 1f);
		byte index = 0;
		int count = 0;
		int common = 0;
		int newC = 0;
		
		for(Face face: f){
			System.out.println(++count);
			/*
			 * Put in order of attributes for clarity (no color)
			 * Since each triangle (face) has 3 vertices, there is going to be some annoyingness
			 */
			Vector3f normal = vn.get((int) (face.normal.x - 1)); //face.normal.x is the index (TO DO: Use something else other than Vector3f)
			Vector3f vertex = v.get((int) (face.vertex.x - 1));
			Vector2f texture = new Vector2f(1f, 1f);//vt.get((int) (face.texture.x - 1));
			
			tempVertexData = new VertexData(vertex, tempColor, texture, normal);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
				newC++;
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
				common++;
			}
			
			normal = vn.get((int) (face.normal.y - 1)); //face.normal.x is the index (TO DO: Use something else other than Vector3f)
			vertex = v.get((int) (face.vertex.y - 1));
			texture = new Vector2f(1f, 1f);//vt.get((int) (face.texture.y - 1));
			
			tempVertexData = new VertexData(vertex, tempColor, texture, normal);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
				newC++;
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
				common++;
			}
			
			normal = vn.get((int) (face.normal.z - 1)); //face.normal.x is the index (TO DO: Use something else other than Vector3f)
			vertex = v.get((int) (face.vertex.z - 1));
			texture = new Vector2f(1f, 1f);//vt.get((int) (face.texture.z - 1));
			
			tempVertexData = new VertexData(vertex, tempColor, texture, normal);
			if(!vboIndexMap.containsKey(tempVertexData)){
				vboIndexMap.put(tempVertexData, index);
				verticesFloatBuffer.put(tempVertexData.getElements());
				vboIndex.add(index++);
				newC++;
			}
			else{
				vboIndex.add(vboIndexMap.get(tempVertexData));
				common++;
			}
			
		}
		
		verticesFloatBuffer.flip();
		byte [] indices = new byte[vboIndex.size()];
		indicesCount = vboIndex.size();
		
		for(int i = 0; i < vboIndex.size(); i++){
			indices[i] = vboIndex.get(vboIndex.size() - 1 - i); //must be flipped
		}
		
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(vboIndex.size());
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		 
		
		// Create a new Vertex Array Object in memory and select it (bind)
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		// Create a new Vertex Buffer Object in memory and select it (bind)
		int vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STATIC_DRAW);
		
		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, VertexData.positionElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.positionByteOffset);
		
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, VertexData.colorElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.colorByteOffset);
		
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, VertexData.textureElementCount, GL11.GL_FLOAT,
				false, VertexData.stride, VertexData.textureByteOffset);
		
		// Put the normal coordinates in attribute list 3
				GL20.glVertexAttribPointer(2, VertexData.textureElementCount, GL11.GL_FLOAT,
						false, VertexData.stride, VertexData.normalByteOffset);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);
		
		// Create a new VBO for the indices and select it (bind) - INDICES
		vboiID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		System.out.println("COMMON: " + common);
		System.out.println("NEWC: " + newC);
				
	}
	
	public int getIndexVBO(){
		return vboiID;
	}
	
	public int getVAO(){
		return vaoID;
	}
	
	public int getIndicesCount(){
		return indicesCount;
	}
	
	
}