import pymongo, sys, os, time
from tabulate import tabulate

start_time = time.time()

c = pymongo.MongoClient("mongodb://localhost:27017/")

db = c["crime_data"]
col = db["street"]

if len(sys.argv) < 3:
    print("Please supply coordinates with script:")
    print("\tcheck_crime_at_point.py [lat] [long] (radius)")
    exit(2)

lat = float(sys.argv[1])
lng = float(sys.argv[2])

search_dist = 0

if len(sys.argv) < 4:
    print("No radius supplied, searching 5km")
    search_dist = 5
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
      "_id":  "$crime_type", 
      "count": { "$sum":1}  
      }    
  },
  { 
    "$sort": { "count": -1 }
  }
]

crimes_in_area = list(db.street.aggregate(pipeline))

output = [["Type", "Count"]]

for entry in crimes_in_area:
  output.append([entry["_id"], entry["count"]])


print(
  tabulate(
    output,
    headers="firstrow"
  )
)


print("--Runtime: {0:.2f} seconds--".format(time.time() - start_time))
