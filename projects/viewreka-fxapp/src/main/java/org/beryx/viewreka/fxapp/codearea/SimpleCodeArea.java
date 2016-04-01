/**
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Inspired by (and using code from) org.fxmisc.richtext.demo.JavaKeywords (https://github.com/TomasMikula/RichTextFX)
 */
package org.beryx.viewreka.fxapp.codearea;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.EventHandler;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link CodeArea} with basic syntax highlighting support.
 */
public class SimpleCodeArea extends CodeArea {
    private static final Logger log = LoggerFactory.getLogger(SimpleCodeArea.class);

    private static final String NEVER_MATCHING = "(?!x)x";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String STRING2_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private final Map<String, StyleGroup> styleGroups = new LinkedHashMap<>();

    private Pattern codePattern;
    private boolean codePatternInvalid = true;

    private String[] keywords = null;
    private boolean caseInsensitive = false;

    private Supplier<String> keywordPatternSupplier = () -> getKeywordPattern();
    private Supplier<Pattern> codePatternCreator = () -> createCodePattern();

    private int tabAdvance = 4;
    private boolean indentationOnEnter = true;

    public static class StyleGroup {
        private final String groupName;
        private final String styleClass;
        private final String groupPattern;

        public StyleGroup(String groupName, String styleClass, String groupPattern) {
            this.groupName = groupName;
            this.styleClass = styleClass;
            this.groupPattern = groupPattern;
        }

        public String getGroupName() {
            return groupName;
        }
        public String getStyleClass() {
            return styleClass;
        }
        public String getGroupPattern() {
            return groupPattern;
        }

        @Override
        public String toString() {
            return groupName + " / " + styleClass + ": " + groupPattern;
        }
    }

    public SimpleCodeArea() {
        this("");
    }

    public SimpleCodeArea(String text) {
        super(text);

        putStyleGroup("KEYWORD", "keyword", NEVER_MATCHING);
        putStyleGroup("PAREN", "paren", PAREN_PATTERN);
        putStyleGroup("BRACE", "brace", BRACE_PATTERN);
        putStyleGroup("BRACKET", "bracket", BRACKET_PATTERN);
        putStyleGroup("SEMICOLON", "semicolon", SEMICOLON_PATTERN);
        putStyleGroup("STRING", "string", STRING_PATTERN);
        putStyleGroup("STRING2", "string", STRING2_PATTERN);
        putStyleGroup("COMMENT", "comment", COMMENT_PATTERN);

        String cssName = SimpleCodeArea.class.getSimpleName() + ".css";
        getStylesheets().add(SimpleCodeArea.class.getResource(cssName).toExternalForm());

        setParagraphGraphicFactory(LineNumberFactory.get(this));
        richChanges().subscribe(change -> {
            setStyleSpans(0, computeHighlighting(getText()));
        });
        EventHandler<? super KeyEvent> tabHandler = EventHandlerHelper
                .on(EventPattern.keyPressed(KeyCode.ENTER)).act(event -> applyEnter())
                .on(EventPattern.keyPressed(KeyCode.TAB)).act(event -> applyTab())
                .on(EventPattern.keyPressed(KeyCode.TAB, KeyCombination.SHIFT_DOWN)).act(event -> applyShiftTab())
                .create();
        EventHandlerHelper.install(onKeyPressedProperty(), tabHandler);
    }

    public int getTabAdvance() {
        return tabAdvance;
    }
    public void setTabAdvance(int tabAdvance) {
        this.tabAdvance = tabAdvance;
    }

    public boolean isIndentationOnEnter() {
        return indentationOnEnter;
    }

    public void setIndentationOnEnter(boolean indentationOnEnter) {
        this.indentationOnEnter = indentationOnEnter;
    }

    protected void applyEnter() {
        String replacement = "\n";
        if(indentationOnEnter) {
            Position startPos = offsetToPosition(getSelection().getStart(), Bias.Forward);
            String text = getText(startPos.getMajor());
            int indent = 0;
            while(indent < startPos.getMinor()) {
                char ch = text.charAt(indent);
                if(ch != ' ' &&  ch != '\t') break;
                indent++;
            }
            replacement += text.substring(0, indent);
        }
        replaceSelection(replacement);
    }

    protected void applyTab() {
        IndexRange selection = getSelection();
        Position startPos = offsetToPosition(selection.getStart(), Bias.Forward);
        if(selection.getLength() == 0) {
            int spaceCount = tabAdvance - startPos.getMinor() % tabAdvance;
            replaceSelection(StringUtils.repeat(' ', spaceCount));
        } else {
            int startParagraph = startPos.getMajor();
            Position endPos = offsetToPosition(selection.getEnd(), Bias.Forward);
            int endParagraph = endPos.getMajor();
            String spaces = StringUtils.repeat(' ', tabAdvance);
            for(int pg = startParagraph; pg <= endParagraph; pg++) {
                insertText(position(pg, 0).toOffset(), spaces);
            }
            int newStartOffset = startPos.offsetBy(tabAdvance, Bias.Forward).toOffset();
            int newEndOffset = endPos.offsetBy(tabAdvance, Bias.Forward).toOffset();
            selectRange(newStartOffset, newEndOffset);
        }
    }

