#!/usr/bin/python

import json
import re
from nltk.tokenize import word_tokenize

class LexiconBuilder(object):

  def __init__(self):
    pass

  def buildSentiLexicon(self, lexiconFile):
    self.lexicon = {}
    with open(lexiconFile, 'r') as lines:
      for line in lines:
        data = line.split('\t')
        word = re.sub('[^a-z]', '', data[0].lower())
        self.lexicon[word] = float(data[1])
      lines.close()
      
class ReviewProcessor(object):
  
  def __init__(self, lexicon):
    self.lexicon = lexicon

  def findWord(self, word):
    for i in range(1, len(word) + 1):
      if word[:i] in self.lexicon:
        return word[:i]
    return None
    
  def findMinAndMax(self, reviewFile):
    norm = lambda word: re.sub('[^a-z]', '', word.lower())
    min = 0.0
    max = 0.0

    with open(reviewFile, 'r') as reviews:
      for review in reviews:
        data = json.loads(review)
        words = word_tokenize(data['text'])
        words = set(norm(word) for word in words)
        sentiStrength = 0.0
        for word in words:
          if word in self.lexicon:
            sentiStrength += self.lexicon[word]
            if sentiStrength < min: min = sentiStrength
            if sentiStrength > max: max = sentiStrength
          else:
            k = self.findWord(word)
            if k:
              sentiStrength += self.lexicon[k]
              if sentiStrength < min: min = sentiStrength
              if sentiStrength > max: max = sentiStrength
      reviews.close()
    print '%f %f' % (min, max)

  def processReview(self, reviewFile, ratingFile):
    # normalize words by lowercasing and dropping non-alpha
    # characters
    norm = lambda word: re.sub('[^a-z]', '', word.lower())

    min = -164.0
    max = 56.0
    strengthRange = max - min
    with open(reviewFile, 'r') as reviews:
      output = open(ratingFile, 'w')
      for review in reviews:
        data = json.loads(review)
        words = word_tokenize(data['text'])
        words = set(norm(word) for word in words)
        sentiStrength = 0.0
        for word in words:
          if word in self.lexicon:
            sentiStrength += self.lexicon[word]
          else:
            k = self.findWord(word)
            if k:
              sentiStrength += self.lexicon[k]
        rating = (sentiStrength - min) / strengthRange * 5
        output.write(str(int(round(rating))) + '\n')
      output.flush() 
      output.close()
      reviews.close()

  def calcAccuracy(self):
    output = open('rating_new.txt', 'r')
    originRating = open('originRating.txt', 'r')
    count = 0
    while 1:
      r1 = output.readline()
      r2 = originRating.readline()
      if not r1 or not r2:
        break
      if r1.strip() == r2.strip():
        count += 1
    print str(float(count) / 229907.0)

if __name__ == '__main__':
  lb = LexiconBuilder()
  lb.buildSentiLexicon('SentiStrength_Sept2011/EmotionLookupTable.txt')
  rp = ReviewProcessor(lb.lexicon)
  #rp.findMinAndMax('/home/junyang/Downloads/yelp_phoenix_academic_dataset/yelp_academic_dataset_review.json')
  #rp.processReview('/home/junyang/Downloads/yelp_phoenix_academic_dataset/yelp_academic_dataset_review.json', 'rating_new.txt')
  rp.calcAccuracy()

