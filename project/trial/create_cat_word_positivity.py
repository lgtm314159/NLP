from mrjob.job import MRJob
from mrjob.protocol import JSONValueProtocol
from subprocess import call
from naive import LexiconBuilder
from nltk.tokenize import word_tokenize
from nltk import PorterStemmer
from nltk.corpus import stopwords
#from nltk import pos_tag
import nltk.data
import re
import sys

tagger = nltk.data.load("taggers/treebank_brill_aubt.pickle")
stwords = stopwords.words('english')[29:]
norm = lambda word: re.sub('[^a-z]', '', word.lower())

MINIMUM_OCCURENCES = 10
MINIMUM_BUSINESSES = 3
MINIMUM_TOTAL = 0

def avg_and_total(ratings):
  count = 0;
  total = 0.0;
  for rating in ratings:
    total += rating;
    count += 1;

  return total / count, total

def normalizeWord(word):
  stemmer = PorterStemmer()
  return stemmer.stem(norm(word))
  return norm(word)
  
class WordPositivityJob(MRJob):
  INPUT_PROTOCOL = JSONValueProtocol
  OUTPUT_PROTOCOL = JSONValueProtocol

  """
  def review_mapper(self, _, data):
    review = data['text']
    rating = data['stars']
    business_id = data['business_id']
    category = data['category']
    words = word_tokenize(review)
    words = set(normalizeWord(word) for word in words)
    for word in words:
      yield (category, word), (business_id, rating, 1)
  """

  def review_mapper(self, _, data):
    review = data['text']
    rating = data['stars']
    business_id = data['business_id']
    category = data['category']
    words = word_tokenize(review)
    words = [norm(word) for word in words if norm(word)]
    words = [word for word in words if word not in stwords]
    tagged_words = tagger.tag(words)
    stemmer = PorterStemmer()
    tagged_words = [(stemmer.stem(tagged_word[0]), tagged_word[1]) for tagged_word in tagged_words]
    for tagged_word in tagged_words:
      yield (category, tagged_word), (business_id, rating, 1)
  
  """
  def word_rating_reducer(self, category_word, businesses_ratings_occurences):
    category, word = category_word
    businesses = set()
    ratings = []
    occurences = 0

    for business_id, rating, occurence in businesses_ratings_occurences:
      businesses.add(business_id)
      ratings.append(rating)
      occurences += occurence 

    if len(businesses) < MINIMUM_BUSINESSES:
      return

    if occurences < MINIMUM_OCCURENCES:
      return

    avg, total = avg_and_total(ratings)
    if total < MINIMUM_TOTAL:
      return
    yield None, {'positivity': int(avg * 100), 'total': total, 'word': word, 'category': category}
    """

  def word_rating_reducer(self, category_word, businesses_ratings_occurences):
    category, tagged_word = category_word
    businesses = set()
    ratings = []
    occurences = 0

    for business_id, rating, occurence in businesses_ratings_occurences:
      businesses.add(business_id)
      ratings.append(rating)
      occurences += occurence 

    if len(businesses) < MINIMUM_BUSINESSES:
      return

    if occurences < MINIMUM_OCCURENCES:
      return

    avg, total = avg_and_total(ratings)
    #if (avg < 2.5 and total < 50) or (avg > 3.5 and total > 50): 
    yield None, {'positivity': int(avg * 100), 'total': total, 'word': tagged_word[0], 'pos': tagged_word[1], 'category': category}

  def steps(self):
    return [self.mr(mapper=self.review_mapper,
        reducer=self.word_rating_reducer) ] 

if __name__ == '__main__':
  if len(sys.argv) < 3:
    print 'Usage: python create_category_word_positivity.py category_no cat_word_positivity_file'
  else:
    sys.stdout = open(sys.argv[2], 'w')
    WordPositivityJob().run()
  
