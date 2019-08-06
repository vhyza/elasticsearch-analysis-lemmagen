package org.elasticsearch.index.analysis;

import java.io.IOException;
import eu.hlavki.text.lemmagen.api.Lemmatizer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;

public class LemmagenFilter extends TokenFilter {

  private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);
  private Lemmatizer lemmatizer = null;

  public LemmagenFilter(final TokenStream input, final Lemmatizer lemmatizer) {
    super(input);
    this.lemmatizer = lemmatizer;
  }

  @Override
  public final boolean incrementToken() throws IOException {
    if (!input.incrementToken()) {
      return false;
    }
    CharSequence lemma = lemmatizer.lemmatize(termAttr);
    if (!keywordAttr.isKeyword() && !equalCharSequences(lemma, termAttr)) {
      termAttr.setEmpty().append(lemma);
    }
    return true;
  }

  /**
   * Compare two char sequences for equality. Assumes non-null arguments.
   */
  private boolean equalCharSequences(CharSequence s1, CharSequence s2) {
    int len1 = s1.length();
    int len2 = s2.length();
    if (len1 != len2)
      return false;
    for (int i = len1; --i >= 0;) {
      if (s1.charAt(i) != s2.charAt(i)) {
        return false;
      }
    }
    return true;
  }
}
