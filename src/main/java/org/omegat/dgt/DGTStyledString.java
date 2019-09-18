package org.omegat.dgt;

import java.util.LinkedList;

import org.omegat.gui.glossary.GlossaryEntry.StyledString;

class DGTStyledString extends StyledString {
    public StringBuilder text = new StringBuilder();
    public LinkedList<AttributesPart> attrParts = new LinkedList<>();
    public LinkedList<AttributesPart> attrBolds = new LinkedList<>();
    public LinkedList<TooltipPart> tooltips = new LinkedList<>();
    public LinkedList<HiddenPart> hiddenParts = new LinkedList<>();

    @Override
    public void markBoldStart() {
        attrBolds.add(new AttributesPart(text.length(), 0, DGTGlossary.PRIORITY_ATTRIBUTES));
    }

    @Override
    public void markBoldEnd() {
        attrBolds.getLast().setEnd(text.length());
    }

    public void startTooltip(String tip) {
        tooltips.add(new TooltipPart(text.length(), 0, tip));
    }

    public void endTooltip() {
        tooltips.getLast().setEnd(text.length());
    }

    public void startHiddenPart(String prefix, String suffix) {
        hiddenParts.add(new HiddenPart(text.length(), 0, prefix, suffix));
        text.append(prefix);
    }

    public void endHiddenPart() {
        text.append(hiddenParts.getLast().suffix);
        hiddenParts.getLast().setEnd(text.length());
    }

    public void append(DGTStyledString str) {
        int off = text.length();
        text.append(str.text);
        for (AttributesPart attr : str.attrParts) {
            attrParts.add(attr.decale(off));
        }
        for (AttributesPart attr : str.attrBolds) {
            attrBolds.add(attr.decale(off));
        }
        for (TooltipPart tt : str.tooltips) {
            tooltips.add(tt.decale(off));
        }
        for (HiddenPart hp : str.hiddenParts) {
            hiddenParts.add(hp.decale(off));
        }
    }

    public void append(String str) {
        text.append(str);
    }

    public DGTStyledString appendError(String src) {
        attrParts.add(new AttributesPart(text.length(), src.length(),
                org.omegat.util.gui.Styles.createAttributeSet(java.awt.Color.RED, null, true, null)));
        text.append(src);
        return this;
    }

    public DGTStyledString appendSource(String src) {
        attrParts.add(new AttributesPart(text.length(), src.length(), DGTGlossary.SOURCE_ATTRIBUTES));
        text.append(bracketEntry(src));
        return this;
    }

    public DGTStyledString appendTarget(String src) {
        attrParts.add(new AttributesPart(text.length(), src.length(), DGTGlossary.TARGET_ATTRIBUTES));
        text.append(bracketEntry(src));
        return this;
    }

    public DGTStyledString appendTarget(String src, boolean priority) {
        attrParts.add(new AttributesPart(text.length(), src.length(), DGTGlossary.TARGET_ATTRIBUTES));
        if (priority) {
            markBoldStart();
        }
        text.append(bracketEntry(src));
        if (priority) {
            markBoldEnd();
        }
        return this;
    }

    public DGTStyledString appendComment(String src) {
        if (src.startsWith("\u0007")) {
            return appendComment(src.substring(1), true);
        }
        attrParts.add(new AttributesPart(text.length(), src.length(), DGTGlossary.NOTES_ATTRIBUTES));
        text.append(src);
        return this;
    }

    public DGTStyledString appendComment(String src, boolean priority) {
        attrParts.add(new AttributesPart(text.length(), src.length(), DGTGlossary.NOTES_ATTRIBUTES));
        if (priority) {
            markBoldStart();
        }
        text.append(src);
        if (priority) {
            markBoldEnd();
        }
        return this;
    }

    /**
     * If a combined glossary entry contains ',', it needs to be bracketed by
     * quotes, to prevent confusion when entries are combined. However, if the entry
     * contains ';' or '"', it will automatically be bracketed by quotes.
     *
     * @param entry A glossary text entry
     * @return A glossary text entry possibly bracketed by quotes
     */
    private static String bracketEntry(String entry) {

        if (entry.contains(",") && !(entry.contains(";") || entry.contains("\""))) {
            entry = '"' + entry + '"';
        }
        return entry;
    }
}