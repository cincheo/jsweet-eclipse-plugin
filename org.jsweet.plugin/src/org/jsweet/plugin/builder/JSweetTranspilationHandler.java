package org.jsweet.plugin.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jsweet.plugin.Log;
import org.jsweet.plugin.preferences.Preferences;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.JSweetTranspiler;
import org.jsweet.transpiler.Severity;
import org.jsweet.transpiler.SourceFile;
import org.jsweet.transpiler.SourcePosition;
import org.jsweet.transpiler.TranspilationHandler;

class JSweetTranspilationHandler implements TranspilationHandler {

	private final BuildingContext context;

	public JSweetTranspilationHandler(BuildingContext context) {
		this.context = context;
	}

	@Override
	public void report(JSweetProblem problem, SourcePosition sourcePosition, String message) {
		if (problem == JSweetProblem.INTERNAL_JAVA_ERROR || isIgnoredErrorMessage(message)) {
			// ignore Java errors because they will be reported by Eclipse
			return;
		}
		String base = context.project.getLocation().toFile().getAbsolutePath();
		if (sourcePosition == null || sourcePosition.getFile() == null) {
			addMarker(context.project, message, -1, -1, -1,
					problem.getSeverity() == Severity.ERROR ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
		} else {
			IFile f = null;
			try {
				final String sourcePosAbsolutePath = sourcePosition.getFile().getAbsolutePath();
				if (base.length() + 1 < sourcePosAbsolutePath.length()) {
					f = (IFile) context.project.findMember(sourcePosAbsolutePath.substring(base.length() + 1));
				}
			} catch (Exception e) {
				Log.error(message, e);
				// swallow
			}
			if (f == null) {
				try {
					f = (IFile) context.project.findMember(sourcePosition.getFile().getPath());
				} catch (Exception e) {
					Log.error(message, e);
					// swallow
				}
			}
			if (f == null) {
				addMarker(context.project, message, -1, -1, -1,
						problem.getSeverity() == Severity.ERROR ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
			} else {
				addMarker(f, message, sourcePosition.getStartLine(), sourcePosition.getStartPosition().getPosition(),
						sourcePosition.getEndPosition().getPosition(),
						problem.getSeverity() == Severity.ERROR ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING);
			}
		}
	}

	private boolean isIgnoredErrorMessage(final String message) {
		final String msg = message.toLowerCase();
		return msg.equals("duplicate identifier 'booleancondition'")
				|| msg.startsWith("namespace 'java' has no exported member");
	}

	@Override
	public void onCompleted(JSweetTranspiler transpiler, boolean fullPass, SourceFile[] files) {
		try {
			if (fullPass) {
				Log.info("refreshing worspace (full)");
				context.project.refreshLocal(IResource.DEPTH_INFINITE, null);
			} else {
				// TODO: refresh only the resources that have changed (using
				// the files argument)
				Log.info("refreshing output directories (incremental)");
				final IResource output = context.project
						.findMember(Preferences.getTsOutputFolder(context.project, context.profile));
				final IResource jsOutput = context.project
						.findMember(Preferences.getJsOutputFolder(context.project, context.profile));
				if (output != null || jsOutput != null) {
					new Thread() {
						@Override
						public void run() {
							try {
								if (output != null) {
									output.refreshLocal(IResource.DEPTH_INFINITE, null);
								}
								if (jsOutput != null && !jsOutput.equals(output)) {
									jsOutput.refreshLocal(IResource.DEPTH_INFINITE, null);
								}
							} catch (CoreException e) {
								Log.error("error while refreshing", e);
							}
						}
					}.start();
				}
			}

		} catch (Exception e) {
			Log.error(e);
		}
	}

	private void addMarker(IResource resource, String message, int lineNumber, int charStart, int charEnd,
			int severity) {
		try {
			IMarker marker = resource.createMarker(JSweetConsts.JSWEET_PROBLEM_MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber >= 0) {
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			}
			if (charStart >= 0) {
				marker.setAttribute(IMarker.CHAR_START, charStart);
			}
			if (charEnd >= 0) {
				marker.setAttribute(IMarker.CHAR_END, charEnd);
			}
		} catch (CoreException e) {
			Log.error(e);
		}
	}

}
