package org.example.Injector;

/**
 * @author ziyuan
 * @since 2024.09
 */
public enum ColorMappingEnum {

    RED("Red", "Red"),
    GREEN("Green", "Green"),
    BLUE("#Blue", "Blue");


    private final String hexCode;
    private final String colorName;

    ColorMappingEnum(String hexCode, String colorName) {
        this.hexCode = hexCode;
        this.colorName = colorName;
    }

    public String getHexCode() {
        return hexCode;
    }


    public String getColorName() {
        return colorName;
    }

    public static ColorMappingEnum fromName(String name) {
        for (ColorMappingEnum color : ColorMappingEnum.values()) {
            if (color.getColorName().equalsIgnoreCase(name)) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown color name: " + name);
    }

    public static ColorMappingEnum fromHexCode(String hexCode) {
        for (ColorMappingEnum color : ColorMappingEnum.values()) {
            if (color.getHexCode().equalsIgnoreCase(hexCode)) {
                return color;
            }
        }
        throw new IllegalArgumentException("Unknown hex code: " + hexCode);
    }

    @Override
    public String toString() {
        return colorName + " (" + hexCode + ")";
    }

}
