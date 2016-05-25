package org.jsweet.plugin.preferences;

import static org.jsweet.plugin.preferences.Preferences.DEFAULT_PROFILE_NAME;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jsweet.plugin.JSweetPlugin;
import org.jsweet.plugin.builder.JSweetBuilder;

public final class JSweetPreferencePage extends FieldEditorProjectPreferencePage implements IWorkbenchPreferencePage {
	private boolean compilerPreferencesModified;
	private Button createProfile;
	private Button deleteProfile;
	private String currentProfile = DEFAULT_PROFILE_NAME;
	ListSelectorFieldEditor profileSelector;

	public JSweetPreferencePage() {
		super(FieldEditorPreferencePage.GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		final boolean process;
		// offer to rebuild the workspace if the compiler preferences were
		// modified
		if (this.compilerPreferencesModified) {
			String title = "JSweet";
			String message = "Changing configuration requires a full build. Do you want to start a build now?";
			String[] buttonLabels = new String[] { IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL,
					IDialogConstants.YES_LABEL };
			MessageDialog dialog = new MessageDialog(this.getShell(), title, null, message, MessageDialog.QUESTION,
					buttonLabels, 2);
			int result = dialog.open();
			if (result == 1) { // cancel
				process = false;
			} else {
				// yes/no
				process = super.performOk();
				// rebuild the workspace
				if (result == 2) {
					if (this.isPropertyPage()) {
						IProject project = (IProject) this.getElement().getAdapter(IProject.class);
						JSweetBuilder.cleanProject(project);
					} else {
						JSweetBuilder.cleanWorkspace();
					}
				}
			}
			this.compilerPreferencesModified = false;
		} else {
			process = super.performOk();
		}
		return process;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		Object source = event.getSource();
		if (event.getProperty().equals(FieldEditor.VALUE)) {
			this.updateFieldEditors();
		}
		if (getFields().contains(source)) {
			this.compilerPreferencesModified = true;
		}
	}

	private void applyProfile(String profile) {
		currentProfile = profile;
		for (Entry<String, FieldEditor> entry : fieldMap.entrySet()) {
			if (Preferences.PROFILES().equals(entry.getKey())) {
				continue;
			}
			entry.getValue().setPreferenceName(Preferences.getProfilePrefix(currentProfile) + entry.getKey());
			entry.getValue().load();
		}
	}

	@Override
	protected void createFieldEditors() {
		if (this.isPropertyPage()) {
			this.addField(profileSelector = new ListSelectorFieldEditor(Preferences.PROFILES(), "Profile",
					getFieldEditorParent()));
			profileSelector.getCombo().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					applyProfile(profileSelector.getCombo().getText());
					updateFieldEditors();
				}
			});
			Composite composite = new Composite(getFieldEditorParent(), SWT.NONE);
			composite.setLayoutData(new GridData(GridData.END, SWT.TOP, true, true, 2, 1));
			RowLayout rl = new RowLayout();
			rl.marginBottom = rl.marginHeight = rl.marginLeft = rl.marginRight = 0;
			composite.setLayout(rl);
			createProfile = new Button(composite, SWT.PUSH);
			createProfile.setFont(getFieldEditorParent().getFont());
			createProfile.setText("Create new profile");
			createProfile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					InputDialog d = new InputDialog(null, "Create new profile", "Enter a profile name", "",
							new IInputValidator() {
						@Override
						public String isValid(String name) {
							if (name.matches("[a-zA-Z0-9]")) {
								return name;
							} else {
								return null;
							}
						}
					});
					if (d.open() == Window.OK) {
						if (!StringUtils.isBlank(d.getValue())) {
							profileSelector.addValue(d.getValue());
							applyProfile(d.getValue());
							updateFieldEditors();
						}
					}
				}
			});
			deleteProfile = new Button(composite, SWT.PUSH);
			deleteProfile.setFont(getFieldEditorParent().getFont());
			deleteProfile.setText("Delete current profile");
			deleteProfile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (MessageDialog.openConfirm(null, "Delete current profile",
							"Do you really want to delete '" + currentProfile + "'?")) {
						profileSelector.removeValue(currentProfile);
						applyProfile(DEFAULT_PROFILE_NAME);
						updateFieldEditors();
					}
				}
			});

			Label horizontalSeparator = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
			horizontalSeparator.setLayoutData(new GridData(GridData.FILL, SWT.TOP, true, false, 2, 1));

		}

		this.addField(new StringFieldEditor(Preferences.SOURCE_FOLDERS(DEFAULT_PROFILE_NAME),
				"Source folders (project ones if empty)", this.getFieldEditorParent()));
		this.addField(new StringFieldEditor(Preferences.SOURCE_INCLUDE_FILTER(DEFAULT_PROFILE_NAME),
				"Include filter (regexp)", this.getFieldEditorParent()));
		this.addField(new StringFieldEditor(Preferences.SOURCE_EXCLUDE_FILTER(DEFAULT_PROFILE_NAME),
				"Exclude filter (regexp)", this.getFieldEditorParent()));
		this.addField(new StringFieldEditor(Preferences.TS_OUTPUT_FOLDER(DEFAULT_PROFILE_NAME),
				"Generated TypeScript folder", this.getFieldEditorParent()));
		this.addField(new StringFieldEditor(Preferences.JS_OUTPUT_FOLDER(DEFAULT_PROFILE_NAME),
				"Generated Javascript folder", this.getFieldEditorParent()));
		this.addField(new StringFieldEditor(Preferences.CANDY_JS_OUTPUT_FOLDER(DEFAULT_PROFILE_NAME),
				"Candies-extracted JavaScript folder", this.getFieldEditorParent()));
		this.addField(new BooleanFieldEditor(Preferences.BUNDLE(DEFAULT_PROFILE_NAME), "Create browser bundle",
				this.getFieldEditorParent()));
		this.addField(new StringFieldEditor(Preferences.BUNDLES_DIRECTORY(DEFAULT_PROFILE_NAME),
				"Bundle folder (in-place bundle if empty)", this.getFieldEditorParent()));
		this.addField(new RadioGroupFieldEditor(Preferences.DEBUG_MODE(DEFAULT_PROFILE_NAME),
				"Debug mode and '.js.map' files", 1,
				new String[][] {
						{ "Java debug (Java files are accessible in the browser's debugger)",
								Preferences.COMPILER_DEBUG_MODE_JAVA },
						{ "Typescript debug (Typescript files are pretty printed and used for debugging)",
								Preferences.COMPILER_DEBUG_MODE_TYPESCRIPT } },
				getFieldEditorParent()));
		this.addField(new ComboFieldEditor(Preferences.MODULE_KIND(DEFAULT_PROFILE_NAME), "Module kind",
				new String[][] { new String[] { "none", "none" }, new String[] { "commonjs", "commonjs" },
						new String[] { "amd", "amd" }, new String[] { "system", "system" },
						new String[] { "umd", "umd" } },
				getFieldEditorParent()));
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return JSweetPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected String getPreferenceNodeId() {
		return "org.jsweet.jsweetPreferencePage";
	}

	@Override
	protected String getSentinelPropertyName() {
		return Preferences.TS_OUTPUT_FOLDER(DEFAULT_PROFILE_NAME);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.updateFieldEditors();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		this.updateFieldEditors();
	}

	@Override
	protected void updateFieldEditors() {
		super.updateFieldEditors();
		// update editor states if needed
		if (this.isPageEnabled()) {
			Composite parent = this.getFieldEditorParent();
			BooleanFieldEditor bundle = this.getField(Preferences.BUNDLE(DEFAULT_PROFILE_NAME));
			StringFieldEditor bundlesDirectory = this.getField(Preferences.BUNDLES_DIRECTORY(DEFAULT_PROFILE_NAME));
			ComboFieldEditor module = this.getField(Preferences.MODULE_KIND(DEFAULT_PROFILE_NAME));
			if (bundle.getBooleanValue()) {
				getPreferenceStore().setValue(Preferences.MODULE_KIND(currentProfile), "none");
				module.load();
			}
			module.setEnabled(!bundle.getBooleanValue(), parent);
			bundlesDirectory.setEnabled(bundle.getBooleanValue(), parent);
		}

	}

}