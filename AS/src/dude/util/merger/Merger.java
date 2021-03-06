/*
 * DuDe - The Duplicate Detection Toolkit
 * 
 * Copyright (C) 2011  Hasso-Plattner-Institut für Softwaresystemtechnik GmbH,
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

package dude.util.merger;

import dude.util.data.DuDeObject;

/**
 * <code>Merger</code> is used to merge two {@link DuDeObject}s into one new {@link DuDeObject}.
 * 
 * @author Johannes Dyck
 * 
 */
public interface Merger {
	
	/**
	 * Merges two {@link DuDeObject}s into one new {@link DuDeObject}.
	 * 
	 * @param leftElement
	 *            One of the <code>DuDeObjects</code> that shall be merged. 
	 * @param rightElement
	 *            The other <code>DuDeObject</code> that shall be merged.
	 * @return A new <code>DuDeObject</code> created by merging the input <code>DuDeObjects</code>.
	 */	
	public DuDeObject merge(DuDeObject leftElement, DuDeObject rightElement);
}
