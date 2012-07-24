/**
 * Copyright (C) Faruk Akgul 2012.
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.akgul;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small library to calculate the most important words of given documents.
 *
 * @author Faruk Akgul
 * @version 0.1
 */

public class TfIdf {

  private final List<String> documents;

  /**
   * Takes a documents of words as parameter.
   *
   * @param documents List of documents.
   */
  public TfIdf(List<String> documents) {
    this.documents = documents;
  }

  /**
   * Returns the documents that contains the documents.
   *
   * @return List<String> documents
   */
  public List<String> getDocuments() {
    return this.documents;
  }

  /**
   * Generates documents of words from the provided documents which are matched by
   * the REGEX.
   *
   * @param document Given document to filter its words.
   * @return List that contains the words of the document which are matched.
   */
  List<String> build_words(String document) {
    List<String> wordList = new ArrayList<String>();
    Matcher matcher = Pattern.compile("[a-zA-Z]+").matcher(document);
    while (matcher.find()) wordList.add(matcher.group());
    return wordList;
  }

  /**
   * Calculates the Tf scores of words which are built by build_words.
   *
   * @return map that contains the words and their Tf scores.
   * @see org.akgul.TfIdf#build_words(String)
   */
  public Map<String, MutableInt> tf() {
    Map<String, MutableInt> map = new HashMap<String, MutableInt>();
    for (String document : documents) {
      List<String> split = build_words(document);
      for (String s : split) {
        MutableInt counter = map.get(s);
        if (counter == null) map.put(s, MutableInt.createMutableInt());
        else counter.increment();
      }
    }
    return map;
  }

  /**
   * Calculates the Tf scores of words of a single document. Words are split
   * by spaces. This should fixed with a smarter REGEX.
   *
   * @param document Single document to calculate the Tf scores of words of it.
   * @return map that contains the words and Tf scores.
   */
  public Map<String, MutableInt> tf(String document) {
    Map<String, MutableInt> map = new HashMap<String, MutableInt>();
    String[] split = document.split(" ");
    for (String s : split) {
      MutableInt counter = map.get(s);
      if (counter == null) map.put(s, MutableInt.createMutableInt());
      else counter.increment();
    }
    return map;
  }

  /**
   * This is the same method as calculating the tf scores. I don't know why I've
   * written the same thing again.
   *
   * @param list2 List that contains the words that are split by space.
   * @return map that contains the words and their tf scores.
   * @see org.akgul.TfIdf#tf()
   */
  private Map<String, MutableInt> counter(final List<String> list2) {
    Map<String, MutableInt> map = new HashMap<String, MutableInt>();
    for (String s : list2) {
      MutableInt counter = map.get(s);
      if (counter == null) map.put(s, MutableInt.createMutableInt());
      else counter.increment();
    }
    return map;
  }

  /**
   * Calculates the df scores of the words in documents.
   *
   * @return Map that contains the words and their df scores.
   */
  public Map<String, MutableInt> df() {
    List<String> list2 = new ArrayList<String>();
    Set<String> set = new HashSet<String>();
    for (String s : documents) {
      for (String d : build_words(s)) set.add(d);
      for (String d : set) list2.add(d);
      set.clear();
    }
    return counter(list2);
  }

  /**
   * Calculates the df scores of the words in given documents documents.
   *
   * @param documents Document documents to calculate df scores.
   * @return Map that contains the words and their df scores.
   */
  public Map<String, MutableInt> df(List<String> documents) {
    List<String> list2 = new ArrayList<String>();
    Set<String> set = new HashSet<String>();
    for (String s : documents) {
      for (String d : build_words(s)) set.add(d);
      for (String d : set) list2.add(d);
      set.clear();
    }
    return counter(list2);
  }

  /**
   * Calculates the idf scores of words.
   *
   * @return Map that contains the words and their idf scores.
   */
  public Map<String, Double> idf() {
    Map<String, MutableInt> map = df();
    Map<String, Double> map2 = new HashMap<String, Double>();
    for (String s : documents) {
      List<String> split = build_words(s);
      for (String d : split) {
        if (map.get(d) != null) {
          double val = 1.0 / (map.get(d).getCounter() + 1e-100);
          map2.put(d, val);
        }
      }
    }
    return map2;
  }

  /**
   * Calculates the tfidf scores of words. Basically, calculating the tfidf scores
   * gives us the most important words of documents. This method has quite a few
   * weakness. For example; this method gives very high scores for very rare words.
   * For more accuracy, @see TfIdf#tfidf_tweak1.
   *
   * @return Map that contains the words and their tfidf scores.
   * @see org.akgul.TfIdf#tfidf_tweak1
   */
  public Map<String, Double> tfidf() {
    Map<String, MutableInt> df_scores = df();
    Map<String, Double> map = new HashMap<String, Double>();
    for (String s : documents) {
      List<String> split = build_words(s);
      Map<String, MutableInt> tf_scores = tf(s);
      for (String d : split) {
        if (map.get(d) == null) {
          double score = tf_scores.get(d).getCounter() / (df_scores.get(d
          ).getCounter() + 0.01);
          map.put(d, score);
        }
      }
    }
    return map;
  }

  /**
   * Calculates the tfidf scores of words. This method also finds out the most
   * important words of given documents. However, this is more intelligent than
   * then pure TfIdf#tfidf method.
   *
   * @return Map that contains the words and their tweaked tfidf scores.
   * @see org.akgul.TfIdf#tfidf()
   */
  public Map<String, Double> tfidf_tweak1() {
    Map<String, MutableInt> df_scores = df();
    final int N = documents.size();
    Map<String, Double> map = new HashMap<String, Double>();
    for (String s : documents) {
      List<String> split = build_words(s);
      Map<String, MutableInt> tf_scores = tf(s);
      for (String d : split) {
        if (map.get(d) == null) {
          double score = tf_scores.get(d).getCounter() * (Math.log(
              N / df_scores.get(d).getCounter()
          ) + 0.01);
          map.put(d, score);
        }
      }
    }
    return map;
  }
}
