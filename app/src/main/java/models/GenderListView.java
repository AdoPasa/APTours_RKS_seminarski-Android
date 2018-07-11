package models;

public class GenderListView {
    public GenderListView(String value, String name) {
        Value = value;
        Name = name;
    }

    public String Value;
    public String Name;

    @Override
    public String toString() {
        return Name;
    }
}
