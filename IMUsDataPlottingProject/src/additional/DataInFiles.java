package additional;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

public class DataInFiles {
	private String fileName;
	private File file;
	private FileOutputStream out;
	private DataOutputStream dos;
	private IntBuffer ib;
	
	public DataInFiles(String fileName) {
		setFileName(fileName);
		file = new File(this.fileName);
	    try {
	        //проверяем, что если файл не существует то создаем его
	        if(!file.exists()) 	file.createNewFile();
	 
	        //PrintWriter обеспечит возможность записи в файл
	        out = new FileOutputStream(file.getAbsoluteFile());
	        dos = new DataOutputStream(out);
	        ib = IntBuffer.allocate(10);	// выделение памяти
	        ib.put(new int[] { 1, 2, 7, 9, 3, 8, 6 });
	    } catch(IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public void write(int val, float fVal) {
	    try {
	        if(file.canWrite()){ 
	        	
	        	dos.writeInt(val);
	        	dos.writeFloat(fVal);
	        	
//		       	 ib.put(0, 0x12ABCDEF);
//		       	 ib.rewind();// на первый элемент
//		         if (ib.hasRemaining()) {
//		              dos.writeInt(ib.get());
//		         }
	 
	        }
	    } catch(Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	public void write(int val) {
	    try {
	        if(file.canWrite()) dos.writeInt(val);
	    } catch(Exception e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void closeFile() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		closeFile();
	}
}
