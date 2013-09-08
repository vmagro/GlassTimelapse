from flask import  Flask, request, jsonify
import vidmaker
import datetime
import json
import base64
import os

app = Flask(__name__, static_url_path='')

os.chdir('.')

@app.route('/')
def root():
	return app.send_static_file('index.html')

@app.route('/create', methods=['POST','GET'])
def create():
	if request.method == 'POST':
		jdata = json.loads(request.data) 
		token = jdata['refreshToken']
		eventid = jdata['eventId']
		userid =jdata['gPlusId']
		authsub_token = jdata['youtubeToken']
		image_array = jdata['images']		
		vid_dir = os.getcwd()+'/static/'+str(userid)+'/'+str(eventid)
		if not os.path.exists(os.getcwd()+'/static/'+str(userid)):
			os.mkdir(os.getcwd()+'/static/'+str(userid))
		if not os.path.exists(os.getcwd()+'/static/'+str(userid)+'/'+str(eventid)):
			os.mkdir(os.getcwd()+'/static/'+str(userid)+'/'+str(eventid))
		imageindex=0
		for image in image_array:
                        f = open(vid_dir+'/glass'+str(imageindex)+'.jpg', 'w')
                        f.write(base64.decodestring(image))
                        f.close()
			imageindex=imageindex+1
		vidmaker.process(vid_dir, userid, token, eventid, authsub_token)
		return 'Success '+str(eventid)
	else:
		return 'What did the fox say?'

if __name__ == '__main__':
	app.run(host='0.0.0.0', port=80, debug=True)
