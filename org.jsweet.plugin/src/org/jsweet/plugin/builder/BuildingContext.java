package org.jsweet.plugin.builder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.SourceFile;

class BuildingContext {
	public final String profile;
	public final IProject project;
	// watch mode does not work (yet?) under Windows, so we do not use it
	public boolean useWatchMode = false;
	public final Map<File, SourceFile> sourceFiles = new HashMap<>();
	public JSweetTranspiler transpiler;

	public BuildingContext(final IProject project, final String profile) {
		this.project = project;
		this.profile = profile;
	}
}