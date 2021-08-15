from flask import Flask
import pandas as pd
from model.Model import Model
import numpy as np
app = Flask('test')

@app.route('/get_result/<site>')
def get_result(site, methods=['GET']):
    print(site)
    try:
        if len(site) > 4 and site[:4] == 'www.':
            return 'user site'
        ans = model.predict([site])
        return 'tech site' if ans == 0 else 'user site'
    except:
        return 'error'

@app.route('/get_result_csv/<file>')
def get_result_csv(file, methods=['GET']):
    try:
        df = pd.read_csv("data/" + file, header=None)
        new_path = file[:-4] + '_res.csv'
        X = np.array(df[0])
        preds = model.predict(X)

        data = np.stack((X, preds), axis=-1)
        for i in range(len(data)):
            if len(data[i][0]) > 4 and data[i][0][:4] == 'www.':
                data[i][1] = 1
        pd.DataFrame(data).to_csv('data/' + new_path, header=None, index=False)
        return new_path
    except:
        return error

model = Model()
model.load_model('best_model')
app.run()
