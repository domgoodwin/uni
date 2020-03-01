import pymongo, sys, os, time
from tabulate import tabulate

start_time = time.time()

c = pymongo.MongoClient("mongodb://localhost:27017/")

db = c["crime_data"]
col = db["street"]

places = []
multi_search = False

sample_size = -1

if os.getenv('SAMPLE_DATA', "false") == "true":
    print("Sampling data for queries")
    sample_size = round(0.04 * col.count_documents({}))
    print("Sample size: " + str(sample_size))

if len(sys.argv) < 2:
    print("No place supplied, searching all\n")
    places = list(db.street.distinct("reported_by"))
    multi_search = True
else:
    places.append(sys.argv[1])

pipeline = [
  { "$group": 
    { 
      "_id":  "$reported_by", 
      "count": { "$sum":1}  
    } 
  }
]
total_per_place = list(db.street.aggregate(pipeline))

output = [["Place", "Top Crime", "Count", "Percentage for location"]]
for place in places:
    total_count = [x for x in total_per_place if x["_id"] == place][0]["count"]
    pipeline = [
      {
        "$match": { "reported_by": place}
      },
      { "$group": 
        { 
          "_id": "$crime_type", 
          "count": { "$sum":1}  
        } 
      },
      { 
        "$sort": { "count": -1 }
      },
      {
        "$limit": 1
      }
    ]
    top_crime = list(db.street.aggregate(pipeline))[0]

    percentage_of_area = (top_crime["count"] / total_count) * 100

    output.append([place, top_crime["_id"], top_crime["count"], percentage_of_area])

print(
  tabulate(
    output,
    headers="firstrow"
  )
)

