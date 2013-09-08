import os,sys
import glob
import overlay
import urllib2
import gdata.youtube
import gdata.youtube.service

def process(dir, userid, token, eventid, authsub_token):
	os.chdir(dir)
	search_dir = os.getcwd()
	files = filter(os.path.isfile, os.listdir(search_dir))
	files = [os.path.join(search_dir, f) for f in files]
	files.sort(key=lambda x: os.path.getmtime(x))

	index = 0
	for file in files:	
		if file.endswith('.jpg'):
			overlay.process(file, index, userid, token, eventid)
			index = index+1
	if (index < 20):
		framerate = 2
	if (index > 20 and index < 100):
		framerate = 1
	if (index > 100):
		framerate = 0.5
	os.system("ffmpeg -y -r "+str(framerate)+" -pattern_type glob -i '*.png' -c:v libx264 -pix_fmt yuv420p out.mp4")

	yt_service = gdata.youtube.service.YouTubeService()
	yt_service.SetAuthSubToken(authsub_token)
	yt_service.UpgradeToSessionToken()

	yt_service.developer_key = 'AI39si7ABIpdltRg7VQNjkJ_5DQdcPKBBaASHKYceOVfTkJzBi6onSsWOfFPFGxuRn2EG7YNJC7WGjVlxgigDpE1LYcgOAAuZA'

	my_media_group = gdata.media.Group(
  		title=gdata.media.Title(text='Glass Timelapse'),
  		description=gdata.media.Description(description_type='plain',
                                      text='Through Glass'),
  		keywords=gdata.media.Keywords(text='Google Glass'),
  		player=None,
  		private=gdata.media.Private()
	)

	video_entry = gdata.youtube.YouTubeVideoEntry(media=my_media_group)

	new_entry = yt_service.InsertVideoEntry(video_entry, '/home/david/GlassTimelapse/GCE/static/'+userid+'/'+eventid+'/out.mp4')
	
