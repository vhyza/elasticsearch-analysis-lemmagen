package org.elasticsearch.plugin.analysis.lemmagen;

import org.elasticsearch.index.analysis.LemmagenFilterFactory;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import static org.elasticsearch.plugins.AnalysisPlugin.requiresAnalysisSettings;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class AnalysisLemmagenPlugin extends Plugin implements AnalysisPlugin {
  @Override
  public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
    return singletonMap("lemmagen", requiresAnalysisSettings(
        (indexSettings, env, name, settings) -> new LemmagenFilterFactory(env, name, settings)));
  }
}
