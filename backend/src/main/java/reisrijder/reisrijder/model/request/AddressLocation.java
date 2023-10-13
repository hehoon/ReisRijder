package reisrijder.reisrijder.model.request;

public class AddressLocation {

    private double latitude;
    private double longitude;

    public AddressLocation() {
        this.latitude = 0;
        this.longitude = 0;
    }

    public AddressLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getUniqueValue() {
        return latitude * longitude + longitude;
    }
}
