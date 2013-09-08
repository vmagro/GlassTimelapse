import os,sys
import glob
import overlay
import urllib2

def process(dir, userid, token, eventid):
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

	os.system("ffmpeg -y -r 2 -pattern_type glob -i '*.png' -c:v libx264 -pix_fmt yuv420p out.mp4")

	upload_result = os.system("python upload_video.py --file "
		+out.mp4+""" --title='Glass Timelaps' --description='A video postcard from Glass' --keywords='Google Glass' 
		--privacyStatus='unlisted'""") 
