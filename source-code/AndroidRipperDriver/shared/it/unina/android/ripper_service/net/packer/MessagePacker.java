package it.unina.android.ripper_service.net.packer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.json.JSONObject;

public class MessagePacker {

	public static byte[] pack(Map map) {
		if (map != null) {
			/*
			if (map.containsKey("xml")) {
				String xml = (String) map.get("xml");

				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					GZIPOutputStream gzip = new GZIPOutputStream(out);
					gzip.write(xml.getBytes());
					gzip.close();
					String xmlGzip = out.toString("ISO-8859-1");
					map.put("xml", xmlGzip);
				} catch (Throwable t) {
				}
			}
			*/
			try {
				JSONObject jsonObject = new JSONObject();

				for (Object k : map.keySet()) {
					String value = (String) map.get(k);
					jsonObject.put((String) k, value);
				}

				System.out.println(jsonObject.toString());
				
				return jsonObject.toString().getBytes();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		return null;
	}

	public static Map unpack(byte[] b) {
		if (b != null) {
			String s = new String(b);

			try {
				JSONObject jsonObject = new JSONObject(s);
				Map<String, Object> map = new HashMap();
				
				Iterator iterator = jsonObject.keys();
				while (iterator.hasNext()) {
					String key = (String)iterator.next();
					map.put(key, jsonObject.get(key));				
				}
				
				/*
				if (map.containsKey("xml")) {
					String dataXml = (String)map.get("xml");
					
					if (dataXml != null)
					{
						byte[] data = dataXml.getBytes("ISO-8859-1");
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						ByteArrayInputStream in = new ByteArrayInputStream(data);
						try {
							InputStream inflater = new GZIPInputStream(in);
							byte[] bbuf = new byte[256];
							while (true) {
								int r = inflater.read(bbuf);
								if (r < 0) {
									break;
								}
								buffer.write(bbuf, 0, r);
							}
						} catch (IOException e) {
							throw new IllegalStateException(e);
						}
						String o = new String(buffer.toByteArray(), "ISO-8859-1");
						map.put("xml", o);
					}
				}
				*/
				return map;

			} catch (Throwable t) {
				t.printStackTrace();
			}

		}

		return null;
	}
}
