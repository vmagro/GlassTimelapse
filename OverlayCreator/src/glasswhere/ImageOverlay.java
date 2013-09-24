package glasswhere;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONException;

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

	static BufferedImage generateOverlay(BufferedImage glassImage,
			String loc_data, String time) throws IOException, JSONException,
			FontFormatException {
		BufferedImage mapImage = MapsAPI.getMap(loc_data);
		Graphics2D g = glassImage.createGraphics();
		AlphaComposite composite = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, (float) 0.6);
		g.setComposite(composite);
		g.drawImage(glassImage, 0, 0, null);
		g.drawImage(mapImage, mapOffsetX, mappOffsetY, null);
		g.drawImage(createInfoCard(MapsAPI.getLocationName(loc_data), time),
				cardOffsetX, cardOffsetY, null);
		g.dispose();
		return glassImage;
	}

	static BufferedImage createInfoCard(String locationName, String time)
			throws FontFormatException, IOException {
		BufferedImage cardImage = new BufferedImage(infoWidth, infoHeight,
				BufferedImage.TYPE_INT_RGB);
		Font roboto = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(
				"res/Roboto-Light.ttf"));
		Font roboto_30 = roboto.deriveFont(30f);
		Font roboto_45 = roboto.deriveFont(45f);
		Graphics2D g = cardImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setBackground(Color.gray);
		g.setFont(roboto_45);
		g.drawString(time, 20, 60);
		g.setFont(roboto_30);
		g.drawString(locationName, 20, 150);
		g.dispose();
		return cardImage;
	}

	public static void main(String[] args) throws IOException, JSONException,
			FontFormatException {
		String imageFolderLocation = "/home/david/Pictures";
		File dir = new File(imageFolderLocation);
		int imageIndex = 0;
		for (File child : dir.listFiles()) {
			BufferedImage glassImage = ImageIO.read(child);
			// Get location data, date
			glassImage = generateOverlay(glassImage, "34.025916,-118.281907",
					"today");
			File outputfile = new File(imageIndex + ".png");
			ImageIO.write(glassImage, "png", outputfile);
			imageIndex++;
		}
	}

}