package derrick.io.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TextReader {

	public static List<String> read(String filePath) {
		return read(filePath, "UTF-8");
	}

	public static List<String> read(String filePath, String encoding) {
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) {
				List<String> rawData = new ArrayList<>();
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					rawData.add(lineTxt);
				}
				read.close();
				return rawData;
			} else {
				System.err.println("can not find this file:" + file.getAbsolutePath());
			}
		} catch (Exception e) {
			System.err.println("read error");
			e.printStackTrace();
		}
		return null;
	}

	private String inputPath;
	private String encoding;
	private InputStreamReader reader;
	private BufferedReader bufferedReader;

	public TextReader(String inputPath, String encoding) {
		this.inputPath = inputPath;
		this.encoding = encoding;
	}

	public boolean init() {
		File file = new File(inputPath);
		if (file.isFile() && file.exists()) {
			try {
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				e.printStackTrace();
			}
			bufferedReader = new BufferedReader(reader);
			return true;
		}
		return false;
	}

	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * read n lines
	 * 
	 * @param num
	 * @return if end return null.
	 */
	public List<String> read(int n) {
		List<String> rawData = new ArrayList<>();
		String lineTxt = null;
		try {
			for (int i = 0; i < n; i++) {
				if ((lineTxt = bufferedReader.readLine()) == null) {
					if (i == 0)
						return null;
					else
						break;
				}
				rawData.add(lineTxt);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rawData;
	}
	
	public String readLine(){
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return null;
	}

}
