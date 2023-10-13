package reisrijder.reisrijder.model.request;

public class AddressAutoComplete {
    private String address;

    public AddressAutoComplete(String address) {
        this.address = address;
    }

    public AddressAutoComplete() {
        address = null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
