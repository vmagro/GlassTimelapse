import os,sys
import glob
import overlay
import urllib2

function process(dir)
os.chdir(sys.argv[1])
search_dir = os.getcwd()
files = filter(os.path.isfile, os.listdir(search_dir))
files = [os.path.join(search_dir, f) for f in files]
files.sort(key=lambda x: os.path.getmtime(x))

index = 0
for file in files:	
	if file.endswith('.jpg'):
		overlay.process(file, index)
		index = index+1

os.system("ffmpeg -y -r 2 -pattern_type glob -i '*.png' -c:v libx264 -pix_fmt yuv420p out.mp4")

#if (os.path.isfile('out.mp4')
#	post_url = ''
#	values = dict(url='http://173.255.121.241/'+gPlusId+'/'+eventId+'/out.mp4')
#	data = urllib.urlencode(values)
#	req = urllib2.Request(post_url, data)
#	rsp = urllib2.urlopen(req)
#	content = rsp.read()
#	if (content = ok):
#		return 'ok'
#	else:
#		return 'fail'
#else:
#	return 'fail'
