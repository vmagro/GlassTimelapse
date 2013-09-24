package com.socaldevs.timelapse.glass;

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
import java.util.List;

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

	public Font roboto;
	public static Font roboto_30;
	public static Font roboto_45;

	public ImageOverlay() throws FileNotFoundException, FontFormatException,
			IOException {
		roboto = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(
				"res/Roboto-Light.ttf"));
		roboto_30 = roboto.deriveFont(30f);
		roboto_45 = roboto.deriveFont(45f);
	}

	static BufferedImage generateOverlay(BufferedImage glassImage,
			String loc_data, String time, String glassID) throws IOException,
			JSONException, FontFormatException {
		BufferedImage mapImage = MapsAPI.getMap(loc_data);
		Graphics2D g = glassImage.createGraphics();
		AlphaComposite composite = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, (float) 0.6);
		g.setComposite(composite);
		g.drawImage(glassImage, 0, 0, null);
		g.drawImage(mapImage, mapOffsetX, mappOffsetY, null);
		g.drawImage(createInfoCard(MapsAPI.getLocationName(loc_data), time),
				cardOffsetX, cardOffsetY, null);
		/*g.drawImage(createUserCard(glassID), 0, overallHeight - borderpx, null);*/
		g.dispose();
		return glassImage;
	}

	static BufferedImage createInfoCard(String locationName, String time)
			throws FontFormatException, IOException {
		BufferedImage cardImage = new BufferedImage(infoWidth, infoHeight,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = cardImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setBackground(Color.gray);
		g.setFont(roboto_45);
		g.drawString(time, 20, 60);
		g.setFont(roboto_30);
		/*List<String> locationStrings = StringUtils.wrap(locationName,
				g.getFontMetrics(roboto_30), infoWidth - 20);
		int index = 0;
		for (String line : locationStrings) {
			g.drawString(line, 20, index + 120);
			index += 30;
		}*/
		g.drawString(locationName, 20, 150);
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
		g.setFont(roboto_45);
		g.drawString(newUser.userName, 150, 40);
		g.drawImage(newUser.profileImage, borderpx, 0, null);
		g.dispose();
		return cardImage;
	}

	public static void main(String[] args) throws IOException, JSONException,
			FontFormatException {
		String imageFolderLocation = "/home/david/Downloads/pictures";
		File dir = new File(imageFolderLocation);
		int imageIndex = 0;
		for (File child : dir.listFiles()) {
			BufferedImage glassImage = ImageIO.read(child);
			glassImage = generateOverlay(glassImage, "34.025916,-118.281907",
					"today", "12345");
			File outputfile = new File(imageIndex + ".png");
			ImageIO.write(glassImage, "png", outputfile);
			imageIndex++;
		}
	}

}