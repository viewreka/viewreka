package org.beryx.viewreka.fxapp.codearea;


import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;
import org.beryx.viewreka.bundle.api.CodeTemplate;
import org.beryx.viewreka.bundle.util.SimpleConfiguration;
import org.beryx.viewreka.bundle.util.SimpleContext;
import org.beryx.viewreka.fxapp.ClassLoaderManager;
import org.beryx.viewreka.model.ProjectModel;
import org.fxmisc.richtext.PopupAlignment;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Specialization of {@link SimpleCodeArea} for Viewreka scripts.
 */
public class ViewrekaCodeArea extends SimpleCodeArea {
    private static final String[] KEYWORDS = new String[] {
        // groovy keywords
        "abstract", "any", "as", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "def", "default", "do", "double",
        "else", "enum", "extends", "false", "final", "finally", "float", "for", "goto", "if", "implements", "import", "in", "instanceof", "int",
        "interface", "it", "long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "threadsafe", "throw", "throws", "traits", "transient", "true", "try", "void", "volatile", "while",

        // viewreka keywords
        "dataset", "datasource", "view", "parameter", "chart", "styles", "xAxis", "yAxis", "series"
    };

    public static final CodeAreaConfig CONFIG = new CodeAreaConfig().withCaseInsensitive(false).withKeywords(KEYWORDS);

    private static final String CARET_MARKER = "\u0000";

    private final Set<CodeTemplate> codeTemplates = new TreeSet<>((t1, t2) -> t1.getName().compareTo(t2.getName()));

    private ClassLoaderManager classLoaderManager;

    private ProjectModel<?> projectModel;

    public ViewrekaCodeArea() {
        applyConfiguration(CONFIG);

        ContextMenu cm = new ContextMenu();
        setPopupWindow(cm);
        setPopupAlignment(PopupAlignment.CARET_BOTTOM);
        setPopupAnchorOffset(new Point2D(-20, 1));

        EventHandler<? super KeyEvent> tabHandler = EventHandlerHelper
                .on(EventPattern.keyPressed(KeyCode.SPACE, KeyCombination.CONTROL_DOWN)).act(ev -> showContextMenu(cm))
                .create();
        EventHandlerHelper.install(onKeyPressedProperty(), tabHandler);
    }

    public Set<CodeTemplate> getCodeTemplates() {
        return codeTemplates;
    }

    public void setClassLoaderManager(ClassLoaderManager classLoaderManager) {
        this.classLoaderManager = classLoaderManager;
    }

    public void setProjectModel(ProjectModel<?> projectModel) {
        this.projectModel = projectModel;
    }

    protected void showContextMenu(ContextMenu cm) {
        if(codeTemplates.isEmpty()) return;

        cm.getItems().clear();
        for(CodeTemplate template : codeTemplates) {
            // TODO retrieve context and create MenuItem only if template.isAllowedInContext() returns true
            MenuItem item = new MenuItem(template.getName());
            item.setOnAction(ev -> {
                if(classLoaderManager != null) {
                    classLoaderManager.setProjectClassLoader();
                }
                try {
                    insertCodeFragment(template);
                } finally {
                    classLoaderManager.resetClassLoader();
                }
            });
            cm.getItems().add(item);
        }
        cm.getItems().sort((item1, item2) -> item1.getText().toLowerCase().compareTo(item2.getText().toLowerCase()));
        cm.show(getScene().getWindow());
    }

    protected CodeTemplate.Context getCodeTemplateContext() {
        return new SimpleContext();
    }

    protected CodeTemplate.Configuration getCodeTemplateConfiguration() {
        return new SimpleConfiguration(projectModel, getCodeTemplateContext());
    }

    public void insertCodeFragment(CodeTemplate template) {
        CodeTemplate.CodeFragment fragment = template.getCodeFragment(getCodeTemplateConfiguration());
        if(fragment == null) return;
        String code = fragment.getCode();
        if(StringUtils.isBlank(code)) return;

        int cPos = fragment.getCaretPosition();
        if(cPos >= 0) {
            code = new StringBuilder(code).insert(cPos, CARET_MARKER).toString();
        }
        String[] codeLines = code.split("\\R", -1);// Split around line breaks without discarding empty lines at the end

        Position startPos = offsetToPosition(getSelection().getStart(), Bias.Forward);
        int startParagraph = startPos.getMajor();
        String textBeforeCaret = getText(startParagraph).substring(0, startPos.getMinor());
        boolean shouldIndent = !textBeforeCaret.isEmpty();
        if(shouldIndent) {
            for(char ch : textBeforeCaret.toCharArray()) {
                if(ch != ' ' &&  ch != '\t') {
                    shouldIndent = false;
                    break;
                }
            }
        }
        if(shouldIndent) {
            for(int i=1; i<codeLines.length; i++) {
                codeLines[i] = textBeforeCaret + codeLines[i];
            }
        }
        code = Arrays.stream(codeLines).collect(Collectors.joining("\n"));
        replaceSelection(code);
        if(cPos >= 0) {
            for(int i=0; i<codeLines.length; i++) {
                int markerPos = getText(startParagraph + i).indexOf(CARET_MARKER);
                if(markerPos >= 0) {
                    int offset = position(startParagraph + i, markerPos).toOffset();
                    replaceText(offset, offset + CARET_MARKER.length(), "");
                    break;
                }
            }
        }
    }
}
