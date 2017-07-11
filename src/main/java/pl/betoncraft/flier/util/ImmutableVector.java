/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
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