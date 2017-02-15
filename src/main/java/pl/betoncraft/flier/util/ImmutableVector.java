/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import org.bukkit.util.Vector;

public class ImmutableVector {
	private final double x, y, z;
	private Double length;
	public ImmutableVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public static ImmutableVector fromVector(Vector vec) {
		return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public ImmutableVector add(ImmutableVector vec) {
		return new ImmutableVector(x + vec.x, y + vec.y, z + vec.z);
	}
	public ImmutableVector subtract(ImmutableVector vec) {
		return new ImmutableVector(x - vec.x, y - vec.y, z - vec.z);
	}
	public ImmutableVector multiply(double m) {
		ImmutableVector result = new ImmutableVector(x*m, y*m, z*m);
		if (length != null) {
			result.length = length * m;
		}
		return result;
	}
	public double length() {
		if (length == null) {
			length = Math.sqrt(x*x + y*y + z*z);
		}
		return length;
	}
	public ImmutableVector normalize() {
		ImmutableVector result = new ImmutableVector(x / length(), y / length(), z / length());
		result.length = 1.0;
		return result;
	}
	public Vector toVector() {
		return new Vector(x, y, z);
	}
	@Override
	public String toString() {
		return String.format("[%.3f,%.3f,%.3f]", x, y, z);
	}
}