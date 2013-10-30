from mrjob.job import MRJob
from mrjob.protocol import JSONValueProtocol
from subprocess import call
from naive import LexiconBuilder
from nltk.tokenize import word_tokenize
import re
import sys

#sys.stdout = open('categoryOriginRating.txt', 'w')
#sys.stdout = open('categoryPredictedRating.txt', 'w')
#sys.stdout = open('categoryReview.txt', 'w')
sys.stdout = open('category_word_rating.json', 'w')

MINIMUM_OCCURENCES = 10
MINIMUM_BUSINESSES = 3

def avg_and_total(ratings):
  count = 0;
  total = 0.0;
  for rating in ratings:
    total += rating;
    count += 1;

  return total / count, total

class CreateCategoryReview(MRJob):
  INPUT_PROTOCOL = JSONValueProtocol
  #OUTPUT_PROTOCOL = JSONValueProtocol

  lb = LexiconBuilder()
  lb.buildSentiLexicon('subjclues.tff')
  lexicon = lb.lexicon

  def review_category_mapper(self, _, data):
    if data['type'] == 'review':
      yield data['business_id'], ('review', (data['text'], data['stars']))
    elif data['type'] == 'business':
      if data['categories']:
        yield data['business_id'], ('categories', data['categories']) 

  def category_join_reducer(self, business_id, reviews_or_categories):
    categories = None
    reviews = []
    for data_type, data in reviews_or_categories:
      if data_type == 'review':
        reviews.append(data)
      else:
        categories = data
  
    if not categories:
      return

    for category in categories:
      for review in reviews:
        yield None, {'category': category, 'business_id': business_id,
            'text': review[0], 'stars': review[1]}

  """
  def category_join_reducer(self, business_id, reviews_or_categories):
    categories = None
    reviews = []
    for data_type, data in reviews_or_categories:
      if data_type == 'review':
        reviews.append(data)
      else:
        categories = data
  
    if not categories:
      return

    for category in categories:
      for review_rating in reviews:
        yield category, (business_id, review_rating)

  def review_mapper(self, category, business_review_rating):
    business_id, (review, rating) = business_review_rating
    norm = lambda word: re.sub('[^a-z]', '', word.lower())
    words = word_tokenize(review)
    words = set(norm(word) for word in words)
    for word in words:
      yield (category, word), (business_id, rating, 1)
    
  def rating_reducer(self, category_word, businesses_ratings_occurences):
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
    yield int(avg * 100), (category, total, word)
  """

  def category_origin_rating_reducer(self, business_id, reviews_or_categories):
    categories = None
    reviews = []
    for data_type, data in reviews_or_categories:
      if data_type == 'review':
        reviews.append(data)
      else:
        categories = data
  
    if not categories:
      return

    for category in categories:
      for review in reviews:
        yield category, review[1]

  def category_rating_prediction_reducer(self, business_id, reviews_or_categories):
    categories = None
    reviews = []
    min = -32.5
    max = 26.0
    strengthRange = max - min
    norm = lambda word: re.sub('[^a-z]', '', word.lower())

    for data_type, data in reviews_or_categories:
      if data_type == 'review':
        reviews.append(data)
      else:
        categories = data
    
    if not categories:
      return

    for category in categories:
      for review in reviews:
        words = set(norm(word) for word in review[0].split())
        sentiStrength = 0.0;
        for word in words:
          if word in self.lexicon:
            sentiStrength += self.lexicon[word]
        rating = (sentiStrength - min) / (strengthRange) * 5 + 1
        yield category, int(round(rating))

  def steps(self):
    #return [ self.mr(self.review_category_mapper, self.category_origin_rating_reducer) ]
    #return [ self.mr(self.review_category_mapper, self.category_rating_prediction_reducer) ]
    return [ self.mr(self.review_category_mapper, self.category_join_reducer) ]

if __name__ == '__main__':
  CreateCategoryReview().run()
  
