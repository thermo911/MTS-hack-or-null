import pandas as pd
import requests
import numpy as np
from concurrent.futures import ThreadPoolExecutor
from tqdm.auto import tqdm

df = pd.read_csv('host.csv', header=None)
start, end = 0, 10000

def process_url(url):
    try:
        r = requests.get('http://' + url, timeout=1)
        if r.status_code != 200 or r.text[:5].upper() != '<!DOC':
            return np.array([url, 0])
        return np.array([url, 1])
    except:
        return np.array([url, 0])

with ThreadPoolExecutor(20) as executor:
    results = list(tqdm(executor.map(process_url, df[0][start:end]), total=end-start))

pd.DataFrame(results).to_csv('data' + str(start) + '-' + str(end) + '.csv', header=None, index=False)
