import requests
import urllib
import pandas as pd
from requests_html import HTML
from requests_html import HTMLSession
import random
from concurrent.futures import ThreadPoolExecutor
from tqdm.auto import tqdm
import numpy as np
from urllib.request import Request, urlopen

n = 100
max_query_len = 2
pages = 3

google_domains = ('https://www.google.',
                  'https://google.',
                  'https://webcache.googleusercontent.',
                  'http://webcache.googleusercontent.',
                  'https://policies.google.',
                  'https://support.google.',
                  'https://maps.google.')

url = "https://svnweb.freebsd.org/csrg/share/dict/words?revision=61569&view=co"
req = Request(url, headers={'User-Agent': 'Mozilla/5.0'})

web_byte = urlopen(req).read()

webpage = web_byte.decode('utf-8')
words = webpage.splitlines()


def get_source(url):
    try:
        session = HTMLSession()
        response = session.get(url)
        return response

    except requests.exceptions.RequestException as e:
        print(e)


def shrink_url(url):
    return url.split('/')[2]


def scrape_google(query):
    links = []

    for i in range(0, (pages - 1) * 10 + 1, 10):
        print(i)
        query = urllib.parse.quote_plus(query)
        response = get_source("https://www.google.com/search?q=" + query + '&start=' + str(i))

        links += list(response.html.absolute_links)

    for url in links:
        if url.startswith(google_domains):
            links.remove(url)

    return set(map(shrink_url, links))


def gen_word_combos(n, max_query_len):
    ans = []
    for i in range(n):
        ans.append(' '.join(random.sample(words, random.randint(1, max_query_len))))
    return ans


combos = gen_word_combos(n, max_query_len)

with ThreadPoolExecutor(20) as executor:
    results = list(tqdm(executor.map(scrape_google, combos), total=len(combos)))

data = set()

for res in results:
    data = data.union(res)

data = np.array(list(data))
pred = np.vectorize(lambda x: not x[0].isdigit())

data = data[pred(data)]

data = np.stack((data, np.ones(len(data), dtype=int)), axis=-1)
pd.DataFrame(data).to_csv('google_data.csv', header=None, index=False)
