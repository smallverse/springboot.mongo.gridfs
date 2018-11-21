package springboot.mongo.gridfs;

public enum EnumFileType {
	PIC("PIC", "图片"), ALL("ALL", "所有文件");

	String value;
	String name;

	private EnumFileType(String value, String name) {
		this.value = value;
		this.name = name;
	}

	public static EnumFileType instByValue(String value) {
		for (EnumFileType inst : EnumFileType.values()) {
			if (inst.getValue().equals(value.trim())) {
				return inst;
			}
		}
		return null;
	}

	public static EnumFileType instByName(String name) {
		for (EnumFileType inst : EnumFileType.values()) {
			if (inst.getName().equals(name.trim())) {
				return inst;
			}
		}
		return null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
