# Flight Tracker

Flight Tracker is an android application to track flights using [The OpenSky Network API](https://openskynetwork.github.io/opensky-api/)

## Project Structure

### SDK Version:

Compile SDK

> The application is built using the Android SDK version 34.

## Configuration Details

### Minimum SDK Version

> The minimum Android version required to run the application is Android 7.0 (API level 24).

### Target SDK Version

> The application is designed to be compatible with Android API level 31.

## Installation

On cmd clone the project

```bash
git clone https://github.com/fodedoumbouya/flight_tracker.git
```

Install all dependencies :

```bash
./gradlew build
```
## Usage

[The OpenSky Network API](https://openskynetwork.github.io/opensky-api/) can sometimes not work so we use JSON File with hard data to make it work.

You can find these files on :

```
└── app
    └── src
        └── assets
            ├── flight.json
            ├── flights_list_sample.json

```
**flights_list_sample.json** : A list of flights for a certain airport which arrived/departure within a given time interval [begin, end]. Which corresponds to the endpoint :

> https://opensky-network.org/api/flights/departure?airport=...&begin=...&end=...

**flight.json** : An object that represent the trajectory for a certain aircraft at a given time. The trajectory is a list of waypoints containing position, barometric altitude, true track and an on-ground flag. Which corresponds to endpoint :

> https://opensky-network.org/api/tracks/all?icao24=...&time=...

## Contributor

Edouard Chevenslove [@Edouard1er](https://github.com/Edouard1er)

Fode Doumbouya [@fodedoumbouya](https://github.com/fodedoumbouya)

Idricealy Mourtadhoi [@Idricealy](https://github.com/Idricealy)