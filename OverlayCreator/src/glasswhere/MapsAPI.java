package glasswhere;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsAPI {
	public static int mapWidth = 159;
	public static int mapHeight = 241;
	public static int mapZoom = 15;

	public static BufferedImage getMap(String loc_data) throws IOException {
		URL mapFetchURL = new URL(
				"http://maps.googleapis.com/maps/api/staticmap?center="
						+ loc_data
						+ "&zoom="
						+ Integer.toString(mapZoom)
						+ "&size="
						+ Integer.toString(mapWidth)
						+ "x"
						+ Integer.toString(mapHeight)
						+ "&markers=color:red%7C"
						+ loc_data
						+ "&style=invert_lightness:true|hue:0x00d4ff|saturation:-77|lightness:23&sensor=false");
		BufferedImage mapImage = ImageIO.read(mapFetchURL);
		return mapImage;
	}

	public static String getLocationName(String loc_data) throws IOException,
			JSONException {
		URL geoFetchURL = new URL(
				"http://maps.googleapis.com/maps/api/geocode/json?latlng="
						+ loc_data + "&sensor=false");
		URLConnection connection = geoFetchURL.openConnection();
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		JSONObject json = new JSONObject(builder.toString());
		JSONArray results = json.getJSONArray("results");
		JSONObject address = (JSONObject) results.get(1);
		JSONArray components = address.getJSONArray("address_components");
		JSONObject address1 = (JSONObject) components.get(0);
		String long_name = address1.getString("long_name");
		return long_name;
	}
}
