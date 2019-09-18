package org.omegat.dgt;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.omegat.gui.glossary.GlossaryEntry;
import org.omegat.util.Preferences;

public class DGTEntry extends GlossaryEntry {

    public static ScriptEngine engine = null;

    private static final String GLOSSARY_PANE_TEMPLATE = "dgt-glossary-template";

    public static final String DEFAULT_TEMPLATE =
              "result.appendSource(entry.srcText); result.append(\": \");\n"
            + "def map = entry.translationWithComments\n"
            + "def count = map.size()\n"
            + "map.each { tra,commentsList -> \n"
            + " result.appendTarget(tra, entry.hasPriorities(tra));\n"
            + " for (com in commentsList) {\n"
            + "     result.append(\"\\n ● \"); result.appendComment(com); \n"
            + " }\n" + " if (count-- > 1) result.append(\",\");\n"
            + "}\n"
            + "result.append(\"\\n\");\n"
            + "return result;";
            ;

    public DGTEntry(GlossaryEntry entry) {
        super(entry.getSrcText(), entry.getLocTerms(false), entry.getComments(), entry.getPriorities(), entry.getOrigins(false));
    }


    @Override
    public DGTStyledString toStyledString() {
        DGTStyledString result = new DGTStyledString();
        try {
            if (engine == null) {
                engine = new ScriptEngineManager().getEngineByName("Groovy");
                String template = Preferences.getPreferenceDefault(GLOSSARY_PANE_TEMPLATE, DEFAULT_TEMPLATE);

                //                if (template.startsWith("file:")) {
                //                    template = template.substring(5);
                //                    File tplFile = new File(template);
                //                    if (!tplFile.exists()) {
                //                        template = Preferences.getPreferenceDefault(Preferences.SCRIPTS_DIRECTORY,
                //                                StaticUtils.installDir() + "/scripts") + "/layout/glossary/" + template;
                //                        tplFile = ScriptingWindow.toJavaFile(template);
                //                    }
                //                    if (tplFile.exists())
                //                        template = new String(Files.readAllBytes(tplFile.toPath()));
                //                    else
                //                        template = "result.appendError(\"Cannot find " + tplFile.getName().replace("\\", "\\\\")
                //                                + " ; using default\\n\");\n" + DEFAULT_TEMPLATE;
                //                }

                engine.eval("def format(entry,result) {\n" + template + "\n}");
            }
            engine.put("result", result);
            engine.put("entry", this);
            engine.eval("format(entry, result)");
        } catch (Exception e) {
            result.appendError(e.getMessage());
        }
        return result;
    }

    public Map<String,LinkedList<String>> getTranslationWithComments() {
        String[] mTargets = getLocTerms(false);
        String[] mComments = getComments();
        boolean[] mPriorities = getPriorities();

        Map<String,LinkedList<String>> res = new TreeMap<>();
        for (int i = 0; i < mTargets.length; i++) {
            LinkedList<String> item = res.get(mTargets[i]);
            if (item == null) {
                res.put (mTargets[i], item = new LinkedList<>());
            }

            if ((mComments[i] != null) && (mComments[i].length() > 0)) {
                if (mPriorities[i]) {
                    item.addFirst("\u0007" + mComments[i]);
                } else {
                    item.add (mComments[i]);
                }
            }
        }
        return res;
    }


    public Map<String,LinkedList<String>> getTranslationWithOrigins() {
        String[] mTargets = getLocTerms(false);
        String[] mOrigins = getOrigins(false);

        Map<String,LinkedList<String>> res = new TreeMap<>();
        for (int i = 0; i < mTargets.length; i++) {
            LinkedList<String> item = res.get(mTargets[i]);
            if (item == null) {
                res.put (mTargets[i], item = new LinkedList<>());
            }
            item.add (mOrigins[i]);
        }
        return res;
    }

    public boolean hasPriorities() {
        boolean[] mPriorities = getPriorities();
        for (boolean b: mPriorities) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPriorities(String tra) {
        String[] mTargets = getLocTerms(false);
        boolean[] mPriorities = getPriorities();
        for (int i = 0; i < mTargets.length; i++) {
            if (mTargets[i].equals(tra) && mPriorities[i]) {
                return true;
            }
        }
        return false;
    }
}
