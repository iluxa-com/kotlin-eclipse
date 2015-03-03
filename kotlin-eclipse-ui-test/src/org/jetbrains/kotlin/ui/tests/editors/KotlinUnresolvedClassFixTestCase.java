/*******************************************************************************
 * Copyright 2000-2014 JetBrains s.r.o.
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
 *
 *******************************************************************************/
package org.jetbrains.kotlin.ui.tests.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.jetbrains.kotlin.eclipse.ui.utils.LineEndUtil;
import org.jetbrains.kotlin.testframework.editor.KotlinEditorTestCase;
import org.jetbrains.kotlin.testframework.editor.KotlinProjectTestCase;
import org.jetbrains.kotlin.testframework.editor.TextEditorTest;
import org.jetbrains.kotlin.testframework.utils.EditorTestUtils;
import org.jetbrains.kotlin.testframework.utils.KotlinTestUtils;
import org.jetbrains.kotlin.testframework.utils.SourceFileData;
import org.jetbrains.kotlin.ui.editors.KotlinCorrectionProcessor;
import org.junit.Before;

public class KotlinUnresolvedClassFixTestCase extends KotlinProjectTestCase {
	@Before
	public void configure() {
		configureProject();
		KotlinTestUtils.addKotlinBuilder(getTestProject().getJavaProject().getProject());
	}
	
	public void doTest(String input, List<SourceFileData> files, String expected) {
		String toEditor = LineEndUtil.replaceAllSeparatorsWithSystemLineSeparators(
				KotlinEditorTestCase.resolveTestTags(input));
		TextEditorTest testEditor = configureEditor("Test.kt", toEditor);

		if (files != null) {
			for (SourceFileData data : files) {
				createSourceFile(data.getFileName(), data.getContent());
			}
		}
		
		testEditor.save();
		KotlinTestUtils.joinBuildThread();
		
		try {
			IMarker[] markers = testEditor.getEditingFile().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			List<ICompletionProposal> proposals = createProposals(testEditor.getEditor(), markers);
			for (ICompletionProposal proposal: proposals) {
				proposal.apply(testEditor.getDocument());
			}
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

		EditorTestUtils.assertByEditor(testEditor.getEditor(), expected);
	}
	
	private List<ICompletionProposal> createProposals(JavaEditor editor, IMarker[] markers) {
		KotlinCorrectionProcessor correctionProcessor = new KotlinCorrectionProcessor(editor);
		List<ICompletionProposal> proposals = new ArrayList<>();
		for (IMarker marker: markers) {
			int caret = marker.getAttribute(IMarker.CHAR_START, -1) + 1;
			proposals.addAll(createProposals(correctionProcessor, editor, caret));
		}
		
		return proposals;
    }
	
	private List<ICompletionProposal> createProposals(KotlinCorrectionProcessor correctionProcessor, JavaEditor editor, int caret) {
		return Arrays.asList(correctionProcessor.computeQuickAssistProposals(new TextInvocationContext(editor.getViewer(), caret, -1)));
	}
}