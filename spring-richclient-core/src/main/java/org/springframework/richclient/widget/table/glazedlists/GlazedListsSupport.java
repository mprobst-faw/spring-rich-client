package org.springframework.richclient.widget.table.glazedlists;

import java.util.Comparator;

import org.springframework.richclient.widget.table.TableDescription;

import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;

/**
 * GlazedListsSupport
 */
public class GlazedListsSupport {
	interface AdvancedWritableTableFormat extends AdvancedTableFormat, WritableTableFormat {
	}

	private static Comparator lowerCaseStringComparator = null;

	public static String[] makeFilterProperties(TableDescription desc) {
		return desc.getPropertiesInTextFilter();
	}

	/**
	 * Conversion of RCP TableDescription to GlazedLists TableFormat
	 *
	 * @param desc
	 * @return AdvancedWritableTableFormat
	 */
	public static TableFormat makeTableFormat(final TableDescription desc) {
		return new AdvancedWritableTableFormat() {
			@Override
			public Class getColumnClass(int i) {
				return desc.getType(i);
			}

			@Override
			public Comparator getColumnComparator(int i) {
				Comparator comp = desc.getColumnComparator(i);
				if (comp != null) {
					return comp;
				}

				Class type = getColumnClass(i);
				if (Boolean.class.isAssignableFrom(type) || Boolean.TYPE.isAssignableFrom(type)) {
					return GlazedLists.booleanComparator();
				} else if (String.class.isAssignableFrom(type)) {
					return getLowerCaseStringComparator();
				} else if (Comparable.class.isAssignableFrom(type)) {
					return GlazedLists.comparableComparator();
				} else {
					return null;
				}
			}

			@Override
			public int getColumnCount() {
				return desc.getColumnCount();
			}

			@Override
			public String getColumnName(int i) {
				return desc.getHeader(i);
			}

			@Override
			public Object getColumnValue(Object obj, int i) {
				return desc.getValue(obj, i);
			}

			@Override
			public boolean isEditable(Object baseObject, int column) {
				return desc.getColumnEditor(column) != null;
			}

			@Override
			public Object setColumnValue(Object baseObject, Object editedValue, int column) {
				desc.setValue(baseObject, column, editedValue);
				return baseObject;
			}
		};
	}

	public static Comparator getLowerCaseStringComparator() {
		if (lowerCaseStringComparator == null) {
			lowerCaseStringComparator = createLowerCaseStringComparator();
		}
		return lowerCaseStringComparator;
	}

	private static Comparator createLowerCaseStringComparator() {
		return new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				String str1 = (String) o1;
				String str2 = (String) o2;
				if (str1 == null) {
					if (str2 == null) {
						return 0;
					}
					return -1;
				} else if (str2 == null) {
					return 1;
				}

				return str1.toLowerCase().compareTo(str2.toLowerCase());
			}
		};
	}
}
