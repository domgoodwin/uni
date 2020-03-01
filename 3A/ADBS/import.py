import pymongo

# All crime data in all areas for 3 years
# Based off crime type, predict outcome?
# Do outcomes change per region?

c = pymongo.MongoClient("mongodb://localhost:27017/")

db = c["crime_data"]
col = db["street"]

def getInput():
    f = open('streetdata2.csv', 'r')
    content =  f.read().split("\n")
    headers = ["crime_id", "month", "reported_by", "falls_within", "longitude", "latitude", "location", "lsoa_code", "lsoa_name", "crime_type", "last_outcome_category", "context"]

    # For each line in file (bar headers), zip with headers and convert to dict
    data = [dict(zip(headers, line.split(","))) for line in content if line != ""]
    to_add = []
    for entry in data:
        try:
            entry['loc_point'] =  [float(entry['longitude']), float(entry['latitude'])]
            to_add.append(entry)
        except ValueError:
            entry['loc_point'] = [None, None]
        except KeyError:
            print("Key error")
            print(entry)
    input = col.insert_many(to_add)
    print(input)




getInput()
col.create_index([("crime_id", pymongo.DESCENDING), ("reported_by", pymongo.DESCENDING), ("crime_type", pymongo.DESCENDING)], background=True, name="outcome")
col.create_index([("loc_point", pymongo.GEO2D)])