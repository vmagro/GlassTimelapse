package com.socaldevs.timelapse.glass;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class GPlusAPI {
	public static class GPlusUser {
		public String userName;
		public BufferedImage profileImage;
	}

	public static String getAuthToken(String glassid) throws IOException {

		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"http://hip-apricot-331.appspot.com/getRefreshToken");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("glassId", glassid));
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = client.execute(httpPost);
		InputStream token = response.getEntity().getContent();
		System.out.println(token);
		
		/*
		 * URL gPlusTokenURL = new URL(
		 * "https://accounts.google.com/o/oauth2/token"); String charset =
		 * "UTF-8"; String our_client_id =
		 * "597615227690-pfgba7ficse1kf1su0qkgjllktcb7psf.apps.googleusercontent.com"
		 * ; String our_client_secret = "RwkS3k8UQKDNALu-B_nQxtDd"; String
		 * requestParams = "refresh_token=token" + "&grant_type=refresh_token" +
		 * "&client_id=" + our_client_id + "&client_secret=" +
		 * our_client_secret; System.out.println(requestParams); URLConnection
		 * connection = gPlusTokenURL.openConnection();
		 * connection.setDoOutput(true);
		 * connection.setRequestProperty("Accept-Charset", charset);
		 * connection.setRequestProperty("Content-Type",
		 * "application/x-www-form-urlencoded;charset=" + charset); OutputStream
		 * output = null; try { output = connection.getOutputStream();
		 * output.write(requestParams.getBytes(charset)); } finally { if (output
		 * != null) try { output.close(); } catch (IOException logOrIgnore) { }
		 * } InputStream response = connection.getInputStream();
		 * System.out.println(response);
		 */
		return "hello";
	}

	public static GPlusUser getUserInfo(String glassID)
			throws MalformedURLException, IOException, JSONException {
		URL profileURL = new URL(
				"https://www.googleapis.com/plus/v1/people/me?access_token="
						+ getAuthToken(glassID));
		URLConnection connection = profileURL.openConnection();
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		JSONObject json = new JSONObject(builder.toString());
		String userFullName = json.getString("displayName");
		JSONObject profPic = json.getJSONObject("image");
		String profPicURL = profPic.getString("url");
		profPicURL = profPicURL.substring(0, profPicURL.length() - 5);
		URL profImageURL = new URL(profPicURL + "sz=100");
		BufferedImage profImage = ImageIO.read(profImageURL);
		GPlusUser user = new GPlusUser();
		user.profileImage = profImage;
		user.userName = userFullName;
		return user;
	}
}
