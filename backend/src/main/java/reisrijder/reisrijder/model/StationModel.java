package reisrijder.reisrijder.model;

import reisrijder.reisrijder.exceptions.InternalServerErrorException;

import static reisrijder.reisrijder.exceptions.ExceptionHandler.handleException;

public class StationModel implements Cloneable {
    private String name;
    private double distance;
    private double latitude;
    private double longitude;
    private boolean ovBike;
    private boolean ovEbike;
    private boolean baggage;

    public StationModel(String name, double latitude, double longitude) {
        this.name = name;
        this.distance = 0;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ovBike = false;
        this.ovEbike = false;
    }

    public String getName() {
        return name;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Calculates the distance in meters between two geolocation coordinates on earth
     * @param latitude latitude of a different location
     * @param longitude longitude of a different location
     * @link <a href="https://stackoverflow.com/questions/639695/how-to-convert-latitude-or-longitude-to-meters">StackOverflow</a> Thanks to b-h-
     * @link <a href="https://en.wikipedia.org/wiki/Haversine_formula">Wikipedia</a>
     */
    public void setDistance(double latitude, double longitude) {
        double radiusEarth = 6378137;// in meters (https://nssdc.gsfc.nasa.gov/planetary/factsheet/earthfact.html)
        double deltaLat = Math.toRadians(latitude) - Math.toRadians(this.latitude);
        double deltaLon = Math.toRadians(longitude) - Math.toRadians(this.longitude);
        double step1 = Math.pow(Math.sin(deltaLat/2), 2) + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(latitude)) * Math.pow(Math.sin(deltaLon/2), 2);
        double step2 = 2 * Math.atan2(Math.sqrt(step1), Math.sqrt(1-step1));
        double distance = radiusEarth * step2;
        setDistance(distance);
    }

    public double getDistance() {
        return distance;
    }

    public int getDistanceMeters() {
        return (int) getDistance();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean isOvBike() {
        return ovBike;
    }

    public void setOvBike(boolean ovBike) {
        this.ovBike = ovBike;
    }

    public boolean isOvEbike() {
        return ovEbike;
    }

    public void setOvEbike(boolean ovEbike) {
        this.ovEbike = ovEbike;
    }

    public boolean isBaggage() {
        return baggage;
    }

    public void setBaggage(boolean baggage) {
        this.baggage = baggage;
    }

    public boolean matchesParameters(boolean ovBike, boolean ovEbike, boolean baggage) {
        return (!ovBike || this.ovBike)  && (!ovEbike || this.ovEbike) && (!baggage || this.baggage);
    }

    @Override
    public StationModel clone() {
        try {
            return (StationModel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Oops, something went wrong");
        }
    }
}
