package derrick.ltp.method;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class LTPRMIProp {

	private static String ipAddr;
	private static String port;
	private static String name;

	static {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("ltp_rmi.properties"));

			ipAddr = properties.getProperty("rmi_addr");
			port = properties.getProperty("rmi_port");
			name = properties.getProperty("rmi_name");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getIpAddr() {
		return ipAddr;
	}

	public static void setIpAddr(String ipAddr) {
		LTPRMIProp.ipAddr = ipAddr;
	}

	public static String getPort() {
		return port;
	}

	public static void setPort(String port) {
		LTPRMIProp.port = port;
	}

	public static String getName() {
		return name;
	}

	public static void setName(String name) {
		LTPRMIProp.name = name;
	}

}
