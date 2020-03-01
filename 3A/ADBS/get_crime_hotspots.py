import pymongo, sys, os, time, folium, webbrowser
from tabulate import tabulate

start_time = time.time()

c = pymongo.MongoClient("mongodb://localhost:27017/")

db = c["crime_data"]
col = db["street"]

if len(sys.argv) < 2:
    print("Please supply coordinates with script:")
    print("\tget_crime_hotspots.py [lat] [long] (radius)")
    exit(2)

lat = float(sys.argv[1])
lng = float(sys.argv[2])

search_dist = 0

if len(sys.argv) < 4:
    print("No radius supplied, searching 5")
    search_dist = 10
else:
    search_dist = int(sys.argv[3])

print("Checking crime in {0}km radius from {1},{2}\n\thttps://www.google.com/maps/place/{1},{2}\n".format(search_dist, lat, lng))

# Distance / radius of sphere(earth)
radians = search_dist / 6378.1

pipeline = [
  {
    "$geoNear": { 
        "near": [lng, lat],
        "key": "loc_point",
        "spherical": True,
        "maxDistance": radians,
        "distanceField": "dist_from",
    }
  },
  { 
    "$group": { 
      "_id":  "$loc_point", 
      "count": { "$sum":1}  
      }    
  },
]

crimes_in_area = list(db.street.aggregate(pipeline))

map = folium.Map(location=[lat, lng], zoom_start=12)

for entry in crimes_in_area:
  loc = entry["_id"]
  folium.CircleMarker((loc[1], loc[0]), radius=3, weight=entry["count"], color='red', fill_color='red', fill_opacity=.5).add_to(map)


map.save('map.html')
print(os.path.realpath("map.html"))

print("--Runtime: {0:.2f} seconds--".format(time.time() - start_time))
