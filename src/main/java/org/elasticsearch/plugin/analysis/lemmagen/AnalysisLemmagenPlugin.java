package org.elasticsearch.plugin.analysis.lemmagen;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.LemmagenAnalysisBinderProcessor;
import org.elasticsearch.plugins.Plugin;

public class AnalysisLemmagenPlugin extends Plugin {

    @Override public String name() {
        return "analysis-lemmagen";
    }

    @Override public String description() {
        return "Lemmagen analysis support";
    }

    public void onModule(AnalysisModule module) {
            module.addProcessor(new LemmagenAnalysisBinderProcessor());
    }
}
