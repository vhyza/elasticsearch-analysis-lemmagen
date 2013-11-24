package org.elasticsearch.plugin.analysis.lemmagen;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.index.analysis.LemmagenAnalysisBinderProcessor;

public class AnalysisLemmagenPlugin extends AbstractPlugin {

    @Override public String name() {
        return "analysis-lemmagen";
    }

    @Override public String description() {
        return "Lemmagen analysis support";
    }

    @Override public void processModule(Module module) {
        if (module instanceof AnalysisModule) {
            AnalysisModule analysisModule = (AnalysisModule) module;
            analysisModule.addProcessor(new LemmagenAnalysisBinderProcessor());
        }
    }
}