    protected void applyShiftTab() {
        Position startPos = offsetToPosition(getSelection().getStart(), Bias.Forward);
        Position endPos = offsetToPosition(getSelection().getEnd(), Bias.Forward);
        int startParagraph = startPos.getMajor();
        int endParagraph = endPos.getMajor();
        int startBackCount = 0;
        int endBackCount = 0;
        for(int pg = startParagraph; pg <= endParagraph; pg++) {
            String text = getText(pg);
            int backCount = 0;
            while(backCount < tabAdvance && backCount < text.length()) {
                if(text.charAt(backCount) == '\t') {
                    backCount++;
                    break;
                }
                if(text.charAt(backCount) != ' ') break;
                backCount++;
            }
            if(backCount > 0) {
                if(pg == startParagraph) startBackCount = backCount;
                if(pg == endParagraph) endBackCount = backCount;
                deleteText(position(pg, 0).toOffset(), position(pg, backCount).toOffset());
            }
        }

        int startMinor = Math.abs(startPos.getMinor() - startBackCount);
        Position newStartPos = startPos.getTargetObject().position(startPos.getMajor(), startMinor);
        int endMinor = Math.abs(endPos.getMinor() - endBackCount);
        Position newEndPos = endPos.getTargetObject().position(endPos.getMajor(), endMinor);
        log.debug("applyShiftTab():\nstartBackCount: {}, endBackCount: {}\n   startPos: {},    endPos: {}\nnewStartPos: {}, newEndPos: {}",
                startBackCount, endBackCount, startPos, endPos, newStartPos, newEndPos);
        selectRange(newStartPos.toOffset(), newEndPos.toOffset());
    }

    public StyleGroup putStyleGroup(String groupName, String styleClass, String groupPattern) {
        return styleGroups.put(groupName, new StyleGroup(groupName, styleClass, groupPattern));
    }
    public StyleGroup removeStyleGroup(String groupName) {
        return styleGroups.remove(groupName);
    }

    public final void applyConfiguration(CodeAreaConfig config) {
        if(config != null) {
            setCaseInsensitive(config.isCaseInsensitive());
            setKeywords(config.getKeywords());
            if(config.getKeywordPatternSupplier() != null) {
                setKeywordPatternSupplier(config.getKeywordPatternSupplier());
            }
            if(config.getCodePatternCreator() != null) {
                setCodePatternCreator(config.getCodePatternCreator());
            }
            config.getStyleGroups().forEach(group -> putStyleGroup(group.groupName, group.styleClass, group.groupPattern));
            config.getRemovedStyleGroups().forEach(groupName -> removeStyleGroup(groupName));
        }
    }

    public void invalidatePattern() {
        codePatternInvalid = true;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
        invalidatePattern();
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
        invalidatePattern();
    }

    public void setKeywordPatternSupplier(Supplier<String> keywordPatternSupplier) {
        this.keywordPatternSupplier = keywordPatternSupplier;
    }

    public void setCodePatternCreator(Supplier<Pattern> codePatternCreator) {
        this.codePatternCreator = codePatternCreator;
    }

    protected String getKeywordPattern() {
        if(keywords == null || keywords.length == 0) return null;
        String pattern = "\\b(" + String.join("|", keywords) + ")\\b";
        if(caseInsensitive) {
            pattern = "(?i)" + pattern + "(?-i)";
        }
        return pattern;
    }

    private Pattern getCodePattern() {
        if(codePattern == null || codePatternInvalid) {
            codePattern = codePatternCreator.get();
            codePatternInvalid = false;
        }
        return codePattern;
    }

    protected Pattern createCodePattern() {
        String keywordPattern = keywordPatternSupplier.get();
        putStyleGroup("KEYWORD", "keyword",  ((keywordPattern == null) ? NEVER_MATCHING : keywordPattern));

        StringBuilder sbRegex = new StringBuilder(1024);
        String sep = "";
        for(StyleGroup group : styleGroups.values()) {
            sbRegex.append(sep + "(?<" + group.groupName + ">" + group.groupPattern + ")");
            sep = "|";
        }
        Pattern pattern = Pattern.compile(sbRegex.toString());
        return pattern;
    }

    public void setText(String newText) {
        replaceText(newText);
    }

    protected StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = getCodePattern().matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        try {
            while(matcher.find()) {
                String styleClass = null;
                for(StyleGroup group : styleGroups.values()) {
                    if(matcher.group(group.groupName) != null) {
                        styleClass = group.styleClass;
                        break;
                    }
                }
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
        } catch(Throwable t) {
            log.warn("Failed to compute highlighting: " + t);
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
