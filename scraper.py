import json
import requests
import sqlite3

conn = sqlite3.connect('crime12.db')
c = conn.cursor()
try:
	c.execute('''CREATE TABLE crimes
    	         (id INTEGER PRIMARY KEY,lat real, lon real,type VARCHAR(10), date DATE, time int,  address text, link text)''')
except:
	print "Table already exists"

#top-bottom, right-left

start_lat = 33.439327
start_lon = -111.980288
end_lat = 33.320657
end_lon = -111.893008
# top-left:33.439327, -111.980288
# top-right:33.439327, -111.893008
# bottom-left: 33.320657, -111.980288
# bottom-right: 33.320657, -111.893008 
incr = .001
print (start_lat-end_lat)/incr
print (start_lon-end_lon)/incr

#500 feet.

added_count = 0


def getTimeInt(i_time):
	time_r = i_time.split(":")
	i_hours = int(time_r[0])
	i_mins = int(time_r[1].split(' ')[0])
	
	if i_hours == 12:
		i_hours -= 12
	if i_time.split(' ')[1] == 'PM':
		i_hours += 12
	return i_hours*60 + i_mins
def parse(reply):

	crimes = json.loads(reply)['crimes']
	# print len(crimes)
	if len(crimes)== 50:
		print 'maxxed!'
	for crime in crimes:
		i_id = crime['cdid']
		i_lon = crime['lon']
		i_lat = crime['lat']
		i_link =  crime['link']
		i_address = crime['address']
		i_datetime =  crime['date']
		i_date, i_time = i_datetime.split(' ',1)
		i_date = '20' + i_date[6:]+"/" + i_date[:5]
		i_date = i_date.replace('/','-')
		i_type = crime['type']
		try:
			c.execute("INSERT INTO crimes VALUES (%s,%s,%s,'%s','%s',%s,'%s','%s')" % (str(i_id),str(i_lat), str(i_lon), i_type, i_date,str(getTimeInt(i_time)), i_address, i_link))
			global added_count
			added_count += 1
		except sqlite3.IntegrityError as detail:
			if "UNIQUE" in str(detail):
				pass
			else:
				print detail

	conn.commit()



payload = {"key":"b9d2f64f622af2bbcb1f0ca47de7943935779b3f2f1cb6cc5ea176cf7688", "radius":incr}
req = requests.get("http://api.spotcrime.com/crimes.json", params = payload)
reply = json.loads(req.text)

# lat
rpt = 0
lat = start_lat
count = 0
while lat > end_lat:
	print count
	lon = start_lon
	while lon < end_lon:
		payload['lon'] = str(lon)
		payload['lat'] = str(lat)
		r = requests.get("http://api.spotcrime.com/crimes.json", params = payload)
		parse(r.text)
		lon += incr
	lat -= incr
	count += 1
