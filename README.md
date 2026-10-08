# Nearby Parking Lookup

Allow the use of an external database to obtain detailed data for nearby parking spaces.

## Description

The project exposes a GET endpoint (/parkings) that allows for lookup of nearby parking spots in France based on
coordinates passed as parameters.
The data can then be parsed in an application and displayed on a map.  
Default configuration uses data provided in the OpenStreetMap database 'osm-france-parking-area' due to it providing
rich data on
* number of parking spots
* coordinates
* type of spots available
* distance from lookup spot


## Getting Started

### Dependencies

* Java 20
* Sprint Boot 3.3


### Executing program

* Build the application
```
mvn clean install
```
* Run the Spring Boot application
```
mvn spring-boot:run
```

## Help

Example URL:
```
http://localhost:8080/parkings?latitude=43.32535569739267&longitude=5.42178123862934&gratuit=true&limit=10
```
Parameters:
* longitude - X coordinate of the lookup spot
* latitude - Y coordinate of the lookup spot
* gratuit - (OPTIONAL) is search narrowed to free spots
* limit - (OPTIONAL) number of desired spots in the response (0-100). The default value is 10

## Authors

Karol Simka  
[ras.p.voice@gmail.com](mailto:ras.p.voice@gmail.com)

## Version History


* 0.1
    * Initial Release
