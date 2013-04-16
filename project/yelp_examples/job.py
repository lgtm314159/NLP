from mrjob.job import MRJob
from mrjob.protocol import JSONValueProtocol
import re

MINIMUM_OCCURENCES = 1000

def avg_and_total(ratings):
  count = 0;
  total = 0.0;
  for rating in ratings:
    total += rating;
    count += 1;

  return total / count, total

class MRWordFreqCount(MRJob):

  INPUT_PROTOCOL = JSONValueProtocol

  def mapper(self, _, data):
    if data['type'] != 'review':
      return

    # normalize words by lowercasing and dropping non-alpha
    # characters
    norm = lambda word: re.sub('[^a-z]', '', word.lower())
    # only include a word once per-review (which de-emphasizes
    # proper nouns)
    words = set(norm(word) for word in data['text'].split())
    for word in words:
      yield word, data['stars']

  def reducer(self, word, ratings):
    avg, total = avg_and_total(ratings)
    if total < MINIMUM_OCCURENCES:
      return

    yield (avg, total), word
  
if __name__ == '__main__':
  MRWordFreqCount().run()

