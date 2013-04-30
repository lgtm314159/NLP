from nltk import PorterStemmer
from nltk.classify.maxent import MaxentClassifier
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
import itertools
import json
import nltk.data
import re
import sys

norm = lambda word: re.sub('[^a-z]', '', word.lower())
tagger = nltk.data.load("taggers/treebank_brill_aubt.pickle")
stwords = stopwords.words('english')[29:]

def normalizeWord(word):
  stemmer = PorterStemmer()
  return stemmer.stem(norm(word))

class LexiconBuilder(object):

  def __init__(self):
    pass

  def buildSentiStrengthLexicon(self, lexiconFile):
    self.lexicon = {}
    with open(lexiconFile, 'r') as lines:
      for line in lines:
        data = json.loads(line)
        self.lexicon[data['word']] = float(data['positivity']) / 100
      lines.close()

  def buildCustomLexicon(self, lexiconFile):
    self.lexicon = {}
    with open(lexiconFile, 'r') as lines:
      for line in lines:
        data = json.loads(line)
        #self.lexicon[(data['word'], data['pos'])] = float(data['positivity']) / 100
        self.lexicon[(data['word'], data['pos'])] = True
      lines.close()

class MaxentClassifierTrainer(object):

  def __init__(self, trainingData, lexicon=None):
    self.trainingData = trainingData
    self.lexicon = lexicon

  # Unigram, word normalizing
  def buildTrainTokens(self):
    self.trainTokens = []
    with open(self.trainingData, 'r') as reviews:
      for review in reviews:
        data = json.loads(review)
        words = word_tokenize(data['text'])
        words = set(norm(word) for word in words if norm(word))
        featureSet = self.buildWordFeatureSet(words)
        self.trainTokens.append((featureSet, data['stars']))
      reviews.close()

  # Unigram, word normalizing, stopwords removal, stemming, POS 
  def buildTrainTokensUnigramPOS(self):
    self.trainTokens = []
    with open(self.trainingData, 'r') as reviews:
      for review in reviews:
        data = json.loads(review)
        words = word_tokenize(data['text'])
        words = [norm(word) for word in words if norm(word)]
        words = [word for word in words if word not in stwords]
        tagged_words = tagger.tag(words)
        stemmer = PorterStemmer()
        tagged_words = [(stemmer.stem(tagged_word[0]), tagged_word[1]) for tagged_word in tagged_words]
        featureSet = self.buildWordFeatureUnigramSetPOS(tagged_words)
        self.trainTokens.append((featureSet, data['stars']))
      reviews.close()

  # Bigram, word normalizing, stopwords removal, stemming 
  def buildTrainTokensBigram(self): 
    self.trainTokens = []
    with open(self.trainingData, 'r') as reviews:
      for review in reviews:
        data = json.loads(review)
        words = word_tokenize(data['text'])
        words = [norm(word) for word in words if norm(word)]
        words = [word for word in words if word not in stwords]
        stemmer = PorterStemmer()
        words = [stemmer.stem(word) for word in words]
        featureSet = self.buildWordFeatureSetBigram(words)
        self.trainTokens.append((featureSet, data['stars']))
      
  """
  def buildWordFeatureSet(self, words):
    return dict([('contains-word(%s)' % w, True) for w in words])
  """  

  """
  def buildWordFeatureSet(self, words):
    featureSet = {}
    for word in words:
      for i in range(1, len(word) + 1):
        if word[:i] in self.lexicon:
          featureSet['strength(%s)' % word] = self.lexicon[word[:i]]
    return featureSet
  """

  def buildWordFeatureSet(self, words):
    featureSet = {}
    for word in words:
      if word in self.lexicon:
        featureSet['strength(%s)' % word] = self.lexicon[word]
    return featureSet

  def buildWordFeatureSetUnigramPOS(self, tagged_words):
    featureSet = {}
    for tagged_word in tagged_words:
      if tagged_word in self.lexicon:
        featureSet[tagged_word] = True
    return featureSet

  def buildWordFeatureSetBigram(self, words):
    featureSet = {}
    args = [iter(words)] * 2
    bigrams = itertools.izip_longest(*args, fillvalue=None)
    for bigram in bigrams:
      featureSet[bigram] = True
    return featureSet

  def train(self, max_iter):
    return MaxentClassifier.train(train_toks=self.trainTokens, max_iter=max_iter)

  def calcAccuracy(self, testingData, predictedRating):
    predRating = open(predictedRating, 'r')
    originRating = open(testingData, 'r')
    total = 0.0
    correct = 0.0
    while 1:
      r1 = predRating.readline()
      r2 = originRating.readline()
      if not r1 or not r2:
        break
      total += 1
      data = json.loads(r2)
      if int(r1.strip()) == data['stars']:
        correct += 1
    print str(float(correct) / total)

