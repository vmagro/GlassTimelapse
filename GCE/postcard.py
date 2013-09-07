from flask import  Flask
import vidmaker
import datetime
app = Flask(__name__, static_url_path='')

@app.route('/')
def root():
	return app.send_static_file('index.html')

@app.route('/create', methods=['POST'])
def create():
	if request.method == 'POST':
		token = request.form['token']
		userid = request.form['userid']
		images = request.form['image_array']
		for image in images:
			
		#vid_dir = '/static/'+userid+'/'+datetime.datetime.now()
		#os.mkdir(vid_dir)
		#upack files from post request
		#vidmaker.process(vid_dir, token, userid)
		return 'Success'
	else:
		return 'What did the fox say?'

if __name__ == '__main__':
	app.run(host='0.0.0.0', port=80)
