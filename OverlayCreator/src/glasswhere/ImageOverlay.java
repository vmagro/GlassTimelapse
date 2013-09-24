package glasswhere;

import glasswhere.GPlusAPI.GPlusUser;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.GpsDirectory;

public class ImageOverlay {
	public static int infoWidth = 266;
	public static int infoHeight = 241;

	public static int overallWidth = 1280;
	public static int overallHeight = 720;

	public static int borderpx = 27;

	public static int mapOffsetX = overallWidth - MapsAPI.mapWidth - borderpx
			- infoWidth;
	public static int mappOffsetY = borderpx;

	public static int cardOffsetX = overallWidth - infoWidth - borderpx;
	public static int cardOffsetY = borderpx;

	public static Font roboto;
	public static Font roboto_30;
	public static Font roboto_60;

	public ImageOverlay() throws FileNotFoundException, FontFormatException,
			IOException {
		
	}

	static BufferedImage generateOverlay(BufferedImage glassImage,
			/*GeoLocation location,*/ Date time, String glassID) throws IOException,
			JSONException, FontFormatException {
		//String geoString = location.getLatitude()+","+location.getLongitude();
		String geoString = "34.025916,-118.281907";
		DateFormat df = new SimpleDateFormat("h:mm");
		String timeString = df.format(time);
		BufferedImage mapImage = MapsAPI.getMap(geoString);
		Graphics2D g = glassImage.createGraphics();
		AlphaComposite compositeInfo = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, (float) 0.55);
		AlphaComposite compositeMap = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, (float) 0.7);
		AlphaComposite compositeName = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, (float) 0.8);
		g.drawImage(glassImage, 0, 0, null);
		g.setComposite(compositeMap);
		g.drawImage(mapImage, mapOffsetX, mappOffsetY, null);
		g.setComposite(compositeInfo);
		g.drawImage(createInfoCard(MapsAPI.getLocationName(geoString), timeString),
				cardOffsetX, cardOffsetY, null);
		g.setComposite(compositeName);
		g.drawImage(createUserCard(glassID), 0, overallHeight - borderpx -100, null);
		g.dispose();
		return glassImage;
	}

	static BufferedImage createInfoCard(String locationName, String time)
			throws FontFormatException, IOException {
		roboto = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(
				"res/Roboto-Light.ttf"));
		roboto_30 = roboto.deriveFont(30f);
		roboto_60 = roboto.deriveFont(60f);
		BufferedImage cardImage = new BufferedImage(infoWidth, infoHeight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = cardImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setBackground(Color.black);
		g.setFont(roboto_60);
		g.drawString(time, 20, 80);
		g.setFont(roboto_30);
		List<String> locationStrings = StringUtils.wrap(locationName,
				g.getFontMetrics(roboto_30), infoWidth - 20);
		int index = 0;
		for (String line : locationStrings) {
			g.drawString(line, 20, index + 150);
			index += 30;
		}
		g.dispose();
		return cardImage;
	}

	static BufferedImage createUserCard(String glassID)
			throws MalformedURLException, IOException, JSONException {
		GPlusUser newUser = GPlusAPI.getUserInfo(glassID);
		BufferedImage cardImage = new BufferedImage(overallWidth, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = cardImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(roboto_60);
		g.drawString(newUser.userName, 150, 70);
		g.drawImage(newUser.profileImage, borderpx, 0, null);
		g.dispose();
		return cardImage;
	}

	public static void main(String[] args) throws IOException, JSONException,
			FontFormatException, ImageProcessingException {
		String imageFolderLocation = "/home/david/Pictures";
		File dir = new File(imageFolderLocation);
		int imageIndex = 0;
		for (File child : dir.listFiles()) {
			BufferedImage glassImage = ImageIO.read(child);
			Metadata glassMeta = ImageMetadataReader.readMetadata(child);
			ExifIFD0Directory directory = glassMeta.getDirectory(ExifIFD0Directory.class);
			Date date = directory.getDate(ExifIFD0Directory.TAG_DATETIME);
			//GpsDirectory geodirectory = glassMeta.getDirectory(GpsDirectory.class);
			//GeoLocation location = geodirectory.getGeoLocation();
			glassImage = generateOverlay(glassImage, 
					date, "12345");
			File outputfile = new File(imageIndex + ".png");
			ImageIO.write(glassImage, "png", outputfile);
			imageIndex++;
		}
	}

}