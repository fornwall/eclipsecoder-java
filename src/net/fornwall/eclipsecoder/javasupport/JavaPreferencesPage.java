package net.fornwall.eclipsecoder.javasupport;


import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 * we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * 
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */
public class JavaPreferencesPage implements IWorkbenchPreferencePage {
	private Text codeTemplateEditor;

	private Label codeTemplateLabel;

	private Composite composite;

	private Button generateJUnitTimeoutEditor;

	@Override
	public Point computeSize() {
		return composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}

	@Override
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		generateJUnitTimeoutEditor = new Button(composite, SWT.CHECK | SWT.LEFT);
		generateJUnitTimeoutEditor.setText("Use timeouts in generated test cases");
		generateJUnitTimeoutEditor.setSelection(JavaSupportPlugin.getInstance()
				.isGenerateJUnitTimeout());

		codeTemplateLabel = new Label(composite, SWT.NONE);
		codeTemplateLabel.setText("Code template:");

		codeTemplateEditor = new Text(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		codeTemplateEditor.setFont(JFaceResources
				.getFont(JFaceResources.TEXT_FONT));
		codeTemplateEditor.setTabs(5);
		codeTemplateEditor.setText(JavaSupportPlugin.getInstance()
				.getCodeTemplate());
		codeTemplateEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	public void dispose() {
		composite.dispose();
	}

	@Override
	public Control getControl() {
		return composite; // codeTemplateEditor;
	}

	@Override
	public String getDescription() {
		return "EclipseCoder java support preferences";
	}

	@Override
	public String getErrorMessage() {
		// null to indicate no error message
		return null;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public String getTitle() {
		return "EclipseCoder Java Preferences";
	}

	@Override
	public void init(IWorkbench workbench) {
		// from IWorkbenchPreferencePage interface
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public boolean okToLeave() {
		return true;
	}

	@Override
	public boolean performCancel() {
		return true;
	}

	@Override
	public void performHelp() {
		// do nothing
	}

	@Override
	public boolean performOk() {
		IPreferenceStore preferenceStore = JavaSupportPlugin.getInstance()
				.getPreferenceStore();
		preferenceStore.setValue(
				JavaSupportPlugin.GENERATE_JUNIT_TIMEOUT_PREFERENCE,
				generateJUnitTimeoutEditor.getSelection());
		preferenceStore.setValue(JavaSupportPlugin.CODE_TEMPLATE_PREFERENCE,
				codeTemplateEditor.getText());
		return true;
	}

	@Override
	public void setContainer(IPreferencePageContainer preferencePageContainer) {
		// do nothing
	}

	@Override
	public void setDescription(String description) {
		// do nothing
	}

	@Override
	public void setImageDescriptor(ImageDescriptor image) {
		// do nothing
	}

	@Override
	public void setSize(Point size) {
		composite.setSize(size);
	}

	@Override
	public void setTitle(String title) {
		// do nothing
	}

	@Override
	public void setVisible(boolean visible) {
		composite.setVisible(visible);
	}

}