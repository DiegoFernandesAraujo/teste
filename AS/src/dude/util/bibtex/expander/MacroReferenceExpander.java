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
 * Created on Mar 29, 2003
 * 
 * @author henkel@cs.colorado.edu
 *  
 */
package dude.util.bibtex.expander;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dude.util.bibtex.data.BibtexAbstractEntry;
import dude.util.bibtex.data.BibtexAbstractValue;
import dude.util.bibtex.data.BibtexConcatenatedValue;
import dude.util.bibtex.data.BibtexEntry;
import dude.util.bibtex.data.BibtexFile;
import dude.util.bibtex.data.BibtexMacroDefinition;
import dude.util.bibtex.data.BibtexMacroReference;
import dude.util.bibtex.data.BibtexPreamble;
import dude.util.bibtex.data.BibtexStandardMacros;
import dude.util.bibtex.data.BibtexString;
import dude.util.bibtex.data.BibtexToplevelComment;

/**
 * This expander expands macro references into strings - have a look at the options that can be given in the constructor.
 * 
 * @author henkel
 */
public final class MacroReferenceExpander extends AbstractExpander implements Expander {

	/**
	 * 
	 * This is just a convenience / backward compatibility constructor. It is equivalent to calling MacroReferenceExpander(expandStandardMacros,
	 * expandMonthAbbreviations, removeMacros, true);
	 */

	public MacroReferenceExpander(boolean expandStandardMacros, boolean expandMonthAbbreviations, boolean removeMacros) {
		this(expandStandardMacros, expandMonthAbbreviations, removeMacros, true);
	}

	/**
	 * @param expandStandardMacros
	 *            Expand all standard macros as defined in the bibtex file plain.bst
	 * @param expandMonthAbbreviations
	 *            Expand all month abbreviations. This parameter has precedence over the first one (note that the month abbreviations are a subset of
	 *            the standard macros).
	 * @param removeMacros
	 *            Remove all macros from the bibtex model.
	 * @param throwAllExpansionExceptions
	 *            Setting this to true means that all exceptions will be thrown immediately. Otherwise, the expander will skip over things it can't
	 *            expand and you can use getExceptions to retrieve the exceptions later
	 */
	public MacroReferenceExpander(boolean expandStandardMacros, boolean expandMonthAbbreviations, boolean removeMacros,
			boolean throwAllExpansionExceptions) {
		super(throwAllExpansionExceptions);
		this.expandMonthAbbreviations = expandMonthAbbreviations;
		this.expandStandardMacros = expandStandardMacros;
		this.removeMacros = removeMacros;
	}

	private final boolean expandStandardMacros;
	private final boolean expandMonthAbbreviations;
	private final boolean removeMacros;

	/**
	 * This method walks over all entries in a BibtexFile and expands macro references. Thus, after the execution of this function, all fields contain
	 * BibtexString entries. Exceptions: 1) the crossref fields 2) 3-letter month abbreviations and standard macros, if specified in the constructor
	 * (MacroReferenceExpander).
	 * 
	 * If you use the flag throwAllExpansionExceptions set to false, you can retrieve all the exceptions using getExceptions()
	 * 
	 * @param bibtexFile
	 */
	@Override
	public void expand(BibtexFile bibtexFile) throws ExpansionException {
		HashMap<String, BibtexAbstractValue> stringKey2StringValue = new HashMap<String, BibtexAbstractValue>();
		for (Iterator<BibtexAbstractEntry> entryIt = bibtexFile.getEntries().iterator(); entryIt.hasNext();) {
			BibtexAbstractEntry abstractEntry = entryIt.next();
			if (abstractEntry instanceof BibtexMacroDefinition) {
				BibtexMacroDefinition bibtexStringDefinition = (BibtexMacroDefinition) abstractEntry;
				BibtexAbstractValue simplifiedValue = simplify(bibtexFile, bibtexStringDefinition.getValue(), stringKey2StringValue);
				bibtexStringDefinition.setValue(simplifiedValue);
				if (this.removeMacros) {
					bibtexFile.removeEntry(bibtexStringDefinition);
				}
				stringKey2StringValue.put(bibtexStringDefinition.getKey().toLowerCase(), simplifiedValue);

			} else if (abstractEntry instanceof BibtexPreamble) {
				BibtexPreamble preamble = (BibtexPreamble) abstractEntry;
				preamble.setContent(simplify(bibtexFile, preamble.getContent(), stringKey2StringValue));
			} else if (abstractEntry instanceof BibtexEntry) {
				BibtexEntry entry = (BibtexEntry) abstractEntry;
				for (Iterator<Map.Entry<String, BibtexAbstractValue>> fieldIt = entry.getFields().entrySet().iterator(); fieldIt.hasNext();) {
					Map.Entry<String, BibtexAbstractValue> field = fieldIt.next();
					if (!(field.getValue() instanceof BibtexString)) {
						entry.setField(field.getKey(), simplify(bibtexFile, field.getValue(), stringKey2StringValue));
					}
				}
			} else if (abstractEntry instanceof BibtexToplevelComment) {
				// don't do anything here ...
			} else {
				throwExpansionException("MacroReferenceExpander.expand(): I don't support \"" + abstractEntry.getClass().getName()
						+ "\". Use the force, read the source!");
			}
		}
		finishExpansion();
	}

	private BibtexAbstractValue simplify(BibtexFile factory, BibtexAbstractValue compositeValue,
			HashMap<String, BibtexAbstractValue> stringKey2StringValue) throws ExpansionException {
		if (compositeValue instanceof BibtexString)
			return compositeValue;
		if (compositeValue instanceof BibtexMacroReference) {
			BibtexMacroReference reference = (BibtexMacroReference) compositeValue;
			String key = reference.getKey();

			BibtexString simplifiedValue = (BibtexString) stringKey2StringValue.get(key);
			if (simplifiedValue == null) {

				if (!this.expandMonthAbbreviations && BibtexStandardMacros.isMonthAbbreviation(key))
					return reference;
				if (!this.expandStandardMacros && BibtexStandardMacros.isStandardMacro(key))
					return reference;

				if (BibtexStandardMacros.isStandardMacro(key)) {
					return factory.makeString(BibtexStandardMacros.resolveStandardMacro(key));
				}

				throwExpansionException("Invalid macro reference (target does not exist): \"" + reference.getKey() + "\"");
				// if the macro definition does not exist: write macro reference instead
				return factory.makeString(key);
			}
			return simplifiedValue;
		}
		if (compositeValue instanceof BibtexConcatenatedValue) {
			BibtexConcatenatedValue concatenatedValue = (BibtexConcatenatedValue) compositeValue;
			BibtexAbstractValue left = simplify(factory, concatenatedValue.getLeft(), stringKey2StringValue);
			BibtexAbstractValue right = simplify(factory, concatenatedValue.getRight(), stringKey2StringValue);
			if (left instanceof BibtexString && right instanceof BibtexString) {
				return factory.makeString(((BibtexString) left).getContent() + ((BibtexString) right).getContent());
			}

			return factory.makeConcatenatedValue(left, right);
		}
		throwExpansionException("MacroReferenceExpander.simplify(): I don't support \"" + compositeValue.getClass().getName()
				+ "\". Use the force, read the source!");
		return factory.makeString(""); // so we don't know what to do, let's
		// use the empty string
	}

}
