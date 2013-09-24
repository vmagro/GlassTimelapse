package glasswhere;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class GPlusAPI {
	public static String getAuthToken() throws IOException {
		URL gPlusTokenURL = new URL(
				"https://accounts.google.com/o/oauth2/token");
		String charset = "UTF-8";
		String our_client_id = "597615227690-pfgba7ficse1kf1su0qkgjllktcb7psf.apps.googleusercontent.com";
		String our_client_secret = "RwkS3k8UQKDNALu-B_nQxtDd";
		String requestParams = "refresh_token=token"
				+ "&grant_type=refresh_token" + "&client_id=" + our_client_id
				+ "&client_secret=" + our_client_secret;
		System.out.println(requestParams);
		URLConnection connection = gPlusTokenURL.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Accept-Charset", charset);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded;charset=" + charset);
		OutputStream output = null;
		try {
			output = connection.getOutputStream();
			output.write(requestParams.getBytes(charset));
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException logOrIgnore) {
				}
		}
		InputStream response = connection.getInputStream();
		System.out.println(response);
		return "hello";
	}
	
	/*public static BufferedImage getProfPic(String googleID) {
		
	}*/
}