# Unigram, SentiStrength lexicon, word normalizing
def classifyWithUnigramAndSentiStrengthLexicon(category_no, max_iter):
  lb = LexiconBuilder()
  lb.buildSentiStrengthLexicon('SentiStrength_Sept2011/EmotionLookupTable.txt')
  trainer = MaxentClassifierTrainer('reviews/category' + category_no + '-training.json', lb.lexicon)
  trainer.buildTrainTokens()
  classifier = trainer.train(int(max_iter))
  with open('reviews/category' + category_no + '-testing.json', 'r') as reviews:
    output = open('maxent_rating.txt', 'w')
    for review in reviews:
      data = json.loads(review)
      words = word_tokenize(data['text'])
      words = [norm(word) for word in words if norm(word)]
      featureSet = trainer.buildWordFeatureSet(words)
      output.write(str(classifier.classify(featureSet)) + '\n')
    output.flush()
    output.close()
    reviews.close()
  trainer.calcAccuracy('reviews/category' + category_no + '-testing.json', 'maxent_rating.txt')

# Unigram, custom lexicon, word normalizing, stopwords removal, stemming, POS
def classifyWithUnigramAndCustomLexicon(category_no, max_iter):
  lb = LexiconBuilder()
  lb.buildCustomLexicon('reviews/category' + category_no + '-word-positivity.json')
  trainer = MaxentClassifierTrainer('reviews/category' + category_no + '-training.json', lb.lexicon)
  trainer.buildTrainTokensUnigramPOS()
  classifier = trainer.train(int(max_iter))
  with open('reviews/category' + category_no + '-testing.json', 'r') as reviews:
    output = open('maxent_rating.txt', 'w')
    for review in reviews:
      data = json.loads(review)
      words = word_tokenize(data['text'])
      words = [norm(word) for word in words if norm(word)]
      words = [word for word in words if word not in stwords]
      tagged_words = tagger.tag(words)
      stemmer = PorterStemmer()
      tagged_words = [(stemmer.stem(tagged_word[0]), tagged_word[1]) for tagged_word in tagged_words]
      featureSet = trainer.buildWordFeatureSetUnigramPOS(tagged_words)
      output.write(str(classifier.classify(featureSet)) + '\n')
    output.flush()
    output.close()
    reviews.close()
  trainer.calcAccuracy('reviews/category' + category_no + '-testing.json', 'maxent_rating.txt')

# Bigram, word normalizing, stopwords removal, stemming 
def classifyWithBigram(category_no, max_iter):
  trainer = MaxentClassifierTrainer('reviews/category' + category_no + '-training.json')
  trainer.buildTrainTokensBigram()
  classifier = trainer.train(int(max_iter))
  with open('reviews/category' + category_no + '-testing.json', 'r') as reviews:
    output = open('maxent_rating.txt', 'w')
    for review in reviews:
      data = json.loads(review)
      words = word_tokenize(data['text'])
      words = [norm(word) for word in words if norm(word)]
      words = [word for word in words if word not in stwords]
      stemmer = PorterStemmer()
      words = [stemmer.stem(word) for word in words]
      featureSet = trainer.buildWordFeatureSetBigram(words)
      output.write(str(classifier.classify(featureSet)) + '\n')
    output.flush()
    output.close()
    reviews.close()
  trainer.calcAccuracy('reviews/category' + category_no + '-testing.json', 'maxent_rating.txt')

if __name__ == '__main__':
  if len(sys.argv) != 3:
    print 'Usage: python train_maxent.py category_no max_iter'
  else:
    #classifyWithUnigramAndCustomLexicon(sys.argv[1], sys.argv[2])
    classifyWithBigram(sys.argv[1], sys.argv[2])

