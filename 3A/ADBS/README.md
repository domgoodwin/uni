

## Mongo db runtime
```
docker run -d -p 27017:27017 mongo:bionic
# exposed on localhost:27017
```
## Python dependencies
```
# Python 3 only
pip3 install tabulate pymongo folium
```
## Concat single input file
```
# Download from https://data.police.uk/data/
unzip ./$DOWNLOADED_ZIP_FILE
# Concat all together in bash
input_folder="./path/to/unzip/folder"
cat "${input_folder}*/*-street.csv" | tail -n+2 > streetdata.csv

# Python script to import data
python import.py

# Alternative for larger data (python loads all into memory and then imports)
# mongoimport --host "localhost:27017" -d crime_data -c street --type csv --file streetdata.csv --headerline
```

## Functions
`python get_top_crime_type.py (region)`
- region: name of police region to get top for (optional, default: all)  
Gets the top crime type for all regions or for single region if specified

Example: `get_top_crime_type.py wiltshire_police`

`python get_crime_hotspots.py [lat] [long] (radius)`
- **lat: Latitude of center search point**
- **long: Longitude of center search point**
- radius: Radius to search from center, in km (optional, default: 10)
Produces map of crime hotspots from radius

`python check_crime_at_point.py [lat] [long] (radius)`
- **lat: Latitude of center search point**
- **long: Longitude of center search point**
- radius: Radius to search from center, in km (optional, default: 5)
Gets sorted crime types in area around point


## Links
- [pymongo APi](https://api.mongodb.com/python/current/)