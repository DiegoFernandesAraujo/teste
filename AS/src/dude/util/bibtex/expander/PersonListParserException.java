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

/*
 * Created on Mar 28, 2003
 *
 * @author henkel@cs.colorado.edu
 * 
 */
package dude.util.bibtex.expander;

/**
 * @author henkel
 */
class PersonListParserException extends java.lang.Exception {

	private static final long serialVersionUID = -7666949411899447202L;

	PersonListParserException(String message) {
		super(message);
	}
}