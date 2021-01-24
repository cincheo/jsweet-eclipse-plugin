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
			if (Preferences.profiles().equals(entry.getKey())) {
				continue;
			}
			entry.getValue().setPreferenceName(Preferences.getProfilePrefix(currentProfile) + entry.getKey());
			entry.getValue().load();
		}
	}

	@Override
	protected void createFieldEditors() {
		if (this.isPropertyPage()) {
			this.addField(profileSelector = new ListSelectorFieldEditor(Preferences.profiles(), "Profile",
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

		addField(new StringFieldEditor(Preferences.srcDirs(DEFAULT_PROFILE_NAME),
				"Source folders (project ones if empty)", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.srcIncludeFilter(DEFAULT_PROFILE_NAME), //
				"Include filter (regexp)", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.srcExcludeFilter(DEFAULT_PROFILE_NAME), //
				"Exclude filter (regexp)", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.tsOutputDir(DEFAULT_PROFILE_NAME), //
				"Generated TypeScript folder", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Preferences.noJs(DEFAULT_PROFILE_NAME), //
				"Do not generate JavaScript", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.jsOutputDir(DEFAULT_PROFILE_NAME), //
				"Generated Javascript folder", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.candyJsOutputDir(DEFAULT_PROFILE_NAME),
				"Candies-extracted JavaScript folder", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Preferences.bundle(DEFAULT_PROFILE_NAME), //
				"Create browser bundle", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.bundlesDir(DEFAULT_PROFILE_NAME),
				"Bundle folder (in-place bundle if empty)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Preferences.declaration(DEFAULT_PROFILE_NAME),
				"Generate TypeScript definitions", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.declarationDir(DEFAULT_PROFILE_NAME),
				"Definitions folder (in-place if empty)", getFieldEditorParent()));
		addField(new BooleanFieldEditor(Preferences.debugMode(DEFAULT_PROFILE_NAME),
				"Debug mode (generate '.js.map' files)", getFieldEditorParent()));
		addField(new ComboFieldEditor(Preferences.moduleKind(DEFAULT_PROFILE_NAME), //
				"Module kind", new String[][] { //
						new String[] { "none", "none" }, //
						new String[] { "es2015", "es2015" }, //
						new String[] { "commonjs", "commonjs" }, //
						new String[] { "amd", "amd" }, //
						new String[] { "system", "system" }, //
						new String[] { "umd", "umd" } }, //
				getFieldEditorParent()));

		addField(new ComboFieldEditor(Preferences.ecmaTargetVersion(DEFAULT_PROFILE_NAME), //
				"Target ECMA version", new String[][] { //
						new String[] { "ES6", "ES6" }, //
						new String[] { "ES5", "ES5" }, //
						new String[] { "ES3", "ES3" } //
				}, getFieldEditorParent()));
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
		return Preferences.tsOutputDir(DEFAULT_PROFILE_NAME);
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
		if (isPageEnabled()) {
			Composite parent = getFieldEditorParent();
			BooleanFieldEditor bundle = this.getField(Preferences.bundle(DEFAULT_PROFILE_NAME));
			StringFieldEditor bundlesDirectory = this.getField(Preferences.bundlesDir(DEFAULT_PROFILE_NAME));
			ComboFieldEditor module = this.getField(Preferences.moduleKind(DEFAULT_PROFILE_NAME));
			if (bundle.getBooleanValue()) {
				getPreferenceStore().setValue(Preferences.moduleKind(currentProfile), "none");
				module.load();
			}
			module.setEnabled(!bundle.getBooleanValue(), parent);
			bundlesDirectory.setEnabled(bundle.getBooleanValue(), parent);

			BooleanFieldEditor declaration = this.getField(Preferences.declaration(DEFAULT_PROFILE_NAME));
			StringFieldEditor declarationDirectory = this.getField(Preferences.declarationDir(DEFAULT_PROFILE_NAME));
			declarationDirectory.setEnabled(declaration.getBooleanValue(), parent);

		}

	}

}
