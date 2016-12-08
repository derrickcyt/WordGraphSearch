package similarity;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import similarity.Similaritable;
import similarity.HownetMeta;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 义原解析器基类，�?��义原存储在xml文件中（当前package中的sememe.xml.tar.gz文件）�?<br/>
 * 算法的核心�?想请参看论文《汉语词语语义相似度计算研究》或《中文信息相似度计算理论与方法�?�?��第三�?br/>
 * 
 * 为提高运算�?度，义原的加载方式做了调整，只把义原的汉语定义和对应的Id加入到MultiMap对象中，并�?过义原的层次化Id计算义原之间的相似度�?br/>
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验�?
 * 
 * @see {@link ke.commons.similarity.Similariable}
 */
public abstract class BaseSememeParser implements HownetMeta, Similaritable {
	protected Log LOG = LogFactory.getLog(this.getClass());

	/** �?��的义原都存放到一个MultiMap, Key为Sememe的中文定�? Value为义原的Id */
	protected static Multimap<String, String> SEMEMES = null;

	public BaseSememeParser() throws IOException {
		if (SEMEMES != null) {
			return;
		}

		SEMEMES = HashMultimap.create();

		String sememeFile = getClass().getPackage().getName().replaceAll("\\.", "/") + "/sememe.xml.gz";
		InputStream input = this.getClass().getClassLoader().getResourceAsStream(sememeFile);
		input = new GZIPInputStream(input);
		load(input);
	}

	/**
	 * 从文件中加载义元知识
	 * 
	 * @throws IOException
	 */
	public void load(InputStream input) throws IOException {
		System.out.print("loading sememes...");
		long time = System.currentTimeMillis();
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader xmlEventReader = inputFactory.createXMLEventReader(input);

			int count = 0;
			while (xmlEventReader.hasNext()) {
				XMLEvent event = xmlEventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (startElement.getName().toString().equals("sememe")) {
						String cnWord = startElement.getAttributeByName(QName.valueOf("cn")).getValue();
						String id = startElement.getAttributeByName(QName.valueOf("id")).getValue();
						SEMEMES.put(cnWord, id);
						count++;
						if (count % 100 == 0) {
							System.out.print(".");
						}
					}
				}
			}
			input.close();
		} catch (Exception e) {
			throw new IOException(e);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("\ncomplete!. time elapsed: " + (time / 1000) + "s");
	}

	/**
	 * 计算两个义原之间的关联度
	 * 
	 * @param sememeName1
	 * @param sememeName2
	 * @return
	 */
	public double getAssociation(String sememeName1, String sememeName2) {
		return 0.0;
	}
}
