package com.socaldevs.timelapse.glass;

import java.util.UUID;

public class Constants {
	
	public static final UUID BT_UUID = UUID.fromString("dea9c2b5-7136-43fc-979d-a293af71018e");
	public static final String BT_SERVICE_NAME = "glasstimelapse";
	
	public static final String BASE_URL = "http://hip-apricot-331.appspot.com/";
	public static final String UPLOAD_URL = BASE_URL + "upload?mode=getUploadUrl";
	public static final String EVENT_URL = BASE_URL + "event";

}
