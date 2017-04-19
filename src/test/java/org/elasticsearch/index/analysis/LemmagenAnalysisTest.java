package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.plugin.analysis.lemmagen.AnalysisLemmagenPlugin;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.test.ESTokenStreamTestCase;

import static org.elasticsearch.test.ESTestCase.createTestAnalysis;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class LemmagenAnalysisTest extends ESTokenStreamTestCase {

    public void testLemmagenFilterFactoryWithDefaultLexicon() throws IOException {
        ESTestCase.TestAnalysis analysis = createAnalysis();

        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("lemmagen_default_filter");
        assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));

        String source = "I was late.";
        String[] expected = new String[]{"I", "be", "late"};

        Tokenizer tokenizer = new UAX29URLEmailTokenizer();
        tokenizer.setReader(new StringReader(source));

        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public void testLemmagenFilterFactoryWithCustomLexicon() throws IOException {
        ESTestCase.TestAnalysis analysis = createAnalysis();

        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("lemmagen_cs_filter");
        assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));

        String source = "Děkuji, že jsi přišel.";
        String[] expected = {"Děkovat", "že", "být", "přijít"};

        Tokenizer tokenizer = new UAX29URLEmailTokenizer();
        tokenizer.setReader(new StringReader(source));

        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public void testLemmagenFilterFactoryWithShortLexiconCode() throws IOException {
        ESTestCase.TestAnalysis analysis = createAnalysis();

        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("lemmagen_fr_filter");
        assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));

        String source = "Il faut encore ajouter une pincée de sel.";
        String[] expected = new String[]{"Il", "falloir", "encore", "ajouter", "un", "pincer", "de", "sel"};

        Tokenizer tokenizer = new UAX29URLEmailTokenizer();
        tokenizer.setReader(new StringReader(source));

        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public void testLemmagenFilterFactoryWithPath() throws IOException {
        ESTestCase.TestAnalysis analysis = createAnalysis();

        TokenFilterFactory tokenFilter = analysis.tokenFilter.get("lemmagen_cs_path_filter");
        assertThat(tokenFilter, instanceOf(LemmagenFilterFactory.class));

        String source = "Děkuji, že jsi přišel.";
        String[] expected = {"Děkovat", "že", "být", "přijít"};

        Tokenizer tokenizer = new UAX29URLEmailTokenizer();
        tokenizer.setReader(new StringReader(source));

        assertTokenStreamContents(tokenFilter.create(tokenizer), expected);
    }

    public ESTestCase.TestAnalysis createAnalysis() throws java.io.IOException {
    public void testAnalyzerWithKeywordRepeatFilter() throws IOException {
        ESTestCase.TestAnalysis analysis = createAnalysis();
        NamedAnalyzer analyzerWithKeywordRepeat = analysis.indexAnalyzers.get("lemmagen_with_keyword_repeat");

        assertTokenStreamContents(analyzerWithKeywordRepeat.tokenStream("test", "am"), new String[]{"am", "be"});
    }

        Settings settings = Settings
                            .builder()
                            .loadFromStream("lemmagen.json", getClass().getResourceAsStream("lemmagen.json"))
                            .build();

        Settings nodeSettings = Settings
                                .builder()
                                .put("path.home", (new File("")).getAbsolutePath())
                                .put("path.conf", (new File("")).getAbsolutePath())
                                .build();

        Index index = new Index("test", "_na_");

        ESTestCase.TestAnalysis analysis = createTestAnalysis(index, nodeSettings, settings, new AnalysisLemmagenPlugin());

        return analysis;
    }

}
