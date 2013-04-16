#!/usr/bin/python

import json
import re

class LexiconBuilder(object):

  def __init__(self):
    pass

  def buildSentiLexicon(self, lexiconFile):
    words = file(lexiconFile, "r")
    self.lexicon = {}
    count = 0;
    for word in words:
      fields = word.split(" ")
      if fields[0].split("=")[1].strip() == "weaksubj":
        if fields[5].split("=")[1].strip() == "positive":
          self.lexicon[fields[2].split("=")[1].strip()] = 0.5
        else:
          self.lexicon[fields[2].split("=")[1].strip()] = -0.5
      else:
        if fields[5].split("=")[1].strip() == "positive":
          self.lexicon[fields[2].split("=")[1].strip()] = 1
        else:
          self.lexicon[fields[2].split("=")[1].strip()] = -1
    words.close()

class ReviewProcessor(object):
  
  def __init__(self, lexicon):
    self.lexicon = lexicon

  def processReview(self, reviewFile, ratingFile):
    # normalize words by lowercasing and dropping non-alpha
    # characters
    norm = lambda word: re.sub('[^a-z]', '', word.lower())

    min = -32.5
    max = 26.0
    strengthRange = max - min
    output = open(ratingFile, 'w')
    originRating = open('originRating.txt', 'w')
    with open(reviewFile, 'r') as reviews:
      count = 0;
      for review in reviews:
        data = json.loads(review)
        originRating.write(str(data['stars']) + '\n')
        # only include a word once per-review (which de-emphasizes
        # proper nouns)
        words = set(norm(word) for word in data['text'].split())
        sentiStrength = 0.0;
        for word in words:
          if word in self.lexicon:
            sentiStrength += self.lexicon[word]
        rating = (sentiStrength - min) / (strengthRange) * 5 + 1
        count += 1;
        """
        output.write(str(count) + ' ' + str(sentiStrength) + ' ' +\
            str(int(round(rating))) + '\n')
        """
        output.write(str(int(round(rating))) + '\n')
      reviews.close()
    originRating.flush()
    originRating.close()
    output.flush()
    output.close()

  def calcAccuracy(self):
    output = open('rating.txt', 'r')
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
  lb.buildSentiLexicon('subjclues.tff')
  rr = ReviewProcessor(lb.lexicon)
  #rr.processReview('reviews.json', 'rating.txt')
  rr.calcAccuracy()
 
