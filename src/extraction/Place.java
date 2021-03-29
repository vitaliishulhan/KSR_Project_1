package extraction;

public enum Place {
    WEST_GERMANY(0),
    USA(1),
    FRANCE(2),
    UK(3),
    CANADA(4),
    JAPAN(5),
    UNDEFINED(-1);

    private final int value;

    Place(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Place getPlaceFromInt(int value) {
        return switch (value) {
            case 0 -> WEST_GERMANY;
            case 1 -> USA;
            case 2 -> FRANCE;
            case 3 -> UK;
            case 4 -> CANADA;
            case 5 -> JAPAN;
            default -> UNDEFINED;
        };
    }

    public static Place getPlaceFromString(String place) {
        return switch (place) {
            case "west-germany" -> WEST_GERMANY;
            case "usa" -> USA;
            case "france" -> FRANCE;
            case "uk" -> UK;
            case "canada" -> CANADA;
            case "japan" -> JAPAN;
            default -> UNDEFINED;
        };
    }

    public static int getPlacesAmount() {
        return Place.values().length;
    }
}
