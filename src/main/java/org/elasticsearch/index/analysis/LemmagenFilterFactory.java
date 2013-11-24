package org.elasticsearch.index.analysis;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import eu.hlavki.text.lemmagen.api.Lemmatizer;
import eu.hlavki.text.lemmagen.LemmatizerFactory;

import org.apache.lucene.analysis.TokenStream;

import org.elasticsearch.env.Environment;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;


public class LemmagenFilterFactory extends AbstractTokenFilterFactory {

    private Lemmatizer lemmatizer;

    @Inject
    public LemmagenFilterFactory(Index index,
                                @IndexSettings Settings indexSettings,
                                Environment env,
                                @Assisted String name,
                                @Assisted Settings settings) throws URISyntaxException {

        super(index, indexSettings, name, settings);

        String lexicon     = settings.get("lexicon", "mlteast-en");
        String lexiconPath = settings.get("lexicon_path", null);

        if (lexiconPath != null) {
            this.lemmatizer = getLemmatizer(env.resolveConfig(lexiconPath).toURI());
        } else {
            lexicon         = lexicon.contains("mlteast-") ? lexicon : "mlteast-" + lexicon;
            this.lemmatizer = getLemmatizer(lexicon);
        }
    }

    public Lemmatizer getLemmatizer(URI lexiconPath) {
        try {
            File lexiconFile = new File(lexiconPath);
            return LemmatizerFactory.read(new FileInputStream(lexiconFile));
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't initialize lemmatizer from resource path " + lexiconPath.toString(), e);
        }
    }

    public Lemmatizer getLemmatizer(String lexicon) {
        try {
            return LemmatizerFactory.getPrebuild(lexicon);
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't initialize lemmatizer from resource " + lexicon, e);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new LemmagenFilter(tokenStream, lemmatizer);
    }

}
