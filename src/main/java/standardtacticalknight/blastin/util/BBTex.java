package standardtacticalknight.blastin.util;

import net.minecraft.client.render.stitcher.IconCoordinate;

public class BBTex {
	public double height;
	public double width;
	public double depth;

	public IconCoordinate texture;
	public BBTex(double height, double width, double depth) {
		this.height = height;
		this.width = width;
		this.depth = depth;
		this.texture = null;
	}
	public BBTex(double height, double width, double depth, IconCoordinate texture) {
		this.height = height;
		this.width = width;
		this.depth = depth;
		this.texture = texture;
	}
}
