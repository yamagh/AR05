package jp.ygmh.ar05.xmlrpc;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.ws.commons.util.Base64;

public class Picture {

	// 画像格納先パス
	// Todo: 画像をDBから取り出すようにする
	public static String[] filepath = {
			 "WebContent/WEB-INF/res/black_cat-25.png"
			,"WebContent/WEB-INF/res/black_cat-256.png"
			};

	/**
	 * 
	 * @param i
	 * @return encoded string by base64
	 */
	public String getPictureBase64(int i){
		File f = 0<i && i<filepath.length ? new File(filepath[i]) : new File(filepath[0]);
		BufferedImage image = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		
		try{
			image = ImageIO.read(f);
			image.flush();
			ImageIO.write(image, "png", bos);
			bos.flush();
			bos.close();
			byte[] b = baos.toByteArray();
			return Base64.encode(b);
		
		}catch(Exception e){
			System.out.println(e);
			return "";
		}
	}
}
