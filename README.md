# TfIdf

This is a small library to calculate the most important words in given documents.

- **Tf**: Term Frequency
- **Idf**: Inverse Document Frequency

## Tf (Term Frequency)

Formula:

	Tf(d, t)

- `d`: Document
- `t`: Number of times term `t` appears in document `d`.

## Df (Document Frequency)

The formula is:

	Df(c, t)

- `c`: Documents (corpus) of a given dataset.
- `t`: Number of times term `t` appears in corpus `c`.

Inverse Document Frequency Formula:

	1 / Df(c, t)


## Tf.Idf

Formula is:

	Tf.Idf(c, d, t) = Tf(d, t) / Df(c, t)

### Tf.Idf Tweak 1

The problem with the simplest Tf.Idf approach is it gives very high score for very rare words so we need to "tweak" the formula. The simplest way would be adding `log`.

Formula:

	Tf.Idf(c, d, t) = Tf(d, t) log (N / Df(c, t))

- **N**: The number of total documents.

Stop words could be filtered out to get more accurate results. The stop words in `stop_words.txt` are from `google.appengine.ext.search` package.

# License

Released under GPL3.