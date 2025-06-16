package dqteam.siret.model;

public enum EnumWorklogType {
	START_END(1),
    PROJECT_TIME(2),
    PERSONAL_SCHEDULE(3);
    
    private final int value;
    
    EnumWorklogType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static EnumWorklogType fromValue(int value) {
        for (EnumWorklogType type : EnumWorklogType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid WorklogType value: " + value);
    }
}
