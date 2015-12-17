package org.jsweet.plugin.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jsweet.plugin.JSweetPlugin;
import org.jsweet.plugin.builder.JSweetBuilder;

public final class JSweetPreferencePage extends FieldEditorProjectPreferencePage implements IWorkbenchPreferencePage {
	private boolean compilerPreferencesModified;

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

	@Override
	protected void createFieldEditors() {
		this.addField(new StringFieldEditor(Preferences.COMPILER_TYPESCRIPT_FOLDER,
				"Generated TypeScript folder (hidden when starting with '.')", this.getFieldEditorParent()));
		this.addField(new StringFieldEditor(Preferences.COMPILER_JAVASCRIPT_FOLDER, "Generated Javascript folder",
				this.getFieldEditorParent()));
		this.addField(new BooleanFieldEditor(Preferences.COMPILER_BUNDLE,
				"Create bundles files from main methods (including js libs)", this.getFieldEditorParent()));
		this.addField(new StringFieldEditor(Preferences.COMPILER_BUNDLES_DIRECTORY,
				"Bundles folder (in-place bundles if empty)", this.getFieldEditorParent()));
		this.addField(new RadioGroupFieldEditor(Preferences.COMPILER_DEBUG_MODE, "Debug mode and '.js.map' files", 1,
				new String[][] {
						{ "Java debug (Java files are accessible in the browser's debugger)",
								Preferences.COMPILER_DEBUG_MODE_JAVA },
						{ "Typescript debug (Typescript files are pretty printed and used for debugging)",
								Preferences.COMPILER_DEBUG_MODE_TYPESCRIPT } },
				getFieldEditorParent()));
		this.addField(new ComboFieldEditor(Preferences.COMPILER_MODULE_KIND, "Module kind",
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
		return Preferences.COMPILER_TYPESCRIPT_FOLDER;
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
			BooleanFieldEditor bundle = this.getField(Preferences.COMPILER_BUNDLE);
			StringFieldEditor bundlesDirectory = this.getField(Preferences.COMPILER_BUNDLES_DIRECTORY);
			ComboFieldEditor module = this.getField(Preferences.COMPILER_MODULE_KIND);
			if (bundle.getBooleanValue()) {
				getPreferenceStore().setValue(Preferences.COMPILER_MODULE_KIND, "commonjs");
				module.load();
			}
			module.setEnabled(!bundle.getBooleanValue(), parent);
			bundlesDirectory.setEnabled(bundle.getBooleanValue(), parent);
		}

	}

}