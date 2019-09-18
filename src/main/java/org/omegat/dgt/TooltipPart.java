package org.omegat.dgt;

class TooltipPart extends Part {
    private String text;

    public TooltipPart(int start, int len, String text) {
        super(start, len);
        this.text = text;
    }

    public TooltipPart decale(int start) {
        return new TooltipPart(this.start + start, len, text);
    }

    public String getText() {
        return text;
    }
}