from flask import  Flask, request
#import vidmaker
import datetime
import base64
app = Flask(__name__, static_url_path='')

@app.route('/')
def root():
	return app.send_static_file('index.html')

@app.route('/create', methods=['POST','GET'])
def create():
	if request.method == 'POST':
		data = request.data
		#token = request.form['accessToken']
		#eventid = request.form['eventId']
		#userid = request.form['gPlusId']
		#image_array = request.form['images']
		#imgindex=0
		#for image in image_array:
	#		f = open('glass'+imgindex+'.jpg', 'w')
#			f.write(base64.decodestring(image))
	#		f.close()		
		#vid_dir = '/static/'+userid+'/'+datetime.datetime.now()
		#os.mkdir(vid_dir)
		#upack files from post request
		#vidmaker.process(vid_dir, token, userid)
	#	return 'Success'+eventid
		return data
	else:
		return 'What did the fox say?'

if __name__ == '__main__':
	app.run(host='0.0.0.0', port=80, debug=True)
