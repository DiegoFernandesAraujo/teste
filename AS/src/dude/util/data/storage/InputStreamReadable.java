/*
 * DuDe - The Duplicate Detection Toolkit
 * 
 * Copyright (C) 2010  Hasso-Plattner-Institut für Softwaresystemtechnik GmbH,
 *                     Potsdam, Germany 
 *
 * This file is part of DuDe.
 * 
 * DuDe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DuDe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DuDe.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package dude.util.data.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.codehaus.jackson.JsonParseException;

import dude.util.data.Jsonable;
import dude.util.data.json.DuDeJsonParser;
import dude.util.data.json.JsonValue;

/**
 * <code>InputStreamReadable</code> can be used to read Json data from any {@link InputStream}.
 * 
 * @author Matthias Pohl
 * 
 * @param <T>
 *            The type of instances that shall be read.
 */
public class InputStreamReadable<T extends Jsonable> implements JsonReadable<T> {

	private class InputStreamReader implements JsonableReader<T> {

		private DuDeJsonParser<T> jsonParser;

		private InputStreamReader() {
			try {
				this.jsonParser = new DuDeJsonParser<T>(InputStreamReadable.this.internalType, InputStreamReadable.this.inputStream);
			} catch (JsonParseException e) {
				throw new IllegalStateException("An JsonParseException occurred while initializing the parser on the passed InputStream.", e);
			} catch (IOException e) {
				throw new IllegalStateException("An IOException occurred while initializing the parser on the passed InputStream.", e);
			}
		}

		@Override
		public Iterator<T> iterator() {
			if (this.jsonParser == null) {
				throw new IllegalStateException("It is only possible to read the InputStream once.");
			}

			return this.jsonParser;
		}

		@Override
		public void close() throws IOException {
			if (this.jsonParser != null) {
				this.jsonParser.close();
				this.jsonParser = null;
			}
		}

	}

	private InputStream inputStream;

	private Class<T> internalType;

	/**
	 * Initializes an <code>InputStreamReadable</code> with no type information. {@link JsonValue} instances will be returned.
	 * 
	 * @param iStream
	 *            The {@link InputStream} from which the data will be read.
	 */
	public InputStreamReadable(InputStream iStream) {
		this(null, iStream);
	}

	/**
	 * Initializes an <code>InputStreamReadable</code> with the passed type information.
	 * @param type The type that is used for generating the instances.
	 * @param iStream The {@link InputStream} from which the data will be read.
	 */
	public InputStreamReadable(Class<T> type, InputStream iStream) {
		if (iStream == null) {
			throw new NullPointerException();
		}

		this.inputStream = iStream;
		this.internalType = type;
	}

	@Override
	public JsonableReader<T> getReader() {
		return new InputStreamReader();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("The size() operation is not supported.");
	}

}
