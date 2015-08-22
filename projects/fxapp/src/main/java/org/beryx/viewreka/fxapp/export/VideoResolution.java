package org.beryx.viewreka.fxapp.export;

/**
 * Enum for video resolution values.
 */
public enum VideoResolution {
	R_640_360(640, 360),
	R_720_480(720, 480),
	R_720_576(720, 576),
	R_768_576(768, 576),
	R_800_480(800, 480),
	R_960_540(960, 540),
	R_960_640(960, 640),
	R_1024_600(1024, 600),
	R_1280_720(1280, 720),
	R_1920_1080(1920, 1080),
	R_3840_2160(3840, 2160),
	R_2560_1440(2560, 1440),
	R_2048_1536(2048, 1536),
	R_4520_2540(4520, 2540),
	R_4096_3072(4096, 3072),
	R_7680_4320(7680, 4320);

	private final int width;
	private final int height;

	private VideoResolution(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return width + " * " + height;
	}
}
