package derrick.io.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

public class TextWriter {

	public static void write(List<String> data, String filePath, String encoding) {

		File file = new File(filePath);
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), encoding);
			BufferedWriter writer = new BufferedWriter(osw);
			for (String line : data) {
				writer.write(line + "\n");
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String encoding;
	private String outputPath;
	private OutputStreamWriter osw;
	private BufferedWriter writer;

	public TextWriter(String outputPath, String encoding) {
		this.outputPath = outputPath;
		this.encoding = encoding;
	}

	public void init() {
		File file = new File(outputPath);
		try {
			osw = new OutputStreamWriter(new FileOutputStream(file), encoding);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
		writer = new BufferedWriter(osw);
	}

	public void write(String str) {
		try {
			writer.write(str);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(List<String> data) {
		try {
			for (String line : data) {
				writer.write(line + "\n");
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeSet(Set<String> data) {
		try {
			for (String line : data) {
				writer.write(line + "\n");
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
