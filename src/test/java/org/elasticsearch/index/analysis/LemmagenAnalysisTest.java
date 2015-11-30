package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.elasticsearch.Version;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.elasticsearch.plugin.analysis.lemmagen.AnalysisLemmagenPlugin;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class LemmagenAnalysisTest extends BaseTokenStreamTestCase {

    @Test
    public void testLemmagenFilterFactoryWithDefaultLexicon() throws IOException {
        AnalysisService analysisService = createAnalysisService();

        TokenFilterFactory tokenFilter = analysisService.tokenFilter("lemmagen_default_filter");
        assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));

        String source = "I was late.";
        String[] expected = new String[]{"I", "be", "late"};

        Tokenizer tokenizer = new UAX29URLEmailTokenizer();
        tokenizer.setReader(new StringReader(source));

        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testLemmagenFilterFactoryWithCustomLexicon() throws IOException {
        AnalysisService analysisService = createAnalysisService();

        TokenFilterFactory tokenFilter = analysisService.tokenFilter("lemmagen_cs_filter");
        assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));
        String source = "Děkuji, že jsi přišel.";
        String[] expected = {"Děkovat", "že", "být", "přijít"};

        Tokenizer tokenizer = new UAX29URLEmailTokenizer();
        tokenizer.setReader(new StringReader(source));

        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testLemmagenFilterFactoryWithShortLexiconCode() throws IOException {
        AnalysisService analysisService = createAnalysisService();

        TokenFilterFactory tokenFilter = analysisService.tokenFilter("lemmagen_fr_filter");
        assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));

        String source = "Il faut encore ajouter une pincée de sel.";
        String[] expected = new String[]{"Il", "falloir", "encore", "ajouter", "un", "pincer", "de", "sel"};

        Tokenizer tokenizer = new UAX29URLEmailTokenizer();
        tokenizer.setReader(new StringReader(source));

        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testLemmagenFilterFactoryWithPath() throws IOException {
        AnalysisService analysisService = createAnalysisService();

        TokenFilterFactory tokenFilter = analysisService.tokenFilter("lemmagen_cs_path_filter");
        assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));

        String source = "Děkuji, že jsi přišel.";
        String[] expected = {"Děkovat", "že", "být", "přijít"};

        Tokenizer tokenizer = new UAX29URLEmailTokenizer();
        tokenizer.setReader(new StringReader(source));

        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public AnalysisService createAnalysisService() {
        Settings settings = Settings
                .settingsBuilder()
                .loadFromStream("lemmagen.json", getClass().getResourceAsStream("lemmagen.json"))
                .put("path.home", (new File("")).getAbsolutePath())
                .build();

        Index index = new Index("test");

        Injector parentInjector = new ModulesBuilder().add(new SettingsModule(settings),
                new EnvironmentModule(new Environment(settings))).createInjector();
        Injector injector = new ModulesBuilder().add(
                new IndexSettingsModule(index, settings),
                new IndexNameModule(index),
                new AnalysisModule(settings, parentInjector.getInstance(IndicesAnalysisService.class)).addProcessor(new LemmagenAnalysisBinderProcessor())).createChildInjector(parentInjector);

        return injector.getInstance(AnalysisService.class);
    }

}
