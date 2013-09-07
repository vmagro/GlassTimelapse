from flask import  Flask, request, jsonify
#import vidmaker
import datetime
import json
import base64
app = Flask(__name__, static_url_path='')

@app.route('/')
def root():
	return app.send_static_file('index.html')

@app.route('/create', methods=['POST','GET'])
def create():
	if request.method == 'POST':
		jdata = json.loads(request.data) 
		token = jdata['accessToken']
		eventid = jdata['eventId']
		userid =jdata['gPlusId']
		image_array = jdata['images']
		imgindex=0
		for image in image_array:
			f = open('glass'+imgindex+'.jpg', 'w')
			f.write(base64.decodestring(image))
			f.close()		
		vid_dir = '/static/'+userid+'/'+eventid
		os.mkdir(vid_dir)
		#vidmaker.process(vid_dir, token, userid)
		return 'Success '+str(eventid)
	else:
		return 'What did the fox say?'

if __name__ == '__main__':
	app.run(host='0.0.0.0', port=80, debug=True)
