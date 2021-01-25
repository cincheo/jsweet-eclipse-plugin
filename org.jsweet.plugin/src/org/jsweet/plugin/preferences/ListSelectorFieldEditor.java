package org.jsweet.plugin.preferences;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A field editor for a list of semicolon-separated strings, which allows for a
 * combo box selection.
 */
public class ListSelectorFieldEditor extends FieldEditor {

	private Combo combo;

	private String[] values;

	/**
	 * Create the field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent composite
	 */
	public ListSelectorFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		if (numColumns > 1) {
			Control control = getLabelControl();
			int left = numColumns;
			if (control != null) {
				((GridData) control.getLayoutData()).horizontalSpan = 1;
				left = left - 1;
			}
			((GridData) combo.getLayoutData()).horizontalSpan = left;
		} else {
			Control control = getLabelControl();
			if (control != null) {
				((GridData) control.getLayoutData()).horizontalSpan = 1;
			}
			((GridData) combo.getLayoutData()).horizontalSpan = 1;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt. widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		int comboC = 1;
		if (numColumns > 1) {
			comboC = numColumns - 1;
		}
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		control.setLayoutData(gd);
		control = getComboBoxControl(parent);
		gd = new GridData();
		gd.horizontalSpan = comboC;
		gd.horizontalAlignment = GridData.FILL;
		control.setLayoutData(gd);
		control.setFont(parent.getFont());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		updateComboForValue(getPreferenceStore().getString(getPreferenceName()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		updateComboForValue(getPreferenceStore().getDefaultString(getPreferenceName()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		if (values == null) {
			getPreferenceStore().setToDefault(getPreferenceName());
			return;
		}
		getPreferenceStore().setValue(getPreferenceName(), StringUtils.join(values, ";"));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/*
	 * Lazily create and return the Combo control.
	 */
	private Combo getComboBoxControl(Composite parent) {
		if (combo == null) {
			combo = new Combo(parent, SWT.READ_ONLY);
			combo.setFont(parent.getFont());
			fillComboFromValues();
		}
		return combo;
	}

	private void fillComboFromValues() {
		combo.removeAll();
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				combo.add(values[i], i);
			}
			combo.select(0);
			combo.setText(values[0]);
		}
	}

	public void addValue(String value) {
		String oldValue = StringUtils.join(values);
		values = ArrayUtils.add(values, value);
		combo.add(value);
		combo.select(values.length - 1);
		combo.setText(values[values.length - 1]);
		fireValueChanged(VALUE, oldValue, StringUtils.join(values));
	}

	public void removeValue(String value) {
		int index = ArrayUtils.indexOf(values, value);
		if (index != -1) {
			String oldValue = StringUtils.join(values);
			values = ArrayUtils.remove(values, index);
			combo.remove(index);
			combo.select(0);
			combo.setText(values[0]);
			fireValueChanged(VALUE, oldValue, StringUtils.join(values));
		}
	}

	/*
	 * Set the name in the combo widget to match the specified value.
	 */
	private void updateComboForValue(String value) {
		values = value.split(";");
		fillComboFromValues();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#setEnabled(boolean,
	 * org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getComboBoxControl(parent).setEnabled(enabled);
	}

	public final Combo getCombo() {
		return combo;
	}

}
