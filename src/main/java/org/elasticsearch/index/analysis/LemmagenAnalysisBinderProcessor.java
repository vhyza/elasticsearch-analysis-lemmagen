package org.elasticsearch.index.analysis;

public class LemmagenAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {
    
    @Override
    public void processTokenFilters(TokenFiltersBindings tokenFiltersBindings) {
        tokenFiltersBindings.processTokenFilter("lemmagen", LemmagenFilterFactory.class);
    }

}
