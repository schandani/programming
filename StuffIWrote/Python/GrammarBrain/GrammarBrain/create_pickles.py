import cPickle as pickle

from GrammarBrain import get_brown_pos_sents as gbp

for i in range(2, 25):
    print i
    nice_sentences = gbp.get_nice_sentences(MAX=i+3, MIN=i+1)
    tuple_file = 'pickles/nice_sentences_%d.p' % i
    pickle.dump(nice_sentences, open(tuple_file, 'wb'))

    sentence_matrices = gbp.construct_sentence_matrices(nice_sentences)
    matrix_file = 'pickles/sentence_matrices_%d.p' % i
    pickle.dump(sentence_matrices, open(matrix_file, 'wb'))

    sentences_words = gbp.sentence_strings(nice_sentences, 10000)
    words_file = 'pickles/sentence_words_%d.p' % i
    pickle.dump(sentences_words, open(words_file, 'wb'))
