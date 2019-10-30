package org.openmrs.module.pihcore.search;

import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.standard.ClassicFilterFactory;
import org.hibernate.search.annotations.Factory;
import org.hibernate.search.cfg.SearchMapping;
import org.openmrs.api.db.hibernate.search.LuceneAnalyzers;


public class PihLuceneAnalyzerFactory {
    @Factory
    public SearchMapping getSearchMapping() {
        SearchMapping mapping = new SearchMapping();
        mapping
                .analyzerDef(LuceneAnalyzers.PHRASE_ANALYZER, KeywordTokenizerFactory.class)
                .filter(ClassicFilterFactory.class)
                .filter(ASCIIFoldingFilterFactory.class)
                .filter(LowerCaseFilterFactory.class);
        mapping.analyzerDef(LuceneAnalyzers.EXACT_ANALYZER, WhitespaceTokenizerFactory.class)
                .filter(ClassicFilterFactory.class)
                .filter(LowerCaseFilterFactory.class);
        mapping.analyzerDef(LuceneAnalyzers.START_ANALYZER, WhitespaceTokenizerFactory.class)
                .filter(ClassicFilterFactory.class)
                .filter(LowerCaseFilterFactory.class)
                .filter(ASCIIFoldingFilterFactory.class)
                .filter(EdgeNGramFilterFactory.class)
                .param("minGramSize", "2")
                .param("maxGramSize", "20");
        mapping.analyzerDef(LuceneAnalyzers.ANYWHERE_ANALYZER, WhitespaceTokenizerFactory.class)
                .filter(ClassicFilterFactory.class)
                .filter(LowerCaseFilterFactory.class)
                .filter(ASCIIFoldingFilterFactory.class)
                .filter(NGramFilterFactory.class)
                .param("minGramSize", "2")
                .param("maxGramSize", "20");
        return mapping;
    }
}
