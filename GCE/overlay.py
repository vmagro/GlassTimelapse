from PIL import Image, ImageFilter, ImageDraw, ImageFont, ImageOps
from PIL.ExifTags import TAGS, GPSTAGS
import urllib, urllib2, cStringIO
import sys, json, textwrap, os 

def process(image_file, index, userid, token, eventid):
    #open original image
    image = Image.open(image_file)
    exif_data = get_exif_data(image)
    loc_data = get_lat_lon(exif_data)
    #create map tile
    width = 164
    height= 252
    zoom = '18'
    url='http://maps.googleapis.com/maps/api/staticmap?center='+loc_data+'&zoom='+zoom+'&size='+str(width)+'x'+str(height)+'&markers=color:blue%7C'+loc_data+'&inverse_lightness=true&sensor=false'
    mapfile = cStringIO.StringIO(urllib.urlopen(url).read())
    map = Image.open(mapfile)
    map = map.convert('RGBA')
    bands = list(map.split())
    bands[3] = bands[3].point(lambda x: x*0.5)
    map = Image.merge(map.mode, bands)
    #create city/time tile    
    card = Image.new('RGBA', (273,252), (0,0,0))
    roboto_30 = ImageFont.truetype("/home/david/GlassTimelapse/GCE/Roboto-Light.ttf", 30)
    roboto_45 = ImageFont.truetype("/home/david/GlassTimelapse/GCE/Roboto-Light.ttf", 45)
    draw = ImageDraw.Draw(card)
    date,time = exif_data['DateTime'].split(' ')
    hour, min, sec = time.split(':')
    if (hour[0] == '0'):
	hour = hour[1:]
    gurl = 'http://maps.googleapis.com/maps/api/geocode/json?latlng='+loc_data+'&style=invert_lightness:true|hue:0x00d4ff&sensor=false'
    response = urllib.urlopen(gurl)
    data = json.load(response)
    city =  data['results'][0]['address_components'][1]['long_name']
    lines = textwrap.wrap(city, 18)
    y_text = 90
    for line in lines:
    	width, height = roboto_30.getsize(line)
        draw.text((20, y_text), line, (255, 255, 255), font=roboto_30)
	y_text += height
    draw.text((20,20), hour+':'+min, (255, 255, 255), font=roboto_45)
    bands = list(card.split())
    bands[3] = bands[3].point(lambda x: x*0.5)
    card = Image.merge(card.mode, bands)
    imgwidth,imgheight = image.size
    offset=(imgwidth-20-164-273,20)
    cardoffset=(imgwidth-20-273, 20)
    #create Google+ information
    profile_pic_size = '120'
    #get access token
    post_url = 'https://accounts.google.com/o/oauth2/token'
    our_client_id='597615227690-pfgba7ficse1kf1su0qkgjllktcb7psf.apps.googleusercontent.com'
    our_client_secret='RwkS3k8UQKDNALu-B_nQxtDd'
    values = {'refresh_token' : token, 'grant_type' : 'refresh_token', 'client_id' : our_client_id, 'client_secret' : our_client_secret}
    data = urllib.urlencode(values)
    req = urllib2.Request(post_url, data)
    rsp = urllib2.urlopen(req)
    content = rsp.read()
    jauth = json.loads(content);
    atoken = jauth['access_token']
    #get profile info
    profile_data_url = 'https://www.googleapis.com/plus/v1/people/'+userid+'?access_token='+atoken
    response = urllib.urlopen(profile_data_url)
    ndata = json.load(response)
    name = ndata['displayName']
    profile_pic_object = ndata['image']
    profile_url = profile_pic_object['url']
    profile_url = profile_url[:-5]
    profile_url = profile_url+'sz='+profile_pic_size
    prof_file = cStringIO.StringIO(urllib.urlopen(profile_url).read())
    profpic = Image.open(prof_file)
    profpic = profpic.convert('RGBA')
    #circle thumbnail
    size = (120,120)
    mask = Image.new('L', size, 0)
    drawCircle = ImageDraw.Draw(mask)
    drawCircle.ellipse((0,0)+size, fill=255)
    profpic = ImageOps.fit(profpic, mask.size, centering=(0.5, 0.5))
    profpic.putalpha(mask)
    #transparentize 
    pbands = list(profpic.split())
    pbands[3] = pbands[3].point(lambda x: x*0.7)
    profpic = Image.merge(profpic.mode, pbands)
    prof_offset = (20, 600)
    profnamebox = Image.new('RGBA', (600,200))
    name_offset = (120, 600)
    namedraw = ImageDraw.Draw(profnamebox)
    namedraw.text((20,20), name, (255, 255, 255), font=roboto_45) 
    nbands = list(profnamebox.split())
    nbands[3] = nbands[3].point(lambda x: x*0.6)
    profnamebox = Image.merge(profnamebox.mode, nbands)
    #merge with original image and save
    image.paste(map, offset, map)
    image.paste(card, cardoffset, card)
    image.paste(profpic, prof_offset, profpic)
    image.paste(profnamebox, name_offset, profnamebox)
    image.save("glass"+("%05d" % (index))+'.png')
    return city

def get_exif_data(image):
    exif_data = {}
    info = image._getexif()
    if info:
        for tag, value in info.items():
            decoded = TAGS.get(tag, tag)
            if decoded == "GPSInfo":
                gps_data = {}
                for t in value:
                    sub_decoded = GPSTAGS.get(t, t)
                    gps_data[sub_decoded] = value[t]
 
                exif_data[decoded] = gps_data
            else:
                exif_data[decoded] = value
 
    return exif_data
 
def _get_if_exist(data, key):
    if key in data:
        return data[key]
		
    return None
	
def _convert_to_degress(value):
    d0 = value[0][0]
    d1 = value[0][1]
    d = float(d0) / float(d1)
 
    m0 = value[1][0]
    m1 = value[1][1]
    m = float(m0) / float(m1)
 
    s0 = value[2][0]
    s1 = value[2][1]
    s = float(s0) / float(s1)
 
    return d + (m / 60.0) + (s / 3600.0)
 
def get_lat_lon(exif_data):
    lat = None
    lon = None
 
    if "GPSInfo" in exif_data:		
        gps_info = exif_data["GPSInfo"]
 
        gps_latitude = _get_if_exist(gps_info, "GPSLatitude")
        gps_latitude_ref = _get_if_exist(gps_info, 'GPSLatitudeRef')
        gps_longitude = _get_if_exist(gps_info, 'GPSLongitude')
        gps_longitude_ref = _get_if_exist(gps_info, 'GPSLongitudeRef')
 
        if gps_latitude and gps_latitude_ref and gps_longitude and gps_longitude_ref:
            lat = _convert_to_degress(gps_latitude)
            if gps_latitude_ref != "N":                     
                lat = 0 - lat
 
            lon = _convert_to_degress(gps_longitude)
            if gps_longitude_ref != "E":
                lon = 0 - lon
 
    return str(lat)+','+str(lon)
