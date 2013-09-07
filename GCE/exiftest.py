import os,sys
from PIL import Image
from PIL.ExifTags import TAGS

exif = Image.open(sys.argv[1])._getexif()

for (k,v) in exif.iteritems():
	print '%s = %s' % (TAGS.get(k), v)
os.system('pause')
