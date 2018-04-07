/*
 * The core of the core of all my projects!
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.core.math;

public class Vector2D {

	public float x, y;

	public Vector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "(" + this.x + "," + this.y + ")";
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public float lengthSquared() {
		return (x * x) + (y * y);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Vector2D) {
			Vector2D p = (Vector2D) o;
			return p.x == x && p.y == y;
		}
		return false;
	}

	public Vector2D reverse() {
		return new Vector2D(-x, -y);
	}

	public Vector2D sum(Vector2D b) {
		return new Vector2D(x + b.x, y + b.y);
	}

	public Vector2D sub(Vector2D b) {
		return new Vector2D(x - b.x, y - b.y);
	}

	public float dot(Vector2D vec) {
		return (x * vec.x) + (y * vec.y);
	}

	public float cross(Vector2D a, Vector2D b) {
		return a.cross(b);
	}

	public float cross(Vector2D vec) {
		return x * vec.y - y * vec.x;
	}

	public float distanceSquared(Vector2D other) {
		float dx = other.x - x;
		float dy = other.y - y;

		return (dx * dx) + (dy * dy);
	}

	public float distance(Vector2D other) {
		return (float) Math.sqrt(distanceSquared(other));
	}

	public float dotProduct(Vector2D other) {
		return other.x * x + other.y * y;
	}

	public Vector2D normalize() {
		float magnitude = (float) Math.sqrt(dotProduct(this));
		return new Vector2D(x / magnitude, y / magnitude);
	}

	public Vector2D mult(float scalar) {
		return new Vector2D(x * scalar, y * scalar);
	}
}
