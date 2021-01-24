/*
 * Copyright 2015 CINCHEO SAS <renaud.pawlak@cincheo.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jsweet.plugin.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPropertyPage;

/**
 * A field editor preference page that can also be used as a property page.
 * <p>
 * Adapted from
 * http://www.eclipse.org/articles/Article-Mutatis-mutandis/overlay-pages.html.
 * <p>
 * Adapted from the TypeScript Eclipse plugin (author: dcicerone).
 */
abstract class FieldEditorProjectPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

	private final List<FieldEditor> fields = new ArrayList<>();
	protected final Map<String, FieldEditor> fieldMap = new HashMap<>();
	private Link configureWorkspaceLink;
	private IAdaptable element;
	private ProjectPreferenceStore projectPreferenceStore;
	private Button projectSpecificCheckbox;

	protected FieldEditorProjectPreferencePage(final int style) {
		super(style);
	}

	@Override
	public IAdaptable getElement() {
		return element;
	}

	@Override
	public void setElement(final IAdaptable element) {
		this.element = element;
	}

	protected abstract String getPreferenceNodeId();

	protected abstract String getSentinelPropertyName();

	@Override
	protected Control createContents(final Composite parent) {
		if (isPropertyPage()) { // properties page
			final Composite composite = new Composite(parent, SWT.NONE);
			final GridLayout layout = new GridLayout(2, false);
			layout.marginWidth = 0;
			layout.marginHeight = 0;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			// enable project specific settings
			projectSpecificCheckbox = new Button(composite, SWT.CHECK);
			projectSpecificCheckbox.setFont(parent.getFont());
			projectSpecificCheckbox.setSelection(projectPreferenceStore.getProjectSpecificSettings());
			projectSpecificCheckbox.setText("Enable project specific settings");
			projectSpecificCheckbox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					final boolean projectSpecific = isProjectSpecific();
					projectPreferenceStore.setProjectSpecificSettings(projectSpecific);
					configureWorkspaceLink.setEnabled(!projectSpecific);
					updateFieldEditors();
				}
			});
			// configure workspace settings
			configureWorkspaceLink = new Link(composite, SWT.NONE);
			configureWorkspaceLink.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
			configureWorkspaceLink.setText("<a>Configure Workspace Settings...</a>");
			configureWorkspaceLink.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					configureWorkspaceSettings();
				}
			});
			// horizontal separator
			final Label horizontalSeparator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			horizontalSeparator.setLayoutData(new GridData(GridData.FILL, SWT.TOP, true, false, 2, 1));
		}
		return super.createContents(parent);
	}

	@Override
	public void createControl(final Composite parent) {
		if (isPropertyPage()) {
			final IProject project = element.getAdapter(IProject.class);
			final String sentinelPropertyName = getSentinelPropertyName();
			projectPreferenceStore = new ProjectPreferenceStore(project, super.getPreferenceStore(),
					sentinelPropertyName);
		}
		super.createControl(parent);
		if (isPropertyPage()) {
			updateFieldEditors();
		}
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		if (projectPreferenceStore != null)
			return projectPreferenceStore;
		return super.getPreferenceStore();
	}

	@Override
	protected void addField(final FieldEditor editor) {
		fields.add(editor);
		fieldMap.put(editor.getPreferenceName(), editor);
		super.addField(editor);
	}

	protected final boolean isPageEnabled() {
		return !isPropertyPage() || isProjectSpecific();
	}

	protected final boolean isPropertyPage() {
		return element != null;
	}

	protected final boolean isProjectSpecific() {
		return isPropertyPage() && projectSpecificCheckbox.getSelection();
	}

	@Override
	protected void performDefaults() {
		if (isPropertyPage()) {
			projectSpecificCheckbox.setSelection(false);
			configureWorkspaceLink.setEnabled(false);
			updateFieldEditors();
		}
		super.performDefaults();
	}

	protected void updateFieldEditors() {
		final boolean pageEnabled = isPageEnabled();
		final Composite parent = getFieldEditorParent();
		for (final FieldEditor field : fields) {
			field.setEnabled(pageEnabled, parent);
		}
	}

	private void configureWorkspaceSettings() {
		final String preferenceNodeId = getPreferenceNodeId();
		final IPreferencePage preferencePage = newPreferencePage();
		final IPreferenceNode preferenceNode = new PreferenceNode(preferenceNodeId, preferencePage);
		final PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(preferenceNode);
		final PreferenceDialog dialog = new PreferenceDialog(getControl().getShell(), manager);
		BusyIndicator.showWhile(this.getControl().getDisplay(), new Runnable() {
			@Override
			public void run() {
				dialog.create();
				dialog.setMessage(preferenceNode.getLabelText());
				dialog.open();
			}
		});
	}

	private IPreferencePage newPreferencePage() {
		try {
			final IPreferencePage preferencePage = this.getClass().newInstance();
			preferencePage.setTitle(getTitle());
			return preferencePage;
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

	public List<FieldEditor> getFields() {
		return fields;
	}

	@SuppressWarnings("unchecked")
	public <T extends FieldEditor> T getField(final String preferenceName) {
		return (T) fieldMap.get(preferenceName);
	}

}