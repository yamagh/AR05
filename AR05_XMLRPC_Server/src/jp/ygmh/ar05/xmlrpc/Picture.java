package jp.ygmh.ar05.xmlrpc;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.ws.commons.util.Base64;

public class Picture {

//	public static void main(String[] args){
//		Picture p = new Picture();
//		p.getPictureByte();
//	}

	public String[] filepath = {
			 "WebContent/WEB-INF/res/black_cat-25.png"
			,"WebContent/WEB-INF/res/black_cat-256.png"
			};

	public String getUrl(){
		return filepath[1];
	}

	public String getPictureBase64(){
		File f = new File(filepath[1]);
		BufferedImage image = null;
		try{
			image = ImageIO.read(f);
		}catch(Exception e){
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		image.flush();
		try{
			ImageIO.write(image, "png", bos);
			bos.flush();
			bos.close();
		}catch(Exception e){
		}

		byte[] b = baos.toByteArray();

		return Base64.encode(b);
	}
}
