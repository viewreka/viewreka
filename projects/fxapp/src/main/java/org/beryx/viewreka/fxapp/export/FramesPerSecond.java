package org.beryx.viewreka.fxapp.export;

/**
 * Enum with predefined frames-per-second values.
 */
public enum FramesPerSecond {
	FPS_24(24),
	FPS_25(25),
	FPS_30(30),
	FPS_48(48);

	private final int fps;

	private FramesPerSecond(int fps) {
		this.fps = fps;
	}

	public int getFps() {
		return fps;
	}

	@Override
	public String toString() {
		return "" + fps;
	}
}
