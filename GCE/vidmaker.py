import os,sys
import glob
import overlay
import urllib, urllib2
import gdata.youtube
import gdata.youtube.service

def process(dir, userid, token, eventid, authsub_token):
	os.chdir(dir)
	search_dir = os.getcwd()
	files = filter(os.path.isfile, os.listdir(search_dir))
	files = [os.path.join(search_dir, f) for f in files]
	files.sort(key=lambda x: os.path.getmtime(x))
	#Process individual photos/frames
	index = 0
	for file in files:	
		if file.endswith('.jpg'):
			city = overlay.process(file, index, userid, token, eventid)
			index = index+1
	#Decide on speed of photos
	if (index < 10):
		framerate = '1/5'
	if (index < 20):
		framerate = '1/2'
	if (index < 100):
		framerate = '1'
	if (index > 100):
		framerate = '3'
	#Encode video
	os.system("ffmpeg -y -r "+framerate+" -pattern_type glob -i '*.png' -c:v libx264 -pix_fmt yuv420p "+str(eventid)+".mp4")
	#Upload Video
	yt_service = gdata.youtube.service.YouTubeService()
	yt_service.SetAuthSubToken(authsub_token)

	yt_service.developer_key = 'AI39si7ABIpdltRg7VQNjkJ_5DQdcPKBBaASHKYceOVfTkJzBi6onSsWOfFPFGxuRn2EG7YNJC7WGjVlxgigDpE1LYcgOAAuZA'
 
	my_media_group = gdata.media.Group(
  		title=gdata.media.Title(text='Glasswhere'),
  		description=gdata.media.Description(description_type='plain',
                                      text='Through Glass. '+city+"."),
  		keywords=gdata.media.Keywords(text='Google, Glass, Glasswhere'),
  		category=[gdata.media.Category(text='Tech',scheme='http://gdata.youtube.com/schemas/2007/categories.cat',label='Tech')],
  		player=None,
  		private=gdata.media.Private()
	)

	video_entry = gdata.youtube.YouTubeVideoEntry(media=my_media_group)

	new_entry = yt_service.InsertVideoEntry(video_entry, '/home/david/GlassTimelapse/GCE/static/'+str(userid)+'/'+str(eventid)+'/out.mp4')
	video_xml=str(new_entry)
	start=video_xml.index('https://www.youtube.com/watch?v')
	video_url = video_xml[start:start+43]
	#Post back to App Engine
	post_url = 'http://glass.ptzlabs.com/uploaded?youtubeUrl='+video_url+'?city='+city+'?eventid='+str(eventid)
        req = urllib2.Request(post_url)
